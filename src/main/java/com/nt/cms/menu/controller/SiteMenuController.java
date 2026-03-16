package com.nt.cms.menu.controller;

import com.nt.cms.auth.security.CustomUserDetails;
import com.nt.cms.common.response.ApiResponse;
import com.nt.cms.menu.dto.SiteMenuCreateRequest;
import com.nt.cms.menu.dto.SiteMenuResponse;
import com.nt.cms.menu.dto.SiteMenuUpdateRequest;
import com.nt.cms.menu.service.SiteMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 사이트 메뉴 REST API 컨트롤러
 * 
 * @author CMS Team
 */
@Tag(name = "Site Menu", description = "사이트 메뉴 관리 API")
@RestController
@RequestMapping("/api/v1/menus")
@RequiredArgsConstructor
public class SiteMenuController {

    private final SiteMenuService siteMenuService;

    /**
     * 전체 메뉴 목록 조회 (flat)
     */
    @Operation(summary = "전체 메뉴 목록 조회", description = "모든 메뉴를 flat 형태로 조회합니다.")
    @GetMapping
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).MENU_READ.value)")
    public ApiResponse<List<SiteMenuResponse>> findAll() {
        return ApiResponse.success(siteMenuService.findAll());
    }

    /**
     * 계층형 메뉴 목록 조회
     */
    @Operation(summary = "계층형 메뉴 목록 조회", description = "메뉴를 계층 구조로 조회합니다.")
    @GetMapping("/hierarchy")
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).MENU_READ.value)")
    public ApiResponse<List<SiteMenuResponse>> findHierarchy() {
        return ApiResponse.success(siteMenuService.findHierarchy());
    }

    /**
     * 최상위 메뉴 목록 조회
     */
    @Operation(summary = "최상위 메뉴 목록 조회", description = "최상위 메뉴만 조회합니다.")
    @GetMapping("/root")
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).MENU_READ.value)")
    public ApiResponse<List<SiteMenuResponse>> findRootMenus() {
        return ApiResponse.success(siteMenuService.findRootMenus());
    }

    /**
     * 하위 메뉴 목록 조회
     */
    @Operation(summary = "하위 메뉴 목록 조회", description = "특정 메뉴의 하위 메뉴를 조회합니다.")
    @GetMapping("/{parentId}/children")
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).MENU_READ.value)")
    public ApiResponse<List<SiteMenuResponse>> findByParentId(@PathVariable Long parentId) {
        return ApiResponse.success(siteMenuService.findByParentId(parentId));
    }

    /**
     * 메뉴 상세 조회
     */
    @Operation(summary = "메뉴 상세 조회", description = "ID로 메뉴 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).MENU_READ.value)")
    public ApiResponse<SiteMenuResponse> findById(@PathVariable Long id) {
        return ApiResponse.success(siteMenuService.findById(id));
    }

    /**
     * 메뉴 코드 중복 체크
     * @param menuCode 메뉴 코드
     * @param excludeId 수정 시 제외할 ID (없으면 null)
     * @return true면 중복, false면 사용 가능
     */
    @Operation(summary = "메뉴 코드 중복 체크", description = "메뉴 코드의 중복 여부를 확인합니다. true면 중복입니다.")
    @GetMapping("/check/code")
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).MENU_READ.value)")
    public ApiResponse<Boolean> checkMenuCode(
            @RequestParam String menuCode,
            @RequestParam(required = false) Long excludeId) {
        boolean isDuplicated = siteMenuService.existsByMenuCode(menuCode, excludeId);
        return ApiResponse.success(isDuplicated);
    }

    /**
     * 메뉴 생성
     */
    @Operation(summary = "메뉴 생성", description = "새로운 메뉴를 생성합니다.")
    @PostMapping
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).MENU_CREATE.value)")
    public ApiResponse<SiteMenuResponse> create(
            @Valid @RequestBody SiteMenuCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.success(siteMenuService.create(request, userDetails.getUserId()));
    }

    /**
     * 메뉴 수정
     */
    @Operation(summary = "메뉴 수정", description = "기존 메뉴를 수정합니다.")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).MENU_UPDATE.value)")
    public ApiResponse<SiteMenuResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody SiteMenuUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.success(siteMenuService.update(id, request, userDetails.getUserId()));
    }

    /**
     * 메뉴 삭제
     */
    @Operation(summary = "메뉴 삭제", description = "메뉴를 삭제합니다. 하위 메뉴가 있으면 삭제할 수 없습니다.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).MENU_DELETE.value)")
    public ApiResponse<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        siteMenuService.delete(id, userDetails.getUserId());
        return ApiResponse.success(null);
    }

    /**
     * 메뉴 정렬 순서 변경
     */
    @Operation(summary = "메뉴 정렬 순서 변경", description = "메뉴의 정렬 순서를 변경합니다.")
    @PatchMapping("/{id}/sort-order")
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).MENU_UPDATE.value)")
    public ApiResponse<Void> updateSortOrder(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request) {
        siteMenuService.updateSortOrder(id, request.get("sortOrder"));
        return ApiResponse.success(null);
    }

    /**
     * 노출 가능한 메뉴 목록 조회 (공개 API)
     */
    @Operation(summary = "노출 가능한 메뉴 목록 조회", description = "현재 노출 가능한 메뉴를 계층 구조로 조회합니다.")
    @GetMapping("/public")
    public ApiResponse<List<SiteMenuResponse>> findVisibleMenus(
            @RequestParam(defaultValue = "false") boolean includeLoginRequired) {
        return ApiResponse.success(siteMenuService.findVisibleMenus(includeLoginRequired));
    }
}
