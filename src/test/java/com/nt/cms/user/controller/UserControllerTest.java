package com.nt.cms.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nt.cms.auth.jwt.JwtAuthenticationFilter;
import com.nt.cms.auth.jwt.JwtTokenProvider;
import com.nt.cms.common.exception.BusinessException;
import com.nt.cms.common.exception.ErrorCode;
import com.nt.cms.common.exception.GlobalExceptionHandler;
import com.nt.cms.common.response.PageResponse;
import com.nt.cms.user.dto.*;
import com.nt.cms.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * UserController 테스트
 */
@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@DisplayName("UserController 테스트")
class UserControllerTest {

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

    private UserResponse createTestUserResponse() {
        return UserResponse.builder()
                .id(1L)
                .username("testuser")
                .name("테스트 사용자")
                .email("test@example.com")
                .roleId(3L)
                .roleCode("USER")
                .roleName("사용자")
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("사용자 목록 조회 API")
    class GetUsersTest {

        @Test
        @DisplayName("검색 조건 없이 목록 조회 성공")
        void getUsers_success() throws Exception {
            // given
            UserResponse user = createTestUserResponse();
            PageResponse<UserResponse> pageResponse = PageResponse.of(
                    Arrays.asList(user), 1, 10, 1L);

            given(userService.getUsers(any(UserSearchRequest.class))).willReturn(pageResponse);

            // when & then
            mockMvc.perform(get("/api/v1/users")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(jsonPath("$.data.content[0].username").value("testuser"));
        }

        @Test
        @DisplayName("검색 조건으로 목록 조회 성공")
        void getUsers_withSearchParams() throws Exception {
            // given
            PageResponse<UserResponse> pageResponse = PageResponse.of(
                    Collections.emptyList(), 1, 10, 0L);

            given(userService.getUsers(any(UserSearchRequest.class))).willReturn(pageResponse);

            // when & then
            mockMvc.perform(get("/api/v1/users")
                            .param("keyword", "test")
                            .param("status", "ACTIVE")
                            .param("page", "1")
                            .param("size", "10")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }
    }

    @Nested
    @DisplayName("사용자 상세 조회 API")
    class GetUserTest {

        @Test
        @DisplayName("사용자 상세 조회 성공")
        void getUser_success() throws Exception {
            // given
            UserResponse user = createTestUserResponse();
            given(userService.getUser(1L)).willReturn(user);

            // when & then
            mockMvc.perform(get("/api/v1/users/1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.id").value(1))
                    .andExpect(jsonPath("$.data.username").value("testuser"));
        }

        @Test
        @DisplayName("존재하지 않는 사용자 조회 시 404 반환")
        void getUser_notFound() throws Exception {
            // given
            given(userService.getUser(999L))
                    .willThrow(new BusinessException(ErrorCode.USER_NOT_FOUND));

            // when & then
            mockMvc.perform(get("/api/v1/users/999")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }

    @Nested
    @DisplayName("아이디/이메일 중복 확인 API")
    class DuplicateCheckTest {

        @Test
        @DisplayName("아이디 중복 - 중복됨")
        void checkUsername_duplicated() throws Exception {
            // given
            given(userService.isUsernameDuplicated("existing")).willReturn(true);

            // when & then
            mockMvc.perform(get("/api/v1/users/check/username")
                            .param("username", "existing")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").value(true));
        }

        @Test
        @DisplayName("아이디 중복 - 사용 가능")
        void checkUsername_available() throws Exception {
            // given
            given(userService.isUsernameDuplicated("newuser")).willReturn(false);

            // when & then
            mockMvc.perform(get("/api/v1/users/check/username")
                            .param("username", "newuser")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").value(false));
        }

        @Test
        @DisplayName("이메일 중복 확인")
        void checkEmail() throws Exception {
            // given
            given(userService.isEmailDuplicated("test@email.com")).willReturn(false);

            // when & then
            mockMvc.perform(get("/api/v1/users/check/email")
                            .param("email", "test@email.com")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }
    }
}
