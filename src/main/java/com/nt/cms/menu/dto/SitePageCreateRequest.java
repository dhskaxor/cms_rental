package com.nt.cms.menu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사이트 페이지 생성 요청 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SitePageCreateRequest {

    /**
     * 페이지 고유 코드
     */
    @NotBlank(message = "페이지 코드는 필수입니다.")
    @Size(max = 100, message = "페이지 코드는 100자 이내여야 합니다.")
    @Pattern(regexp = "^[a-z0-9_-]+$", message = "페이지 코드는 영문 소문자, 숫자, 하이픈, 언더스코어만 허용됩니다.")
    private String pageCode;

    /**
     * 페이지 제목
     */
    @NotBlank(message = "페이지 제목은 필수입니다.")
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
     * 사용자 사이트 페이지 템플릿 코드 (미지정 시 default)
     */
    @Size(max = 50, message = "템플릿 코드는 50자 이내여야 합니다.")
    private String templateCode;
}
