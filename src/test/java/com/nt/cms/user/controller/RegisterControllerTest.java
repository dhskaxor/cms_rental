package com.nt.cms.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nt.cms.auth.jwt.JwtAuthenticationFilter;
import com.nt.cms.auth.jwt.JwtTokenProvider;
import com.nt.cms.common.exception.BusinessException;
import com.nt.cms.common.exception.ErrorCode;
import com.nt.cms.common.exception.GlobalExceptionHandler;
import com.nt.cms.user.dto.RegisterRequest;
import com.nt.cms.user.dto.UserResponse;
import com.nt.cms.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * RegisterController 테스트
 */
@WebMvcTest(RegisterController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@DisplayName("RegisterController 테스트")
class RegisterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @DisplayName("회원가입 성공")
    void register_success() throws Exception {
        // given
        RegisterRequest request = RegisterRequest.builder()
                .username("newuser")
                .password("Test123!")
                .passwordConfirm("Test123!")
                .name("신규 사용자")
                .email("newuser@example.com")
                .build();

        UserResponse response = UserResponse.builder()
                .id(1L)
                .username("newuser")
                .name("신규 사용자")
                .email("newuser@example.com")
                .roleId(3L)
                .roleCode("USER")
                .roleName("사용자")
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .build();

        given(userService.register(any(RegisterRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value("newuser"))
                .andExpect(jsonPath("$.data.name").value("신규 사용자"));
    }

    @Test
    @DisplayName("회원가입 실패 - 중복 아이디")
    void register_duplicateUsername() throws Exception {
        // given
        RegisterRequest request = RegisterRequest.builder()
                .username("existinguser")
                .password("Test123!")
                .passwordConfirm("Test123!")
                .name("신규 사용자")
                .email("newuser@example.com")
                .build();

        given(userService.register(any(RegisterRequest.class)))
                .willThrow(new BusinessException(ErrorCode.DUPLICATE_USERNAME));

        // when & then
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("회원가입 실패 - 비밀번호 불일치")
    void register_passwordNotMatch() throws Exception {
        // given
        RegisterRequest request = RegisterRequest.builder()
                .username("newuser")
                .password("Test123!")
                .passwordConfirm("Different!")
                .name("신규 사용자")
                .email("newuser@example.com")
                .build();

        given(userService.register(any(RegisterRequest.class)))
                .willThrow(new BusinessException(ErrorCode.PASSWORD_NOT_MATCH));

        // when & then
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("회원가입 실패 - 유효성 검사 실패 (아이디 누락)")
    void register_validationFail_usernameBlank() throws Exception {
        // given
        RegisterRequest request = RegisterRequest.builder()
                .username("")
                .password("Test123!")
                .passwordConfirm("Test123!")
                .name("신규 사용자")
                .email("newuser@example.com")
                .build();

        // when & then
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("회원가입 실패 - 유효성 검사 실패 (잘못된 이메일)")
    void register_validationFail_invalidEmail() throws Exception {
        // given
        RegisterRequest request = RegisterRequest.builder()
                .username("newuser")
                .password("Test123!")
                .passwordConfirm("Test123!")
                .name("신규 사용자")
                .email("invalid-email")
                .build();

        // when & then
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }
}
