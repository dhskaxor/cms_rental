package com.nt.cms.auth.jwt;

import com.nt.cms.auth.security.CustomUserDetails;
import com.nt.cms.common.config.CmsProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * JWT 토큰 생성 및 검증 Provider
 * 
 * @author CMS Team
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final CmsProperties cmsProperties;
    
    private SecretKey secretKey;

    /**
     * 초기화 - Secret Key 생성
     * Base64 인코딩된 시크릿 또는 일반 텍스트 시크릿 모두 지원
     */
    @PostConstruct
    public void init() {
        String secret = cmsProperties.getJwt().getSecret();
        byte[] keyBytes;

        try {
            keyBytes = Decoders.BASE64.decode(secret);
        } catch (Exception e) {
            // 일반 텍스트 시크릿인 경우 UTF-8 바이트 사용
            keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        }

        // HMAC-SHA256은 최소 256비트(32바이트) 필요
        if (keyBytes.length < 32) {
            keyBytes = Arrays.copyOf(keyBytes, 32);
        }

        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Access Token 생성
     * 
     * @param userDetails 사용자 정보
     * @return Access Token
     */
    public String createAccessToken(CustomUserDetails userDetails) {
        return createToken(userDetails, cmsProperties.getJwt().getAccessTokenValidity() * 1000);
    }

    /**
     * Refresh Token 생성
     * 
     * @param userDetails 사용자 정보
     * @return Refresh Token
     */
    public String createRefreshToken(CustomUserDetails userDetails) {
        return createToken(userDetails, cmsProperties.getJwt().getRefreshTokenValidity() * 1000);
    }

    /**
     * 토큰 생성 공통 메서드
     * 
     * @param userDetails 사용자 정보
     * @param validityInMillis 유효 시간 (밀리초)
     * @return JWT 토큰
     */
    private String createToken(CustomUserDetails userDetails, long validityInMillis) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMillis);

        // 권한 정보를 문자열로 변환
        String authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("userId", userDetails.getUserId())
                .claim("roleCode", userDetails.getRoleCode())
                .claim("authorities", authorities)
                .issuedAt(now)
                .expiration(validity)
                .signWith(secretKey)
                .compact();
    }

    /**
     * 토큰에서 Authentication 객체 추출
     * 
     * @param token JWT 토큰
     * @return Authentication 객체
     */
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);
        
        Long userId = claims.get("userId", Long.class);
        String username = claims.getSubject();
        String roleCode = claims.get("roleCode", String.class);
        String authoritiesStr = claims.get("authorities", String.class);

        Collection<GrantedAuthority> authorities = new ArrayList<>();
        if (authoritiesStr != null && !authoritiesStr.isEmpty()) {
            authorities = Arrays.stream(authoritiesStr.split(","))
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }

        CustomUserDetails userDetails = CustomUserDetails.builder()
                .userId(userId)
                .username(username)
                .password("")
                .roleCode(roleCode)
                .authorities(authorities)
                .enabled(true)
                .build();

        return new UsernamePasswordAuthenticationToken(userDetails, token, authorities);
    }

    /**
     * 토큰 유효성 검증
     * 
     * @param token JWT 토큰
     * @return 유효하면 true
     */
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("만료된 JWT 토큰입니다: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("지원하지 않는 JWT 토큰입니다: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("잘못된 형식의 JWT 토큰입니다: {}", e.getMessage());
        } catch (SignatureException e) {
            log.warn("JWT 서명 검증에 실패했습니다: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("JWT 토큰이 비어있습니다: {}", e.getMessage());
        }
        return false;
    }

    /**
     * 토큰에서 Claims 추출
     * 
     * @param token JWT 토큰
     * @return Claims
     */
    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 토큰에서 사용자 ID 추출
     * 
     * @param token JWT 토큰
     * @return 사용자 ID
     */
    public Long getUserId(String token) {
        return getClaims(token).get("userId", Long.class);
    }

    /**
     * 토큰에서 사용자명 추출
     * 
     * @param token JWT 토큰
     * @return 사용자명
     */
    public String getUsername(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * 토큰 만료 여부 확인
     * 
     * @param token JWT 토큰
     * @return 만료되었으면 true
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = getClaims(token).getExpiration();
            return expiration.before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    /**
     * Access Token 유효 시간 반환 (초)
     * 
     * @return 유효 시간 (초)
     */
    public long getAccessTokenValidityInSeconds() {
        return cmsProperties.getJwt().getAccessTokenValidity();
    }

    /**
     * Refresh Token 유효 시간 반환 (초)
     * 
     * @return 유효 시간 (초)
     */
    public long getRefreshTokenValidityInSeconds() {
        return cmsProperties.getJwt().getRefreshTokenValidity();
    }
}
