package com.nt.cms.site.util;

import com.nt.cms.menu.dto.SiteMenuResponse;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 사용자 사이트 메뉴 유틸리티
 *
 * <p>Thymeleaf 템플릿에서 메뉴 href 해석에 사용한다. Spring Bean으로 등록되어 {@code @siteMenuUtil.resolveHref(menu)} 호출.</p>
 *
 * @author CMS Team
 */
@Component("siteMenuUtil")
public class SiteMenuUtil {

    /**
     * 메뉴의 링크 URL 계산
     *
     * @param menu 메뉴 응답
     * @return 해석된 URL
     */
    public String resolveHref(SiteMenuResponse menu) {
        if (menu == null) {
            return "#";
        }
        if ("LINK".equals(menu.getMenuType()) && menu.getLinkUrl() != null && !menu.getLinkUrl().isBlank()) {
            return menu.getLinkUrl();
        }
        if (menu.getUrlPath() != null && !menu.getUrlPath().isBlank()) {
            return menu.getUrlPath();
        }
        if ("PAGE".equals(menu.getMenuType()) && menu.getPageCode() != null) {
            return "/site/page/" + encode(menu.getPageCode());
        }
        if ("BOARD".equals(menu.getMenuType())) {
            if (menu.getBoardCode() != null && !menu.getBoardCode().isBlank()) {
                return "/site/board/" + encode(menu.getBoardCode());
            }
            if (menu.getBoardId() != null) {
                return "/site/board/" + menu.getBoardId();
            }
        }
        return "#";
    }

    private String encode(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }
}
