package com.nt.cms.audit.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nt.cms.audit.dto.AuditLogResponse;
import com.nt.cms.audit.dto.AuditLogSearchRequest;
import com.nt.cms.audit.mapper.AuditLogMapper;
import com.nt.cms.audit.vo.AuditLogVO;
import com.nt.cms.common.exception.BusinessException;
import com.nt.cms.common.exception.ErrorCode;
import com.nt.cms.common.response.PageResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

/**
 * 감사 로그 서비스 구현체
 * 
 * <p>수정/삭제 작업 시 다른 서비스에서 log()를 호출하여 이력 저장.</p>
 * <p>REQUIRES_NEW: 감사 로그 실패가 메인 트랜잭션에 영향 주지 않도록 분리.</p>
 * 
 * @author CMS Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefaultAuditLogService implements AuditLogService {

    private final AuditLogMapper auditLogMapper;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void record(Long actorId, String action, String targetType, Long targetId, Object before, Object after) {
        try {
            String beforeJson = toJson(before);
            String afterJson = toJson(after);
            log(actorId, null, action, targetType, targetId, beforeJson, afterJson);
        } catch (Exception e) {
            log.warn("감사 로그 기록 실패 (record): action={}, targetType={}, targetId={}, error={}",
                    action, targetType, targetId, e.getMessage());
        }
    }

    /**
     * 객체를 JSON 문자열로 변환
     */
    private String toJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.warn("감사 로그 JSON 직렬화 실패: {}", e.getMessage());
            return null;
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void log(Long userId, String username, String action, String targetType, Long targetId,
                    String beforeData, String afterData) {
        String ipAddress = null;
        String userAgent = null;
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                if (request != null) {
                    ipAddress = getClientIp(request);
                    userAgent = request.getHeader("User-Agent");
                }
            }
        } catch (Exception e) {
            log.debug("RequestContext에서 IP/UserAgent 추출 실패 (비동기 등): {}", e.getMessage());
        }
        log(userId, username, action, targetType, targetId, beforeData, afterData, ipAddress, userAgent);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void log(Long userId, String username, String action, String targetType, Long targetId,
                    String beforeData, String afterData, String ipAddress, String userAgent) {
        try {
            AuditLogVO auditLog = AuditLogVO.builder()
                    .userId(userId)
                    .username(username)
                    .action(action)
                    .targetType(targetType)
                    .targetId(targetId)
                    .beforeData(truncateIfNeeded(beforeData, 65535))
                    .afterData(truncateIfNeeded(afterData, 65535))
                    .ipAddress(ipAddress)
                    .userAgent(truncateIfNeeded(userAgent, 255))
                    .build();

            auditLogMapper.insert(auditLog);
            log.debug("감사 로그 기록: action={}, targetType={}, targetId={}", action, targetType, targetId);
        } catch (Exception e) {
            log.error("감사 로그 기록 실패: action={}, targetType={}, targetId={}", action, targetType, targetId, e);
            throw e;
        }
    }

    @Override
    public PageResponse<AuditLogResponse> getAuditLogs(AuditLogSearchRequest request) {
        log.debug("감사 로그 목록 조회: {}", request);

        List<AuditLogVO> list = auditLogMapper.findAll(request);
        long total = auditLogMapper.countAll(request);

        List<AuditLogResponse> content = list.stream()
                .map(AuditLogResponse::from)
                .toList();

        return PageResponse.of(content, request.getPage(), request.getSize(), total);
    }

    @Override
    public AuditLogResponse getAuditLog(Long id) {
        log.debug("감사 로그 상세 조회: id={}", id);

        AuditLogVO vo = auditLogMapper.findById(id);
        if (vo == null) {
            throw new BusinessException(ErrorCode.AUDIT_LOG_NOT_FOUND);
        }

        return AuditLogResponse.from(vo);
    }

    /**
     * 클라이언트 IP 추출 (X-Forwarded-For 대응)
     */
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    /**
     * TEXT/CLOB 필드 길이 제한 시 truncate
     */
    private String truncateIfNeeded(String value, int maxLength) {
        if (value == null) {
            return null;
        }
        if (value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }
}
