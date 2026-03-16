package com.nt.cms.auth.controller;

import com.nt.cms.auth.dto.LoginRequest;
import com.nt.cms.auth.dto.LoginResponse;
import com.nt.cms.auth.dto.TokenRefreshRequest;
import com.nt.cms.auth.dto.TokenRefreshResponse;
import com.nt.cms.auth.security.CustomUserDetails;
import com.nt.cms.auth.service.AuthService;
import com.nt.cms.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 인증 API 컨트롤러
 * 
 * @author CMS Team
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "인증", description = "로그인, 로그아웃, 토큰 갱신 API")
public class AuthController {

    private final AuthService authService;

    /**
     * 로그인
     * 
     * @param request 로그인 요청
     * @param httpRequest HTTP 요청 (User-Agent, IP 추출용)
     * @return 로그인 응답
     */
    @PostMapping("/login")
    @Operation(summary = "로그인", description = "사용자 인증 후 JWT 토큰을 발급합니다.")
    public ApiResponse<LoginResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {
        
        String userAgent = httpRequest.getHeader("User-Agent");
        String ipAddress = getClientIp(httpRequest);
        
        LoginResponse response = authService.login(request, userAgent, ipAddress);
        return ApiResponse.success(response);
    }

    /**
     * 토큰 갱신
     * 
     * @param request 토큰 갱신 요청
     * @return 토큰 갱신 응답
     */
    @PostMapping("/refresh")
    @Operation(summary = "토큰 갱신", description = "Refresh Token으로 새로운 Access Token을 발급합니다.")
    public ApiResponse<TokenRefreshResponse> refreshToken(
            @Valid @RequestBody TokenRefreshRequest request) {
        
        TokenRefreshResponse response = authService.refreshToken(request);
        return ApiResponse.success(response);
    }

    /**
     * 로그아웃
     * 
     * @param request 토큰 갱신 요청 (Refresh Token 포함)
     * @return 성공 응답
     */
    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "현재 세션의 Refresh Token을 폐기합니다.")
    public ApiResponse<Void> logout(@Valid @RequestBody TokenRefreshRequest request) {
        authService.logout(request.getRefreshToken());
        return ApiResponse.success();
    }

    /**
     * 전체 로그아웃 (모든 디바이스)
     * 
     * @param userDetails 인증된 사용자 정보
     * @return 성공 응답
     */
    @PostMapping("/logout-all")
    @Operation(summary = "전체 로그아웃", description = "모든 디바이스에서 로그아웃합니다.")
    public ApiResponse<Void> logoutAll(@AuthenticationPrincipal CustomUserDetails userDetails) {
        authService.logoutAll(userDetails.getUserId());
        return ApiResponse.success();
    }

    /**
     * 현재 사용자 정보 조회
     * 
     * @param userDetails 인증된 사용자 정보
     * @return 사용자 정보
     */
    @GetMapping("/me")
    @Operation(summary = "내 정보", description = "현재 로그인한 사용자 정보를 조회합니다.")
    public ApiResponse<LoginResponse.UserInfo> me(@AuthenticationPrincipal CustomUserDetails userDetails) {
        LoginResponse.UserInfo userInfo = LoginResponse.UserInfo.builder()
                .id(userDetails.getUserId())
                .username(userDetails.getUsername())
                .name(userDetails.getName())
                .email(userDetails.getEmail())
                .roleCode(userDetails.getRoleCode())
                .build();
        
        return ApiResponse.success(userInfo);
    }

    /**
     * 클라이언트 IP 주소 추출
     * 
     * @param request HTTP 요청
     * @return IP 주소
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        
        // 여러 IP가 있는 경우 첫 번째 IP 반환
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        
        return ip;
    }
}
