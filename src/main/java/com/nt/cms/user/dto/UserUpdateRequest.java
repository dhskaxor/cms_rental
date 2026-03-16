package com.nt.cms.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 수정 요청 DTO
 * 
 * @author CMS Team
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {

    /**
     * 사용자 이름
     */
    @NotBlank(message = "이름을 입력해주세요.")
    @Size(max = 100, message = "이름은 100자 이하로 입력해주세요.")
    private String name;

    /**
     * 이메일
     */
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @Size(max = 150, message = "이메일은 150자 이하로 입력해주세요.")
    private String email;

    /**
     * 역할 ID
     */
    @NotNull(message = "역할을 선택해주세요.")
    private Long roleId;

    /**
     * 상태 (ACTIVE, LOCKED)
     */
    private String status;
}
