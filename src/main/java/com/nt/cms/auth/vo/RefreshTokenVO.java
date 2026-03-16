package com.nt.cms.auth.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * JWT Refresh Token VO
 * 
 * @author CMS Team
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenVO {

    /**
     * Primary Key
     */
    private Long id;

    /**
     * 사용자 ID
     */
    private Long userId;

    /**
     * Refresh Token 문자열
     */
    private String refreshToken;

    /**
     * 만료 일시
     */
    private LocalDateTime expiresAt;

    /**
     * 폐기 여부
     */
    private Boolean revoked;

    /**
     * 클라이언트 User Agent
     */
    private String userAgent;

    /**
     * 클라이언트 IP 주소
     */
    private String ipAddress;

    /**
     * 생성 일시
     */
    private LocalDateTime createdAt;

    /**
     * 삭제 여부 (Soft Delete)
     */
    private Boolean deleted;

    /**
     * 토큰 만료 여부 확인
     * 
     * @return 만료되었으면 true
     */
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }

    /**
     * 토큰 유효성 확인 (폐기되지 않고 만료되지 않음)
     * 
     * @return 유효하면 true
     */
    public boolean isValid() {
        return !Boolean.TRUE.equals(revoked) && !isExpired() && !Boolean.TRUE.equals(deleted);
    }
}
