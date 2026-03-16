package com.nt.cms.role.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 역할 생성 요청 DTO
 * 
 * @author CMS Team
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleCreateRequest {

    /**
     * 역할 코드 (영문 대문자, 언더스코어만 허용)
     */
    @NotBlank(message = "역할 코드를 입력해주세요.")
    @Size(max = 50, message = "역할 코드는 50자 이하로 입력해주세요.")
    @Pattern(regexp = "^[A-Z][A-Z0-9_]*$", message = "역할 코드는 영문 대문자로 시작하고, 영문 대문자/숫자/언더스코어만 허용됩니다.")
    private String roleCode;

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
