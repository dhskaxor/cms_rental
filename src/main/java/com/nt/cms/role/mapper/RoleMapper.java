package com.nt.cms.role.mapper;

import com.nt.cms.role.vo.RoleVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 역할 Mapper
 * 
 * @author CMS Team
 */
@Mapper
public interface RoleMapper {

    /**
     * 역할 목록 조회
     * 
     * @return 역할 목록
     */
    List<RoleVO> findAll();

    /**
     * ID로 역할 조회
     * 
     * @param id 역할 ID
     * @return 역할 정보
     */
    RoleVO findById(@Param("id") Long id);

    /**
     * 역할 코드로 조회
     * 
     * @param roleCode 역할 코드
     * @return 역할 정보
     */
    RoleVO findByRoleCode(@Param("roleCode") String roleCode);

    /**
     * 역할 코드 존재 여부 확인
     * 
     * @param roleCode 역할 코드
     * @return 존재하면 true
     */
    boolean existsByRoleCode(@Param("roleCode") String roleCode);

    /**
     * 역할 등록
     * 
     * @param role 역할 정보
     * @return 등록된 행 수
     */
    int insert(RoleVO role);

    /**
     * 역할 수정
     * 
     * @param role 역할 정보
     * @return 수정된 행 수
     */
    int update(RoleVO role);

    /**
     * 역할 삭제 (Soft Delete)
     * 
     * @param id 역할 ID
     * @param deletedBy 삭제자 ID
     * @return 삭제된 행 수
     */
    int delete(@Param("id") Long id, @Param("deletedBy") Long deletedBy);

    /**
     * 역할-권한 매핑 삭제
     * 
     * @param roleId 역할 ID
     * @return 삭제된 행 수
     */
    int deleteRolePermissions(@Param("roleId") Long roleId);

    /**
     * 역할-권한 매핑 등록
     * 
     * @param roleId 역할 ID
     * @param permissionId 권한 ID
     * @return 등록된 행 수
     */
    int insertRolePermission(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);

    /**
     * 역할별 권한 ID 목록 조회
     * 
     * @param roleId 역할 ID
     * @return 권한 ID 목록
     */
    List<Long> findPermissionIdsByRoleId(@Param("roleId") Long roleId);
}
