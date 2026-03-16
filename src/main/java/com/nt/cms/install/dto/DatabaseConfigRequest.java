package com.nt.cms.install.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

/**
 * 데이터베이스 연결 설정 요청 DTO
 * 
 * @author CMS Team
 */
@Getter
@Setter
public class DatabaseConfigRequest {

    /**
     * 데이터베이스 호스트
     */
    @NotBlank(message = "호스트를 입력해주세요.")
    private String host = "localhost";

    /**
     * 데이터베이스 포트
     */
    @NotNull(message = "포트를 입력해주세요.")
    @Min(value = 1, message = "포트는 1 이상이어야 합니다.")
    @Max(value = 65535, message = "포트는 65535 이하여야 합니다.")
    private Integer port = 3306;

    /**
     * 데이터베이스 이름
     */
    @NotBlank(message = "데이터베이스 이름을 입력해주세요.")
    private String databaseName = "cms";

    /**
     * 데이터베이스 사용자명
     */
    @NotBlank(message = "사용자명을 입력해주세요.")
    private String username;

    /**
     * 데이터베이스 비밀번호
     */
    private String password;

    /**
     * JDBC URL 직접 지정 (테스트용, null이면 host/port/databaseName으로 생성)
     */
    private String jdbcUrlOverride;

    /**
     * JDBC URL 생성
     * jdbcUrlOverride가 설정된 경우 해당 URL 반환, 아니면 MariaDB URL 생성
     *
     * @return JDBC URL
     */
    public String toJdbcUrl() {
        if (jdbcUrlOverride != null && !jdbcUrlOverride.isBlank()) {
            return jdbcUrlOverride;
        }
        return String.format(
            "jdbc:mariadb://%s:%d/%s?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Seoul",
            host, port, databaseName
        );
    }
}
