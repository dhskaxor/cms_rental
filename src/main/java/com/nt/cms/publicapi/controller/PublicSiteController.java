package com.nt.cms.publicapi.controller;

import com.nt.cms.auth.dto.SessionUser;
import com.nt.cms.common.constant.SessionConstants;
import com.nt.cms.common.response.ApiResponse;
import com.nt.cms.commoncode.dto.CommonCodeResponse;
import com.nt.cms.commoncode.service.CommonCodeService;
import com.nt.cms.menu.dto.SiteMenuResponse;
import com.nt.cms.menu.dto.SitePageResponse;
import com.nt.cms.menu.service.SiteMenuService;
import com.nt.cms.menu.service.SitePageService;
import com.nt.cms.popup.dto.PopupResponse;
import com.nt.cms.popup.service.PopupService;
import com.nt.cms.file.service.FileService;
import com.nt.cms.siteconfig.dto.SiteConfigResponse;
import com.nt.cms.siteconfig.service.SiteConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 사용자단 공개 사이트 API 컨트롤러
 *
 * <p>메뉴, 페이지, 팝업, 공통코드 등 사이트 기초 데이터를 인증 없이 조회한다.</p>
 * <p>기존 Service를 재사용하며, /api/v1/public/** 경로로 통합 제공한다.</p>
 *
 * @author CMS Team
 */
@Tag(name = "Public Site", description = "사용자단 공개 사이트 API (인증 불필요)")
@RestController
@RequestMapping("/api/v1/public")
@RequiredArgsConstructor
public class PublicSiteController {

    private final SiteMenuService siteMenuService;
    private final SitePageService sitePageService;
    private final PopupService popupService;
    private final CommonCodeService commonCodeService;
    private final ObjectProvider<SiteConfigService> siteConfigServiceProvider;
    private final ObjectProvider<FileService> fileServiceProvider;

    /**
     * 사이트 설정 조회 (공개)
     */
    @Operation(summary = "사이트 설정 조회", description = "사이트명, favicon, SEO, 회사정보를 조회합니다. 인증 불필요.")
    @GetMapping("/site/config")
    public ApiResponse<SiteConfigResponse> getSiteConfig() {
        SiteConfigService siteConfigService = siteConfigServiceProvider.getIfAvailable();
        return ApiResponse.success(siteConfigService != null ? siteConfigService.getForPublic() : null);
    }

    /**
     * 사용자 사이트 현재 로그인 사용자 조회
     * SITE_CURRENT_USER 세션이 있으면 반환, 없으면 null
     */
    @Operation(summary = "사이트 로그인 사용자 조회", description = "사용자 사이트 세션 사용자 정보를 반환합니다. 미로그인 시 null.")
    @GetMapping("/site/me")
    public ApiResponse<SessionUser> getSiteMe(HttpSession session) {
        SessionUser user = (SessionUser) session.getAttribute(SessionConstants.SITE_CURRENT_USER);
        return ApiResponse.success(user);
    }

    /**
     * 노출 가능한 메뉴 목록 조회
     *
     * @param includeLoginRequired true면 로그인 필수 메뉴 포함
     */
    @Operation(summary = "메뉴 목록 조회", description = "현재 노출 가능한 메뉴를 계층 구조로 조회합니다.")
    @GetMapping("/menus")
    public ApiResponse<List<SiteMenuResponse>> getMenus(
            @RequestParam(defaultValue = "false") boolean includeLoginRequired) {
        return ApiResponse.success(siteMenuService.findVisibleMenus(includeLoginRequired));
    }

    /**
     * 페이지 코드로 페이지 조회
     *
     * @param pageCode 페이지 코드
     */
    @Operation(summary = "페이지 조회", description = "페이지 코드로 게시된 페이지를 조회합니다.")
    @GetMapping("/pages/{pageCode}")
    public ApiResponse<SitePageResponse> getPage(@PathVariable String pageCode) {
        return ApiResponse.success(sitePageService.findByPageCode(pageCode));
    }

    /**
     * 노출 대상 팝업 목록 조회
     *
     * @param positionType 노출 위치 (예: MAIN)
     * @param deviceType 디바이스 타입 (예: PC, MOBILE)
     * @param isLogin 로그인 여부
     */
    @Operation(summary = "팝업 목록 조회", description = "현재 노출 대상 팝업을 조회합니다.")
    @GetMapping("/popups")
    public ApiResponse<List<PopupResponse>> getPopups(
            @RequestParam(defaultValue = "MAIN") String positionType,
            @RequestParam(defaultValue = "PC") String deviceType,
            @RequestParam(defaultValue = "false") boolean isLogin) {
        return ApiResponse.success(popupService.getDisplayPopups(positionType, deviceType, isLogin));
    }

    /**
     * 그룹 코드로 활성 공통코드 조회
     *
     * @param groupCode 그룹 코드
     */
    @Operation(summary = "공통코드 조회", description = "그룹 코드로 현재 유효한(활성+기간) 코드 목록을 조회합니다.")
    @GetMapping("/common-codes/{groupCode}")
    public ApiResponse<List<CommonCodeResponse>> getCommonCodes(@PathVariable String groupCode) {
        return ApiResponse.success(commonCodeService.getActiveCodesByGroupCode(groupCode));
    }

    /**
     * 사이트 favicon 서빙 (인증 불필요)
     */
    @Operation(summary = "Favicon", description = "설정된 favicon 파일을 반환합니다.")
    @GetMapping("/site/favicon")
    public ResponseEntity<byte[]> getFavicon() {
        SiteConfigService siteConfigService = siteConfigServiceProvider.getIfAvailable();
        FileService fileService = fileServiceProvider.getIfAvailable();
        if (siteConfigService == null || fileService == null) {
            return ResponseEntity.noContent().build();
        }
        var config = siteConfigService.getOrCreate();
        if (config.getFaviconFileId() == null) {
            return ResponseEntity.noContent().build();
        }
        var fileInfo = fileService.getFileInfo(config.getFaviconFileId());
        if (fileInfo == null || !"SITE_CONFIG".equals(fileInfo.getRefType())) {
            return ResponseEntity.notFound().build();
        }
        var result = fileService.downloadFile(config.getFaviconFileId());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(result.mimeType()));
        return ResponseEntity.ok().headers(headers).body(result.bytes());
    }
}
