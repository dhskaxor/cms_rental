package com.nt.cms.user.mapper;

import com.nt.cms.user.dto.UserSearchRequest;
import com.nt.cms.user.vo.UserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 사용자 Mapper
 * 
 * @author CMS Team
 */
@Mapper
public interface UserMapper {

    /**
     * 사용자 목록 조회 (검색 + 페이징)
     * 
     * @param request 검색 조건
     * @return 사용자 목록
     */
    List<UserVO> findAll(UserSearchRequest request);

    /**
     * 전체 건수 조회
     * 
     * @param request 검색 조건
     * @return 전체 건수
     */
    long countAll(UserSearchRequest request);

    /**
     * ID로 사용자 조회
     * 
     * @param id 사용자 ID
     * @return 사용자 정보
     */
    UserVO findById(@Param("id") Long id);

    /**
     * ID로 사용자명 조회 (감사 로그 등용)
     * 
     * @param id 사용자 ID
     * @return 사용자명 (없으면 null)
     */
    String findUsernameById(@Param("id") Long id);

    /**
     * 사용자명으로 사용자 조회
     * 
     * @param username 사용자명
     * @return 사용자 정보
     */
    UserVO findByUsername(@Param("username") String username);

    /**
     * 이메일로 사용자 조회
     * 
     * @param email 이메일
     * @return 사용자 정보
     */
    UserVO findByEmail(@Param("email") String email);

    /**
     * 사용자명 존재 여부 확인
     * 
     * @param username 사용자명
     * @return 존재하면 true
     */
    boolean existsByUsername(@Param("username") String username);

    /**
     * 이메일 존재 여부 확인
     * 
     * @param email 이메일
     * @return 존재하면 true
     */
    boolean existsByEmail(@Param("email") String email);

    /**
     * 사용자 등록
     * 
     * @param user 사용자 정보
     * @return 등록된 행 수
     */
    int insert(UserVO user);

    /**
     * 사용자 정보 수정
     * 
     * @param user 사용자 정보
     * @return 수정된 행 수
     */
    int update(UserVO user);

    /**
     * 비밀번호 변경
     * 
     * @param id 사용자 ID
     * @param password 암호화된 비밀번호
     * @param updatedBy 수정자 ID
     * @return 수정된 행 수
     */
    int updatePassword(@Param("id") Long id, 
                       @Param("password") String password, 
                       @Param("updatedBy") Long updatedBy);

    /**
     * 상태 변경
     * 
     * @param id 사용자 ID
     * @param status 상태
     * @param updatedBy 수정자 ID
     * @return 수정된 행 수
     */
    int updateStatus(@Param("id") Long id, 
                     @Param("status") String status, 
                     @Param("updatedBy") Long updatedBy);

    /**
     * 사용자 삭제 (Soft Delete)
     * 
     * @param id 사용자 ID
     * @param deletedBy 삭제자 ID
     * @return 삭제된 행 수
     */
    int delete(@Param("id") Long id, @Param("deletedBy") Long deletedBy);

    /**
     * 역할별 사용자 수 조회
     * 
     * @param roleId 역할 ID
     * @return 사용자 수
     */
    long countByRoleId(@Param("roleId") Long roleId);

    /**
     * 역할 코드로 역할 ID 조회
     *
     * @param roleCode 역할 코드
     * @return 역할 ID (없으면 null)
     */
    Long findRoleIdByCode(@Param("roleCode") String roleCode);
}
