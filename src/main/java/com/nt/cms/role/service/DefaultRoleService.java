package com.nt.cms.role.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nt.cms.audit.service.AuditLogService;
import com.nt.cms.common.exception.BusinessException;
import com.nt.cms.common.exception.ErrorCode;
import com.nt.cms.role.dto.*;
import com.nt.cms.role.mapper.RoleMapper;
import com.nt.cms.role.vo.RoleVO;
import com.nt.cms.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 역할 서비스 구현체
 * 
 * @author CMS Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefaultRoleService implements RoleService {

    private final RoleMapper roleMapper;
    private final AuditLogService auditLogService;
    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;

    @Override
    public List<RoleResponse> getRoles() {
        log.debug("역할 목록 조회");

        List<RoleVO> roles = roleMapper.findAll();
        return roles.stream()
                .map(RoleResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public RoleResponse getRole(Long id) {
        log.debug("역할 상세 조회: {}", id);

        RoleVO role = roleMapper.findById(id);
        if (role == null) {
            throw new BusinessException(ErrorCode.ROLE_NOT_FOUND);
        }

        return RoleResponse.from(role);
    }

    @Override
    public RoleResponse getRoleByCode(String roleCode) {
        log.debug("역할 코드로 조회: {}", roleCode);

        RoleVO role = roleMapper.findByRoleCode(roleCode);
        if (role == null) {
            throw new BusinessException(ErrorCode.ROLE_NOT_FOUND);
        }

        return RoleResponse.from(role);
    }

    @Override
    @Transactional
    public RoleResponse createRole(RoleCreateRequest request, Long createdBy) {
        log.info("역할 생성: {}", request.getRoleCode());

        // 중복 확인
        if (roleMapper.existsByRoleCode(request.getRoleCode())) {
            throw new BusinessException(ErrorCode.ROLE_ALREADY_EXISTS);
        }

        // 역할 생성
        RoleVO role = RoleVO.builder()
                .roleCode(request.getRoleCode())
                .roleName(request.getRoleName())
                .description(request.getDescription())
                .createdBy(createdBy)
                .build();

        roleMapper.insert(role);
        log.info("역할 생성 완료: id={}", role.getId());

        recordAuditLog("CREATE", "ROLE", role.getId(), null, role, createdBy);

        // 권한 매핑
        if (request.getPermissionIds() != null && !request.getPermissionIds().isEmpty()) {
            for (Long permissionId : request.getPermissionIds()) {
                roleMapper.insertRolePermission(role.getId(), permissionId);
            }
            log.debug("역할-권한 매핑 완료: roleId={}, permissionIds={}", role.getId(), request.getPermissionIds());
        }

        return getRole(role.getId());
    }

    @Override
    @Transactional
    public RoleResponse updateRole(Long id, RoleUpdateRequest request, Long updatedBy) {
        log.info("역할 수정: id={}", id);

        // 역할 존재 확인
        RoleVO existingRole = roleMapper.findById(id);
        if (existingRole == null) {
            throw new BusinessException(ErrorCode.ROLE_NOT_FOUND);
        }

        // 역할 수정
        RoleVO role = RoleVO.builder()
                .id(id)
                .roleName(request.getRoleName())
                .description(request.getDescription())
                .updatedBy(updatedBy)
                .build();

        roleMapper.update(role);
        log.info("역할 수정 완료: id={}", id);

        RoleVO afterRole = roleMapper.findById(id);
        recordAuditLog("UPDATE", "ROLE", id, existingRole, afterRole, updatedBy);

        // 권한 매핑 업데이트 (기존 삭제 후 새로 추가)
        roleMapper.deleteRolePermissions(id);
        if (request.getPermissionIds() != null && !request.getPermissionIds().isEmpty()) {
            for (Long permissionId : request.getPermissionIds()) {
                roleMapper.insertRolePermission(id, permissionId);
            }
            log.debug("역할-권한 매핑 업데이트 완료: roleId={}, permissionIds={}", id, request.getPermissionIds());
        }

        return getRole(id);
    }

    @Override
    @Transactional
    public void deleteRole(Long id, Long deletedBy) {
        log.info("역할 삭제: id={}", id);

        // 역할 존재 확인
        RoleVO role = roleMapper.findById(id);
        if (role == null) {
            throw new BusinessException(ErrorCode.ROLE_NOT_FOUND);
        }

        // 사용 중인지 확인
        if (role.getUserCount() != null && role.getUserCount() > 0) {
            throw new BusinessException(ErrorCode.ROLE_IN_USE);
        }

        // 역할-권한 매핑 삭제
        roleMapper.deleteRolePermissions(id);

        // 역할 삭제
        roleMapper.delete(id, deletedBy);
        log.info("역할 삭제 완료: id={}", id);

        recordAuditLog("DELETE", "ROLE", id, role, null, deletedBy);
    }

    /** 감사 로그 기록 */
    private void recordAuditLog(String action, String targetType, Long targetId,
                               RoleVO before, RoleVO after, Long actorId) {
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
    public boolean isRoleCodeDuplicated(String roleCode) {
        return roleMapper.existsByRoleCode(roleCode);
    }
}
