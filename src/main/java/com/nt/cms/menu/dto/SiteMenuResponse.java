package com.nt.cms.menu.dto;

import com.nt.cms.menu.vo.SiteMenuVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 사이트 메뉴 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SiteMenuResponse {

    private Long id;
    private Long parentId;
    private String parentMenuName;
    private Integer depth;
    private String menuName;
    private String menuCode;
    private String menuType;
    private String urlPath;
    private String linkUrl;
    private Long boardId;
    private String boardName;
    private String boardCode;
    private Long pageId;
    private String pageCode;
    private String pageTitle;
    private String icon;
    private String target;
    private Integer sortOrder;
    private Boolean isVisible;
    private Boolean isLoginRequired;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private String seoTitle;
    private String seoDescription;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer childCount;
    private List<SiteMenuResponse> children;

    /**
     * VO를 응답 DTO로 변환
     *
     * @param vo SiteMenuVO
     * @return SiteMenuResponse
     */
    public static SiteMenuResponse from(SiteMenuVO vo) {
        if (vo == null) {
            return null;
        }

        SiteMenuResponseBuilder builder = SiteMenuResponse.builder()
                .id(vo.getId())
                .parentId(vo.getParentId())
                .parentMenuName(vo.getParentMenuName())
                .depth(vo.getDepth())
                .menuName(vo.getMenuName())
                .menuCode(vo.getMenuCode())
                .menuType(vo.getMenuType())
                .urlPath(vo.getUrlPath())
                .linkUrl(vo.getLinkUrl())
                .boardId(vo.getBoardId())
                .boardName(vo.getBoardName())
                .boardCode(vo.getBoardCode())
                .pageId(vo.getPageId())
                .pageCode(vo.getPageCode())
                .pageTitle(vo.getPageTitle())
                .icon(vo.getIcon())
                .target(vo.getTarget())
                .sortOrder(vo.getSortOrder())
                .isVisible(vo.getIsVisible())
                .isLoginRequired(vo.getIsLoginRequired())
                .startAt(vo.getStartAt())
                .endAt(vo.getEndAt())
                .seoTitle(vo.getSeoTitle())
                .seoDescription(vo.getSeoDescription())
                .createdAt(vo.getCreatedAt())
                .updatedAt(vo.getUpdatedAt())
                .childCount(vo.getChildCount());

        if (vo.getChildren() != null && !vo.getChildren().isEmpty()) {
            builder.children(vo.getChildren().stream()
                    .map(SiteMenuResponse::from)
                    .collect(Collectors.toList()));
        }

        return builder.build();
    }

    /**
     * VO 리스트를 응답 DTO 리스트로 변환
     *
     * @param voList SiteMenuVO 리스트
     * @return SiteMenuResponse 리스트
     */
    public static List<SiteMenuResponse> fromList(List<SiteMenuVO> voList) {
        if (voList == null) {
            return List.of();
        }
        return voList.stream()
                .map(SiteMenuResponse::from)
                .collect(Collectors.toList());
    }
}
