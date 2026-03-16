package com.nt.cms.admin.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 관리자 페이지 컨트롤러
 * 
 * @author CMS Team
 */
@Slf4j
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    /**
     * 대시보드 페이지
     * 통계 데이터: 미구현 (Phase별 구현 예정)
     */
    @GetMapping({"", "/"})
    public String dashboard(Model model) {
        model.addAttribute("currentMenu", "dashboard");
        model.addAttribute("pageTitle", "대시보드");
        model.addAttribute("stats", null);

        return "admin/dashboard";
    }

    /**
     * 설정 페이지
     * 미구현 (Phase별 구현 예정)
     */
    @GetMapping("/settings")
    public String settings(Model model) {
        model.addAttribute("currentMenu", "settings");
        model.addAttribute("pageTitle", "설정");

        return "admin/settings";
    }
}
