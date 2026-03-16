package com.nt.cms.role.controller;

import com.nt.cms.auth.security.CustomUserDetails;
import com.nt.cms.common.response.ApiResponse;
import com.nt.cms.role.dto.*;
import com.nt.cms.role.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 역할 REST API 컨트롤러
 * 
 * @author CMS Team
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@Tag(name = "역할", description = "역할 관리 API")
public class RoleController {

    private final RoleService roleService;

    /**
     * 역할 목록 조회
     */
    @GetMapping
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).ROLE_READ.value)")
    @Operation(summary = "역할 목록 조회", description = "모든 역할 목록을 조회합니다.")
    public ApiResponse<List<RoleResponse>> getRoles() {
        List<RoleResponse> response = roleService.getRoles();
        return ApiResponse.success(response);
    }

    /**
     * 역할 상세 조회
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).ROLE_READ.value)")
    @Operation(summary = "역할 상세 조회", description = "역할 ID로 상세 정보를 조회합니다.")
    public ApiResponse<RoleResponse> getRole(@PathVariable Long id) {
        RoleResponse response = roleService.getRole(id);
        return ApiResponse.success(response);
    }

    /**
     * 역할 생성
     */
    @PostMapping
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).ROLE_CREATE.value)")
    @Operation(summary = "역할 생성", description = "새로운 역할을 생성합니다.")
    public ApiResponse<RoleResponse> createRole(
            @Valid @RequestBody RoleCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        RoleResponse response = roleService.createRole(request, userDetails.getUserId());
        return ApiResponse.success(response);
    }

    /**
     * 역할 수정
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).ROLE_UPDATE.value)")
    @Operation(summary = "역할 수정", description = "역할 정보를 수정합니다.")
    public ApiResponse<RoleResponse> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody RoleUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        RoleResponse response = roleService.updateRole(id, request, userDetails.getUserId());
        return ApiResponse.success(response);
    }

    /**
     * 역할 삭제
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).ROLE_DELETE.value)")
    @Operation(summary = "역할 삭제", description = "역할을 삭제합니다.")
    public ApiResponse<Void> deleteRole(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        roleService.deleteRole(id, userDetails.getUserId());
        return ApiResponse.success();
    }

    /**
     * 역할 코드 중복 확인
     */
    @GetMapping("/check/code")
    @Operation(summary = "역할 코드 중복 확인", description = "역할 코드 중복 여부를 확인합니다.")
    public ApiResponse<Boolean> checkRoleCode(@RequestParam String roleCode) {
        boolean isDuplicated = roleService.isRoleCodeDuplicated(roleCode);
        return ApiResponse.success(isDuplicated);
    }
}
