package com.nt.cms.siteconfig.vo;

import com.nt.cms.common.vo.BaseVO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * 사이트 설정 VO
 *
 * <p>사이트명, favicon, SEO, 회사정보 등 사용자 사이트 공통 설정. 단일 행(id=1)만 사용.</p>
 *
 * @author CMS Team
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SiteConfigVO extends BaseVO {

    /**
     * 사이트명
     */
    private String siteName;

    /**
     * favicon 파일 ID (file 테이블 FK)
     */
    private Long faviconFileId;

    /**
     * SEO 기본 제목
     */
    private String seoTitle;

    /**
     * SEO 기본 설명
     */
    private String seoDescription;

    /**
     * SEO 기본 키워드
     */
    private String seoKeywords;

    /**
     * 회사 주소
     */
    private String companyAddress;

    /**
     * 회사 전화번호
     */
    private String companyPhone;

    /**
     * 관리자 이메일
     */
    private String adminEmail;
}
