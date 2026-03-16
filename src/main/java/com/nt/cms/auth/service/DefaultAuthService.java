package com.nt.cms.auth.service;

import com.nt.cms.auth.dto.LoginRequest;
import com.nt.cms.auth.dto.LoginResponse;
import com.nt.cms.auth.dto.TokenRefreshRequest;
import com.nt.cms.auth.dto.TokenRefreshResponse;
import com.nt.cms.auth.jwt.JwtTokenProvider;
import com.nt.cms.auth.mapper.AuthMapper;
import com.nt.cms.auth.security.CustomUserDetails;
import com.nt.cms.auth.security.CustomUserDetailsService;
import com.nt.cms.auth.vo.RefreshTokenVO;
import com.nt.cms.common.exception.BusinessException;
import com.nt.cms.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 인증 서비스 구현체
 * 
 * @author CMS Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefaultAuthService implements AuthService {

    private final AuthMapper authMapper;
    private final CustomUserDetailsService userDetailsService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request, String userAgent, String ipAddress) {
        log.info("로그인 시도: {}", request.getUsername());

        // 사용자 조회 및 비밀번호 검증
        CustomUserDetails userDetails;
        try {
            userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(request.getUsername());
        } catch (Exception e) {
            log.warn("로그인 실패 - 사용자 조회 실패: {}", request.getUsername());
            throw new BusinessException(ErrorCode.LOGIN_FAILED);
        }

        // 계정 상태 확인
        if (!userDetails.isAccountNonLocked()) {
            log.warn("로그인 실패 - 계정 잠금: {}", request.getUsername());
            throw new BusinessException(ErrorCode.ACCOUNT_LOCKED);
        }

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), userDetails.getPassword())) {
            log.warn("로그인 실패 - 비밀번호 불일치: {}", request.getUsername());
            throw new BusinessException(ErrorCode.LOGIN_FAILED);
        }

        // 기존 Refresh Token 모두 폐기 (단일 디바이스 로그인 정책)
        authMapper.revokeAllRefreshTokensByUserId(userDetails.getUserId());

        // 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(userDetails);
        String refreshToken = jwtTokenProvider.createRefreshToken(userDetails);

        // Refresh Token 저장
        saveRefreshToken(userDetails.getUserId(), refreshToken, userAgent, ipAddress);

        // 마지막 로그인 시간 업데이트
        authMapper.updateLastLoginAt(userDetails.getUserId());

        log.info("로그인 성공: {}", request.getUsername());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getAccessTokenValidityInSeconds())
                .user(LoginResponse.UserInfo.builder()
                        .id(userDetails.getUserId())
                        .username(userDetails.getUsername())
                        .name(userDetails.getName())
                        .email(userDetails.getEmail())
                        .roleCode(userDetails.getRoleCode())
                        .build())
                .build();
    }

    @Override
    @Transactional
    public TokenRefreshResponse refreshToken(TokenRefreshRequest request) {
        log.debug("토큰 갱신 요청");

        String refreshTokenStr = request.getRefreshToken();

        // Refresh Token 조회
        RefreshTokenVO refreshToken = authMapper.findRefreshToken(refreshTokenStr);
        
        if (refreshToken == null) {
            log.warn("토큰 갱신 실패 - Refresh Token 없음");
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 토큰 유효성 검증
        if (!refreshToken.isValid()) {
            log.warn("토큰 갱신 실패 - Refresh Token 무효 (userId: {})", refreshToken.getUserId());
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // JWT 토큰 유효성 검증
        if (!jwtTokenProvider.validateToken(refreshTokenStr)) {
            log.warn("토큰 갱신 실패 - JWT 검증 실패 (userId: {})", refreshToken.getUserId());
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 사용자 정보 조회
        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserById(refreshToken.getUserId());

        // 새 Access Token 생성
        String newAccessToken = jwtTokenProvider.createAccessToken(userDetails);

        log.info("토큰 갱신 성공: userId={}", refreshToken.getUserId());

        return TokenRefreshResponse.builder()
                .accessToken(newAccessToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getAccessTokenValidityInSeconds())
                .build();
    }

    @Override
    @Transactional
    public void logout(String refreshToken) {
        log.info("로그아웃 요청");
        
        int updated = authMapper.revokeRefreshToken(refreshToken);
        
        if (updated > 0) {
            log.info("로그아웃 성공");
        } else {
            log.warn("로그아웃 - Refresh Token 없음 또는 이미 폐기됨");
        }
    }

    @Override
    @Transactional
    public void logoutAll(Long userId) {
        log.info("전체 로그아웃 요청: userId={}", userId);
        
        int updated = authMapper.revokeAllRefreshTokensByUserId(userId);
        log.info("전체 로그아웃 완료: userId={}, 폐기된 토큰 수={}", userId, updated);
    }

    /**
     * Refresh Token 저장
     * 
     * @param userId 사용자 ID
     * @param refreshToken Refresh Token 문자열
     * @param userAgent User Agent
     * @param ipAddress IP 주소
     */
    private void saveRefreshToken(Long userId, String refreshToken, String userAgent, String ipAddress) {
        RefreshTokenVO token = RefreshTokenVO.builder()
                .userId(userId)
                .refreshToken(refreshToken)
                .expiresAt(LocalDateTime.now().plusSeconds(jwtTokenProvider.getRefreshTokenValidityInSeconds()))
                .userAgent(userAgent)
                .ipAddress(ipAddress)
                .build();

        authMapper.insertRefreshToken(token);
    }
}
