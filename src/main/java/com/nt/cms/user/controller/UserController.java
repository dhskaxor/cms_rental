package com.nt.cms.user.controller;

import com.nt.cms.auth.security.CustomUserDetails;
import com.nt.cms.common.response.ApiResponse;
import com.nt.cms.common.response.PageResponse;
import com.nt.cms.user.dto.*;
import com.nt.cms.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 사용자 REST API 컨트롤러
 * 
 * @author CMS Team
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "사용자", description = "사용자 관리 API")
public class UserController {

    private final UserService userService;

    /**
     * 사용자 목록 조회
     */
    @GetMapping
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).USER_READ.value)")
    @Operation(summary = "사용자 목록 조회", description = "검색 조건에 맞는 사용자 목록을 조회합니다.")
    public ApiResponse<PageResponse<UserResponse>> getUsers(UserSearchRequest request) {
        PageResponse<UserResponse> response = userService.getUsers(request);
        return ApiResponse.success(response);
    }

    /**
     * 사용자 상세 조회
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).USER_READ.value)")
    @Operation(summary = "사용자 상세 조회", description = "사용자 ID로 상세 정보를 조회합니다.")
    public ApiResponse<UserResponse> getUser(@PathVariable Long id) {
        UserResponse response = userService.getUser(id);
        return ApiResponse.success(response);
    }

    /**
     * 사용자 생성
     */
    @PostMapping
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).USER_CREATE.value)")
    @Operation(summary = "사용자 생성", description = "새로운 사용자를 생성합니다.")
    public ApiResponse<UserResponse> createUser(
            @Valid @RequestBody UserCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        UserResponse response = userService.createUser(request, userDetails.getUserId());
        return ApiResponse.success(response);
    }

    /**
     * 사용자 수정
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).USER_UPDATE.value)")
    @Operation(summary = "사용자 수정", description = "사용자 정보를 수정합니다.")
    public ApiResponse<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        UserResponse response = userService.updateUser(id, request, userDetails.getUserId());
        return ApiResponse.success(response);
    }

    /**
     * 사용자 삭제
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).USER_DELETE.value)")
    @Operation(summary = "사용자 삭제", description = "사용자를 삭제합니다.")
    public ApiResponse<Void> deleteUser(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.deleteUser(id, userDetails.getUserId());
        return ApiResponse.success();
    }

    /**
     * 비밀번호 초기화 (관리자)
     */
    @PutMapping("/{id}/password/reset")
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).USER_UPDATE.value)")
    @Operation(summary = "비밀번호 초기화", description = "관리자가 사용자의 비밀번호를 초기화합니다.")
    public ApiResponse<Void> resetPassword(
            @PathVariable Long id,
            @RequestBody PasswordResetRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.resetPassword(id, request.getNewPassword(), userDetails.getUserId());
        return ApiResponse.success();
    }

    /**
     * 계정 잠금
     */
    @PutMapping("/{id}/lock")
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).USER_UPDATE.value)")
    @Operation(summary = "계정 잠금", description = "사용자 계정을 잠급니다.")
    public ApiResponse<Void> lockUser(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.lockUser(id, userDetails.getUserId());
        return ApiResponse.success();
    }

    /**
     * 계정 잠금 해제
     */
    @PutMapping("/{id}/unlock")
    @PreAuthorize("hasAuthority(T(com.nt.cms.common.constant.Permission).USER_UPDATE.value)")
    @Operation(summary = "계정 잠금 해제", description = "잠긴 사용자 계정을 해제합니다.")
    public ApiResponse<Void> unlockUser(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.unlockUser(id, userDetails.getUserId());
        return ApiResponse.success();
    }

    /**
     * 아이디 중복 확인
     */
    @GetMapping("/check/username")
    @Operation(summary = "아이디 중복 확인", description = "사용자명 중복 여부를 확인합니다.")
    public ApiResponse<Boolean> checkUsername(@RequestParam String username) {
        boolean isDuplicated = userService.isUsernameDuplicated(username);
        return ApiResponse.success(isDuplicated);
    }

    /**
     * 이메일 중복 확인
     */
    @GetMapping("/check/email")
    @Operation(summary = "이메일 중복 확인", description = "이메일 중복 여부를 확인합니다.")
    public ApiResponse<Boolean> checkEmail(@RequestParam String email) {
        boolean isDuplicated = userService.isEmailDuplicated(email);
        return ApiResponse.success(isDuplicated);
    }

    /**
     * 내 정보 수정
     */
    @PutMapping("/me")
    @Operation(summary = "내 정보 수정", description = "로그인한 사용자의 정보를 수정합니다.")
    public ApiResponse<UserResponse> updateMyInfo(
            @Valid @RequestBody UserUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        UserResponse response = userService.updateUser(
                userDetails.getUserId(), request, userDetails.getUserId());
        return ApiResponse.success(response);
    }

    /**
     * 내 비밀번호 변경
     */
    @PutMapping("/me/password")
    @Operation(summary = "내 비밀번호 변경", description = "로그인한 사용자의 비밀번호를 변경합니다.")
    public ApiResponse<Void> changeMyPassword(
            @Valid @RequestBody PasswordChangeRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.changePassword(userDetails.getUserId(), request, userDetails.getUserId());
        return ApiResponse.success();
    }
}
