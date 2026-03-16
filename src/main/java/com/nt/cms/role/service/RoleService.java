package com.nt.cms.role.service;

import com.nt.cms.role.dto.*;

import java.util.List;

/**
 * 역할 서비스 인터페이스
 * 
 * @author CMS Team
 */
public interface RoleService {

    /**
     * 역할 목록 조회
     * 
     * @return 역할 목록
     */
    List<RoleResponse> getRoles();

    /**
     * 역할 상세 조회
     * 
     * @param id 역할 ID
     * @return 역할 정보
     */
    RoleResponse getRole(Long id);

    /**
     * 역할 코드로 조회
     * 
     * @param roleCode 역할 코드
     * @return 역할 정보
     */
    RoleResponse getRoleByCode(String roleCode);

    /**
     * 역할 생성
     * 
     * @param request 생성 요청
     * @param createdBy 생성자 ID
     * @return 생성된 역할 정보
     */
    RoleResponse createRole(RoleCreateRequest request, Long createdBy);

    /**
     * 역할 수정
     * 
     * @param id 역할 ID
     * @param request 수정 요청
     * @param updatedBy 수정자 ID
     * @return 수정된 역할 정보
     */
    RoleResponse updateRole(Long id, RoleUpdateRequest request, Long updatedBy);

    /**
     * 역할 삭제
     * 
     * @param id 역할 ID
     * @param deletedBy 삭제자 ID
     */
    void deleteRole(Long id, Long deletedBy);

    /**
     * 역할 코드 중복 확인
     * 
     * @param roleCode 역할 코드
     * @return 중복이면 true
     */
    boolean isRoleCodeDuplicated(String roleCode);
}
