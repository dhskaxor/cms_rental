package com.nt.cms.siteconfig.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사이트 설정 응답 DTO (공개 API / 사용자 사이트용)
 *
 * <p>favicon_url은 파일 다운로드 URL로 서버에서 조립.</p>
 *
 * @author CMS Team
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SiteConfigResponse {

    private Long id;
    private String siteName;
    /** favicon 파일 ID (관리자 폼용, 공개 API에서는 null) */
    private Long faviconFileId;
    private String faviconUrl;
    private String seoTitle;
    private String seoDescription;
    private String seoKeywords;
    private String companyAddress;
    private String companyPhone;
    private String adminEmail;
}
