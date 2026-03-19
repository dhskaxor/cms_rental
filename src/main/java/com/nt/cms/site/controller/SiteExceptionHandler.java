package com.nt.cms.site.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import static com.nt.cms.common.constant.SiteConstant.SITE_ERROR_VIEW;

/**
 * 사용자 사이트(/site/**) 전용 예외 처리
 *
 * <p>/site/ 경로에서 발생한 예외를 HTML 에러 페이지로 처리한다.</p>
 * <p>Accept: text/html 요청에만 적용되며, API(JSON) 요청은 GlobalExceptionHandler로 처리된다.</p>
 *
 * @author CMS Team
 */
@Slf4j
@ControllerAdvice(basePackages = "com.nt.cms.site.controller")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SiteExceptionHandler {

    /**
     * /site/ 경로 여부 및 HTML 요청 여부 확인
     */
    private boolean isSiteHtmlRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (uri == null || (!uri.startsWith("/site/") && !uri.equals("/site"))) {
            return false;
        }
        String accept = request.getHeader("Accept");
        return accept != null && accept.contains("text/html");
    }

    /**
     * 404 Not Found - /site/** 경로 HTML 요청 시
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ModelAndView handleNoHandlerFound(NoHandlerFoundException e, HttpServletRequest request) throws NoHandlerFoundException {
        if (!isSiteHtmlRequest(request)) {
            throw e;
        }
        log.warn("사이트 404: {} {}", e.getHttpMethod(), e.getRequestURL());
        return createErrorView(404, "요청한 페이지를 찾을 수 없습니다.", request.getRequestURI());
    }

    /**
     * 기타 예외 - /site/** 경로 HTML 요청 시
     */
    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception e, HttpServletRequest request) throws Exception {
        if (!isSiteHtmlRequest(request)) {
            throw e;
        }
        log.error("사이트 예외: {}", request.getRequestURI(), e);
        return createErrorView(500, "서버 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.", request.getRequestURI());
    }

    private ModelAndView createErrorView(int statusCode, String message, String path) {
        ModelAndView mav = new ModelAndView(SITE_ERROR_VIEW);
        mav.addObject("statusCode", statusCode);
        mav.addObject("error", HttpStatus.valueOf(statusCode).getReasonPhrase());
        mav.addObject("message", message);
        mav.addObject("path", path);
        mav.setStatus(HttpStatus.valueOf(statusCode));
        return mav;
    }
}
