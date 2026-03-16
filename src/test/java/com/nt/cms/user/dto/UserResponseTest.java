package com.nt.cms.user.dto;

import com.nt.cms.user.vo.UserVO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UserResponse 테스트
 */
@DisplayName("UserResponse 테스트")
class UserResponseTest {

    @Test
    @DisplayName("UserVO에서 UserResponse 생성")
    void testFromUserVO() {
        // given
        LocalDateTime now = LocalDateTime.now();
        UserVO userVO = UserVO.builder()
                .id(1L)
                .username("testuser")
                .password("encoded_password")
                .name("테스트 사용자")
                .email("test@example.com")
                .roleId(2L)
                .roleCode("USER")
                .roleName("사용자")
                .status("ACTIVE")
                .lastLoginAt(now)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // when
        UserResponse response = UserResponse.from(userVO);

        // then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUsername()).isEqualTo("testuser");
        assertThat(response.getName()).isEqualTo("테스트 사용자");
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        assertThat(response.getRoleId()).isEqualTo(2L);
        assertThat(response.getRoleCode()).isEqualTo("USER");
        assertThat(response.getRoleName()).isEqualTo("사용자");
        assertThat(response.getStatus()).isEqualTo("ACTIVE");
        assertThat(response.getLastLoginAt()).isEqualTo(now);
        assertThat(response.getCreatedAt()).isEqualTo(now);
        assertThat(response.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("null UserVO에서 null 반환")
    void testFromNull() {
        // when
        UserResponse response = UserResponse.from(null);

        // then
        assertThat(response).isNull();
    }

    @Test
    @DisplayName("비밀번호 필드가 Response에 포함되지 않음")
    void testPasswordNotIncluded() {
        // given
        UserVO userVO = UserVO.builder()
                .id(1L)
                .username("testuser")
                .password("secret_password")
                .build();

        // when
        UserResponse response = UserResponse.from(userVO);

        // then
        // UserResponse에는 password 필드가 없음을 확인
        // (컴파일 레벨에서 확인됨 - password getter가 없음)
        assertThat(response).isNotNull();
        assertThat(response.getUsername()).isEqualTo("testuser");
    }
}
