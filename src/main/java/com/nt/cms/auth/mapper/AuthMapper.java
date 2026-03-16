package com.nt.cms.auth.mapper;

import com.nt.cms.auth.vo.RefreshTokenVO;
import com.nt.cms.auth.vo.UserAuthVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 인증 관련 Mapper
 * 
 * @author CMS Team
 */
@Mapper
public interface AuthMapper {

    /**
     * 사용자명으로 인증 정보 조회
     * 
     * @param username 사용자명
     * @return 사용자 인증 정보
     */
    UserAuthVO findByUsername(@Param("username") String username);

    /**
     * 사용자 ID로 인증 정보 조회
     * 
     * @param userId 사용자 ID
     * @return 사용자 인증 정보
     */
    UserAuthVO findById(@Param("userId") Long userId);

    /**
     * 사용자의 권한 코드 목록 조회
     * 
     * @param roleId 역할 ID
     * @return 권한 코드 목록
     */
    List<String> findPermissionsByRoleId(@Param("roleId") Long roleId);

    /**
     * 마지막 로그인 시간 업데이트
     * 
     * @param userId 사용자 ID
     * @return 업데이트된 행 수
     */
    int updateLastLoginAt(@Param("userId") Long userId);

    /**
     * 사용자 상태 업데이트
     * 
     * @param userId 사용자 ID
     * @param status 상태 (ACTIVE, LOCKED)
     * @return 업데이트된 행 수
     */
    int updateStatus(@Param("userId") Long userId, @Param("status") String status);

    /**
     * Refresh Token 저장
     * 
     * @param refreshToken Refresh Token VO
     * @return 삽입된 행 수
     */
    int insertRefreshToken(RefreshTokenVO refreshToken);

    /**
     * Refresh Token으로 토큰 정보 조회
     * 
     * @param refreshToken Refresh Token 문자열
     * @return Refresh Token VO
     */
    RefreshTokenVO findRefreshToken(@Param("refreshToken") String refreshToken);

    /**
     * 사용자의 모든 Refresh Token 폐기 (새 로그인 시)
     * 
     * @param userId 사용자 ID
     * @return 업데이트된 행 수
     */
    int revokeAllRefreshTokensByUserId(@Param("userId") Long userId);

    /**
     * 특정 Refresh Token 폐기
     * 
     * @param refreshToken Refresh Token 문자열
     * @return 업데이트된 행 수
     */
    int revokeRefreshToken(@Param("refreshToken") String refreshToken);

    /**
     * 만료된 Refresh Token 삭제 (배치 작업용)
     * 
     * @return 삭제된 행 수
     */
    int deleteExpiredRefreshTokens();
}
