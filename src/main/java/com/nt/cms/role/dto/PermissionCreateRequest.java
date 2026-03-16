package com.nt.cms.role.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 권한 생성 요청 DTO
 * 
 * @author CMS Team
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionCreateRequest {

    /**
     * 권한 코드 (영문 대문자, 언더스코어만 허용)
     */
    @NotBlank(message = "권한 코드를 입력해주세요.")
    @Size(max = 100, message = "권한 코드는 100자 이하로 입력해주세요.")
    @Pattern(regexp = "^[A-Z][A-Z0-9_]*$", message = "권한 코드는 영문 대문자로 시작하고, 영문 대문자/숫자/언더스코어만 허용됩니다.")
    private String permissionCode;

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
