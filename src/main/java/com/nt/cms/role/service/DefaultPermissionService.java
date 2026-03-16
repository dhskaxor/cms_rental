package com.nt.cms.role.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nt.cms.audit.service.AuditLogService;
import com.nt.cms.common.exception.BusinessException;
import com.nt.cms.common.exception.ErrorCode;
import com.nt.cms.role.dto.*;
import com.nt.cms.role.mapper.PermissionMapper;
import com.nt.cms.role.vo.PermissionVO;
import com.nt.cms.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 권한 서비스 구현체
 * 
 * @author CMS Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefaultPermissionService implements PermissionService {

    private final PermissionMapper permissionMapper;
    private final AuditLogService auditLogService;
    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;

    @Override
    public List<PermissionResponse> getPermissions() {
        log.debug("권한 목록 조회");

        List<PermissionVO> permissions = permissionMapper.findAll();
        return permissions.stream()
                .map(PermissionResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public PermissionResponse getPermission(Long id) {
        log.debug("권한 상세 조회: {}", id);

        PermissionVO permission = permissionMapper.findById(id);
        if (permission == null) {
            throw new BusinessException(ErrorCode.PERMISSION_NOT_FOUND);
        }

        return PermissionResponse.from(permission);
    }

    @Override
    public List<PermissionResponse> getPermissionsByRoleId(Long roleId) {
        log.debug("역할별 권한 목록 조회: roleId={}", roleId);

        List<PermissionVO> permissions = permissionMapper.findByRoleId(roleId);
        return permissions.stream()
                .map(PermissionResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PermissionResponse createPermission(PermissionCreateRequest request, Long createdBy) {
        log.info("권한 생성: {}", request.getPermissionCode());

        // 중복 확인
        if (permissionMapper.existsByPermissionCode(request.getPermissionCode())) {
            throw new BusinessException(ErrorCode.PERMISSION_ALREADY_EXISTS);
        }

        // 권한 생성
        PermissionVO permission = PermissionVO.builder()
                .permissionCode(request.getPermissionCode())
                .permissionName(request.getPermissionName())
                .description(request.getDescription())
                .createdBy(createdBy)
                .build();

        permissionMapper.insert(permission);
        log.info("권한 생성 완료: id={}", permission.getId());

        recordAuditLog("CREATE", "PERMISSION", permission.getId(), null, permission, createdBy);

        return getPermission(permission.getId());
    }

    @Override
    @Transactional
    public PermissionResponse updatePermission(Long id, PermissionUpdateRequest request, Long updatedBy) {
        log.info("권한 수정: id={}", id);

        // 권한 존재 확인
        PermissionVO existingPermission = permissionMapper.findById(id);
        if (existingPermission == null) {
            throw new BusinessException(ErrorCode.PERMISSION_NOT_FOUND);
        }

        // 권한 수정
        PermissionVO permission = PermissionVO.builder()
                .id(id)
                .permissionName(request.getPermissionName())
                .description(request.getDescription())
                .updatedBy(updatedBy)
                .build();

        permissionMapper.update(permission);
        log.info("권한 수정 완료: id={}", id);

        PermissionVO afterPermission = permissionMapper.findById(id);
        recordAuditLog("UPDATE", "PERMISSION", id, existingPermission, afterPermission, updatedBy);

        return getPermission(id);
    }

    @Override
    @Transactional
    public void deletePermission(Long id, Long deletedBy) {
        log.info("권한 삭제: id={}", id);

        // 권한 존재 확인
        PermissionVO permission = permissionMapper.findById(id);
        if (permission == null) {
            throw new BusinessException(ErrorCode.PERMISSION_NOT_FOUND);
        }

        // 역할에 매핑되어 있는지 확인
        long mappingCount = permissionMapper.countRoleMappings(id);
        if (mappingCount > 0) {
            throw new BusinessException(ErrorCode.PERMISSION_IN_USE);
        }

        // 권한 삭제
        permissionMapper.delete(id, deletedBy);
        log.info("권한 삭제 완료: id={}", id);

        recordAuditLog("DELETE", "PERMISSION", id, permission, null, deletedBy);
    }

    /** 감사 로그 기록 */
    private void recordAuditLog(String action, String targetType, Long targetId,
                               PermissionVO before, PermissionVO after, Long actorId) {
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
    public boolean isPermissionCodeDuplicated(String permissionCode) {
        return permissionMapper.existsByPermissionCode(permissionCode);
    }
}
