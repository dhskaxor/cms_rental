package com.nt.cms.site.config;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

import static com.nt.cms.common.constant.SiteConstant.SITE_ERROR_VIEW;

/**
 * 사용자 사이트 에러 컨트롤러
 *
 * <p>Spring Boot 3에서 제거된 ErrorViewResolver 대체용.</p>
 * <p>/error 로 디스패치된 HTML 에러 요청을 모두 site/error 템플릿으로 렌더링한다.</p>
 * <p>statusCode, message, path 정보를 모델에 담아서 전달한다.</p>
 *
 * @author CMS Team
 */
@Controller
public class SiteErrorViewResolver implements ErrorController {

    private final org.springframework.boot.web.servlet.error.ErrorAttributes errorAttributes;

    public SiteErrorViewResolver(org.springframework.boot.web.servlet.error.ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    @RequestMapping(value = "/error", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView errorHtml(HttpServletRequest request) {
        return resolveSiteErrorView(request);
    }

    private ModelAndView resolveSiteErrorView(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (statusCode == null) {
            statusCode = 500;
        }
        String error = (String) request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        String message = (String) request.getAttribute("jakarta.servlet.error.message");
        String path = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);

        ModelAndView mav = new ModelAndView(SITE_ERROR_VIEW);
        mav.addObject("statusCode", statusCode);
        mav.addObject("error", error != null && !error.isEmpty() ? error : HttpStatus.valueOf(statusCode).getReasonPhrase());
        mav.addObject("message", message != null && !message.isEmpty() ? message : getDefaultMessage(statusCode));
        mav.addObject("path", path);
        mav.setStatus(HttpStatus.valueOf(statusCode));
        return mav;
    }

    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (statusCode != null) {
            try {
                return HttpStatus.valueOf(statusCode);
            } catch (Exception ignored) {
            }
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private String getDefaultMessage(Integer status) {
        if (status == null) return "서버 오류가 발생했습니다.";
        return switch (status) {
            case 404 -> "요청한 페이지를 찾을 수 없습니다.";
            case 403 -> "접근 권한이 없습니다.";
            case 500 -> "서버 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.";
            default -> "오류가 발생했습니다.";
        };
    }
}
