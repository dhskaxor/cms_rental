package com.nt.cms.menu.dto;

import com.nt.cms.menu.vo.SitePageVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 사이트 페이지 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SitePageResponse {

    private Long id;
    private String pageCode;
    private String pageTitle;
    private String content;
    private Boolean isPublished;
    private String templateCode;
    private LocalDateTime createdAt;
    private Long createdBy;
    private String createdByName;
    private LocalDateTime updatedAt;
    private Long updatedBy;
    private String updatedByName;

    /**
     * VO를 응답 DTO로 변환
     *
     * @param vo SitePageVO
     * @return SitePageResponse
     */
    public static SitePageResponse from(SitePageVO vo) {
        if (vo == null) {
            return null;
        }

        return SitePageResponse.builder()
                .id(vo.getId())
                .pageCode(vo.getPageCode())
                .pageTitle(vo.getPageTitle())
                .content(vo.getContent())
                .isPublished(vo.getIsPublished())
                .templateCode(vo.getTemplateCode())
                .createdAt(vo.getCreatedAt())
                .createdBy(vo.getCreatedBy())
                .createdByName(vo.getCreatedByName())
                .updatedAt(vo.getUpdatedAt())
                .updatedBy(vo.getUpdatedBy())
                .updatedByName(vo.getUpdatedByName())
                .build();
    }

    /**
     * VO 리스트를 응답 DTO 리스트로 변환
     *
     * @param voList SitePageVO 리스트
     * @return SitePageResponse 리스트
     */
    public static List<SitePageResponse> fromList(List<SitePageVO> voList) {
        if (voList == null) {
            return List.of();
        }
        return voList.stream()
                .map(SitePageResponse::from)
                .collect(Collectors.toList());
    }
}
