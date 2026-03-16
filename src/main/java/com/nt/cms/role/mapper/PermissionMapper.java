package com.nt.cms.role.mapper;

import com.nt.cms.role.vo.PermissionVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 권한 Mapper
 * 
 * @author CMS Team
 */
@Mapper
public interface PermissionMapper {

    /**
     * 권한 목록 조회
     * 
     * @return 권한 목록
     */
    List<PermissionVO> findAll();

    /**
     * ID로 권한 조회
     * 
     * @param id 권한 ID
     * @return 권한 정보
     */
    PermissionVO findById(@Param("id") Long id);

    /**
     * 권한 코드로 조회
     * 
     * @param permissionCode 권한 코드
     * @return 권한 정보
     */
    PermissionVO findByPermissionCode(@Param("permissionCode") String permissionCode);

    /**
     * 역할 ID로 권한 목록 조회
     * 
     * @param roleId 역할 ID
     * @return 권한 목록
     */
    List<PermissionVO> findByRoleId(@Param("roleId") Long roleId);

    /**
     * 권한 코드 존재 여부 확인
     * 
     * @param permissionCode 권한 코드
     * @return 존재하면 true
     */
    boolean existsByPermissionCode(@Param("permissionCode") String permissionCode);

    /**
     * 권한 등록
     * 
     * @param permission 권한 정보
     * @return 등록된 행 수
     */
    int insert(PermissionVO permission);

    /**
     * 권한 수정
     * 
     * @param permission 권한 정보
     * @return 수정된 행 수
     */
    int update(PermissionVO permission);

    /**
     * 권한 삭제 (Soft Delete)
     * 
     * @param id 권한 ID
     * @param deletedBy 삭제자 ID
     * @return 삭제된 행 수
     */
    int delete(@Param("id") Long id, @Param("deletedBy") Long deletedBy);

    /**
     * 권한이 역할에 매핑되어 있는지 확인
     * 
     * @param permissionId 권한 ID
     * @return 매핑 개수
     */
    long countRoleMappings(@Param("permissionId") Long permissionId);
}
