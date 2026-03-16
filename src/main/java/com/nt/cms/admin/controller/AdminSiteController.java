package com.nt.cms.admin.controller;

import com.nt.cms.auth.security.CustomUserDetails;
import com.nt.cms.common.constant.Permission;
import com.nt.cms.siteconfig.dto.SiteConfigResponse;
import com.nt.cms.siteconfig.dto.SiteConfigUpdateRequest;
import com.nt.cms.siteconfig.service.SiteConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 관리자 사이트 설정 컨트롤러
 *
 * @author CMS Team
 */
@Controller
@RequestMapping("/admin/site")
@RequiredArgsConstructor
public class AdminSiteController {

    private final SiteConfigService siteConfigService;

    /**
     * 사이트 설정 폼
     */
    @GetMapping
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).SITE_READ.value)")
    public String form(Model model) {
        SiteConfigResponse config = siteConfigService.getForPublic();
        model.addAttribute("currentMenu", "site");
        model.addAttribute("pageTitle", "사이트 관리");
        model.addAttribute("siteConfig", config);
        return "admin/site/form";
    }

    /**
     * 사이트 설정 저장
     */
    @PostMapping
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).SITE_UPDATE.value)")
    public String save(
            @ModelAttribute SiteConfigUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails user,
            RedirectAttributes redirectAttributes) {
        Long userId = user != null ? user.getUserId() : null;
        siteConfigService.update(request, userId);
        redirectAttributes.addFlashAttribute("message", "사이트 설정이 저장되었습니다.");
        return "redirect:/admin/site";
    }
}
