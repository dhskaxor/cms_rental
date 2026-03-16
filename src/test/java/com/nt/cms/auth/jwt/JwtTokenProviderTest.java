package com.nt.cms.auth.jwt;

import com.nt.cms.auth.security.CustomUserDetails;
import com.nt.cms.common.config.CmsProperties;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JwtTokenProvider 테스트
 * 
 * @author CMS Team
 */
class JwtTokenProviderTest {

    private CmsProperties cmsProperties;
    private CmsProperties.Jwt jwtProperties;
    private JwtTokenProvider jwtTokenProvider;
    private CustomUserDetails testUserDetails;

    @BeforeEach
    void setUp() {
        // 실제 객체 사용
        cmsProperties = new CmsProperties();
        jwtProperties = new CmsProperties.Jwt();
        jwtProperties.setSecret("dGVzdC1zZWNyZXQta2V5LWZvci1qd3QtdG9rZW4tcHJvdmlkZXItdGVzdA==");
        jwtProperties.setAccessTokenValidity(3600L);
        jwtProperties.setRefreshTokenValidity(1209600L);
        cmsProperties.setJwt(jwtProperties);

        // JwtTokenProvider 초기화
        jwtTokenProvider = new JwtTokenProvider(cmsProperties);
        jwtTokenProvider.init();

        // 테스트용 UserDetails 생성
        testUserDetails = CustomUserDetails.builder()
                .userId(1L)
                .username("testuser")
                .password("encodedPassword")
                .name("테스트유저")
                .email("test@test.com")
                .roleCode("USER")
                .authorities(Arrays.asList(
                        new SimpleGrantedAuthority("ROLE_USER"),
                        new SimpleGrantedAuthority("USER_READ"),
                        new SimpleGrantedAuthority("USER_CREATE")
                ))
                .enabled(true)
                .accountNonLocked(true)
                .build();
    }

    @Nested
    @DisplayName("토큰 생성 테스트")
    class CreateTokenTest {

        @Test
        @DisplayName("Access Token이 정상적으로 생성되어야 한다")
        void createAccessToken_success() {
            // when
            String token = jwtTokenProvider.createAccessToken(testUserDetails);

            // then
            assertNotNull(token);
            assertFalse(token.isEmpty());
            assertTrue(token.split("\\.").length == 3); // JWT 형식 검증 (header.payload.signature)
        }

        @Test
        @DisplayName("Refresh Token이 정상적으로 생성되어야 한다")
        void createRefreshToken_success() {
            // when
            String token = jwtTokenProvider.createRefreshToken(testUserDetails);

            // then
            assertNotNull(token);
            assertFalse(token.isEmpty());
            assertTrue(token.split("\\.").length == 3);
        }

        @Test
        @DisplayName("생성된 토큰에 사용자 정보가 포함되어야 한다")
        void createToken_containsUserInfo() {
            // when
            String token = jwtTokenProvider.createAccessToken(testUserDetails);
            Claims claims = jwtTokenProvider.getClaims(token);

            // then
            assertEquals("testuser", claims.getSubject());
            assertEquals(1L, claims.get("userId", Long.class));
            assertEquals("USER", claims.get("roleCode", String.class));
            assertTrue(claims.get("authorities", String.class).contains("ROLE_USER"));
        }
    }

    @Nested
    @DisplayName("토큰 검증 테스트")
    class ValidateTokenTest {

        @Test
        @DisplayName("유효한 토큰은 true를 반환해야 한다")
        void validateToken_validToken() {
            // given
            String token = jwtTokenProvider.createAccessToken(testUserDetails);

            // when
            boolean isValid = jwtTokenProvider.validateToken(token);

            // then
            assertTrue(isValid);
        }

        @Test
        @DisplayName("잘못된 형식의 토큰은 false를 반환해야 한다")
        void validateToken_malformedToken() {
            // when
            boolean isValid = jwtTokenProvider.validateToken("invalid.token.here");

            // then
            assertFalse(isValid);
        }

        @Test
        @DisplayName("빈 토큰은 false를 반환해야 한다")
        void validateToken_emptyToken() {
            // when
            boolean isValid = jwtTokenProvider.validateToken("");

            // then
            assertFalse(isValid);
        }

        @Test
        @DisplayName("null 토큰은 false를 반환해야 한다")
        void validateToken_nullToken() {
            // when
            boolean isValid = jwtTokenProvider.validateToken(null);

            // then
            assertFalse(isValid);
        }
    }

    @Nested
    @DisplayName("Authentication 추출 테스트")
    class GetAuthenticationTest {

        @Test
        @DisplayName("토큰에서 Authentication 객체가 정상적으로 추출되어야 한다")
        void getAuthentication_success() {
            // given
            String token = jwtTokenProvider.createAccessToken(testUserDetails);

            // when
            Authentication authentication = jwtTokenProvider.getAuthentication(token);

            // then
            assertNotNull(authentication);
            assertEquals("testuser", authentication.getName());
            assertTrue(authentication.isAuthenticated());
        }

