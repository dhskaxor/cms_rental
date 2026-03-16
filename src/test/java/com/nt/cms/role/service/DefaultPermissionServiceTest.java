package com.nt.cms.role.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nt.cms.audit.service.AuditLogService;
import com.nt.cms.common.exception.BusinessException;
import com.nt.cms.common.exception.ErrorCode;
import com.nt.cms.role.dto.*;
import com.nt.cms.role.mapper.PermissionMapper;
import com.nt.cms.user.mapper.UserMapper;
import com.nt.cms.role.vo.PermissionVO;
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
 * DefaultPermissionService 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DefaultPermissionService 테스트")
class DefaultPermissionServiceTest {

    @Mock
    private PermissionMapper permissionMapper;

    @Mock
    private AuditLogService auditLogService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private DefaultPermissionService permissionService;

    private PermissionVO testPermission;

    @BeforeEach
    void setUp() {
        testPermission = PermissionVO.builder()
                .id(1L)
                .permissionCode("USER_READ")
                .permissionName("사용자 조회")
                .description("사용자 조회 권한")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("권한 목록 조회")
    class GetPermissionsTest {

        @Test
        @DisplayName("권한 목록 조회 성공")
        void getPermissions_success() {
            // given
            given(permissionMapper.findAll()).willReturn(Arrays.asList(testPermission));

            // when
            List<PermissionResponse> response = permissionService.getPermissions();

            // then
            assertThat(response).hasSize(1);
            assertThat(response.get(0).getPermissionCode()).isEqualTo("USER_READ");
        }

        @Test
        @DisplayName("권한이 없을 때 빈 목록 반환")
        void getPermissions_empty() {
            // given
            given(permissionMapper.findAll()).willReturn(Collections.emptyList());

            // when
            List<PermissionResponse> response = permissionService.getPermissions();

            // then
            assertThat(response).isEmpty();
        }
    }

    @Nested
    @DisplayName("권한 상세 조회")
    class GetPermissionTest {

        @Test
        @DisplayName("ID로 권한 조회 성공")
        void getPermission_success() {
            // given
            given(permissionMapper.findById(1L)).willReturn(testPermission);

            // when
            PermissionResponse response = permissionService.getPermission(1L);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getPermissionCode()).isEqualTo("USER_READ");
        }

        @Test
        @DisplayName("존재하지 않는 권한 조회 시 예외 발생")
        void getPermission_notFound() {
            // given
            given(permissionMapper.findById(999L)).willReturn(null);

            // when & then
            assertThatThrownBy(() -> permissionService.getPermission(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PERMISSION_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("역할별 권한 조회")
    class GetPermissionsByRoleIdTest {

        @Test
        @DisplayName("역할별 권한 목록 조회 성공")
        void getPermissionsByRoleId_success() {
            // given
            given(permissionMapper.findByRoleId(1L)).willReturn(Arrays.asList(testPermission));

            // when
            List<PermissionResponse> response = permissionService.getPermissionsByRoleId(1L);

            // then
            assertThat(response).hasSize(1);
        }
    }

    @Nested
    @DisplayName("권한 생성")
    class CreatePermissionTest {

        @Test
        @DisplayName("권한 생성 성공")
        void createPermission_success() {
            // given
            PermissionCreateRequest request = PermissionCreateRequest.builder()
                    .permissionCode("NEW_PERM")
                    .permissionName("새 권한")
                    .description("새 권한 설명")
                    .build();

            given(permissionMapper.existsByPermissionCode("NEW_PERM")).willReturn(false);
            given(permissionMapper.insert(any(PermissionVO.class))).willAnswer(invocation -> {
                PermissionVO perm = invocation.getArgument(0);
                perm.setId(2L);
                return 1;
            });
            given(permissionMapper.findById(2L)).willReturn(PermissionVO.builder()
                    .id(2L)
                    .permissionCode("NEW_PERM")
                    .permissionName("새 권한")
                    .build());

            // when
            PermissionResponse response = permissionService.createPermission(request, 1L);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getPermissionCode()).isEqualTo("NEW_PERM");
        }

        @Test
        @DisplayName("중복된 권한 코드일 때 예외 발생")
        void createPermission_duplicate() {
            // given
            PermissionCreateRequest request = PermissionCreateRequest.builder()
                    .permissionCode("USER_READ")
                    .permissionName("사용자 조회")
                    .build();

            given(permissionMapper.existsByPermissionCode("USER_READ")).willReturn(true);

            // when & then
            assertThatThrownBy(() -> permissionService.createPermission(request, 1L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PERMISSION_ALREADY_EXISTS);
        }
    }

    @Nested
    @DisplayName("권한 수정")
    class UpdatePermissionTest {

        @Test
        @DisplayName("권한 수정 성공")
        void updatePermission_success() {
            // given
            PermissionUpdateRequest request = PermissionUpdateRequest.builder()
                    .permissionName("수정된 권한명")
                    .description("수정된 설명")
                    .build();

            given(permissionMapper.findById(1L)).willReturn(testPermission);
            given(permissionMapper.update(any(PermissionVO.class))).willReturn(1);

            PermissionVO updatedPerm = PermissionVO.builder()
                    .id(1L)
                    .permissionCode("USER_READ")
                    .permissionName("수정된 권한명")
                    .description("수정된 설명")
                    .build();
            given(permissionMapper.findById(1L)).willReturn(testPermission).willReturn(updatedPerm);

            // when
            PermissionResponse response = permissionService.updatePermission(1L, request, 1L);

            // then
            assertThat(response).isNotNull();
            verify(permissionMapper).update(any(PermissionVO.class));
        }

        @Test
        @DisplayName("존재하지 않는 권한 수정 시 예외 발생")
        void updatePermission_notFound() {
            // given
            PermissionUpdateRequest request = PermissionUpdateRequest.builder()
                    .permissionName("권한명")
                    .build();

            given(permissionMapper.findById(999L)).willReturn(null);

            // when & then
            assertThatThrownBy(() -> permissionService.updatePermission(999L, request, 1L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PERMISSION_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("권한 삭제")
    class DeletePermissionTest {

        @Test
        @DisplayName("권한 삭제 성공")
        void deletePermission_success() {
            // given
            given(permissionMapper.findById(1L)).willReturn(testPermission);
            given(permissionMapper.countRoleMappings(1L)).willReturn(0L);
            given(permissionMapper.delete(1L, 1L)).willReturn(1);

            // when
            permissionService.deletePermission(1L, 1L);

            // then
            verify(permissionMapper).delete(1L, 1L);
        }

        @Test
        @DisplayName("존재하지 않는 권한 삭제 시 예외 발생")
        void deletePermission_notFound() {
            // given
            given(permissionMapper.findById(999L)).willReturn(null);

            // when & then
            assertThatThrownBy(() -> permissionService.deletePermission(999L, 1L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PERMISSION_NOT_FOUND);
        }

        @Test
        @DisplayName("역할에 매핑된 권한 삭제 시 예외 발생")
        void deletePermission_inUse() {
            // given
            given(permissionMapper.findById(1L)).willReturn(testPermission);
            given(permissionMapper.countRoleMappings(1L)).willReturn(2L);

            // when & then
            assertThatThrownBy(() -> permissionService.deletePermission(1L, 1L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PERMISSION_IN_USE);
        }
    }
}
