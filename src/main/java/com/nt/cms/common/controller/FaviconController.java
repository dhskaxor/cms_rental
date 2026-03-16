package com.nt.cms.common.controller;

import com.nt.cms.siteconfig.service.SiteConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * favicon.ico 요청 처리
 *
 * <p>브라우저가 자동으로 요청하는 favicon.ico에 대해
 * 사이트 설정에 favicon이 있으면 해당 파일로 리다이렉트하고,
 * 없으면 204 No Content를 반환한다.</p>
 *
 * @author CMS Team
 */
@Controller
@RequiredArgsConstructor
public class FaviconController {

    private final SiteConfigService siteConfigService;

    /**
     * favicon.ico 요청 시 설정된 favicon으로 리다이렉트 또는 204 No Content 반환
     */
    @GetMapping("favicon.ico")
    public ResponseEntity<Void> favicon() {
        var config = siteConfigService.getOrCreate();
        if (config.getFaviconFileId() != null) {
            return ResponseEntity.status(302)
                    .header("Location", "/api/v1/public/site/favicon")
                    .build();
        }
        return ResponseEntity.noContent().build();
    }
}
