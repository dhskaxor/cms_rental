package com.nt.cms.user.service;

import com.nt.cms.common.response.PageResponse;
import com.nt.cms.user.dto.*;

/**
 * 사용자 서비스 인터페이스
 * 
 * @author CMS Team
 */
public interface UserService {

    /**
     * 사용자 목록 조회 (검색 + 페이징)
     * 
     * @param request 검색 조건
     * @return 페이징된 사용자 목록
     */
    PageResponse<UserResponse> getUsers(UserSearchRequest request);

    /**
     * 사용자 상세 조회
     * 
     * @param id 사용자 ID
     * @return 사용자 정보
     */
    UserResponse getUser(Long id);

    /**
     * 사용자 생성 (관리자)
     * 
     * @param request 생성 요청
     * @param createdBy 생성자 ID
     * @return 생성된 사용자 정보
     */
    UserResponse createUser(UserCreateRequest request, Long createdBy);

    /**
     * 사용자 수정
     * 
     * @param id 사용자 ID
     * @param request 수정 요청
     * @param updatedBy 수정자 ID
     * @return 수정된 사용자 정보
     */
    UserResponse updateUser(Long id, UserUpdateRequest request, Long updatedBy);

    /**
     * 사용자 삭제
     * 
     * @param id 사용자 ID
     * @param deletedBy 삭제자 ID
     */
    void deleteUser(Long id, Long deletedBy);

    /**
     * 비밀번호 변경
     * 
     * @param id 사용자 ID
     * @param request 비밀번호 변경 요청
     * @param updatedBy 수정자 ID
     */
    void changePassword(Long id, PasswordChangeRequest request, Long updatedBy);

    /**
     * 비밀번호 초기화 (관리자)
     * 
     * @param id 사용자 ID
     * @param newPassword 새 비밀번호
     * @param updatedBy 수정자 ID
     */
    void resetPassword(Long id, String newPassword, Long updatedBy);

    /**
     * 계정 잠금
     * 
     * @param id 사용자 ID
     * @param updatedBy 수정자 ID
     */
    void lockUser(Long id, Long updatedBy);

    /**
     * 계정 잠금 해제
     * 
     * @param id 사용자 ID
     * @param updatedBy 수정자 ID
     */
    void unlockUser(Long id, Long updatedBy);

    /**
     * 회원가입
     * 
     * @param request 회원가입 요청
     * @return 생성된 사용자 정보
     */
    UserResponse register(RegisterRequest request);

    /**
     * 사용자명 중복 확인
     * 
     * @param username 사용자명
     * @return 중복이면 true
     */
    boolean isUsernameDuplicated(String username);

    /**
     * 이메일 중복 확인
     * 
     * @param email 이메일
     * @return 중복이면 true
     */
    boolean isEmailDuplicated(String email);
}
