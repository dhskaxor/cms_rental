package com.nt.cms.menu.controller;

import com.nt.cms.auth.security.CustomUserDetails;
import com.nt.cms.common.response.ApiResponse;
import com.nt.cms.menu.dto.SitePageCreateRequest;
import com.nt.cms.menu.dto.SitePageResponse;
import com.nt.cms.menu.dto.SitePageUpdateRequest;
import com.nt.cms.menu.service.SitePageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 사이트 페이지 REST API 컨트롤러
 * 
 * @author CMS Team
 */
@Tag(name = "Site Page", description = "사이트 정적 페이지 관리 API")
@RestController
@RequestMapping("/api/v1/pages")
@RequiredArgsConstructor
public class SitePageController {

    private final SitePageService sitePageService;

    /**
     * 전체 페이지 목록 조회
     */
    @Operation(summary = "전체 페이지 목록 조회", description = "모든 페이지를 조회합니다.")
    @GetMapping
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).PAGE_READ.value)")
    public ApiResponse<List<SitePageResponse>> findAll() {
        return ApiResponse.success(sitePageService.findAll());
    }

    /**
     * 페이지 상세 조회
     */
    @Operation(summary = "페이지 상세 조회", description = "ID로 페이지 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).PAGE_READ.value)")
    public ApiResponse<SitePageResponse> findById(@PathVariable Long id) {
        return ApiResponse.success(sitePageService.findById(id));
    }

    /**
     * 사용 가능한 페이지 템플릿 코드 목록 조회
     * templates/site/page/ 하위 폴더명을 템플릿 코드로 사용
     */
    @Operation(summary = "페이지 템플릿 목록 조회", description = "templates/site/page/ 하위 폴더 목록을 반환합니다.")
    @GetMapping("/templates")
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).PAGE_READ.value)")
    public ApiResponse<List<String>> getTemplateCodes() {
        return ApiResponse.success(sitePageService.getAvailablePageTemplateCodes());
    }

    /**
     * 페이지 코드로 조회 (공개 API)
     */
    @Operation(summary = "페이지 코드로 조회", description = "페이지 코드로 게시된 페이지를 조회합니다.")
    @GetMapping("/public/{pageCode}")
    public ApiResponse<SitePageResponse> findByPageCode(@PathVariable String pageCode) {
        return ApiResponse.success(sitePageService.findByPageCode(pageCode));
    }

    /**
     * 페이지 코드 중복 체크
     * @param pageCode 페이지 코드
     * @param excludeId 수정 시 제외할 ID (없으면 null)
     * @return true면 중복, false면 사용 가능
     */
    @Operation(summary = "페이지 코드 중복 체크", description = "페이지 코드의 중복 여부를 확인합니다. true면 중복입니다.")
    @GetMapping("/check/code")
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).PAGE_READ.value)")
    public ApiResponse<Boolean> checkPageCode(
            @RequestParam String pageCode,
            @RequestParam(required = false) Long excludeId) {
        boolean isDuplicated = sitePageService.existsByPageCode(pageCode, excludeId);
        return ApiResponse.success(isDuplicated);
    }

    /**
     * 페이지 생성
     */
    @Operation(summary = "페이지 생성", description = "새로운 정적 페이지를 생성합니다.")
    @PostMapping
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).PAGE_CREATE.value)")
    public ApiResponse<SitePageResponse> create(
            @Valid @RequestBody SitePageCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.success(sitePageService.create(request, userDetails.getUserId()));
    }

    /**
     * 페이지 수정
     */
    @Operation(summary = "페이지 수정", description = "기존 정적 페이지를 수정합니다.")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).PAGE_UPDATE.value)")
    public ApiResponse<SitePageResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody SitePageUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ApiResponse.success(sitePageService.update(id, request, userDetails.getUserId()));
    }

    /**
     * 페이지 삭제
     */
    @Operation(summary = "페이지 삭제", description = "정적 페이지를 삭제합니다. 메뉴에서 사용 중이면 삭제할 수 없습니다.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).PAGE_DELETE.value)")
    public ApiResponse<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        sitePageService.delete(id, userDetails.getUserId());
        return ApiResponse.success(null);
    }
}
