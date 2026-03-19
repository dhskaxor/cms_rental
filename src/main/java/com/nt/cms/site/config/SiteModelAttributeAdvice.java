package com.nt.cms.site.config;

import com.nt.cms.auth.dto.SessionUser;
import com.nt.cms.common.constant.SessionConstants;
import com.nt.cms.menu.dto.SiteMenuResponse;
import com.nt.cms.menu.service.SiteMenuService;
import com.nt.cms.popup.dto.PopupResponse;
import com.nt.cms.popup.service.PopupService;
import com.nt.cms.siteconfig.dto.SiteConfigResponse;
import com.nt.cms.siteconfig.service.SiteConfigService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 사용자 사이트 공통 모델 속성 주입
 *
 * <p>/site/** 경로 요청 시 siteUser, siteMenus를 Model에 자동 주입한다.</p>
 *
 * @author CMS Team
 */
@Slf4j
@ControllerAdvice(basePackages = "com.nt.cms.site.controller")
@RequiredArgsConstructor
public class SiteModelAttributeAdvice {

    private final ObjectProvider<SiteMenuService> siteMenuServiceProvider;
    private final ObjectProvider<PopupService> popupServiceProvider;
    private final ObjectProvider<SiteConfigService> siteConfigServiceProvider;

    /**
     * 사이트 설정 (헤더·푸터·SEO용)
     */
    @ModelAttribute("siteConfig")
    public SiteConfigResponse addSiteConfig(HttpServletRequest request) {
        if (!request.getRequestURI().startsWith("/site")) {
            return null;
        }
        SiteConfigService siteConfigService = siteConfigServiceProvider.getIfAvailable();
        return siteConfigService != null ? siteConfigService.getForPublic() : null;
    }

    /**
     * 사이트 로그인 사용자 (SITE_CURRENT_USER 세션)
     */
    @ModelAttribute("siteUser")
    public SessionUser addSiteUser(HttpServletRequest request) {
        if (!request.getRequestURI().startsWith("/site")) {
            return null;
        }
        HttpSession session = request.getSession(false);
        return session != null ? (SessionUser) session.getAttribute(SessionConstants.SITE_CURRENT_USER) : null;
    }

    /**
     * 사이트 메뉴 (헤더/네비게이션용, 로그인 필요 메뉴 포함)
     */
    @ModelAttribute("siteMenus")
    public List<SiteMenuResponse> addSiteMenus(HttpServletRequest request) {
        if (!request.getRequestURI().startsWith("/site")) {
            return List.of();
        }
        SiteMenuService siteMenuService = siteMenuServiceProvider.getIfAvailable();
        return siteMenuService != null ? siteMenuService.findVisibleMenus(true) : List.of();
    }

    /**
     * 사이트 팝업 (레이아웃용, MAIN/현재 디바이스/로그인 여부 기준)
     */
    @ModelAttribute("sitePopups")
    public List<PopupResponse> addSitePopups(HttpServletRequest request) {
        if (!request.getRequestURI().startsWith("/site")) {
            return List.of();
        }
        PopupService popupService = popupServiceProvider.getIfAvailable();
        if (popupService == null) {
            return List.of();
        }
        String deviceType = isMobileUserAgent(request) ? "MOBILE" : "PC";
        boolean isLogin = request.getSession(false) != null
                && request.getSession().getAttribute(SessionConstants.SITE_CURRENT_USER) != null;
        var popups = popupService.getDisplayPopups("MAIN", deviceType, isLogin);
        var now = LocalDateTime.now();
        var cookieHeader = request.getHeader("Cookie");
        return popups.stream()
                .filter(p -> p != null && Boolean.TRUE.equals(p.getIsActive()) && Boolean.TRUE.equals(p.getIsPublished()))
                .filter(p -> (p.getStartAt() == null || !p.getStartAt().isAfter(now)) && (p.getEndAt() == null || !p.getEndAt().isBefore(now)))
                .filter(p -> !Boolean.TRUE.equals(p.getIsTodayCloseEnabled()) || cookieHeader == null || !cookieHeader.contains("site_popup_close_" + p.getId() + "=1"))
                .toList();
    }

    private boolean isMobileUserAgent(HttpServletRequest request) {
        String ua = request.getHeader("User-Agent");
        return ua != null && (ua.toLowerCase().contains("mobile") || ua.contains("Android") || ua.contains("iPhone"));
    }
}
