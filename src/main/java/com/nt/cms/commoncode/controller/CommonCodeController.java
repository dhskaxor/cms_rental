package com.nt.cms.commoncode.controller;

import com.nt.cms.auth.security.CustomUserDetails;
import com.nt.cms.common.response.ApiResponse;
import com.nt.cms.commoncode.dto.CommonCodeCreateRequest;
import com.nt.cms.commoncode.dto.CommonCodeResponse;
import com.nt.cms.commoncode.dto.CommonCodeUpdateRequest;
import com.nt.cms.commoncode.service.CommonCodeService;
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
 * 공통 코드 REST API 컨트롤러
 *
 * @author CMS Team
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/common-codes")
@RequiredArgsConstructor
@Tag(name = "공통 코드", description = "공통 코드 관리 API")
public class CommonCodeController {

    private final CommonCodeService codeService;

    /**
     * 그룹별 코드 목록 조회 (평면)
     */
    @GetMapping("/group/{groupId}")
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).COMMON_CODE_READ.value)")
    @Operation(summary = "그룹별 코드 목록 (평면)", description = "그룹 ID로 코드 목록을 평면 구조로 조회합니다.")
    public ApiResponse<List<CommonCodeResponse>> getCodesByGroupId(@PathVariable Long groupId) {
        List<CommonCodeResponse> response = codeService.getCodesByGroupId(groupId);
        return ApiResponse.success(response);
    }

    /**
     * 그룹별 코드 목록 조회 (계층)
     */
    @GetMapping("/group/{groupId}/hierarchy")
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).COMMON_CODE_READ.value)")
    @Operation(summary = "그룹별 코드 목록 (계층)", description = "그룹 ID로 코드 목록을 계층 구조로 조회합니다.")
    public ApiResponse<List<CommonCodeResponse>> getCodesByGroupIdHierarchy(@PathVariable Long groupId) {
        List<CommonCodeResponse> response = codeService.getCodesByGroupIdHierarchy(groupId);
        return ApiResponse.success(response);
    }

    /**
     * 그룹 코드로 활성 코드 조회 (공개 API, 기간 조건 적용)
     */
    @GetMapping("/active/{groupCode}")
    @Operation(summary = "활성 코드 조회", description = "그룹 코드로 현재 유효한(활성+기간) 코드 목록을 조회합니다. 인증 없이 사용 가능.")
    public ApiResponse<List<CommonCodeResponse>> getActiveCodesByGroupCode(@PathVariable String groupCode) {
        List<CommonCodeResponse> response = codeService.getActiveCodesByGroupCode(groupCode);
        return ApiResponse.success(response);
    }

    /**
     * 코드 상세 조회
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).COMMON_CODE_READ.value)")
    @Operation(summary = "코드 상세 조회", description = "코드 ID로 상세 정보를 조회합니다.")
    public ApiResponse<CommonCodeResponse> getCode(@PathVariable Long id) {
        CommonCodeResponse response = codeService.getCode(id);
        return ApiResponse.success(response);
    }

    /**
     * 코드 생성
     */
    @PostMapping
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).COMMON_CODE_CREATE.value)")
    @Operation(summary = "코드 생성", description = "새로운 공통 코드를 생성합니다.")
    public ApiResponse<CommonCodeResponse> createCode(
            @Valid @RequestBody CommonCodeCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        CommonCodeResponse response = codeService.createCode(request, userDetails.getUserId());
        return ApiResponse.success(response);
    }

    /**
     * 코드 수정
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).COMMON_CODE_UPDATE.value)")
    @Operation(summary = "코드 수정", description = "공통 코드 정보를 수정합니다.")
    public ApiResponse<CommonCodeResponse> updateCode(
            @PathVariable Long id,
            @Valid @RequestBody CommonCodeUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        CommonCodeResponse response = codeService.updateCode(id, request, userDetails.getUserId());
        return ApiResponse.success(response);
    }

    /**
     * 코드 삭제
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).COMMON_CODE_DELETE.value)")
    @Operation(summary = "코드 삭제", description = "공통 코드를 삭제합니다.")
    public ApiResponse<Void> deleteCode(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        codeService.deleteCode(id, userDetails.getUserId());
        return ApiResponse.success();
    }

    /**
     * 그룹 내 코드 중복 확인
     */
    @GetMapping("/check/duplicate")
    @Operation(summary = "코드 중복 확인", description = "같은 그룹 내 코드 중복 여부를 확인합니다.")
    public ApiResponse<Boolean> checkCodeDuplicate(
            @RequestParam Long groupId,
            @RequestParam String code,
            @RequestParam(required = false) Long excludeId) {
        boolean isDuplicated = codeService.isCodeDuplicated(groupId, code, excludeId);
        return ApiResponse.success(isDuplicated);
    }
}
