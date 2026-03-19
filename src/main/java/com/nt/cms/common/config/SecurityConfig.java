package com.nt.cms.common.config;

import com.nt.cms.auth.jwt.JwtAuthenticationFilter;
import com.nt.cms.auth.security.AdminSessionAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Spring Security 설정
 * 
 * <p>JWT 기반 인증(API)과 세션 기반 인증(관리자 웹)을 모두 지원한다.</p>
 * 
 * @author CMS Team
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AdminSessionAuthenticationFilter adminSessionAuthenticationFilter;
    private final CorsConfigurationSource corsConfigurationSource;

    /**
     * 인증 없이 접근 가능한 경로
     */
    private static final String[] PUBLIC_URLS = {
            // 루트 경로
            "/",
            // 설치 관련
            "/install/**",
            // 웹 로그인 관련
            "/auth/login",
            "/auth/logout",
            // API 인증 관련
            "/api/v1/auth/login",
            "/api/v1/auth/register",
            "/api/v1/auth/refresh",
            "/api/v1/auth/password-reset/**",
            // 중복 확인 API
            "/api/v1/users/check/**",
            // 공통 코드 활성 조회 (드롭다운 등 공개 API)
            "/api/v1/common-codes/active/**",
            // 팝업 노출 대상 조회 (사용자 페이지 공개 API)
            "/api/v1/popups/display",
            // 사용자단 공개 API (메뉴, 페이지, 게시판/게시글 조회 등)
            "/api/v1/public/**",
            // 사용자단 예약 캘린더 공개 조회
            "/api/v1/rental/search",
            "/api/v1/rental/search/**",
            // 관리자 페이지 (세션 기반 인증으로 별도 처리)
            "/admin/**",
            // 사용자 사이트 (세션 기반 인증으로 별도 처리)
            "/site/**",
            // Swagger
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/api-docs/**",
            "/v3/api-docs/**",
            // H2 Console (개발용)
            "/h2-console/**",
            // 정적 리소스
            "/css/**",
            "/js/**",
            "/images/**",
            "/favicon.ico",
            // 에러 페이지
            "/error"
    };

    /**
     * Security Filter Chain 설정
     * 
     * @param http HttpSecurity
     * @return SecurityFilterChain
     * @throws Exception 설정 예외
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CORS 설정 적용 (브라우저 preflight OPTIONS 허용)
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                // CSRF 비활성화 (REST API와 혼용)
                .csrf(AbstractHttpConfigurer::disable)
                
                // 세션 설정: API는 JWT, 웹은 세션 사용
                .sessionManagement(session -> 
                        session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                
                // H2 Console을 위한 frameOptions 설정
                .headers(headers -> 
                        headers.frameOptions(frameOptions -> frameOptions.sameOrigin()))
                
                // URL별 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // preflight 요청은 인증 없이 허용
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // 공개 대관 API 중 "예약 생성/내 예약 조회/상세/취소"만 인증 사용자 허용
                        .requestMatchers("/api/v1/public/rentals/rooms/*/reservations").authenticated()
                        .requestMatchers("/api/v1/public/rentals/reservations/**").authenticated()
                        .requestMatchers(PUBLIC_URLS).permitAll()
                        .anyRequest().authenticated()
                )
                
                // JWT 인증 필터 추가
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // 관리자 세션 인증 필터 (JWT 없을 때 세션으로 API 인증)
                .addFilterAfter(adminSessionAuthenticationFilter, JwtAuthenticationFilter.class);
        
        return http.build();
    }

    /**
     * AuthenticationManager Bean 등록
     * 
     * @param authenticationConfiguration 인증 설정
     * @return AuthenticationManager
     * @throws Exception 설정 예외
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * 비밀번호 인코더 (BCrypt)
     * 
     * @return PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
