package com.nt.cms.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 비밀번호 변경 요청 DTO
 * 
 * @author CMS Team
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordChangeRequest {

    /**
     * 현재 비밀번호
     */
    @NotBlank(message = "현재 비밀번호를 입력해주세요.")
    private String currentPassword;

    /**
     * 새 비밀번호
     */
    @NotBlank(message = "새 비밀번호를 입력해주세요.")
    @Size(min = 6, max = 12, message = "비밀번호는 6~12자 사이로 입력해주세요.")
    private String newPassword;

    /**
     * 새 비밀번호 확인
     */
    @NotBlank(message = "새 비밀번호 확인을 입력해주세요.")
    private String newPasswordConfirm;
}
