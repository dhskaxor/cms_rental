package com.nt.cms.popup.controller;

import com.nt.cms.auth.security.CustomUserDetails;
import com.nt.cms.common.response.ApiResponse;
import com.nt.cms.common.response.PageResponse;
import com.nt.cms.popup.dto.PopupCreateRequest;
import com.nt.cms.popup.dto.PopupResponse;
import com.nt.cms.popup.dto.PopupSearchRequest;
import com.nt.cms.popup.dto.PopupUpdateRequest;
import com.nt.cms.popup.service.PopupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/popups")
@RequiredArgsConstructor
@Tag(name = "팝업", description = "팝업 관리 API")
public class PopupController {

    private final PopupService popupService;

    @GetMapping
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).POPUP_READ.value)")
    @Operation(summary = "팝업 목록 조회")
    public ApiResponse<PageResponse<PopupResponse>> getPopups(PopupSearchRequest request) {
        return ApiResponse.success(popupService.getPopups(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).POPUP_READ.value)")
    @Operation(summary = "팝업 상세 조회")
    public ApiResponse<PopupResponse> getPopup(@PathVariable Long id) {
        return ApiResponse.success(popupService.getPopup(id));
    }

    @GetMapping("/code/{popupCode}")
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).POPUP_READ.value)")
    @Operation(summary = "팝업 코드로 조회")
    public ApiResponse<PopupResponse> getPopupByCode(@PathVariable String popupCode) {
        return ApiResponse.success(popupService.getPopupByCode(popupCode));
    }

    @GetMapping("/display")
    @Operation(summary = "노출 대상 팝업 조회 (공개)")
    public ApiResponse<List<PopupResponse>> getDisplayPopups(
            @RequestParam(defaultValue = "MAIN") String positionType,
            @RequestParam(defaultValue = "PC") String deviceType,
            @RequestParam(defaultValue = "false") boolean isLogin) {
        return ApiResponse.success(popupService.getDisplayPopups(positionType, deviceType, isLogin));
    }

    @PostMapping
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).POPUP_CREATE.value)")
    @Operation(summary = "팝업 생성")
    public ApiResponse<PopupResponse> createPopup(
            @Valid @RequestBody PopupCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.success(popupService.createPopup(request, userDetails.getUserId()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).POPUP_UPDATE.value)")
    @Operation(summary = "팝업 수정")
    public ApiResponse<PopupResponse> updatePopup(
            @PathVariable Long id,
            @Valid @RequestBody PopupUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.success(popupService.updatePopup(id, request, userDetails.getUserId()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).POPUP_DELETE.value)")
    @Operation(summary = "팝업 삭제")
    public ApiResponse<Void> deletePopup(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        popupService.deletePopup(id, userDetails.getUserId());
        return ApiResponse.success();
    }

    @GetMapping("/check/code")
    @Operation(summary = "팝업 코드 중복 확인")
    public ApiResponse<Boolean> checkPopupCode(@RequestParam String popupCode) {
        return ApiResponse.success(popupService.isPopupCodeDuplicated(popupCode));
    }
}
