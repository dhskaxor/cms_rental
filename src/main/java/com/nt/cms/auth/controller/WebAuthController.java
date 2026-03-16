package com.nt.cms.auth.controller;

import com.nt.cms.auth.dto.SessionUser;
import com.nt.cms.auth.security.CustomUserDetails;
import com.nt.cms.auth.security.CustomUserDetailsService;
import com.nt.cms.common.constant.SessionConstants;
import com.nt.cms.common.exception.BusinessException;
import com.nt.cms.common.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 웹 기반 인증 컨트롤러
 * 관리자 페이지 로그인/로그아웃 처리
 * 
 * @author CMS Team
 */
@Slf4j
@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class WebAuthController {

    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    /**
     * 로그인 페이지
     */
    @GetMapping("/login")
    public String loginPage(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            HttpSession session,
            Model model) {
        
        // 이미 로그인된 경우 대시보드로 리다이렉트
        if (session.getAttribute(SessionConstants.CURRENT_USER) != null) {
            return "redirect:/admin";
        }

        if (error != null) {
            model.addAttribute("error", "아이디 또는 비밀번호가 올바르지 않습니다.");
        }
        if (logout != null) {
            model.addAttribute("logout", true);
        }

        return "auth/login";
    }

    /**
     * 로그인 처리
     */
    @PostMapping("/login")
    public String login(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        
        log.info("웹 로그인 시도: {}", username);

        try {
            // 사용자 조회
            CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(username);

            // 계정 상태 확인
            if (!userDetails.isAccountNonLocked()) {
                log.warn("로그인 실패 - 계정 잠금: {}", username);
                redirectAttributes.addFlashAttribute("error", "계정이 잠겨있습니다. 관리자에게 문의하세요.");
                return "redirect:/auth/login";
            }

            // 비밀번호 검증
            if (!passwordEncoder.matches(password, userDetails.getPassword())) {
                log.warn("로그인 실패 - 비밀번호 불일치: {}", username);
                redirectAttributes.addFlashAttribute("error", "아이디 또는 비밀번호가 올바르지 않습니다.");
                return "redirect:/auth/login";
            }

            // 세션에 사용자 정보 저장
            SessionUser sessionUser = SessionUser.builder()
                    .id(userDetails.getUserId())
                    .username(userDetails.getUsername())
                    .name(userDetails.getName())
                    .email(userDetails.getEmail())
                    .roleCode(userDetails.getRoleCode())
                    .build();
            
            session.setAttribute(SessionConstants.CURRENT_USER, sessionUser);
            session.setMaxInactiveInterval(3600); // 1시간

            log.info("웹 로그인 성공: {}", username);
            return "redirect:/admin";

        } catch (Exception e) {
            log.error("로그인 처리 중 오류: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "아이디 또는 비밀번호가 올바르지 않습니다.");
            return "redirect:/auth/login";
        }
    }

    /**
     * 로그아웃 처리
     */
    @PostMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        SessionUser user = (SessionUser) session.getAttribute(SessionConstants.CURRENT_USER);
        if (user != null) {
            log.info("웹 로그아웃: {}", user.getUsername());
        }
        
        session.invalidate();
        redirectAttributes.addFlashAttribute("logout", true);
        return "redirect:/auth/login";
    }

}
