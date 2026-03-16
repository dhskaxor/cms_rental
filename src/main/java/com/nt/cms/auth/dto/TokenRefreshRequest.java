package com.nt.cms.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 토큰 갱신 요청 DTO
 * 
 * @author CMS Team
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenRefreshRequest {

    /**
     * Refresh Token
     */
    @NotBlank(message = "Refresh Token을 입력해주세요.")
    private String refreshToken;
}
