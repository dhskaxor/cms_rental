package com.nt.cms.install.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 스키마 파일에 DROP TABLE IF EXISTS가 포함되는지 검증하는 테스트
 * 
 * @author CMS Team
 */
class SchemaDropTableTest {

    private static final List<String> EXPECTED_TABLES = Arrays.asList(
            "role",
            "permission",
            "role_permission",
            "user",
            "jwt_refresh_token",
            "board_group",
            "board",
            "board_permission",
            "board_post",
            "board_comment",
            "file",
            "audit_log",
            "site_config",
            "site_menu",
            "site_page",
            "common_code_group",
            "common_code",
            "site_popup",
            "rental_place",
            "rental_room",
            "rental_place_closed_rule",
            "rental_room_unavailable_slot",
            "rental_room_pricing_base",
            "rental_room_pricing_weekend_holiday",
            "rental_room_pricing_special",
            "rental_reservation"
    );

    @Test
    @DisplayName("schema-mariadb.sql에 모든 테이블에 대한 DROP TABLE IF EXISTS가 포함되어야 한다")
    void schemaMariaDb_containsDropTableForAllTables() throws Exception {
        String schema = loadSchema("schema/schema-mariadb.sql");
        Set<String> dropTables = extractDroppedTableNames(schema);

        for (String table : EXPECTED_TABLES) {
            assertTrue(dropTables.contains(table),
                    "schema-mariadb.sql에 DROP TABLE IF EXISTS " + table + " 가 없습니다.");
        }
        assertEquals(EXPECTED_TABLES.size(), dropTables.size(),
                "DROP TABLE 개수가 예상 테이블 수와 일치해야 합니다. 실제: " + dropTables);
    }

    @Test
    @DisplayName("schema-mariadb.sql에 SET FOREIGN_KEY_CHECKS = 0이 DROP 이전에 있어야 한다")
    void schemaMariaDb_containsForeignKeyChecksOffBeforeDrop() throws Exception {
        String schema = loadSchema("schema/schema-mariadb.sql");
        int fkChecksOff = schema.indexOf("SET FOREIGN_KEY_CHECKS = 0");
        int firstDrop = schema.indexOf("DROP TABLE IF EXISTS");

        assertTrue(fkChecksOff >= 0, "SET FOREIGN_KEY_CHECKS = 0 이 있어야 합니다.");
        assertTrue(firstDrop >= 0, "DROP TABLE IF EXISTS 가 있어야 합니다.");
        assertTrue(fkChecksOff < firstDrop, "SET FOREIGN_KEY_CHECKS = 0 이 DROP 보다 먼저 와야 합니다.");
    }

    @Test
    @DisplayName("schema-h2.sql에 H2 스키마의 모든 테이블에 대한 DROP TABLE IF EXISTS가 포함되어야 한다")
    void schemaH2_containsDropTableForAllTables() throws Exception {
        // H2 스키마에는 site_menu, site_page 없음
        List<String> h2Tables = Arrays.asList(
                "role", "permission", "role_permission", "user", "jwt_refresh_token",
                "board_group", "board", "board_permission", "board_post", "board_comment",
                "file", "audit_log", "site_config", "common_code_group", "common_code", "site_popup",
                "rental_place", "rental_room", "rental_place_closed_rule",
                "rental_room_unavailable_slot", "rental_room_pricing_base",
                "rental_room_pricing_weekend_holiday", "rental_room_pricing_special",
                "rental_reservation"
        );

        String schema = loadSchema("schema/schema-h2.sql");
        Set<String> dropTables = extractDroppedTableNames(schema);

        for (String table : h2Tables) {
            assertTrue(dropTables.contains(table),
                    "schema-h2.sql에 DROP TABLE IF EXISTS " + table + " 가 없습니다.");
        }
        assertEquals(h2Tables.size(), dropTables.size(),
                "DROP TABLE 개수가 예상 테이블 수와 일치해야 합니다. 실제: " + dropTables);
    }

    private String loadSchema(String path) throws Exception {
        return FileCopyUtils.copyToString(
                new java.io.InputStreamReader(
                        new ClassPathResource(path).getInputStream(),
                        StandardCharsets.UTF_8));
    }

    private Set<String> extractDroppedTableNames(String schema) {
        return Arrays.stream(schema.split("\n"))
                .map(String::trim)
                .filter(line -> line.toUpperCase().startsWith("DROP TABLE IF EXISTS"))
                .map(line -> {
                    String rest = line.substring("DROP TABLE IF EXISTS".length()).trim();
                    String tableName = rest.split("\\s+")[0].trim();
                    tableName = tableName.replaceAll(";+$", ""); // 끝 세미콜론 제거
                    // H2: "user" 같이 쌍따옴표로 감싼 식별자 정규화
                    if (tableName.startsWith("\"") && tableName.endsWith("\"")) {
                        tableName = tableName.substring(1, tableName.length() - 1);
                    }
                    return tableName;
                })
                .collect(Collectors.toSet());
    }
}
