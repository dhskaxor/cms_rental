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
 * 사용자 생성 요청 DTO
 * 
 * @author CMS Team
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRequest {

    /**
     * 로그인 아이디
     */
    @NotBlank(message = "아이디를 입력해주세요.")
    @Size(min = 4, max = 50, message = "아이디는 4~50자 사이로 입력해주세요.")
    private String username;

    /**
     * 비밀번호
     */
    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min = 6, max = 12, message = "비밀번호는 6~12자 사이로 입력해주세요.")
    private String password;

    /**
     * 비밀번호 확인
     */
    @NotBlank(message = "비밀번호 확인을 입력해주세요.")
    private String passwordConfirm;

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
}
