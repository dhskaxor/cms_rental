package com.nt.cms.commoncode.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 공통 코드 그룹 생성 요청 DTO
 *
 * @author CMS Team
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommonCodeGroupCreateRequest {

    /**
     * 코드 그룹 고유 코드
     */
    @NotBlank(message = "그룹 코드를 입력해주세요.")
    @Size(max = 100, message = "그룹 코드는 100자 이하로 입력해주세요.")
    @Pattern(regexp = "^[A-Za-z0-9_]+$", message = "그룹 코드는 영문, 숫자, 언더스코어만 허용됩니다.")
    private String groupCode;

    /**
     * 코드 그룹명
     */
    @NotBlank(message = "그룹명을 입력해주세요.")
    @Size(max = 200, message = "그룹명은 200자 이하로 입력해주세요.")
    private String groupName;

    /**
     * 설명
     */
    @Size(max = 500, message = "설명은 500자 이하로 입력해주세요.")
    private String description;

    /**
     * 시스템 코드 여부 (기본: false)
     */
    private Boolean isSystem;

    /**
     * 활성 여부 (기본: true)
     */
    private Boolean isActive;
}
