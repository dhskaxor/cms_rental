package com.nt.cms.role.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * RoleVO 테스트
 */
@DisplayName("RoleVO 테스트")
class RoleVOTest {

    @Test
    @DisplayName("Builder 패턴으로 객체 생성")
    void testBuilder() {
        // given
        Long id = 1L;
        String roleCode = "ADMIN";
        String roleName = "관리자";
        String description = "시스템 관리자";

        // when
        RoleVO role = RoleVO.builder()
                .id(id)
                .roleCode(roleCode)
                .roleName(roleName)
                .description(description)
                .build();

        // then
        assertThat(role.getId()).isEqualTo(id);
        assertThat(role.getRoleCode()).isEqualTo(roleCode);
        assertThat(role.getRoleName()).isEqualTo(roleName);
        assertThat(role.getDescription()).isEqualTo(description);
    }

    @Test
    @DisplayName("권한 목록 포함")
    void testWithPermissions() {
        // given
        PermissionVO perm1 = PermissionVO.builder()
                .id(1L)
                .permissionCode("USER_READ")
                .permissionName("사용자 조회")
                .build();

        PermissionVO perm2 = PermissionVO.builder()
                .id(2L)
                .permissionCode("USER_CREATE")
                .permissionName("사용자 생성")
                .build();

        List<PermissionVO> permissions = Arrays.asList(perm1, perm2);

        // when
        RoleVO role = RoleVO.builder()
                .id(1L)
                .roleCode("ADMIN")
                .roleName("관리자")
                .permissions(permissions)
                .build();

        // then
        assertThat(role.getPermissions()).hasSize(2);
        assertThat(role.getPermissions().get(0).getPermissionCode()).isEqualTo("USER_READ");
    }

    @Test
    @DisplayName("사용자 수 포함")
    void testWithUserCount() {
        // given & when
        RoleVO role = RoleVO.builder()
                .id(1L)
                .roleCode("USER")
                .roleName("사용자")
                .userCount(50L)
                .build();

        // then
        assertThat(role.getUserCount()).isEqualTo(50L);
    }
}
