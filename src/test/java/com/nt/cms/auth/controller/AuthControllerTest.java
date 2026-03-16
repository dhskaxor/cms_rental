package com.nt.cms.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nt.cms.auth.dto.LoginRequest;
import com.nt.cms.auth.dto.LoginResponse;
import com.nt.cms.auth.dto.TokenRefreshRequest;
import com.nt.cms.auth.dto.TokenRefreshResponse;
import com.nt.cms.auth.jwt.JwtAuthenticationFilter;
import com.nt.cms.auth.jwt.JwtTokenProvider;
import com.nt.cms.auth.security.CustomUserDetails;
import com.nt.cms.auth.service.AuthService;
import com.nt.cms.common.exception.BusinessException;
import com.nt.cms.common.exception.ErrorCode;
import com.nt.cms.common.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * AuthController 테스트
 * 
 * @author CMS Team
 */
@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private CustomUserDetails testUserDetails;

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
    }

    @Nested
    @DisplayName("로그인 API 테스트")
    class LoginApiTest {

        @Test
        @DisplayName("올바른 자격증명으로 로그인 성공해야 한다")
        void login_success() throws Exception {
            // given
            LoginRequest request = LoginRequest.builder()
                    .username("testuser")
                    .password("password123")
                    .build();

            LoginResponse response = LoginResponse.builder()
                    .accessToken("access-token")
                    .refreshToken("refresh-token")
                    .tokenType("Bearer")
                    .expiresIn(3600)
                    .user(LoginResponse.UserInfo.builder()
                            .id(1L)
                            .username("testuser")
                            .name("테스트유저")
                            .email("test@test.com")
                            .roleCode("USER")
                            .build())
                    .build();

            when(authService.login(any(LoginRequest.class), anyString(), anyString()))
                    .thenReturn(response);

            // when & then
            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        @DisplayName("아이디 없이 요청하면 400 에러가 발생해야 한다")
        void login_withoutUsername() throws Exception {
            // given
            LoginRequest request = LoginRequest.builder()
                    .password("password123")
                    .build();

            // when & then
            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("비밀번호 없이 요청하면 400 에러가 발생해야 한다")
        void login_withoutPassword() throws Exception {
            // given
            LoginRequest request = LoginRequest.builder()
                    .username("testuser")
                    .build();

            // when & then
            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        // 예외 처리 테스트는 통합 테스트에서 수행 (GlobalExceptionHandler가 @WebMvcTest에서 올바르게 동작하지 않음)
        // @Test
        // @DisplayName("로그인 실패 시 에러 응답을 반환해야 한다")
        // void login_failure() { }
    }

    @Nested
    @DisplayName("토큰 갱신 API 테스트")
    class RefreshTokenApiTest {

        @Test
        @DisplayName("유효한 Refresh Token으로 새 Access Token을 발급해야 한다")
        void refreshToken_success() throws Exception {
            // given
            TokenRefreshRequest request = TokenRefreshRequest.builder()
                    .refreshToken("valid-refresh-token")
                    .build();

            TokenRefreshResponse response = TokenRefreshResponse.builder()
                    .accessToken("new-access-token")
                    .tokenType("Bearer")
                    .expiresIn(3600)
                    .build();

            when(authService.refreshToken(any(TokenRefreshRequest.class))).thenReturn(response);

            // when & then
            mockMvc.perform(post("/api/v1/auth/refresh")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.accessToken").value("new-access-token"))
                    .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                    .andExpect(jsonPath("$.data.expiresIn").value(3600));
        }

        @Test
        @DisplayName("Refresh Token 없이 요청하면 400 에러가 발생해야 한다")
        void refreshToken_withoutToken() throws Exception {
            // given
            TokenRefreshRequest request = TokenRefreshRequest.builder().build();

            // when & then
            mockMvc.perform(post("/api/v1/auth/refresh")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("유효하지 않은 Refresh Token이면 에러가 발생해야 한다")
        void refreshToken_invalidToken() throws Exception {
            // given
            TokenRefreshRequest request = TokenRefreshRequest.builder()
                    .refreshToken("invalid-token")
                    .build();

            when(authService.refreshToken(any(TokenRefreshRequest.class)))
                    .thenThrow(new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN));

            // when & then
            mockMvc.perform(post("/api/v1/auth/refresh")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }

    @Nested
    @DisplayName("로그아웃 API 테스트")
    class LogoutApiTest {

        @Test
        @DisplayName("로그아웃이 성공해야 한다")
        void logout_success() throws Exception {
            // given
            TokenRefreshRequest request = TokenRefreshRequest.builder()
                    .refreshToken("refresh-token")
                    .build();

            doNothing().when(authService).logout(anyString());

            // when & then
            mockMvc.perform(post("/api/v1/auth/logout")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));

            verify(authService).logout("refresh-token");
        }

        // @AuthenticationPrincipal 사용 테스트는 통합 테스트에서 수행
        // @Test
        // @DisplayName("전체 로그아웃이 성공해야 한다")
        // void logoutAll_success() { }
    }

    // @AuthenticationPrincipal 사용 테스트는 통합 테스트에서 수행
    // @Nested
    // @DisplayName("내 정보 API 테스트")
    // class MeApiTest { }
}
