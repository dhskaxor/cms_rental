package com.nt.cms.commoncode.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nt.cms.audit.service.AuditLogService;
import com.nt.cms.common.exception.BusinessException;
import com.nt.cms.common.exception.ErrorCode;
import com.nt.cms.commoncode.dto.CommonCodeGroupCreateRequest;
import com.nt.cms.commoncode.dto.CommonCodeGroupResponse;
import com.nt.cms.commoncode.dto.CommonCodeGroupUpdateRequest;
import com.nt.cms.commoncode.mapper.CommonCodeGroupMapper;
import com.nt.cms.commoncode.mapper.CommonCodeMapper;
import com.nt.cms.commoncode.vo.CommonCodeGroupVO;
import com.nt.cms.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 공통 코드 그룹 서비스 구현체
 *
 * @author CMS Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefaultCommonCodeGroupService implements CommonCodeGroupService {

    private final CommonCodeGroupMapper groupMapper;
    private final CommonCodeMapper codeMapper;
    private final AuditLogService auditLogService;
    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;

    @Override
    public List<CommonCodeGroupResponse> getGroups() {
        log.debug("공통 코드 그룹 목록 조회");
        List<CommonCodeGroupVO> list = groupMapper.findAll();
        return list.stream().map(CommonCodeGroupResponse::from).collect(Collectors.toList());
    }

    @Override
    public CommonCodeGroupResponse getGroup(Long id) {
        log.debug("공통 코드 그룹 상세 조회: {}", id);
        CommonCodeGroupVO vo = groupMapper.findById(id);
        if (vo == null) {
            throw new BusinessException(ErrorCode.COMMON_CODE_GROUP_NOT_FOUND);
        }
        return CommonCodeGroupResponse.from(vo);
    }

    @Override
    public CommonCodeGroupResponse getGroupByCode(String groupCode) {
        log.debug("공통 코드 그룹 조회: {}", groupCode);
        CommonCodeGroupVO vo = groupMapper.findByGroupCode(groupCode);
        if (vo == null) {
            throw new BusinessException(ErrorCode.COMMON_CODE_GROUP_NOT_FOUND);
        }
        return CommonCodeGroupResponse.from(vo);
    }

    @Override
    @Transactional
    public CommonCodeGroupResponse createGroup(CommonCodeGroupCreateRequest request, Long createdBy) {
        log.info("공통 코드 그룹 생성: {}", request.getGroupCode());

        if (groupMapper.existsByGroupCode(request.getGroupCode())) {
            throw new BusinessException(ErrorCode.COMMON_CODE_GROUP_ALREADY_EXISTS);
        }

        CommonCodeGroupVO vo = CommonCodeGroupVO.builder()
                .groupCode(request.getGroupCode())
                .groupName(request.getGroupName())
                .description(request.getDescription())
                .isSystem(request.getIsSystem() != null && request.getIsSystem())
                .isActive(request.getIsActive() == null || request.getIsActive())
                .createdBy(createdBy)
                .build();

        vo.onCreate(createdBy);
        groupMapper.insert(vo);
        log.info("공통 코드 그룹 생성 완료: id={}", vo.getId());

        recordAuditLog("CREATE", "COMMON_CODE_GROUP", vo.getId(), null, vo, createdBy);

        return getGroup(vo.getId());
    }

    @Override
    @Transactional
    public CommonCodeGroupResponse updateGroup(Long id, CommonCodeGroupUpdateRequest request, Long updatedBy) {
        log.info("공통 코드 그룹 수정: id={}", id);

        CommonCodeGroupVO existing = groupMapper.findById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.COMMON_CODE_GROUP_NOT_FOUND);
        }
        if (Boolean.TRUE.equals(existing.getIsSystem())) {
            throw new BusinessException(ErrorCode.COMMON_CODE_GROUP_SYSTEM_RESTRICTED);
        }

        CommonCodeGroupVO vo = CommonCodeGroupVO.builder()
                .id(id)
                .groupName(request.getGroupName())
                .description(request.getDescription())
                .isActive(request.getIsActive())
                .updatedBy(updatedBy)
                .build();

        vo.onUpdate(updatedBy);
        groupMapper.update(vo);
        log.info("공통 코드 그룹 수정 완료: id={}", id);

        CommonCodeGroupVO afterVo = groupMapper.findById(id);
        recordAuditLog("UPDATE", "COMMON_CODE_GROUP", id, existing, afterVo, updatedBy);

        return getGroup(id);
    }

    @Override
    @Transactional
    public void deleteGroup(Long id, Long deletedBy) {
        log.info("공통 코드 그룹 삭제: id={}", id);

        CommonCodeGroupVO existing = groupMapper.findById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.COMMON_CODE_GROUP_NOT_FOUND);
        }
        if (Boolean.TRUE.equals(existing.getIsSystem())) {
            throw new BusinessException(ErrorCode.COMMON_CODE_GROUP_SYSTEM_RESTRICTED);
        }

        int codeCount = groupMapper.countCodesByGroupId(id);
        if (codeCount > 0) {
            throw new BusinessException(ErrorCode.COMMON_CODE_GROUP_HAS_CODES);
        }

        groupMapper.delete(id, deletedBy);
        log.info("공통 코드 그룹 삭제 완료: id={}", id);

        recordAuditLog("DELETE", "COMMON_CODE_GROUP", id, existing, null, deletedBy);
    }

    /** 감사 로그 기록 */
    private void recordAuditLog(String action, String targetType, Long targetId,
                               CommonCodeGroupVO before, CommonCodeGroupVO after, Long actorId) {
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
    public boolean isGroupCodeDuplicated(String groupCode) {
        return groupMapper.existsByGroupCode(groupCode);
    }
}
