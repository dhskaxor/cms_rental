package com.nt.cms.siteconfig.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 사이트 설정 수정 요청 DTO
 *
 * @author CMS Team
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SiteConfigUpdateRequest {

    @Size(max = 200)
    private String siteName;

    /**
     * favicon 파일 ID (업로드 후 file.id)
     */
    private Long faviconFileId;

    @Size(max = 200)
    private String seoTitle;

    @Size(max = 500)
    private String seoDescription;

    @Size(max = 500)
    private String seoKeywords;

    @Size(max = 500)
    private String companyAddress;

    @Size(max = 50)
    private String companyPhone;

    @Size(max = 200)
    private String adminEmail;

    @Size(max = 50)
    private String siteTheme;
}
