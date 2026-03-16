package com.nt.cms.admin.interceptor;

import com.nt.cms.common.constant.SessionConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 관리자 페이지 세션 검증 인터셉터
 *
 * <p>/admin/** 경로 접근 시 로그인 여부를 확인하고,
 * 미로그인 시 /auth/login으로 리다이렉트한다.</p>
 *
 * @author CMS Team
 */
@Slf4j
@Component
public class AdminSessionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(SessionConstants.CURRENT_USER) == null) {
            response.sendRedirect("/auth/login");
            return false;
        }
        return true;
    }
}
