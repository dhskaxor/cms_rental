package com.nt.cms.install.service;

import com.nt.cms.common.config.CmsProperties;
import com.nt.cms.install.dto.DatabaseConfigRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * DefaultInstallService executeSchema 통합 테스트
 * H2 인메모리 DB로 DROP 후 재생성 동작 검증
 *
 * @author CMS Team
 */
@ExtendWith(MockitoExtension.class)
class DefaultInstallServiceExecuteSchemaIntegrationTest {

    private static final String H2_JDBC_URL = "jdbc:h2:mem:install_schema_test_"
            + System.currentTimeMillis()
            + ";MODE=MariaDB;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE";

    @Mock
    private CmsProperties cmsProperties;

    @Mock
    private PasswordEncoder passwordEncoder;

    private DefaultInstallService installService;
    private DatabaseConfigRequest dbConfig;

    @BeforeEach
    void setUp() {
        installService = new DefaultInstallService(cmsProperties, passwordEncoder);
        dbConfig = new DatabaseConfigRequest();
        dbConfig.setHost("localhost");
        dbConfig.setPort(3306);
        dbConfig.setDatabaseName("cms");
        dbConfig.setUsername("sa");
        dbConfig.setPassword("");
        dbConfig.setJdbcUrlOverride(H2_JDBC_URL);
        installService.saveDatabaseConfig(dbConfig);
    }

    @Test
    @DisplayName("executeSchema 실행 시 테이블이 생성되어야 한다")
    void executeSchema_createsTables() throws Exception {
        // when: 최소 스키마로 테스트 (테스트 리소스의 schema-h2-minimal.sql)
        boolean result = installService.executeSchema("schema/schema-h2-minimal.sql");

        // then
        assertTrue(result);
        assertTableExists("role");
    }

    @Test
    @DisplayName("이미 테이블이 있는 경우 executeSchema 재실행 시 DROP 후 새로 생성되어야 한다")
    void executeSchema_whenTablesExist_dropsAndRecreates() throws Exception {
        // given: 첫 번째 실행
        assertTrue(installService.executeSchema("schema/schema-h2-minimal.sql"));
        assertTableExists("role");

        // when: 두 번째 실행 (기존 테이블 존재 상태)
        boolean result = installService.executeSchema("schema/schema-h2-minimal.sql");

        // then
        assertTrue(result);
        assertTableExists("role");
        assertEquals(0, countRows("role"));
    }

    @Test
    @DisplayName("executeSchema 재실행 후 role 테이블이 정상적으로 존재해야 한다")
    void executeSchema_twice_roleTableExists() throws Exception {
        installService.executeSchema("schema/schema-h2-minimal.sql");
        installService.executeSchema("schema/schema-h2-minimal.sql");

        assertTableExists("role");
    }

    private void assertTableExists(String tableName) throws Exception {
        try (Connection conn = DriverManager.getConnection(H2_JDBC_URL, "sa", "");
             Statement stmt = conn.createStatement()) {
            stmt.execute("SELECT COUNT(*) FROM " + tableName);
            // 예외 없이 실행되면 테이블 존재
        }
    }

    private int countRows(String tableName) throws Exception {
        try (Connection conn = DriverManager.getConnection(H2_JDBC_URL, "sa", "");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + tableName)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
}
