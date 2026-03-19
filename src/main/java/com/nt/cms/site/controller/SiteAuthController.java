package com.nt.cms.site.controller;

import com.nt.cms.auth.dto.SessionUser;
import com.nt.cms.auth.security.CustomUserDetails;
import com.nt.cms.auth.security.CustomUserDetailsService;
import com.nt.cms.common.constant.SessionConstants;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 사용자 사이트 인증 컨트롤러
 *
 * <p>/site/auth 하위에서 로그인/로그아웃을 처리한다.</p>
 * <p>Admin 세션과 분리하여 SITE_CURRENT_USER 세션을 사용한다.</p>
 *
 * @author CMS Team
 */
@Slf4j
@Controller
@RequestMapping("/site/auth")
@RequiredArgsConstructor
public class SiteAuthController {

    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    /**
     * 사용자 사이트 로그인 페이지
     *
     * @param error  로그인 실패 시 쿼리 파라미터
     * @param logout 로그아웃 후 쿼리 파라미터
     * @param session HTTP 세션
     * @param model Model
     * @return 로그인 템플릿 또는 /site/ 리다이렉트
     */
    @GetMapping("/login")
    public String loginPage(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            @RequestParam(value = "redirect", required = false) String redirect,
            HttpSession session,
            Model model) {

        // 이미 사이트 로그인된 경우 redirect 또는 메인으로 리다이렉트
        if (session.getAttribute(SessionConstants.SITE_CURRENT_USER) != null) {
            return isValidRedirect(redirect) ? "redirect:" + redirect : "redirect:/site/";
        }

        if (error != null) {
            model.addAttribute("error", "아이디 또는 비밀번호가 올바르지 않습니다.");
        }
        if (logout != null) {
            model.addAttribute("logout", true);
        }
        model.addAttribute("redirect", (redirect != null && !redirect.isEmpty()) ? redirect : null);

        return "site/auth/login";
    }

    /**
     * 사용자 사이트 회원가입 페이지
     */
    @GetMapping("/register")
    public String registerPage(
            @RequestParam(value = "redirect", required = false) String redirect,
            HttpSession session,
            Model model) {
        if (session.getAttribute(SessionConstants.SITE_CURRENT_USER) != null) {
            return isValidRedirect(redirect) ? "redirect:" + redirect : "redirect:/site/";
        }
        model.addAttribute("redirect", (redirect != null && !redirect.isEmpty()) ? redirect : "/site/");
        return "site/auth/register";
    }

    /**
     * redirect URL이 /site/ 내부인지 검증 (오픈 리다이렉트 방지)
     */
    private boolean isValidRedirect(String redirect) {
        if (redirect == null || redirect.isEmpty()) {
            return false;
        }
        return redirect.startsWith("/site/") && !redirect.startsWith("//");
    }

    /**
     * 사용자 사이트 로그인 처리
     *
     * @param username 아이디
     * @param password 비밀번호
     * @param session HTTP 세션
     * @param redirectAttributes RedirectAttributes
     * @return /site/ 리다이렉트 또는 로그인 페이지 리다이렉트
     */
    @PostMapping("/login")
    public String login(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam(value = "redirect", required = false) String redirect,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        log.info("사용자 사이트 로그인 시도: {}", username);

        String loginPath = "/site/auth/login";
        if (redirect != null && !redirect.isEmpty()) {
            redirectAttributes.addAttribute("redirect", redirect);
        }

        try {
            CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(username);

            if (!userDetails.isAccountNonLocked()) {
                log.warn("사이트 로그인 실패 - 계정 잠금: {}", username);
                redirectAttributes.addFlashAttribute("error", "계정이 잠겨있습니다. 관리자에게 문의하세요.");
                return "redirect:" + loginPath;
            }

            if (!passwordEncoder.matches(password, userDetails.getPassword())) {
                log.warn("사이트 로그인 실패 - 비밀번호 불일치: {}", username);
                redirectAttributes.addFlashAttribute("error", "아이디 또는 비밀번호가 올바르지 않습니다.");
                return "redirect:" + loginPath;
            }

            SessionUser sessionUser = SessionUser.builder()
                    .id(userDetails.getUserId())
                    .username(userDetails.getUsername())
                    .name(userDetails.getName())
                    .email(userDetails.getEmail())
                    .roleCode(userDetails.getRoleCode())
                    .build();

            session.setAttribute(SessionConstants.SITE_CURRENT_USER, sessionUser);
            session.setMaxInactiveInterval(3600); // 1시간

            // Spring Security 인증 주체도 세션에 연동 (@AuthenticationPrincipal 사용)
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()));
            SecurityContextHolder.setContext(context);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

            log.info("사용자 사이트 로그인 성공: {}", username);
            return isValidRedirect(redirect) ? "redirect:" + redirect : "redirect:/site/";

        } catch (Exception e) {
            log.error("사이트 로그인 처리 중 오류: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "아이디 또는 비밀번호가 올바르지 않습니다.");
            return "redirect:" + loginPath;
        }
    }

    /**
     * 사용자 사이트 로그아웃 처리
     *
     * @param session HTTP 세션
     * @param redirectAttributes RedirectAttributes
     * @return /site/auth/login 리다이렉트
     */
    @PostMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        SessionUser user = (SessionUser) session.getAttribute(SessionConstants.SITE_CURRENT_USER);
        if (user != null) {
            log.info("사용자 사이트 로그아웃: {}", user.getUsername());
        }

        session.removeAttribute(SessionConstants.SITE_CURRENT_USER);
        session.removeAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
        SecurityContextHolder.clearContext();
        redirectAttributes.addFlashAttribute("logout", true);
        return "redirect:/site/auth/login";
    }
}