        @Test
        @DisplayName("추출된 Authentication에 권한 정보가 포함되어야 한다")
        void getAuthentication_containsAuthorities() {
            // given
            String token = jwtTokenProvider.createAccessToken(testUserDetails);

            // when
            Authentication authentication = jwtTokenProvider.getAuthentication(token);

            // then
            assertNotNull(authentication.getAuthorities());
            assertTrue(authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
        }

        @Test
        @DisplayName("추출된 Authentication의 Principal이 CustomUserDetails여야 한다")
        void getAuthentication_principalIsCustomUserDetails() {
            // given
            String token = jwtTokenProvider.createAccessToken(testUserDetails);

            // when
            Authentication authentication = jwtTokenProvider.getAuthentication(token);

            // then
            assertTrue(authentication.getPrincipal() instanceof CustomUserDetails);
            CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
            assertEquals(1L, principal.getUserId());
            assertEquals("USER", principal.getRoleCode());
        }
    }

    @Nested
    @DisplayName("사용자 정보 추출 테스트")
    class ExtractUserInfoTest {

        @Test
        @DisplayName("토큰에서 사용자 ID가 정상적으로 추출되어야 한다")
        void getUserId_success() {
            // given
            String token = jwtTokenProvider.createAccessToken(testUserDetails);

            // when
            Long userId = jwtTokenProvider.getUserId(token);

            // then
            assertEquals(1L, userId);
        }

        @Test
        @DisplayName("토큰에서 사용자명이 정상적으로 추출되어야 한다")
        void getUsername_success() {
            // given
            String token = jwtTokenProvider.createAccessToken(testUserDetails);

            // when
            String username = jwtTokenProvider.getUsername(token);

            // then
            assertEquals("testuser", username);
        }
    }

    @Nested
    @DisplayName("Secret 초기화 테스트 (Base64 디코딩 실패 케이스)")
    class SecretInitializationTest {

        @Test
        @DisplayName("일반 텍스트 시크릿(하이픈 포함)으로 초기화 시 정상 동작해야 한다 - application.yml 기본값 케이스")
        void init_plainTextSecretWithHyphens_shouldSucceed() {
            // given: application.yml의 기본 JWT 시크릿 (Base64가 아닌 일반 텍스트, 하이픈 포함)
            // 이전에 Decoders.BASE64.decode() 시 Illegal base64 character: '-' 로 실패했던 케이스
            CmsProperties props = new CmsProperties();
            CmsProperties.Jwt jwt = new CmsProperties.Jwt();
            jwt.setSecret("cms-default-secret-key-please-change-in-production-environment");
            jwt.setAccessTokenValidity(3600L);
            jwt.setRefreshTokenValidity(1209600L);
            props.setJwt(jwt);

            JwtTokenProvider provider = new JwtTokenProvider(props);

            // when: init 호출 시 예외 없이 완료되어야 함
            assertDoesNotThrow(() -> provider.init());

            // then: 토큰 생성 및 검증이 정상 동작해야 함
            String token = provider.createAccessToken(testUserDetails);
            assertNotNull(token);
            assertTrue(token.split("\\.").length == 3);
            assertTrue(provider.validateToken(token));
            assertEquals("testuser", provider.getUsername(token));
        }

        @Test
        @DisplayName("Base64 인코딩된 시크릿으로 초기화 시 정상 동작해야 한다")
        void init_base64EncodedSecret_shouldSucceed() {
            // given: Base64 인코딩된 시크릿
            CmsProperties props = new CmsProperties();
            CmsProperties.Jwt jwt = new CmsProperties.Jwt();
            jwt.setSecret("dGVzdC1zZWNyZXQta2V5LWZvci1qd3QtdG9rZW4tcHJvdmlkZXItdGVzdA==");
            jwt.setAccessTokenValidity(3600L);
            jwt.setRefreshTokenValidity(1209600L);
            props.setJwt(jwt);

            JwtTokenProvider provider = new JwtTokenProvider(props);

            // when & then
            assertDoesNotThrow(() -> provider.init());
            String token = provider.createAccessToken(testUserDetails);
            assertNotNull(token);
            assertTrue(provider.validateToken(token));
        }
    }

    @Nested
    @DisplayName("토큰 만료 테스트")
    class TokenExpirationTest {

        @Test
        @DisplayName("유효한 토큰은 만료되지 않아야 한다")
        void isTokenExpired_validToken() {
            // given
            String token = jwtTokenProvider.createAccessToken(testUserDetails);

            // when
            boolean isExpired = jwtTokenProvider.isTokenExpired(token);

            // then
            assertFalse(isExpired);
        }

        @Test
        @DisplayName("Access Token 유효 시간이 올바르게 반환되어야 한다")
        void getAccessTokenValidityInSeconds() {
            // when
            long validity = jwtTokenProvider.getAccessTokenValidityInSeconds();

            // then
            assertEquals(3600L, validity);
        }

        @Test
        @DisplayName("Refresh Token 유효 시간이 올바르게 반환되어야 한다")
        void getRefreshTokenValidityInSeconds() {
            // when
            long validity = jwtTokenProvider.getRefreshTokenValidityInSeconds();

            // then
            assertEquals(1209600L, validity);
        }
    }
}
