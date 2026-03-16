package com.nt.cms.role.dto;

import com.nt.cms.role.vo.PermissionVO;
import com.nt.cms.role.vo.RoleVO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * RoleResponse 테스트
 */
@DisplayName("RoleResponse 테스트")
class RoleResponseTest {

    @Test
    @DisplayName("RoleVO에서 RoleResponse 생성")
    void testFromRoleVO() {
        // given
        LocalDateTime now = LocalDateTime.now();
        PermissionVO perm = PermissionVO.builder()
                .id(1L)
                .permissionCode("USER_READ")
                .permissionName("사용자 조회")
                .description("사용자 조회 권한")
                .build();

        RoleVO roleVO = RoleVO.builder()
                .id(1L)
                .roleCode("ADMIN")
                .roleName("관리자")
                .description("시스템 관리자")
                .permissions(Arrays.asList(perm))
                .userCount(5L)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // when
        RoleResponse response = RoleResponse.from(roleVO);

        // then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getRoleCode()).isEqualTo("ADMIN");
        assertThat(response.getRoleName()).isEqualTo("관리자");
        assertThat(response.getDescription()).isEqualTo("시스템 관리자");
        assertThat(response.getUserCount()).isEqualTo(5L);
        assertThat(response.getPermissions()).hasSize(1);
        assertThat(response.getPermissions().get(0).getPermissionCode()).isEqualTo("USER_READ");
    }

    @Test
    @DisplayName("null RoleVO에서 null 반환")
    void testFromNull() {
        // when
        RoleResponse response = RoleResponse.from(null);

        // then
        assertThat(response).isNull();
    }

    @Test
    @DisplayName("권한이 없는 역할")
    void testWithoutPermissions() {
        // given
        RoleVO roleVO = RoleVO.builder()
                .id(1L)
                .roleCode("USER")
                .roleName("사용자")
                .build();

        // when
        RoleResponse response = RoleResponse.from(roleVO);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getPermissions()).isNull();
    }
}
