package com.nt.cms.audit.service;

import com.nt.cms.audit.dto.AuditLogResponse;
import com.nt.cms.audit.dto.AuditLogSearchRequest;
import com.nt.cms.common.response.PageResponse;

/**
 * 감사 로그 서비스 인터페이스
 * 
 * @author CMS Team
 */
public interface AuditLogService {

    /**
     * 감사 로그 기록 (Object 자동 JSON 직렬화 + 사용자명 자동 조회)
     *
     * <p>각 서비스에서 중복되는 recordAuditLog 메서드를 대체한다.</p>
     *
     * @param actorId 수행 사용자 ID (사용자명은 자동 조회)
     * @param action 액션 (CREATE, UPDATE, DELETE)
     * @param targetType 대상 타입 (USER, POST, BOARD 등)
     * @param targetId 대상 ID
     * @param before 변경 전 객체 (null 가능, 자동 JSON 직렬화)
     * @param after 변경 후 객체 (null 가능, 자동 JSON 직렬화)
     */
    void record(Long actorId, String action, String targetType, Long targetId, Object before, Object after);

    /**
     * 감사 로그 기록
     * 
     * @param userId 수행 사용자 ID
     * @param username 사용자명
     * @param action 액션 (CREATE, UPDATE, DELETE)
     * @param targetType 대상 타입 (USER, POST, BOARD 등)
     * @param targetId 대상 ID
     * @param beforeData 변경 전 데이터 (JSON, null 가능)
     * @param afterData 변경 후 데이터 (JSON, null 가능)
     */
    void log(Long userId, String username, String action, String targetType, Long targetId,
             String beforeData, String afterData);

    /**
     * 감사 로그 기록 (IP, User-Agent 포함)
     * 
     * @param userId 수행 사용자 ID
     * @param username 사용자명
     * @param action 액션
     * @param targetType 대상 타입
     * @param targetId 대상 ID
     * @param beforeData 변경 전 데이터
     * @param afterData 변경 후 데이터
     * @param ipAddress 클라이언트 IP
     * @param userAgent User Agent
     */
    void log(Long userId, String username, String action, String targetType, Long targetId,
             String beforeData, String afterData, String ipAddress, String userAgent);

    /**
     * 감사 로그 목록 조회 (페이징, 검색)
     */
    PageResponse<AuditLogResponse> getAuditLogs(AuditLogSearchRequest request);

    /**
     * 감사 로그 상세 조회
     */
    AuditLogResponse getAuditLog(Long id);
}
