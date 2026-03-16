package com.nt.cms.role.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 역할 수정 요청 DTO
 * 
 * @author CMS Team
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleUpdateRequest {

    /**
     * 역할명
     */
    @NotBlank(message = "역할명을 입력해주세요.")
    @Size(max = 100, message = "역할명은 100자 이하로 입력해주세요.")
    private String roleName;

    /**
     * 역할 설명
     */
    @Size(max = 255, message = "설명은 255자 이하로 입력해주세요.")
    private String description;

    /**
     * 권한 ID 목록
     */
    private List<Long> permissionIds;
}
