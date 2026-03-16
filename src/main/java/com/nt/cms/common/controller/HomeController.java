package com.nt.cms.common.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 홈 컨트롤러
 * 루트 경로 처리
 * 
 * @author CMS Team
 */
@Controller
public class HomeController {

    /**
     * 루트 경로 접근 시 사용자 사이트(/site)로 리다이렉트
     */
    @GetMapping("/")
    public String home() {
        return "redirect:/site/";
    }
}
