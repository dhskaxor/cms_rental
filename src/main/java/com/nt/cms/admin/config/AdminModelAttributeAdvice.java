package com.nt.cms.admin.config;

import com.nt.cms.common.constant.SessionConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * 관리자 페이지 공통 모델 속성 주입
 *
 * <p>/admin/** 경로 요청 시 currentUser를 Model에 자동 주입한다.</p>
 *
 * @author CMS Team
 */
@Slf4j
@ControllerAdvice(basePackages = "com.nt.cms.admin.controller")
public class AdminModelAttributeAdvice {

    @ModelAttribute("currentUser")
    public Object addCurrentUser(HttpServletRequest request) {
        if (!request.getRequestURI().startsWith("/admin")) {
            return null;
        }
        HttpSession session = request.getSession(false);
        return session != null ? session.getAttribute(SessionConstants.CURRENT_USER) : null;
    }
}
