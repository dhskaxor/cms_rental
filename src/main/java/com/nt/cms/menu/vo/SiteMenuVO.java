package com.nt.cms.menu.vo;

import com.nt.cms.common.vo.BaseVO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 사이트 메뉴 VO
 * 
 * <p>사용자 사이트의 메뉴 구조를 관리한다.</p>
 * <ul>
 *   <li>계층형 메뉴 구조 (parent_id, depth)</li>
 *   <li>메뉴 유형: PAGE, BOARD, LINK</li>
 *   <li>게시판/페이지 연결</li>
 *   <li>노출 제어 및 SEO 정보</li>
 * </ul>
 * 
 * @author CMS Team
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SiteMenuVO extends BaseVO {

    /**
     * 상위 메뉴 ID (NULL이면 최상위)
     */
    private Long parentId;

    /**
     * 메뉴 깊이 (1부터 시작)
     */
    private Integer depth;

    /**
     * 메뉴명
     */
    private String menuName;

    /**
     * 메뉴 고유 코드
     */
    private String menuCode;

    /**
     * 메뉴 유형 (PAGE, BOARD, LINK)
     */
    private String menuType;

    /**
     * 프론트 URL 경로 (/about, /board/free 등)
     */
    private String urlPath;

    /**
     * 외부 링크 URL
     */
    private String linkUrl;

    /**
     * 연결된 게시판 ID
     */
    private Long boardId;

    /**
     * 연결된 페이지 ID
     */
    private Long pageId;

    /**
     * 아이콘 클래스명
     */
    private String icon;

    /**
     * 링크 타겟 (_self, _blank)
     */
    private String target;

    /**
     * 정렬 순서
     */
    private Integer sortOrder;

    /**
     * 노출 여부
     */
    private Boolean isVisible;

    /**
     * 로그인 필요 여부
     */
    private Boolean isLoginRequired;

    /**
     * 노출 시작일
     */
    private LocalDateTime startAt;

    /**
     * 노출 종료일
     */
    private LocalDateTime endAt;

    /**
     * SEO 제목
     */
    private String seoTitle;

    /**
     * SEO 설명
     */
    private String seoDescription;

    // ========== 조인 필드 ==========

    /**
     * 상위 메뉴명 (조인)
     */
    private String parentMenuName;

    /**
     * 연결된 게시판명 (조인)
     */
    private String boardName;

    /**
     * 연결된 게시판 코드 (조인, board.board_code)
     */
    private String boardCode;

    /**
     * 연결된 페이지 코드 (조인, site_page.page_code)
     */
    private String pageCode;

    /**
     * 연결된 페이지 제목 (조인)
     */
    private String pageTitle;

    /**
     * 하위 메뉴 목록 (계층 조회 시)
     */
    private List<SiteMenuVO> children;

    /**
     * 하위 메뉴 개수 (집계)
     */
    private Integer childCount;
}
