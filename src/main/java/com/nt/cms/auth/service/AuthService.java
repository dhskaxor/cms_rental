package com.nt.cms.auth.service;

import com.nt.cms.auth.dto.LoginRequest;
import com.nt.cms.auth.dto.LoginResponse;
import com.nt.cms.auth.dto.TokenRefreshRequest;
import com.nt.cms.auth.dto.TokenRefreshResponse;

/**
 * 인증 서비스 인터페이스
 * 
 * @author CMS Team
 */
public interface AuthService {

    /**
     * 로그인
     * 
     * @param request 로그인 요청
     * @param userAgent 클라이언트 User Agent
     * @param ipAddress 클라이언트 IP 주소
     * @return 로그인 응답 (토큰 포함)
     */
    LoginResponse login(LoginRequest request, String userAgent, String ipAddress);

    /**
     * 토큰 갱신
     * 
     * @param request 토큰 갱신 요청
     * @return 토큰 갱신 응답
     */
    TokenRefreshResponse refreshToken(TokenRefreshRequest request);

    /**
     * 로그아웃
     * 
     * @param refreshToken Refresh Token
     */
    void logout(String refreshToken);

    /**
     * 모든 세션 로그아웃 (모든 디바이스에서 로그아웃)
     * 
     * @param userId 사용자 ID
     */
    void logoutAll(Long userId);
}
