package com.nt.cms.menu.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사이트 페이지 수정 요청 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SitePageUpdateRequest {

    /**
     * 페이지 제목
     */
    @Size(max = 200, message = "페이지 제목은 200자 이내여야 합니다.")
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
     * 사용자 사이트 페이지 템플릿 코드
     */
    @Size(max = 50, message = "템플릿 코드는 50자 이내여야 합니다.")
    private String templateCode;
}
