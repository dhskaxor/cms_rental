package com.nt.cms.user.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UserVO 테스트
 */
@DisplayName("UserVO 테스트")
class UserVOTest {

    @Test
    @DisplayName("Builder 패턴으로 객체 생성")
    void testBuilder() {
        // given
        Long id = 1L;
        String username = "testuser";
        String password = "encoded_password";
        String name = "테스트 사용자";
        String email = "test@example.com";
        Long roleId = 2L;
        String roleCode = "USER";
        String roleName = "사용자";
        String status = "ACTIVE";
        LocalDateTime lastLoginAt = LocalDateTime.now();

        // when
        UserVO user = UserVO.builder()
                .id(id)
                .username(username)
                .password(password)
                .name(name)
                .email(email)
                .roleId(roleId)
                .roleCode(roleCode)
                .roleName(roleName)
                .status(status)
                .lastLoginAt(lastLoginAt)
                .build();

        // then
        assertThat(user.getId()).isEqualTo(id);
        assertThat(user.getUsername()).isEqualTo(username);
        assertThat(user.getPassword()).isEqualTo(password);
        assertThat(user.getName()).isEqualTo(name);
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getRoleId()).isEqualTo(roleId);
        assertThat(user.getRoleCode()).isEqualTo(roleCode);
        assertThat(user.getRoleName()).isEqualTo(roleName);
        assertThat(user.getStatus()).isEqualTo(status);
        assertThat(user.getLastLoginAt()).isEqualTo(lastLoginAt);
    }

    @Test
    @DisplayName("isActive - ACTIVE 상태일 때 true 반환")
    void testIsActive_whenActive() {
        // given
        UserVO user = UserVO.builder()
                .status("ACTIVE")
                .build();

        // when & then
        assertThat(user.isActive()).isTrue();
        assertThat(user.isLocked()).isFalse();
    }

    @Test
    @DisplayName("isLocked - LOCKED 상태일 때 true 반환")
    void testIsLocked_whenLocked() {
        // given
        UserVO user = UserVO.builder()
                .status("LOCKED")
                .build();

        // when & then
        assertThat(user.isLocked()).isTrue();
        assertThat(user.isActive()).isFalse();
    }

    @Test
    @DisplayName("상태가 null일 때 isActive, isLocked 모두 false")
    void testStatusNull() {
        // given
        UserVO user = UserVO.builder().build();

        // when & then
        assertThat(user.isActive()).isFalse();
        assertThat(user.isLocked()).isFalse();
    }

    @Test
    @DisplayName("BaseVO 상속 확인")
    void testBaseVOInheritance() {
        // given
        LocalDateTime now = LocalDateTime.now();
        Long createdBy = 1L;
        Long updatedBy = 2L;

        // when
        UserVO user = UserVO.builder()
                .id(1L)
                .createdAt(now)
                .createdBy(createdBy)
                .updatedAt(now)
                .updatedBy(updatedBy)
                .deleted(false)
                .build();

        // then
        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getCreatedAt()).isEqualTo(now);
        assertThat(user.getCreatedBy()).isEqualTo(createdBy);
        assertThat(user.getUpdatedAt()).isEqualTo(now);
        assertThat(user.getUpdatedBy()).isEqualTo(updatedBy);
        assertThat(user.isDeleted()).isFalse();
    }
}
