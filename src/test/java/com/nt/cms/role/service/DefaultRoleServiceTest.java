package com.nt.cms.role.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nt.cms.audit.service.AuditLogService;
import com.nt.cms.common.exception.BusinessException;
import com.nt.cms.common.exception.ErrorCode;
import com.nt.cms.role.dto.*;
import com.nt.cms.role.mapper.RoleMapper;
import com.nt.cms.user.mapper.UserMapper;
import com.nt.cms.role.vo.PermissionVO;
import com.nt.cms.role.vo.RoleVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * DefaultRoleService 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DefaultRoleService 테스트")
class DefaultRoleServiceTest {

    @Mock
    private RoleMapper roleMapper;

    @Mock
    private AuditLogService auditLogService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private DefaultRoleService roleService;

    private RoleVO testRole;

    @BeforeEach
    void setUp() {
        PermissionVO perm = PermissionVO.builder()
                .id(1L)
                .permissionCode("USER_READ")
                .permissionName("사용자 조회")
                .build();

        testRole = RoleVO.builder()
                .id(1L)
                .roleCode("ADMIN")
                .roleName("관리자")
                .description("시스템 관리자")
                .permissions(Arrays.asList(perm))
                .userCount(5L)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("역할 목록 조회")
    class GetRolesTest {

        @Test
        @DisplayName("역할 목록 조회 성공")
        void getRoles_success() {
            // given
            given(roleMapper.findAll()).willReturn(Arrays.asList(testRole));

            // when
            List<RoleResponse> response = roleService.getRoles();

            // then
            assertThat(response).hasSize(1);
            assertThat(response.get(0).getRoleCode()).isEqualTo("ADMIN");
            verify(roleMapper).findAll();
        }

        @Test
        @DisplayName("역할이 없을 때 빈 목록 반환")
        void getRoles_empty() {
            // given
            given(roleMapper.findAll()).willReturn(Collections.emptyList());

            // when
            List<RoleResponse> response = roleService.getRoles();

            // then
            assertThat(response).isEmpty();
        }
    }

    @Nested
    @DisplayName("역할 상세 조회")
    class GetRoleTest {

        @Test
        @DisplayName("ID로 역할 조회 성공")
        void getRole_success() {
            // given
            given(roleMapper.findById(1L)).willReturn(testRole);

            // when
            RoleResponse response = roleService.getRole(1L);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getRoleCode()).isEqualTo("ADMIN");
        }

        @Test
        @DisplayName("존재하지 않는 역할 조회 시 예외 발생")
        void getRole_notFound() {
            // given
            given(roleMapper.findById(999L)).willReturn(null);

            // when & then
            assertThatThrownBy(() -> roleService.getRole(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ROLE_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("역할 생성")
    class CreateRoleTest {

        @Test
        @DisplayName("역할 생성 성공")
        void createRole_success() {
            // given
            RoleCreateRequest request = RoleCreateRequest.builder()
                    .roleCode("MANAGER")
                    .roleName("매니저")
                    .description("매니저 역할")
                    .permissionIds(Arrays.asList(1L, 2L))
                    .build();

            given(roleMapper.existsByRoleCode("MANAGER")).willReturn(false);
            given(roleMapper.insert(any(RoleVO.class))).willAnswer(invocation -> {
                RoleVO role = invocation.getArgument(0);
                role.setId(2L);
                return 1;
            });
            given(roleMapper.findById(2L)).willReturn(RoleVO.builder()
                    .id(2L)
                    .roleCode("MANAGER")
                    .roleName("매니저")
                    .description("매니저 역할")
                    .build());

            // when
            RoleResponse response = roleService.createRole(request, 1L);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getRoleCode()).isEqualTo("MANAGER");
            verify(roleMapper).insert(any(RoleVO.class));
            verify(roleMapper, times(2)).insertRolePermission(anyLong(), anyLong());
        }

        @Test
        @DisplayName("중복된 역할 코드일 때 예외 발생")
        void createRole_duplicateRoleCode() {
            // given
            RoleCreateRequest request = RoleCreateRequest.builder()
                    .roleCode("ADMIN")
                    .roleName("관리자")
                    .build();

            given(roleMapper.existsByRoleCode("ADMIN")).willReturn(true);

            // when & then
            assertThatThrownBy(() -> roleService.createRole(request, 1L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ROLE_ALREADY_EXISTS);
        }
    }

    @Nested
    @DisplayName("역할 수정")
    class UpdateRoleTest {

        @Test
        @DisplayName("역할 수정 성공")
        void updateRole_success() {
            // given
            RoleUpdateRequest request = RoleUpdateRequest.builder()
                    .roleName("수정된 역할명")
                    .description("수정된 설명")
                    .permissionIds(Arrays.asList(1L))
                    .build();

            given(roleMapper.findById(1L)).willReturn(testRole);
            given(roleMapper.update(any(RoleVO.class))).willReturn(1);

            RoleVO updatedRole = RoleVO.builder()
                    .id(1L)
                    .roleCode("ADMIN")
                    .roleName("수정된 역할명")
                    .description("수정된 설명")
                    .build();
            given(roleMapper.findById(1L)).willReturn(testRole).willReturn(updatedRole);

            // when
            RoleResponse response = roleService.updateRole(1L, request, 1L);

            // then
            assertThat(response).isNotNull();
            verify(roleMapper).update(any(RoleVO.class));
            verify(roleMapper).deleteRolePermissions(1L);
            verify(roleMapper).insertRolePermission(1L, 1L);
        }

        @Test
        @DisplayName("존재하지 않는 역할 수정 시 예외 발생")
        void updateRole_notFound() {
            // given
            RoleUpdateRequest request = RoleUpdateRequest.builder()
                    .roleName("역할명")
                    .build();

            given(roleMapper.findById(999L)).willReturn(null);

            // when & then
            assertThatThrownBy(() -> roleService.updateRole(999L, request, 1L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ROLE_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("역할 삭제")
    class DeleteRoleTest {

        @Test
        @DisplayName("역할 삭제 성공")
        void deleteRole_success() {
            // given
            RoleVO roleWithNoUsers = RoleVO.builder()
                    .id(1L)
                    .roleCode("TEST")
                    .roleName("테스트")
                    .userCount(0L)
                    .build();

            given(roleMapper.findById(1L)).willReturn(roleWithNoUsers);
            given(roleMapper.delete(1L, 1L)).willReturn(1);

            // when
            roleService.deleteRole(1L, 1L);

            // then
            verify(roleMapper).deleteRolePermissions(1L);
            verify(roleMapper).delete(1L, 1L);
        }

        @Test
        @DisplayName("존재하지 않는 역할 삭제 시 예외 발생")
        void deleteRole_notFound() {
            // given
            given(roleMapper.findById(999L)).willReturn(null);

            // when & then
            assertThatThrownBy(() -> roleService.deleteRole(999L, 1L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ROLE_NOT_FOUND);
        }

        @Test
        @DisplayName("사용자가 할당된 역할 삭제 시 예외 발생")
        void deleteRole_inUse() {
            // given
            given(roleMapper.findById(1L)).willReturn(testRole);

            // when & then
            assertThatThrownBy(() -> roleService.deleteRole(1L, 1L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ROLE_IN_USE);
        }
    }

    @Nested
    @DisplayName("역할 코드 중복 확인")
    class DuplicateCheckTest {

        @Test
        @DisplayName("역할 코드 중복 - true")
        void isRoleCodeDuplicated_true() {
            // given
            given(roleMapper.existsByRoleCode("ADMIN")).willReturn(true);

            // when
            boolean result = roleService.isRoleCodeDuplicated("ADMIN");

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("역할 코드 중복 아님 - false")
        void isRoleCodeDuplicated_false() {
            // given
            given(roleMapper.existsByRoleCode("NEW_ROLE")).willReturn(false);

            // when
            boolean result = roleService.isRoleCodeDuplicated("NEW_ROLE");

            // then
            assertThat(result).isFalse();
        }
    }
}
