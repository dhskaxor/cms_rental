package com.nt.cms.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 로그인 요청 DTO
 * 
 * @author CMS Team
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    /**
     * 사용자 아이디
     */
    @NotBlank(message = "아이디를 입력해주세요.")
    private String username;

    /**
     * 비밀번호
     */
    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;
}
