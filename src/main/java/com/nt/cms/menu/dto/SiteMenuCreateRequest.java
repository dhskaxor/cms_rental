package com.nt.cms.menu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 사이트 메뉴 생성 요청 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SiteMenuCreateRequest {

    /**
     * 상위 메뉴 ID (NULL이면 최상위)
     */
    private Long parentId;

    /**
     * 메뉴명
     */
    @NotBlank(message = "메뉴명은 필수입니다.")
    @Size(max = 200, message = "메뉴명은 200자 이내여야 합니다.")
    private String menuName;

    /**
     * 메뉴 고유 코드
     */
    @NotBlank(message = "메뉴 코드는 필수입니다.")
    @Size(max = 100, message = "메뉴 코드는 100자 이내여야 합니다.")
    @Pattern(regexp = "^[a-z0-9_-]+$", message = "메뉴 코드는 영문 소문자, 숫자, 하이픈, 언더스코어만 허용됩니다.")
    private String menuCode;

    /**
     * 메뉴 유형 (PAGE, BOARD, LINK, CUSTOM)
     */
    @NotBlank(message = "메뉴 유형은 필수입니다.")
    @Pattern(regexp = "^(PAGE|BOARD|LINK|CUSTOM)$", message = "메뉴 유형은 PAGE, BOARD, LINK, CUSTOM 중 하나여야 합니다.")
    private String menuType;

    /**
     * 프론트 URL 경로
     */
    @Size(max = 255, message = "URL 경로는 255자 이내여야 합니다.")
    private String urlPath;

    /**
     * 외부 링크 URL
     */
    @Size(max = 500, message = "외부 링크 URL은 500자 이내여야 합니다.")
    private String linkUrl;

    /**
     * 연결할 게시판 ID
     */
    private Long boardId;

    /**
     * 연결할 페이지 ID
     */
    private Long pageId;

    /**
     * 아이콘 클래스명
     */
    @Size(max = 100, message = "아이콘 클래스명은 100자 이내여야 합니다.")
    private String icon;

    /**
     * 링크 타겟 (_self, _blank)
     */
    @Pattern(regexp = "^(_self|_blank)$", message = "링크 타겟은 _self 또는 _blank여야 합니다.")
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
    @Size(max = 255, message = "SEO 제목은 255자 이내여야 합니다.")
    private String seoTitle;

    /**
     * SEO 설명
     */
    @Size(max = 500, message = "SEO 설명은 500자 이내여야 합니다.")
    private String seoDescription;
}
