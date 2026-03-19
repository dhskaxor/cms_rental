package com.nt.cms.auth.security;

import com.nt.cms.auth.dto.SessionUser;
import com.nt.cms.common.constant.SessionConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 관리자 세션 기반 인증 필터
 *
 * <p>/api/v1/* 또는 /admin/** 경로에서 세션 로그인 시
 * JWT가 없으면 세션의 SessionUser를 기반으로 SecurityContext에 인증 정보를 설정한다.</p>
 * <p>/admin/** HTML 페이지에서 @PreAuthorize가 동작하려면 이 필터가 필요하다.</p>
 *
 * @author CMS Team
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminSessionAuthenticationFilter extends OncePerRequestFilter {

    private final ObjectProvider<CustomUserDetailsService> userDetailsServiceProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // 이미 인증된 경우 스킵 (JWT 등으로 인증됨)
            if (SecurityContextHolder.getContext().getAuthentication() != null
                    && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
                filterChain.doFilter(request, response);
                return;
            }

            // /api/v1/* 또는 /admin/** 경로에서만 세션 인증 시도
            String path = request.getRequestURI();
            if (!path.startsWith("/api/v1/") && !path.startsWith("/admin/")) {
                filterChain.doFilter(request, response);
                return;
            }

            HttpSession session = request.getSession(false);
            if (session == null) {
                filterChain.doFilter(request, response);
                return;
            }

            // Admin 세션 우선, 없으면 Site 세션 사용 (동시 로그인 지원)
            Object attr = session.getAttribute(SessionConstants.CURRENT_USER);
            if (attr == null) {
                attr = session.getAttribute(SessionConstants.SITE_CURRENT_USER);
            }
            if (!(attr instanceof SessionUser sessionUser)) {
                filterChain.doFilter(request, response);
                return;
            }

            if (!StringUtils.hasText(sessionUser.getUsername())) {
                filterChain.doFilter(request, response);
                return;
            }

            CustomUserDetailsService userDetailsService = userDetailsServiceProvider.getIfAvailable();
            if (userDetailsService == null) {
                filterChain.doFilter(request, response);
                return;
            }

            // 세션 사용자로부터 UserDetails 로드하여 SecurityContext에 설정
            CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserById(sessionUser.getId());
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.debug("세션 인증 성공 - 사용자: {}, URI: {}", sessionUser.getUsername(), path);
        } catch (Exception e) {
            log.warn("관리자 세션 인증 처리 중 오류: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return !path.startsWith("/api/v1/") && !path.startsWith("/admin/");
    }
}
