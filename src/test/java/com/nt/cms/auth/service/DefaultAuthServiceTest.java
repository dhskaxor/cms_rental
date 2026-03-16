package com.nt.cms.auth.service;

import com.nt.cms.auth.dto.LoginRequest;
import com.nt.cms.auth.dto.LoginResponse;
import com.nt.cms.auth.dto.TokenRefreshRequest;
import com.nt.cms.auth.dto.TokenRefreshResponse;
import com.nt.cms.auth.jwt.JwtTokenProvider;
import com.nt.cms.auth.mapper.AuthMapper;
import com.nt.cms.auth.security.CustomUserDetails;
import com.nt.cms.auth.security.CustomUserDetailsService;
import com.nt.cms.auth.vo.RefreshTokenVO;
import com.nt.cms.common.exception.BusinessException;
import com.nt.cms.common.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * DefaultAuthService 테스트
 * 
 * @author CMS Team
 */
@ExtendWith(MockitoExtension.class)
class DefaultAuthServiceTest {

    @Mock
    private AuthMapper authMapper;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private DefaultAuthService authService;

    private CustomUserDetails testUserDetails;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        testUserDetails = CustomUserDetails.builder()
                .userId(1L)
                .username("testuser")
                .password("encodedPassword")
                .name("테스트유저")
                .email("test@test.com")
                .roleCode("USER")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .enabled(true)
                .accountNonLocked(true)
                .build();

