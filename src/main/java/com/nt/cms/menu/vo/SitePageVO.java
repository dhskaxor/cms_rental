package com.nt.cms.menu.vo;

import com.nt.cms.common.vo.BaseVO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * 사이트 정적 페이지 VO
 * 
 * <p>사용자 사이트의 정적 페이지를 관리한다.</p>
 * <ul>
 *   <li>고유 코드 기반 페이지 관리</li>
 *   <li>HTML 콘텐츠</li>
 *   <li>게시 여부 설정</li>
 * </ul>
 * 
 * @author CMS Team
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SitePageVO extends BaseVO {

    /**
     * 페이지 고유 코드
     */
    private String pageCode;

    /**
     * 페이지 제목
     */
    private String pageTitle;

    /**
     * 페이지 HTML 내용
     */
    private String content;

    /**
     * 게시 여부
     */
    private Boolean isPublished;

    /**
     * 사용자 사이트 페이지 템플릿 코드 (templates/site/page/{code}/page.html)
     */
    private String templateCode;

    // ========== 조인 필드 ==========

    /**
     * 생성자 이름 (조인)
     */
    private String createdByName;

    /**
     * 수정자 이름 (조인)
     */
    private String updatedByName;
}
