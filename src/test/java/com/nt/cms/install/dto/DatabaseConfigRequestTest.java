package com.nt.cms.install.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DatabaseConfigRequest 테스트
 * 
 * @author CMS Team
 */
class DatabaseConfigRequestTest {

    @Test
    @DisplayName("기본값이 올바르게 설정되어야 한다")
    void defaultValues() {
        // given
        DatabaseConfigRequest request = new DatabaseConfigRequest();

        // then
        assertEquals("localhost", request.getHost());
        assertEquals(3306, request.getPort());
        assertEquals("cms", request.getDatabaseName());
    }

    @Test
    @DisplayName("JDBC URL이 올바르게 생성되어야 한다")
    void toJdbcUrl() {
        // given
        DatabaseConfigRequest request = new DatabaseConfigRequest();
        request.setHost("192.168.1.100");
        request.setPort(3307);
        request.setDatabaseName("test_cms");
        request.setUsername("admin");
        request.setPassword("password");

        // when
        String jdbcUrl = request.toJdbcUrl();

        // then
        assertTrue(jdbcUrl.contains("192.168.1.100:3307"));
        assertTrue(jdbcUrl.contains("test_cms"));
        assertTrue(jdbcUrl.contains("jdbc:mariadb://"));
        assertTrue(jdbcUrl.contains("useUnicode=true"));
        assertTrue(jdbcUrl.contains("characterEncoding=UTF-8"));
    }

    @Test
    @DisplayName("기본 포트로 JDBC URL이 생성되어야 한다")
    void toJdbcUrl_withDefaultPort() {
        // given
        DatabaseConfigRequest request = new DatabaseConfigRequest();
        request.setUsername("root");

        // when
        String jdbcUrl = request.toJdbcUrl();

        // then
        assertTrue(jdbcUrl.contains("localhost:3306"));
        assertTrue(jdbcUrl.contains("/cms?"));
    }
}
