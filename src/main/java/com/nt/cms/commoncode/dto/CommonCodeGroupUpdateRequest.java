package com.nt.cms.commoncode.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 공통 코드 그룹 수정 요청 DTO
 *
 * @author CMS Team
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommonCodeGroupUpdateRequest {

    /**
     * 코드 그룹명
     */
    @Size(max = 200, message = "그룹명은 200자 이하로 입력해주세요.")
    private String groupName;

    /**
     * 설명
     */
    @Size(max = 500, message = "설명은 500자 이하로 입력해주세요.")
    private String description;

    /**
     * 활성 여부
     */
    private Boolean isActive;
}
