package com.nt.cms.install.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 사이트 기본 설정 요청 DTO
 * 
 * @author CMS Team
 */
@Getter
@Setter
public class SiteConfigRequest {

    /**
     * 사이트명
     */
    @NotBlank(message = "사이트명을 입력해주세요.")
    private String siteName = "CMS Core";

    /**
     * 파일 업로드 경로
     */
    @NotBlank(message = "파일 업로드 경로를 입력해주세요.")
    private String uploadPath = "C:/cms/files";
}
