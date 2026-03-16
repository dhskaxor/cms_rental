package com.nt.cms.install.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 관리자 계정 생성 요청 DTO
 * 
 * @author CMS Team
 */
@Getter
@Setter
public class AdminAccountRequest {

    /**
     * 관리자 아이디
     */
    @NotBlank(message = "아이디를 입력해주세요.")
    @Size(min = 4, max = 20, message = "아이디는 4~20자로 입력해주세요.")
    private String username;

    /**
     * 관리자 비밀번호
     */
    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min = 6, max = 12, message = "비밀번호는 6~12자로 입력해주세요.")
    private String password;

    /**
     * 비밀번호 확인
     */
    @NotBlank(message = "비밀번호 확인을 입력해주세요.")
    private String passwordConfirm;

    /**
     * 관리자 이름
     */
    @NotBlank(message = "이름을 입력해주세요.")
    private String name;

    /**
     * 관리자 이메일
     */
    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "올바른 이메일 형식을 입력해주세요.")
    private String email;

    /**
     * 비밀번호 일치 여부 확인
     * 
     * @return 일치하면 true
     */
    public boolean isPasswordMatch() {
        return password != null && password.equals(passwordConfirm);
    }
}
