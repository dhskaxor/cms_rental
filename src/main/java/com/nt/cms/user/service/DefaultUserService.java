package com.nt.cms.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nt.cms.audit.service.AuditLogService;
import com.nt.cms.common.exception.BusinessException;
import com.nt.cms.common.exception.ErrorCode;
import com.nt.cms.common.response.PageResponse;
import com.nt.cms.common.util.PasswordUtil;
import com.nt.cms.user.dto.*;
import com.nt.cms.user.mapper.UserMapper;
import com.nt.cms.user.vo.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 사용자 서비스 구현체
 * 
 * @author CMS Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefaultUserService implements UserService {

    /**
     * 기본 사용자 역할 ID fallback 값 (USER)
     * 역할 코드 조회 실패 시 기존 동작 호환을 위해 사용한다.
     */
    private static final Long DEFAULT_ROLE_ID = 3L;
    private static final String DEFAULT_ROLE_CODE = "USER";

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;
    private final ObjectMapper objectMapper;

    @Override
    public PageResponse<UserResponse> getUsers(UserSearchRequest request) {
        log.debug("사용자 목록 조회: {}", request);

        List<UserVO> users = userMapper.findAll(request);
        long total = userMapper.countAll(request);

        List<UserResponse> content = users.stream()
                .map(UserResponse::from)
                .collect(Collectors.toList());

        return PageResponse.of(content, request.getPage(), request.getSize(), total);
    }

    @Override
    public UserResponse getUser(Long id) {
        log.debug("사용자 상세 조회: {}", id);

        UserVO user = userMapper.findById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        return UserResponse.from(user);
    }

    @Override
    @Transactional
    public UserResponse createUser(UserCreateRequest request, Long createdBy) {
        log.info("사용자 생성: {}", request.getUsername());

        // 비밀번호 확인
        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new BusinessException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        // 비밀번호 정책 검증
        if (!PasswordUtil.isValid(request.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD_FORMAT, 
                    PasswordUtil.getViolationReason(request.getPassword()));
        }

        // 중복 확인
        if (userMapper.existsByUsername(request.getUsername())) {
            throw new BusinessException(ErrorCode.DUPLICATE_USERNAME);
        }

        if (request.getEmail() != null && !request.getEmail().isEmpty() 
                && userMapper.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }

        // 사용자 생성
        UserVO user = UserVO.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .email(request.getEmail())
                .roleId(request.getRoleId())
                .status("ACTIVE")
                .createdBy(createdBy)
                .build();

        userMapper.insert(user);
        log.info("사용자 생성 완료: id={}", user.getId());

        // 감사 로그 기록
        recordAuditLog("CREATE", "USER", user.getId(), null, user, createdBy);

        return getUser(user.getId());
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request, Long updatedBy) {
        log.info("사용자 수정: id={}", id);

        // 사용자 존재 확인
        UserVO existingUser = userMapper.findById(id);
        if (existingUser == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // 이메일 중복 확인 (자기 자신 제외)
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            UserVO userByEmail = userMapper.findByEmail(request.getEmail());
            if (userByEmail != null && !userByEmail.getId().equals(id)) {
                throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
            }
        }

        // 사용자 수정
        UserVO user = UserVO.builder()
                .id(id)
                .name(request.getName())
                .email(request.getEmail())
                .roleId(request.getRoleId())
                .status(request.getStatus() != null ? request.getStatus() : existingUser.getStatus())
                .updatedBy(updatedBy)
                .build();

        userMapper.update(user);
        log.info("사용자 수정 완료: id={}", id);

        // 감사 로그 기록 (수정 후 상태)
        UserVO afterState = UserVO.builder()
                .id(id)
                .username(existingUser.getUsername())
                .name(user.getName() != null ? user.getName() : existingUser.getName())
                .email(user.getEmail() != null ? user.getEmail() : existingUser.getEmail())
                .roleId(user.getRoleId() != null ? user.getRoleId() : existingUser.getRoleId())
                .status(user.getStatus() != null ? user.getStatus() : existingUser.getStatus())
                .build();
        recordAuditLog("UPDATE", "USER", id, existingUser, afterState, updatedBy);

        return getUser(id);
    }

    @Override
    @Transactional
    public UserResponse updateMyInfo(Long id, UserMyUpdateRequest request, Long updatedBy) {
        log.info("내 정보 수정: id={}", id);

        UserVO existingUser = userMapper.findById(id);
        if (existingUser == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // 이메일 중복 확인 (자기 자신 제외)
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            UserVO userByEmail = userMapper.findByEmail(request.getEmail());
            if (userByEmail != null && !userByEmail.getId().equals(id)) {
                throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
            }
        }

        UserVO user = UserVO.builder()
                .id(id)
                .name(request.getName())
                .email(request.getEmail())
                .roleId(existingUser.getRoleId())
                .status(existingUser.getStatus())
                .updatedBy(updatedBy)
                .build();

        userMapper.update(user);
        recordAuditLog("UPDATE_MY_INFO", "USER", id, existingUser, user, updatedBy);
        return getUser(id);
    }

    @Override
    @Transactional
    public void deleteUser(Long id, Long deletedBy) {
        log.info("사용자 삭제: id={}", id);

        // 사용자 존재 확인
        UserVO user = userMapper.findById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // 자기 자신 삭제 방지
        if (id.equals(deletedBy)) {
            throw new BusinessException(ErrorCode.CANNOT_DELETE_SELF);
        }

        userMapper.delete(id, deletedBy);
        log.info("사용자 삭제 완료: id={}", id);

        // 감사 로그 기록
        recordAuditLog("DELETE", "USER", id, user, null, deletedBy);
    }

    @Override
    @Transactional
    public void changePassword(Long id, PasswordChangeRequest request, Long updatedBy) {
        log.info("비밀번호 변경: id={}", id);

        // 사용자 존재 확인
        UserVO user = userMapper.findById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_CURRENT_PASSWORD);
        }

        // 새 비밀번호 확인
        if (!request.getNewPassword().equals(request.getNewPasswordConfirm())) {
            throw new BusinessException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        // 비밀번호 정책 검증
        if (!PasswordUtil.isValid(request.getNewPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD_FORMAT,
                    PasswordUtil.getViolationReason(request.getNewPassword()));
        }

        userMapper.updatePassword(id, passwordEncoder.encode(request.getNewPassword()), updatedBy);
        log.info("비밀번호 변경 완료: id={}", id);
    }

    @Override
    @Transactional
    public void resetPassword(Long id, String newPassword, Long updatedBy) {
        log.info("비밀번호 초기화: id={}", id);

        // 사용자 존재 확인
        UserVO user = userMapper.findById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // 비밀번호 정책 검증
        if (!PasswordUtil.isValid(newPassword)) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD_FORMAT,
                    PasswordUtil.getViolationReason(newPassword));
        }

        userMapper.updatePassword(id, passwordEncoder.encode(newPassword), updatedBy);
        log.info("비밀번호 초기화 완료: id={}", id);
    }

    @Override
    @Transactional
    public void lockUser(Long id, Long updatedBy) {
        log.info("계정 잠금: id={}", id);

        UserVO user = userMapper.findById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        userMapper.updateStatus(id, "LOCKED", updatedBy);
        log.info("계정 잠금 완료: id={}", id);
    }

    @Override
    @Transactional
    public void unlockUser(Long id, Long updatedBy) {
        log.info("계정 잠금 해제: id={}", id);

        UserVO user = userMapper.findById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        userMapper.updateStatus(id, "ACTIVE", updatedBy);
        log.info("계정 잠금 해제 완료: id={}", id);
    }

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        log.info("회원가입: {}", request.getUsername());

        // 비밀번호 확인
        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new BusinessException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        // 비밀번호 정책 검증
        if (!PasswordUtil.isValid(request.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD_FORMAT,
                    PasswordUtil.getViolationReason(request.getPassword()));
        }

        // 중복 확인
        if (userMapper.existsByUsername(request.getUsername())) {
            throw new BusinessException(ErrorCode.DUPLICATE_USERNAME);
        }

        if (userMapper.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }

        // 사용자 생성 (기본 역할: USER)
        Long defaultRoleId = resolveDefaultRoleId();
        UserVO user = UserVO.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .email(request.getEmail())
                .roleId(defaultRoleId)
                .status("ACTIVE")
                .build();

        userMapper.insert(user);
        log.info("회원가입 완료: id={}", user.getId());

        return getUser(user.getId());
    }

    @Override
    public boolean isUsernameDuplicated(String username) {
        return userMapper.existsByUsername(username);
    }

    @Override
    public boolean isEmailDuplicated(String email) {
        return userMapper.existsByEmail(email);
    }

    /**
     * 감사 로그 기록 (비밀번호 제외)
     */
    private void recordAuditLog(String action, String targetType, Long targetId,
                               UserVO before, UserVO after, Long actorId) {
        try {
            String actorUsername = userMapper.findUsernameById(actorId);
            String beforeJson = before != null ? objectMapper.writeValueAsString(toAuditMap(before)) : null;
            String afterJson = after != null ? objectMapper.writeValueAsString(toAuditMap(after)) : null;
            auditLogService.log(actorId, actorUsername, action, targetType, targetId, beforeJson, afterJson);
        } catch (JsonProcessingException e) {
            log.warn("감사 로그 JSON 직렬화 실패: {}", e.getMessage());
        }
    }

    /** 비밀번호 제외한 감사용 Map 생성 */
    private Map<String, Object> toAuditMap(UserVO user) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", user.getId());
        map.put("username", user.getUsername());
        map.put("name", user.getName());
        map.put("email", user.getEmail());
        map.put("roleId", user.getRoleId());
        map.put("status", user.getStatus());
        return map;
    }

    /**
     * 기본 사용자 역할(USER) ID를 조회한다.
     * role 테이블 기준으로 동적으로 조회하고, 실패 시 fallback 값을 사용한다.
     */
    private Long resolveDefaultRoleId() {
        Long roleId = userMapper.findRoleIdByCode(DEFAULT_ROLE_CODE);
        return roleId != null ? roleId : DEFAULT_ROLE_ID;
    }
}