        loginRequest = LoginRequest.builder()
                .username("testuser")
                .password("password123")
                .build();
    }

    @Nested
    @DisplayName("로그인 테스트")
    class LoginTest {

        @Test
        @DisplayName("올바른 자격증명으로 로그인 성공해야 한다")
        void login_success() {
            // given
            when(userDetailsService.loadUserByUsername("testuser")).thenReturn(testUserDetails);
            when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
            when(jwtTokenProvider.createAccessToken(any())).thenReturn("access-token");
            when(jwtTokenProvider.createRefreshToken(any())).thenReturn("refresh-token");
            when(jwtTokenProvider.getAccessTokenValidityInSeconds()).thenReturn(3600L);
            when(jwtTokenProvider.getRefreshTokenValidityInSeconds()).thenReturn(1209600L);
            when(authMapper.insertRefreshToken(any())).thenReturn(1);
            when(authMapper.updateLastLoginAt(anyLong())).thenReturn(1);

            // when
            LoginResponse response = authService.login(loginRequest, "Mozilla/5.0", "127.0.0.1");

            // then
            assertNotNull(response);
            assertEquals("access-token", response.getAccessToken());
            assertEquals("refresh-token", response.getRefreshToken());
            assertEquals("Bearer", response.getTokenType());
            assertEquals(3600L, response.getExpiresIn());
            assertNotNull(response.getUser());
            assertEquals(1L, response.getUser().getId());
            assertEquals("testuser", response.getUser().getUsername());
        }

        @Test
        @DisplayName("기존 Refresh Token이 모두 폐기되어야 한다")
        void login_revokesExistingTokens() {
            // given
            when(userDetailsService.loadUserByUsername("testuser")).thenReturn(testUserDetails);
            when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
            when(jwtTokenProvider.createAccessToken(any())).thenReturn("access-token");
            when(jwtTokenProvider.createRefreshToken(any())).thenReturn("refresh-token");
            when(jwtTokenProvider.getAccessTokenValidityInSeconds()).thenReturn(3600L);
            when(jwtTokenProvider.getRefreshTokenValidityInSeconds()).thenReturn(1209600L);

            // when
            authService.login(loginRequest, "Mozilla/5.0", "127.0.0.1");

            // then
            verify(authMapper).revokeAllRefreshTokensByUserId(1L);
        }

        @Test
        @DisplayName("사용자를 찾을 수 없으면 예외가 발생해야 한다")
        void login_userNotFound() {
            // given
            when(userDetailsService.loadUserByUsername("testuser"))
                    .thenThrow(new RuntimeException("User not found"));

            // when & then
            BusinessException exception = assertThrows(BusinessException.class, 
                    () -> authService.login(loginRequest, "Mozilla/5.0", "127.0.0.1"));
            
            assertEquals(ErrorCode.LOGIN_FAILED, exception.getErrorCode());
        }

        @Test
        @DisplayName("비밀번호가 일치하지 않으면 예외가 발생해야 한다")
        void login_wrongPassword() {
            // given
            when(userDetailsService.loadUserByUsername("testuser")).thenReturn(testUserDetails);
            when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(false);

            // when & then
            BusinessException exception = assertThrows(BusinessException.class, 
                    () -> authService.login(loginRequest, "Mozilla/5.0", "127.0.0.1"));
            
            assertEquals(ErrorCode.LOGIN_FAILED, exception.getErrorCode());
        }

        @Test
        @DisplayName("계정이 잠긴 경우 예외가 발생해야 한다")
        void login_accountLocked() {
            // given
            CustomUserDetails lockedUser = CustomUserDetails.builder()
                    .userId(1L)
                    .username("testuser")
                    .password("encodedPassword")
                    .roleCode("USER")
                    .authorities(Collections.emptyList())
                    .enabled(true)
                    .accountNonLocked(false) // 잠긴 계정
                    .build();

            when(userDetailsService.loadUserByUsername("testuser")).thenReturn(lockedUser);

            // when & then
            BusinessException exception = assertThrows(BusinessException.class, 
                    () -> authService.login(loginRequest, "Mozilla/5.0", "127.0.0.1"));
            
            assertEquals(ErrorCode.ACCOUNT_LOCKED, exception.getErrorCode());
        }

        @Test
        @DisplayName("마지막 로그인 시간이 업데이트되어야 한다")
        void login_updatesLastLoginAt() {
            // given
            when(userDetailsService.loadUserByUsername("testuser")).thenReturn(testUserDetails);
            when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
            when(jwtTokenProvider.createAccessToken(any())).thenReturn("access-token");
            when(jwtTokenProvider.createRefreshToken(any())).thenReturn("refresh-token");
            when(jwtTokenProvider.getAccessTokenValidityInSeconds()).thenReturn(3600L);
            when(jwtTokenProvider.getRefreshTokenValidityInSeconds()).thenReturn(1209600L);

            // when
            authService.login(loginRequest, "Mozilla/5.0", "127.0.0.1");

            // then
            verify(authMapper).updateLastLoginAt(1L);
        }
    }

    @Nested
    @DisplayName("토큰 갱신 테스트")
    class RefreshTokenTest {

        @Test
        @DisplayName("유효한 Refresh Token으로 새 Access Token을 발급해야 한다")
        void refreshToken_success() {
            // given
            TokenRefreshRequest request = TokenRefreshRequest.builder()
                    .refreshToken("valid-refresh-token")
                    .build();

            RefreshTokenVO storedToken = RefreshTokenVO.builder()
                    .id(1L)
                    .userId(1L)
                    .refreshToken("valid-refresh-token")
                    .expiresAt(LocalDateTime.now().plusDays(7))
                    .revoked(false)
                    .deleted(false)
                    .build();

            when(authMapper.findRefreshToken("valid-refresh-token")).thenReturn(storedToken);
            when(jwtTokenProvider.validateToken("valid-refresh-token")).thenReturn(true);
            when(userDetailsService.loadUserById(1L)).thenReturn(testUserDetails);
            when(jwtTokenProvider.createAccessToken(any())).thenReturn("new-access-token");
            when(jwtTokenProvider.getAccessTokenValidityInSeconds()).thenReturn(3600L);

            // when
            TokenRefreshResponse response = authService.refreshToken(request);

            // then
            assertNotNull(response);
            assertEquals("new-access-token", response.getAccessToken());
            assertEquals("Bearer", response.getTokenType());
            assertEquals(3600L, response.getExpiresIn());
        }

        @Test
        @DisplayName("존재하지 않는 Refresh Token이면 예외가 발생해야 한다")
        void refreshToken_tokenNotFound() {
            // given
            TokenRefreshRequest request = TokenRefreshRequest.builder()
                    .refreshToken("invalid-token")
                    .build();

            when(authMapper.findRefreshToken("invalid-token")).thenReturn(null);

            // when & then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> authService.refreshToken(request));
            
            assertEquals(ErrorCode.INVALID_REFRESH_TOKEN, exception.getErrorCode());
        }

        @Test
        @DisplayName("폐기된 Refresh Token이면 예외가 발생해야 한다")
        void refreshToken_revokedToken() {
            // given
            TokenRefreshRequest request = TokenRefreshRequest.builder()
                    .refreshToken("revoked-token")
                    .build();

            RefreshTokenVO revokedToken = RefreshTokenVO.builder()
                    .id(1L)
                    .userId(1L)
                    .refreshToken("revoked-token")
                    .expiresAt(LocalDateTime.now().plusDays(7))
                    .revoked(true) // 폐기됨
                    .deleted(false)
                    .build();

            when(authMapper.findRefreshToken("revoked-token")).thenReturn(revokedToken);

            // when & then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> authService.refreshToken(request));
            
            assertEquals(ErrorCode.INVALID_REFRESH_TOKEN, exception.getErrorCode());
        }

        @Test
        @DisplayName("만료된 Refresh Token이면 예외가 발생해야 한다")
        void refreshToken_expiredToken() {
            // given
            TokenRefreshRequest request = TokenRefreshRequest.builder()
                    .refreshToken("expired-token")
                    .build();

            RefreshTokenVO expiredToken = RefreshTokenVO.builder()
                    .id(1L)
                    .userId(1L)
                    .refreshToken("expired-token")
                    .expiresAt(LocalDateTime.now().minusDays(1)) // 만료됨
                    .revoked(false)
                    .deleted(false)
                    .build();

            when(authMapper.findRefreshToken("expired-token")).thenReturn(expiredToken);

            // when & then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> authService.refreshToken(request));
            
            assertEquals(ErrorCode.INVALID_REFRESH_TOKEN, exception.getErrorCode());
        }
    }

    @Nested
    @DisplayName("로그아웃 테스트")
    class LogoutTest {

        @Test
        @DisplayName("로그아웃 시 Refresh Token이 폐기되어야 한다")
        void logout_success() {
            // given
            when(authMapper.revokeRefreshToken("refresh-token")).thenReturn(1);

            // when
            authService.logout("refresh-token");

            // then
            verify(authMapper).revokeRefreshToken("refresh-token");
        }

        @Test
        @DisplayName("전체 로그아웃 시 사용자의 모든 Refresh Token이 폐기되어야 한다")
        void logoutAll_success() {
            // given
            when(authMapper.revokeAllRefreshTokensByUserId(1L)).thenReturn(3);

            // when
            authService.logoutAll(1L);

            // then
            verify(authMapper).revokeAllRefreshTokensByUserId(1L);
        }
    }
}
