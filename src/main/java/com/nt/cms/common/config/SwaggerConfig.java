package com.nt.cms.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger/OpenAPI 설정
 * 
 * <p>API 문서를 자동으로 생성하여 /swagger-ui.html에서 확인할 수 있다.</p>
 * 
 * @author CMS Team
 */
@Configuration
public class SwaggerConfig {

    /**
     * JWT 인증 스키마 이름
     */
    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    /**
     * OpenAPI 설정
     * 
     * @return OpenAPI
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                // API 정보
                .info(apiInfo())
                // 서버 정보
                .servers(List.of(
                        new Server().url("/").description("현재 서버")
                ))
                // 보안 설정 (JWT Bearer Token)
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, securityScheme()));
    }

    /**
     * API 정보 설정
     * 
     * @return Info
     */
    private Info apiInfo() {
        return new Info()
                .title("CMS Core Platform API")
                .description("재사용 가능한 CMS Core 시스템의 REST API 문서")
                .version("0.0.1-SNAPSHOT")
                .contact(new Contact()
                        .name("CMS Team")
                        .email("support@example.com"))
                .license(new License()
                        .name("Private License"));
    }

    /**
     * JWT 인증 스키마 설정
     * 
     * @return SecurityScheme
     */
    private SecurityScheme securityScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization")
                .description("JWT 토큰을 입력하세요. (Bearer 접두사 없이 토큰만 입력)");
    }
}
