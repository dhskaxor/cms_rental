package com.nt.cms.role.service;

import com.nt.cms.role.dto.*;

import java.util.List;

/**
 * 권한 서비스 인터페이스
 * 
 * @author CMS Team
 */
public interface PermissionService {

    /**
     * 권한 목록 조회
     * 
     * @return 권한 목록
     */
    List<PermissionResponse> getPermissions();

    /**
     * 권한 상세 조회
     * 
     * @param id 권한 ID
     * @return 권한 정보
     */
    PermissionResponse getPermission(Long id);

    /**
     * 역할별 권한 목록 조회
     * 
     * @param roleId 역할 ID
     * @return 권한 목록
     */
    List<PermissionResponse> getPermissionsByRoleId(Long roleId);

    /**
     * 권한 생성
     * 
     * @param request 생성 요청
     * @param createdBy 생성자 ID
     * @return 생성된 권한 정보
     */
    PermissionResponse createPermission(PermissionCreateRequest request, Long createdBy);

    /**
     * 권한 수정
     * 
     * @param id 권한 ID
     * @param request 수정 요청
     * @param updatedBy 수정자 ID
     * @return 수정된 권한 정보
     */
    PermissionResponse updatePermission(Long id, PermissionUpdateRequest request, Long updatedBy);

    /**
     * 권한 삭제
     * 
     * @param id 권한 ID
     * @param deletedBy 삭제자 ID
     */
    void deletePermission(Long id, Long deletedBy);

    /**
     * 권한 코드 중복 확인
     * 
     * @param permissionCode 권한 코드
     * @return 중복이면 true
     */
    boolean isPermissionCodeDuplicated(String permissionCode);
}
