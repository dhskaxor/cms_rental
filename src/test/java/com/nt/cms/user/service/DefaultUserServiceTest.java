package com.nt.cms.user.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nt.cms.audit.service.AuditLogService;
import com.nt.cms.common.exception.BusinessException;
import com.nt.cms.common.exception.ErrorCode;
import com.nt.cms.common.response.PageResponse;
import com.nt.cms.user.dto.*;
import com.nt.cms.user.mapper.UserMapper;
import com.nt.cms.user.vo.UserVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

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
 * DefaultUserService 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DefaultUserService 테스트")
class DefaultUserServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuditLogService auditLogService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private DefaultUserService userService;

    private UserVO testUser;

    @BeforeEach
    void setUp() {
        userService = new DefaultUserService(userMapper, passwordEncoder, auditLogService, objectMapper);
        testUser = UserVO.builder()
                .id(1L)
                .username("testuser")
                .password("encoded_password")
                .name("테스트 사용자")
                .email("test@example.com")
                .roleId(3L)
                .roleCode("USER")
                .roleName("사용자")
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("사용자 목록 조회")
    class GetUsersTest {

        @Test
        @DisplayName("검색 조건에 맞는 사용자 목록 반환")
        void getUsers_success() {
            // given
            UserSearchRequest request = UserSearchRequest.builder()
                    .page(1)
                    .size(10)
                    .build();

            List<UserVO> users = Arrays.asList(testUser);
            given(userMapper.findAll(any(UserSearchRequest.class))).willReturn(users);
            given(userMapper.countAll(any(UserSearchRequest.class))).willReturn(1L);

            // when
            PageResponse<UserResponse> response = userService.getUsers(request);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getContent()).hasSize(1);
            assertThat(response.getTotalElements()).isEqualTo(1L);
            assertThat(response.getPage()).isEqualTo(1);
            verify(userMapper).findAll(request);
            verify(userMapper).countAll(request);
        }

        @Test
        @DisplayName("결과가 없을 때 빈 목록 반환")
        void getUsers_empty() {
            // given
            UserSearchRequest request = UserSearchRequest.builder().build();
            given(userMapper.findAll(any(UserSearchRequest.class))).willReturn(Collections.emptyList());
            given(userMapper.countAll(any(UserSearchRequest.class))).willReturn(0L);

            // when
            PageResponse<UserResponse> response = userService.getUsers(request);

            // then
            assertThat(response.getContent()).isEmpty();
            assertThat(response.getTotalElements()).isEqualTo(0L);
        }
    }

    @Nested
    @DisplayName("사용자 상세 조회")
    class GetUserTest {

        @Test
        @DisplayName("ID로 사용자 조회 성공")
        void getUser_success() {
            // given
            given(userMapper.findById(1L)).willReturn(testUser);

            // when
            UserResponse response = userService.getUser(1L);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getUsername()).isEqualTo("testuser");
            verify(userMapper).findById(1L);
        }

        @Test
        @DisplayName("존재하지 않는 사용자 조회 시 예외 발생")
        void getUser_notFound() {
            // given
            given(userMapper.findById(999L)).willReturn(null);

            // when & then
            assertThatThrownBy(() -> userService.getUser(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("사용자 생성")
    class CreateUserTest {

        @Test
        @DisplayName("사용자 생성 성공")
        void createUser_success() {
            // given
            UserCreateRequest request = UserCreateRequest.builder()
                    .username("newuser")
                    .password("Test123!")
                    .passwordConfirm("Test123!")
                    .name("새 사용자")
                    .email("new@example.com")
                    .roleId(3L)
                    .build();

            given(userMapper.existsByUsername("newuser")).willReturn(false);
            given(userMapper.existsByEmail("new@example.com")).willReturn(false);
            given(userMapper.findUsernameById(1L)).willReturn("admin");
            given(passwordEncoder.encode("Test123!")).willReturn("encoded_password");
            given(userMapper.insert(any(UserVO.class))).willAnswer(invocation -> {
                UserVO user = invocation.getArgument(0);
                user.setId(2L);
                return 1;
            });
            given(userMapper.findById(2L)).willReturn(UserVO.builder()
                    .id(2L)
                    .username("newuser")
                    .name("새 사용자")
                    .email("new@example.com")
                    .roleId(3L)
                    .status("ACTIVE")
                    .build());

            // when
            UserResponse response = userService.createUser(request, 1L);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getUsername()).isEqualTo("newuser");
            verify(userMapper).insert(any(UserVO.class));
        }

        @Test
        @DisplayName("비밀번호 불일치 시 예외 발생")
        void createUser_passwordNotMatch() {
            // given
            UserCreateRequest request = UserCreateRequest.builder()
                    .username("newuser")
                    .password("Test123!")
                    .passwordConfirm("Different123!")
                    .name("새 사용자")
                    .email("new@example.com")
                    .roleId(3L)
                    .build();

            // when & then
            assertThatThrownBy(() -> userService.createUser(request, 1L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PASSWORD_NOT_MATCH);
        }

        @Test
        @DisplayName("유효하지 않은 비밀번호 형식일 때 예외 발생")
        void createUser_invalidPasswordFormat() {
            // given
            UserCreateRequest request = UserCreateRequest.builder()
                    .username("newuser")
                    .password("simple")
                    .passwordConfirm("simple")
                    .name("새 사용자")
                    .email("new@example.com")
                    .roleId(3L)
                    .build();

            // when & then
            assertThatThrownBy(() -> userService.createUser(request, 1L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_PASSWORD_FORMAT);
        }

        @Test
        @DisplayName("중복된 아이디일 때 예외 발생")
        void createUser_duplicateUsername() {
            // given
            UserCreateRequest request = UserCreateRequest.builder()
                    .username("existinguser")
                    .password("Test123!")
                    .passwordConfirm("Test123!")
                    .name("새 사용자")
                    .email("new@example.com")
                    .roleId(3L)
                    .build();

            given(userMapper.existsByUsername("existinguser")).willReturn(true);

            // when & then
            assertThatThrownBy(() -> userService.createUser(request, 1L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATE_USERNAME);
        }

        @Test
        @DisplayName("중복된 이메일일 때 예외 발생")
        void createUser_duplicateEmail() {
            // given
            UserCreateRequest request = UserCreateRequest.builder()
                    .username("newuser")
                    .password("Test123!")
                    .passwordConfirm("Test123!")
                    .name("새 사용자")
                    .email("existing@example.com")
                    .roleId(3L)
                    .build();

            given(userMapper.existsByUsername("newuser")).willReturn(false);
            given(userMapper.existsByEmail("existing@example.com")).willReturn(true);

            // when & then
            assertThatThrownBy(() -> userService.createUser(request, 1L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATE_EMAIL);
        }
    }

    @Nested
    @DisplayName("사용자 수정")
    class UpdateUserTest {

        @Test
        @DisplayName("사용자 수정 성공")
        void updateUser_success() {
            // given
            UserUpdateRequest request = UserUpdateRequest.builder()
                    .name("수정된 이름")
                    .email("updated@example.com")
                    .roleId(2L)
                    .status("ACTIVE")
                    .build();

            given(userMapper.findById(1L)).willReturn(testUser);
            given(userMapper.findByEmail("updated@example.com")).willReturn(null);
            given(userMapper.findUsernameById(1L)).willReturn("admin");
            given(userMapper.update(any(UserVO.class))).willReturn(1);

            UserVO updatedUser = UserVO.builder()
                    .id(1L)
                    .username("testuser")
                    .name("수정된 이름")
                    .email("updated@example.com")
                    .roleId(2L)
                    .status("ACTIVE")
                    .build();
            given(userMapper.findById(1L)).willReturn(testUser).willReturn(updatedUser);

            // when
            UserResponse response = userService.updateUser(1L, request, 1L);

            // then
            assertThat(response).isNotNull();
            verify(userMapper).update(any(UserVO.class));
        }

        @Test
        @DisplayName("존재하지 않는 사용자 수정 시 예외 발생")
        void updateUser_notFound() {
            // given
            UserUpdateRequest request = UserUpdateRequest.builder()
                    .name("수정된 이름")
                    .email("updated@example.com")
                    .roleId(2L)
                    .build();

            given(userMapper.findById(999L)).willReturn(null);

            // when & then
            assertThatThrownBy(() -> userService.updateUser(999L, request, 1L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
        }

        @Test
        @DisplayName("다른 사용자의 이메일과 중복될 때 예외 발생")
        void updateUser_duplicateEmail() {
            // given
            UserUpdateRequest request = UserUpdateRequest.builder()
                    .name("수정된 이름")
                    .email("other@example.com")
                    .roleId(2L)
                    .build();

            UserVO otherUser = UserVO.builder()
                    .id(2L)
                    .email("other@example.com")
                    .build();

            given(userMapper.findById(1L)).willReturn(testUser);
            given(userMapper.findByEmail("other@example.com")).willReturn(otherUser);

            // when & then
            assertThatThrownBy(() -> userService.updateUser(1L, request, 1L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATE_EMAIL);
        }
    }

    @Nested
    @DisplayName("사용자 삭제")
    class DeleteUserTest {

        @Test
        @DisplayName("사용자 삭제 성공")
        void deleteUser_success() {
            // given
            given(userMapper.findById(1L)).willReturn(testUser);
            given(userMapper.findUsernameById(2L)).willReturn("admin");
            given(userMapper.delete(1L, 2L)).willReturn(1);

            // when
            userService.deleteUser(1L, 2L);

            // then
            verify(userMapper).delete(1L, 2L);
        }

        @Test
        @DisplayName("존재하지 않는 사용자 삭제 시 예외 발생")
        void deleteUser_notFound() {
            // given
            given(userMapper.findById(999L)).willReturn(null);

            // when & then
            assertThatThrownBy(() -> userService.deleteUser(999L, 1L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
        }

        @Test
        @DisplayName("자기 자신 삭제 시 예외 발생")
        void deleteUser_cannotDeleteSelf() {
            // given
            given(userMapper.findById(1L)).willReturn(testUser);

            // when & then
            assertThatThrownBy(() -> userService.deleteUser(1L, 1L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CANNOT_DELETE_SELF);
        }
    }

    @Nested
    @DisplayName("비밀번호 변경")
    class ChangePasswordTest {

        @Test
        @DisplayName("비밀번호 변경 성공")
        void changePassword_success() {
            // given
            PasswordChangeRequest request = PasswordChangeRequest.builder()
                    .currentPassword("currentPwd!")
                    .newPassword("New123!!")
                    .newPasswordConfirm("New123!!")
                    .build();

            given(userMapper.findById(1L)).willReturn(testUser);
            given(passwordEncoder.matches("currentPwd!", "encoded_password")).willReturn(true);
            given(passwordEncoder.encode("New123!!")).willReturn("new_encoded");
            given(userMapper.updatePassword(eq(1L), eq("new_encoded"), eq(1L))).willReturn(1);

            // when
            userService.changePassword(1L, request, 1L);

            // then
            verify(userMapper).updatePassword(1L, "new_encoded", 1L);
        }

        @Test
        @DisplayName("현재 비밀번호가 틀렸을 때 예외 발생")
        void changePassword_invalidCurrentPassword() {
            // given
            PasswordChangeRequest request = PasswordChangeRequest.builder()
                    .currentPassword("wrongPassword")
                    .newPassword("New123!!")
                    .newPasswordConfirm("New123!!")
                    .build();

            given(userMapper.findById(1L)).willReturn(testUser);
            given(passwordEncoder.matches("wrongPassword", "encoded_password")).willReturn(false);

            // when & then
            assertThatThrownBy(() -> userService.changePassword(1L, request, 1L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_CURRENT_PASSWORD);
        }

        @Test
        @DisplayName("새 비밀번호 불일치 시 예외 발생")
        void changePassword_newPasswordNotMatch() {
            // given
            PasswordChangeRequest request = PasswordChangeRequest.builder()
                    .currentPassword("currentPwd!")
                    .newPassword("New123!!")
                    .newPasswordConfirm("Different123!")
                    .build();

            given(userMapper.findById(1L)).willReturn(testUser);
            given(passwordEncoder.matches("currentPwd!", "encoded_password")).willReturn(true);

            // when & then
            assertThatThrownBy(() -> userService.changePassword(1L, request, 1L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PASSWORD_NOT_MATCH);
        }
    }

    @Nested
    @DisplayName("계정 잠금/해제")
    class LockUnlockTest {

        @Test
        @DisplayName("계정 잠금 성공")
        void lockUser_success() {
            // given
            given(userMapper.findById(1L)).willReturn(testUser);
            given(userMapper.updateStatus(1L, "LOCKED", 1L)).willReturn(1);

            // when
            userService.lockUser(1L, 1L);

            // then
            verify(userMapper).updateStatus(1L, "LOCKED", 1L);
        }

        @Test
        @DisplayName("계정 잠금 해제 성공")
        void unlockUser_success() {
            // given
            testUser.setStatus("LOCKED");
            given(userMapper.findById(1L)).willReturn(testUser);
            given(userMapper.updateStatus(1L, "ACTIVE", 1L)).willReturn(1);

            // when
            userService.unlockUser(1L, 1L);

            // then
            verify(userMapper).updateStatus(1L, "ACTIVE", 1L);
        }
    }

    @Nested
    @DisplayName("회원가입")
    class RegisterTest {

        @Test
        @DisplayName("회원가입 성공")
        void register_success() {
            // given
            RegisterRequest request = RegisterRequest.builder()
                    .username("newmember")
                    .password("Test123!")
                    .passwordConfirm("Test123!")
                    .name("신규회원")
                    .email("newmember@example.com")
                    .build();

            given(userMapper.existsByUsername("newmember")).willReturn(false);
            given(userMapper.existsByEmail("newmember@example.com")).willReturn(false);
            given(passwordEncoder.encode("Test123!")).willReturn("encoded");
            given(userMapper.insert(any(UserVO.class))).willAnswer(invocation -> {
                UserVO user = invocation.getArgument(0);
                user.setId(10L);
                return 1;
            });
            given(userMapper.findById(10L)).willReturn(UserVO.builder()
                    .id(10L)
                    .username("newmember")
                    .name("신규회원")
                    .email("newmember@example.com")
                    .roleId(3L)
                    .status("ACTIVE")
                    .build());

            // when
            UserResponse response = userService.register(request);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getUsername()).isEqualTo("newmember");
            verify(userMapper).findRoleIdByCode("USER");
            verify(userMapper).insert(argThat(user ->
                    user.getRoleId() != null && user.getStatus().equals("ACTIVE")));
        }

        @Test
        @DisplayName("비밀번호 불일치 시 예외 발생")
        void register_passwordNotMatch() {
            // given
            RegisterRequest request = RegisterRequest.builder()
                    .username("newmember")
                    .password("Test123!")
                    .passwordConfirm("Different!")
                    .name("신규회원")
                    .email("newmember@example.com")
                    .build();

            // when & then
            assertThatThrownBy(() -> userService.register(request))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PASSWORD_NOT_MATCH);
        }
    }

    @Nested
    @DisplayName("중복 확인")
    class DuplicateCheckTest {

        @Test
        @DisplayName("아이디 중복 확인 - 중복됨")
        void isUsernameDuplicated_true() {
            // given
            given(userMapper.existsByUsername("existing")).willReturn(true);

            // when
            boolean result = userService.isUsernameDuplicated("existing");

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("아이디 중복 확인 - 중복 아님")
        void isUsernameDuplicated_false() {
            // given
            given(userMapper.existsByUsername("newname")).willReturn(false);

            // when
            boolean result = userService.isUsernameDuplicated("newname");

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("이메일 중복 확인 - 중복됨")
        void isEmailDuplicated_true() {
            // given
            given(userMapper.existsByEmail("existing@email.com")).willReturn(true);

            // when
            boolean result = userService.isEmailDuplicated("existing@email.com");

            // then
            assertThat(result).isTrue();
        }
    }
}
