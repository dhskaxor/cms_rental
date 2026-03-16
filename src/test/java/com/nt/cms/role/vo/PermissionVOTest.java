package com.nt.cms.role.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * PermissionVO 테스트
 */
@DisplayName("PermissionVO 테스트")
class PermissionVOTest {

    @Test
    @DisplayName("Builder 패턴으로 객체 생성")
    void testBuilder() {
        // given
        Long id = 1L;
        String permissionCode = "USER_CREATE";
        String permissionName = "사용자 생성";
        String description = "사용자를 생성할 수 있는 권한";
        LocalDateTime now = LocalDateTime.now();

        // when
        PermissionVO permission = PermissionVO.builder()
                .id(id)
                .permissionCode(permissionCode)
                .permissionName(permissionName)
                .description(description)
                .createdAt(now)
                .build();

        // then
        assertThat(permission.getId()).isEqualTo(id);
        assertThat(permission.getPermissionCode()).isEqualTo(permissionCode);
        assertThat(permission.getPermissionName()).isEqualTo(permissionName);
        assertThat(permission.getDescription()).isEqualTo(description);
        assertThat(permission.getCreatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("BaseVO 상속 확인")
    void testBaseVOInheritance() {
        // given
        Long createdBy = 1L;

        // when
        PermissionVO permission = PermissionVO.builder()
                .id(1L)
                .createdBy(createdBy)
                .deleted(false)
                .build();

        // then
        assertThat(permission.getCreatedBy()).isEqualTo(createdBy);
        assertThat(permission.isDeleted()).isFalse();
    }
}
