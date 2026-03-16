package com.nt.cms.role.controller;

import com.nt.cms.auth.security.CustomUserDetails;
import com.nt.cms.common.response.ApiResponse;
import com.nt.cms.role.dto.*;
import com.nt.cms.role.service.PermissionService;
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
 * 권한 REST API 컨트롤러
 * 
 * @author CMS Team
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
@Tag(name = "권한", description = "권한 관리 API")
public class PermissionController {

    private final PermissionService permissionService;

    /**
     * 권한 목록 조회
     */
    @GetMapping
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).ROLE_READ.value)")
    @Operation(summary = "권한 목록 조회", description = "모든 권한 목록을 조회합니다.")
    public ApiResponse<List<PermissionResponse>> getPermissions() {
        List<PermissionResponse> response = permissionService.getPermissions();
        return ApiResponse.success(response);
    }

    /**
     * 권한 상세 조회
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).ROLE_READ.value)")
    @Operation(summary = "권한 상세 조회", description = "권한 ID로 상세 정보를 조회합니다.")
    public ApiResponse<PermissionResponse> getPermission(@PathVariable Long id) {
        PermissionResponse response = permissionService.getPermission(id);
        return ApiResponse.success(response);
    }

    /**
     * 역할별 권한 목록 조회
     */
    @GetMapping("/role/{roleId}")
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).ROLE_READ.value)")
    @Operation(summary = "역할별 권한 목록", description = "역할에 부여된 권한 목록을 조회합니다.")
    public ApiResponse<List<PermissionResponse>> getPermissionsByRoleId(@PathVariable Long roleId) {
        List<PermissionResponse> response = permissionService.getPermissionsByRoleId(roleId);
        return ApiResponse.success(response);
    }

    /**
     * 권한 생성
     */
    @PostMapping
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).ROLE_CREATE.value)")
    @Operation(summary = "권한 생성", description = "새로운 권한을 생성합니다.")
    public ApiResponse<PermissionResponse> createPermission(
            @Valid @RequestBody PermissionCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        PermissionResponse response = permissionService.createPermission(request, userDetails.getUserId());
        return ApiResponse.success(response);
    }

    /**
     * 권한 수정
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).ROLE_UPDATE.value)")
    @Operation(summary = "권한 수정", description = "권한 정보를 수정합니다.")
    public ApiResponse<PermissionResponse> updatePermission(
            @PathVariable Long id,
            @Valid @RequestBody PermissionUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        PermissionResponse response = permissionService.updatePermission(id, request, userDetails.getUserId());
        return ApiResponse.success(response);
    }

    /**
     * 권한 삭제
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).ROLE_DELETE.value)")
    @Operation(summary = "권한 삭제", description = "권한을 삭제합니다.")
    public ApiResponse<Void> deletePermission(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        permissionService.deletePermission(id, userDetails.getUserId());
        return ApiResponse.success();
    }

    /**
     * 권한 코드 중복 확인
     */
    @GetMapping("/check/code")
    @Operation(summary = "권한 코드 중복 확인", description = "권한 코드 중복 여부를 확인합니다.")
    public ApiResponse<Boolean> checkPermissionCode(@RequestParam String permissionCode) {
        boolean isDuplicated = permissionService.isPermissionCodeDuplicated(permissionCode);
        return ApiResponse.success(isDuplicated);
    }
}
