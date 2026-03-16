package com.nt.cms.auth.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CustomUserDetails 테스트
 * 
 * @author CMS Team
 */
class CustomUserDetailsTest {

    @Nested
    @DisplayName("Builder 테스트")
    class BuilderTest {

        @Test
        @DisplayName("모든 필드가 올바르게 설정되어야 한다")
        void builder_allFields() {
            // given
            Collection<GrantedAuthority> authorities = Arrays.asList(
                    new SimpleGrantedAuthority("ROLE_USER"),
                    new SimpleGrantedAuthority("USER_READ")
            );

            // when
            CustomUserDetails userDetails = CustomUserDetails.builder()
                    .userId(1L)
                    .username("testuser")
                    .password("password123")
                    .name("테스트유저")
                    .email("test@test.com")
                    .roleCode("USER")
                    .authorities(authorities)
                    .enabled(true)
                    .accountNonExpired(true)
                    .accountNonLocked(true)
                    .credentialsNonExpired(true)
                    .build();

            // then
            assertEquals(1L, userDetails.getUserId());
            assertEquals("testuser", userDetails.getUsername());
            assertEquals("password123", userDetails.getPassword());
            assertEquals("테스트유저", userDetails.getName());
            assertEquals("test@test.com", userDetails.getEmail());
            assertEquals("USER", userDetails.getRoleCode());
            assertEquals(2, userDetails.getAuthorities().size());
        }

        @Test
        @DisplayName("기본값이 올바르게 설정되어야 한다")
        void builder_defaultValues() {
            // when
            CustomUserDetails userDetails = CustomUserDetails.builder()
                    .userId(1L)
                    .username("testuser")
                    .password("password")
                    .roleCode("USER")
                    .authorities(Collections.emptyList())
                    .build();

            // then
            assertTrue(userDetails.isEnabled());
            assertTrue(userDetails.isAccountNonExpired());
            assertTrue(userDetails.isAccountNonLocked());
            assertTrue(userDetails.isCredentialsNonExpired());
        }
    }

    @Nested
    @DisplayName("UserDetails 인터페이스 테스트")
    class UserDetailsInterfaceTest {

        private CustomUserDetails createUserDetails(boolean enabled, boolean accountNonLocked) {
            return CustomUserDetails.builder()
                    .userId(1L)
                    .username("testuser")
                    .password("password")
                    .roleCode("USER")
                    .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                    .enabled(enabled)
                    .accountNonLocked(accountNonLocked)
                    .build();
        }

        @Test
        @DisplayName("활성화된 계정은 isEnabled()가 true를 반환해야 한다")
        void isEnabled_true() {
            // given
            CustomUserDetails userDetails = createUserDetails(true, true);

            // then
            assertTrue(userDetails.isEnabled());
        }

        @Test
        @DisplayName("비활성화된 계정은 isEnabled()가 false를 반환해야 한다")
        void isEnabled_false() {
            // given
            CustomUserDetails userDetails = createUserDetails(false, true);

            // then
            assertFalse(userDetails.isEnabled());
        }

        @Test
        @DisplayName("잠금 해제된 계정은 isAccountNonLocked()가 true를 반환해야 한다")
        void isAccountNonLocked_true() {
            // given
            CustomUserDetails userDetails = createUserDetails(true, true);

            // then
            assertTrue(userDetails.isAccountNonLocked());
        }

        @Test
        @DisplayName("잠금된 계정은 isAccountNonLocked()가 false를 반환해야 한다")
        void isAccountNonLocked_false() {
            // given
            CustomUserDetails userDetails = createUserDetails(true, false);

            // then
            assertFalse(userDetails.isAccountNonLocked());
        }

        @Test
        @DisplayName("getAuthorities()가 권한 목록을 반환해야 한다")
        void getAuthorities() {
            // given
            Collection<GrantedAuthority> authorities = Arrays.asList(
                    new SimpleGrantedAuthority("ROLE_ADMIN"),
                    new SimpleGrantedAuthority("USER_CREATE"),
                    new SimpleGrantedAuthority("USER_DELETE")
            );

            CustomUserDetails userDetails = CustomUserDetails.builder()
                    .userId(1L)
                    .username("admin")
                    .password("password")
                    .roleCode("ADMIN")
                    .authorities(authorities)
                    .build();

            // then
            assertEquals(3, userDetails.getAuthorities().size());
            assertTrue(userDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
            assertTrue(userDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("USER_CREATE")));
        }
    }

    @Nested
    @DisplayName("추가 필드 테스트")
    class AdditionalFieldsTest {

        @Test
        @DisplayName("userId가 올바르게 반환되어야 한다")
        void getUserId() {
            // given
            CustomUserDetails userDetails = CustomUserDetails.builder()
                    .userId(123L)
                    .username("testuser")
                    .password("password")
                    .roleCode("USER")
                    .authorities(Collections.emptyList())
                    .build();

            // then
            assertEquals(123L, userDetails.getUserId());
        }

        @Test
        @DisplayName("roleCode가 올바르게 반환되어야 한다")
        void getRoleCode() {
            // given
            CustomUserDetails userDetails = CustomUserDetails.builder()
                    .userId(1L)
                    .username("testuser")
                    .password("password")
                    .roleCode("MANAGER")
                    .authorities(Collections.emptyList())
                    .build();

            // then
            assertEquals("MANAGER", userDetails.getRoleCode());
        }

        @Test
        @DisplayName("name과 email이 올바르게 반환되어야 한다")
        void getNameAndEmail() {
            // given
            CustomUserDetails userDetails = CustomUserDetails.builder()
                    .userId(1L)
                    .username("testuser")
                    .password("password")
                    .name("홍길동")
                    .email("hong@test.com")
                    .roleCode("USER")
                    .authorities(Collections.emptyList())
                    .build();

            // then
            assertEquals("홍길동", userDetails.getName());
            assertEquals("hong@test.com", userDetails.getEmail());
        }
    }
}
