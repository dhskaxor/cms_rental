package com.nt.cms.commoncode.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nt.cms.audit.service.AuditLogService;
import com.nt.cms.common.exception.BusinessException;
import com.nt.cms.common.exception.ErrorCode;
import com.nt.cms.commoncode.dto.CommonCodeCreateRequest;
import com.nt.cms.commoncode.dto.CommonCodeResponse;
import com.nt.cms.commoncode.dto.CommonCodeUpdateRequest;
import com.nt.cms.commoncode.mapper.CommonCodeGroupMapper;
import com.nt.cms.commoncode.mapper.CommonCodeMapper;
import com.nt.cms.commoncode.vo.CommonCodeGroupVO;
import com.nt.cms.commoncode.vo.CommonCodeVO;
import com.nt.cms.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 공통 코드 서비스 구현체
 *
 * @author CMS Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefaultCommonCodeService implements CommonCodeService {

    private final CommonCodeGroupMapper groupMapper;
    private final CommonCodeMapper codeMapper;
    private final AuditLogService auditLogService;
    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;

    @Override
    public List<CommonCodeResponse> getCodesByGroupId(Long groupId) {
        log.debug("공통 코드 목록 조회 (평면): groupId={}", groupId);

        if (groupMapper.findById(groupId) == null) {
            throw new BusinessException(ErrorCode.COMMON_CODE_GROUP_NOT_FOUND);
        }

        List<CommonCodeVO> list = codeMapper.findByGroupId(groupId);
        return list.stream().map(CommonCodeResponse::from).collect(Collectors.toList());
    }

    @Override
    public List<CommonCodeResponse> getCodesByGroupIdHierarchy(Long groupId) {
        log.debug("공통 코드 목록 조회 (계층): groupId={}", groupId);

        if (groupMapper.findById(groupId) == null) {
            throw new BusinessException(ErrorCode.COMMON_CODE_GROUP_NOT_FOUND);
        }

        List<CommonCodeVO> roots = codeMapper.findRootByGroupId(groupId);
        List<CommonCodeVO> withChildren = new ArrayList<>();
        for (CommonCodeVO root : roots) {
            withChildren.add(buildHierarchy(root));
        }
        return withChildren.stream().map(CommonCodeResponse::from).collect(Collectors.toList());
    }

    @Override
    public List<Long> findDescendantIds(Long codeId) {
        if (codeId == null) {
            return List.of();
        }
        Set<Long> result = new HashSet<>();
        collectDescendantIds(codeId, result);
        return new ArrayList<>(result);
    }

    /**
     * 재귀적으로 자손 ID 수집
     */
    private void collectDescendantIds(Long parentId, Set<Long> result) {
        List<CommonCodeVO> children = codeMapper.findChildrenByParentId(parentId);
        if (children == null || children.isEmpty()) return;
        for (CommonCodeVO child : children) {
            result.add(child.getId());
            collectDescendantIds(child.getId(), result);
        }
    }

    /**
     * 재귀적으로 하위 코드를 로드하여 계층 구조 생성
     */
    private CommonCodeVO buildHierarchy(CommonCodeVO parent) {
        List<CommonCodeVO> children = codeMapper.findChildrenByParentId(parent.getId());
        if (!children.isEmpty()) {
            List<CommonCodeVO> childrenWithDescendants = new ArrayList<>();
            for (CommonCodeVO child : children) {
                childrenWithDescendants.add(buildHierarchy(child));
            }
            parent.setChildren(childrenWithDescendants);
        }
        return parent;
    }

    @Override
    public List<CommonCodeResponse> getActiveCodesByGroupCode(String groupCode) {
        log.debug("활성 공통 코드 조회 (API): groupCode={}", groupCode);

        if (groupMapper.findByGroupCode(groupCode) == null) {
            throw new BusinessException(ErrorCode.COMMON_CODE_GROUP_NOT_FOUND);
        }

        List<CommonCodeVO> list = codeMapper.findActiveByGroupCode(groupCode);
        return list.stream().map(CommonCodeResponse::from).collect(Collectors.toList());
    }

    @Override
    public CommonCodeResponse getCode(Long id) {
        log.debug("공통 코드 상세 조회: {}", id);

        CommonCodeVO vo = codeMapper.findById(id);
        if (vo == null) {
            throw new BusinessException(ErrorCode.COMMON_CODE_NOT_FOUND);
        }
        return CommonCodeResponse.from(vo);
    }

    @Override
    @Transactional
    public CommonCodeResponse createCode(CommonCodeCreateRequest request, Long createdBy) {
        log.info("공통 코드 생성: groupId={}, code={}", request.getGroupId(), request.getCode());

        CommonCodeGroupVO group = groupMapper.findById(request.getGroupId());
        if (group == null) {
            throw new BusinessException(ErrorCode.COMMON_CODE_GROUP_NOT_FOUND);
        }

        if (codeMapper.existsByGroupIdAndCode(request.getGroupId(), request.getCode(), null)) {
            throw new BusinessException(ErrorCode.COMMON_CODE_ALREADY_EXISTS);
        }

        int depth = 1;
        if (request.getParentId() != null && request.getParentId() > 0) {
            CommonCodeVO parent = codeMapper.findById(request.getParentId());
            if (parent == null || !parent.getGroupId().equals(request.getGroupId())) {
                throw new BusinessException(ErrorCode.COMMON_CODE_NOT_FOUND);
            }
            depth = (parent.getDepth() != null ? parent.getDepth() : 1) + 1;
        }

        CommonCodeVO vo = CommonCodeVO.builder()
                .groupId(request.getGroupId())
                .parentId(request.getParentId() != null && request.getParentId() > 0 ? request.getParentId() : null)
                .depth(depth)
                .code(request.getCode())
                .codeName(request.getCodeName())
                .codeValue(request.getCodeValue())
                .sortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0)
                .isActive(request.getIsActive() == null || request.getIsActive())
                .isSystem(request.getIsSystem() != null && request.getIsSystem())
                .startAt(request.getStartAt())
                .endAt(request.getEndAt())
                .createdBy(createdBy)
                .build();

        vo.onCreate(createdBy);
        codeMapper.insert(vo);
        log.info("공통 코드 생성 완료: id={}", vo.getId());

        recordAuditLog("CREATE", "COMMON_CODE", vo.getId(), null, vo, createdBy);

        return getCode(vo.getId());
    }

    @Override
    @Transactional
    public CommonCodeResponse updateCode(Long id, CommonCodeUpdateRequest request, Long updatedBy) {
        log.info("공통 코드 수정: id={}", id);

        CommonCodeVO existing = codeMapper.findById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.COMMON_CODE_NOT_FOUND);
        }
        if (Boolean.TRUE.equals(existing.getIsSystem())) {
            throw new BusinessException(ErrorCode.COMMON_CODE_SYSTEM_RESTRICTED);
        }

        CommonCodeVO vo = CommonCodeVO.builder()
                .id(id)
                .codeName(request.getCodeName())
                .codeValue(request.getCodeValue())
                .sortOrder(request.getSortOrder())
                .isActive(request.getIsActive())
                .startAt(request.getStartAt())
                .endAt(request.getEndAt())
                .updatedBy(updatedBy)
                .build();

        vo.onUpdate(updatedBy);
        codeMapper.update(vo);
        log.info("공통 코드 수정 완료: id={}", id);

        CommonCodeVO afterVo = codeMapper.findById(id);
        recordAuditLog("UPDATE", "COMMON_CODE", id, existing, afterVo, updatedBy);

        return getCode(id);
    }

    @Override
    @Transactional
    public void deleteCode(Long id, Long deletedBy) {
        log.info("공통 코드 삭제: id={}", id);

        CommonCodeVO existing = codeMapper.findById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.COMMON_CODE_NOT_FOUND);
        }
        if (Boolean.TRUE.equals(existing.getIsSystem())) {
            throw new BusinessException(ErrorCode.COMMON_CODE_SYSTEM_RESTRICTED);
        }

        int childCount = codeMapper.countChildrenByParentId(id);
        if (childCount > 0) {
            throw new BusinessException(ErrorCode.COMMON_CODE_HAS_CHILDREN);
        }

        codeMapper.delete(id, deletedBy);
        log.info("공통 코드 삭제 완료: id={}", id);

        recordAuditLog("DELETE", "COMMON_CODE", id, existing, null, deletedBy);
    }

    /** 감사 로그 기록 */
    private void recordAuditLog(String action, String targetType, Long targetId,
                               CommonCodeVO before, CommonCodeVO after, Long actorId) {
        try {
            String username = userMapper.findUsernameById(actorId);
            String beforeJson = before != null ? objectMapper.writeValueAsString(before) : null;
            String afterJson = after != null ? objectMapper.writeValueAsString(after) : null;
            auditLogService.log(actorId, username, action, targetType, targetId, beforeJson, afterJson);
        } catch (JsonProcessingException e) {
            log.warn("감사 로그 JSON 직렬화 실패: {}", e.getMessage());
        }
    }

    @Override
    public boolean isCodeDuplicated(Long groupId, String code, Long excludeId) {
        return codeMapper.existsByGroupIdAndCode(groupId, code, excludeId);
    }
}
