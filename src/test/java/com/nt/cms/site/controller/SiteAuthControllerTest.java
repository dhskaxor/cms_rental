package com.nt.cms.site.controller;

import com.nt.cms.auth.security.CustomUserDetails;
import com.nt.cms.auth.security.CustomUserDetailsService;
import com.nt.cms.common.constant.SessionConstants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * SiteAuthController 단위 테스트
 *
 * @author CMS Team
 */
@WebMvcTest(controllers = SiteAuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class SiteAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @MockBean
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Nested
    @DisplayName("GET /site/auth/login")
    class LoginPage {

        @Test
        @DisplayName("로그인 페이지 표시")
        void loginPage_success() throws Exception {
            mockMvc.perform(get("/site/auth/login"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("site/auth/login"));
        }
    }

    @Nested
    @DisplayName("POST /site/auth/login")
    class Login {

        @Test
        @DisplayName("올바른 자격증명으로 로그인 성공 시 /site/ 리다이렉트")
        void login_success() throws Exception {
            var userDetails = CustomUserDetails.builder()
                    .userId(1L)
                    .username("user1")
                    .password("$2a$10$encoded")
                    .name("테스트")
                    .email("test@test.com")
                    .roleCode("USER")
                    .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                    .accountNonLocked(true)
                    .build();

            when(userDetailsService.loadUserByUsername("user1")).thenReturn(userDetails);
            when(passwordEncoder.matches("password1", userDetails.getPassword())).thenReturn(true);

            mockMvc.perform(post("/site/auth/login")
                            .with(csrf())
                            .param("username", "user1")
                            .param("password", "password1"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/site/"))
                    .andExpect(request().sessionAttribute(SessionConstants.SITE_CURRENT_USER, org.hamcrest.Matchers.notNullValue()));
        }

        @Test
        @DisplayName("잘못된 비밀번호 시 로그인 페이지로 리다이렉트")
        void login_invalidPassword() throws Exception {
            var userDetails = CustomUserDetails.builder()
                    .userId(1L)
                    .username("user1")
                    .password("$2a$10$encoded")
                    .name("테스트")
                    .email("test@test.com")
                    .roleCode("USER")
                    .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                    .accountNonLocked(true)
                    .build();

            when(userDetailsService.loadUserByUsername("user1")).thenReturn(userDetails);
            when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

            mockMvc.perform(post("/site/auth/login")
                            .with(csrf())
                            .param("username", "user1")
                            .param("password", "wrong"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/site/auth/login"));
        }
    }
}
