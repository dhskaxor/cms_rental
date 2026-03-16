package com.nt.cms.commoncode.controller;

import com.nt.cms.auth.security.CustomUserDetails;
import com.nt.cms.common.response.ApiResponse;
import com.nt.cms.commoncode.dto.CommonCodeGroupCreateRequest;
import com.nt.cms.commoncode.dto.CommonCodeGroupResponse;
import com.nt.cms.commoncode.dto.CommonCodeGroupUpdateRequest;
import com.nt.cms.commoncode.service.CommonCodeGroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/common-code-groups")
@RequiredArgsConstructor
@Tag(name = "공통 코드 그룹", description = "공통 코드 그룹 관리 API")
public class CommonCodeGroupController {

    private final CommonCodeGroupService groupService;

    @GetMapping
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).COMMON_CODE_READ.value)")
    @Operation(summary = "그룹 목록 조회")
    public ApiResponse<List<CommonCodeGroupResponse>> getGroups() {
        return ApiResponse.success(groupService.getGroups());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).COMMON_CODE_READ.value)")
    @Operation(summary = "그룹 상세 조회")
    public ApiResponse<CommonCodeGroupResponse> getGroup(@PathVariable Long id) {
        return ApiResponse.success(groupService.getGroup(id));
    }

    @GetMapping("/code/{groupCode}")
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).COMMON_CODE_READ.value)")
    @Operation(summary = "그룹 코드로 조회")
    public ApiResponse<CommonCodeGroupResponse> getGroupByCode(@PathVariable String groupCode) {
        return ApiResponse.success(groupService.getGroupByCode(groupCode));
    }

    @PostMapping
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).COMMON_CODE_CREATE.value)")
    @Operation(summary = "그룹 생성")
    public ApiResponse<CommonCodeGroupResponse> createGroup(
            @Valid @RequestBody CommonCodeGroupCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.success(groupService.createGroup(request, userDetails.getUserId()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).COMMON_CODE_UPDATE.value)")
    @Operation(summary = "그룹 수정")
    public ApiResponse<CommonCodeGroupResponse> updateGroup(
            @PathVariable Long id,
            @Valid @RequestBody CommonCodeGroupUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.success(groupService.updateGroup(id, request, userDetails.getUserId()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).COMMON_CODE_DELETE.value)")
    @Operation(summary = "그룹 삭제")
    public ApiResponse<Void> deleteGroup(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        groupService.deleteGroup(id, userDetails.getUserId());
        return ApiResponse.success();
    }

    @GetMapping("/check/code")
    @Operation(summary = "그룹 코드 중복 확인")
    public ApiResponse<Boolean> checkGroupCode(@RequestParam String groupCode) {
        return ApiResponse.success(groupService.isGroupCodeDuplicated(groupCode));
    }
}
