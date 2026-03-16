package com.nt.cms.auth.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UserAuthVO 테스트
 * 
 * @author CMS Team
 */
class UserAuthVOTest {

    @Nested
    @DisplayName("계정 상태 테스트")
    class AccountStatusTest {

        @Test
        @DisplayName("ACTIVE 상태의 계정은 isActive()가 true를 반환해야 한다")
        void isActive_active() {
            // given
            UserAuthVO user = UserAuthVO.builder()
                    .id(1L)
                    .username("testuser")
                    .status("ACTIVE")
                    .deleted(false)
                    .build();

            // then
            assertTrue(user.isActive());
        }

        @Test
        @DisplayName("LOCKED 상태의 계정은 isActive()가 false를 반환해야 한다")
        void isActive_locked() {
            // given
            UserAuthVO user = UserAuthVO.builder()
                    .id(1L)
                    .username("testuser")
                    .status("LOCKED")
                    .deleted(false)
                    .build();

            // then
            assertFalse(user.isActive());
        }

        @Test
        @DisplayName("삭제된 계정은 isActive()가 false를 반환해야 한다")
        void isActive_deleted() {
            // given
            UserAuthVO user = UserAuthVO.builder()
                    .id(1L)
                    .username("testuser")
                    .status("ACTIVE")
                    .deleted(true)
                    .build();

            // then
            assertFalse(user.isActive());
        }

        @Test
        @DisplayName("LOCKED 상태의 계정은 isLocked()가 true를 반환해야 한다")
        void isLocked_locked() {
            // given
            UserAuthVO user = UserAuthVO.builder()
                    .id(1L)
                    .username("testuser")
                    .status("LOCKED")
                    .build();

            // then
            assertTrue(user.isLocked());
        }

        @Test
        @DisplayName("ACTIVE 상태의 계정은 isLocked()가 false를 반환해야 한다")
        void isLocked_active() {
            // given
            UserAuthVO user = UserAuthVO.builder()
                    .id(1L)
                    .username("testuser")
                    .status("ACTIVE")
                    .build();

            // then
            assertFalse(user.isLocked());
        }
    }

    @Nested
    @DisplayName("Builder 테스트")
    class BuilderTest {

        @Test
        @DisplayName("모든 필드가 올바르게 설정되어야 한다")
        void builder_allFields() {
            // given
            LocalDateTime lastLoginAt = LocalDateTime.now();

            // when
            UserAuthVO user = UserAuthVO.builder()
                    .id(1L)
                    .username("testuser")
                    .password("encodedPassword")
                    .name("테스트유저")
                    .email("test@test.com")
                    .roleId(2L)
                    .roleCode("USER")
                    .status("ACTIVE")
                    .lastLoginAt(lastLoginAt)
                    .deleted(false)
                    .permissions(Arrays.asList("USER_READ", "USER_CREATE"))
                    .build();

            // then
            assertEquals(1L, user.getId());
            assertEquals("testuser", user.getUsername());
            assertEquals("encodedPassword", user.getPassword());
            assertEquals("테스트유저", user.getName());
            assertEquals("test@test.com", user.getEmail());
            assertEquals(2L, user.getRoleId());
            assertEquals("USER", user.getRoleCode());
            assertEquals("ACTIVE", user.getStatus());
            assertEquals(lastLoginAt, user.getLastLoginAt());
            assertFalse(user.getDeleted());
            assertEquals(2, user.getPermissions().size());
            assertTrue(user.getPermissions().contains("USER_READ"));
        }

        @Test
        @DisplayName("권한 목록이 null이어도 정상 동작해야 한다")
        void builder_nullPermissions() {
            // when
            UserAuthVO user = UserAuthVO.builder()
                    .id(1L)
                    .username("testuser")
                    .status("ACTIVE")
                    .permissions(null)
                    .build();

            // then
            assertNull(user.getPermissions());
            assertTrue(user.isActive());
        }
    }

    @Nested
    @DisplayName("Edge Case 테스트")
    class EdgeCaseTest {

        @Test
        @DisplayName("status가 null이면 isActive()가 false를 반환해야 한다")
        void isActive_nullStatus() {
            // given
            UserAuthVO user = UserAuthVO.builder()
                    .id(1L)
                    .username("testuser")
                    .status(null)
                    .deleted(false)
                    .build();

            // then
            assertFalse(user.isActive());
        }

        @Test
        @DisplayName("status가 null이면 isLocked()가 false를 반환해야 한다")
        void isLocked_nullStatus() {
            // given
            UserAuthVO user = UserAuthVO.builder()
                    .id(1L)
                    .username("testuser")
                    .status(null)
                    .build();

            // then
            assertFalse(user.isLocked());
        }

        @Test
        @DisplayName("deleted가 null이면 isActive()가 true를 반환해야 한다")
        void isActive_nullDeleted() {
            // given
            UserAuthVO user = UserAuthVO.builder()
                    .id(1L)
                    .username("testuser")
                    .status("ACTIVE")
                    .deleted(null)
                    .build();

            // then
            assertTrue(user.isActive());
        }
    }
}
