package com.nt.cms.role.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 권한 수정 요청 DTO
 * 
 * @author CMS Team
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionUpdateRequest {

    /**
     * 권한명
     */
    @NotBlank(message = "권한명을 입력해주세요.")
    @Size(max = 100, message = "권한명은 100자 이하로 입력해주세요.")
    private String permissionName;

    /**
     * 권한 설명
     */
    @Size(max = 255, message = "설명은 255자 이하로 입력해주세요.")
    private String description;
}
