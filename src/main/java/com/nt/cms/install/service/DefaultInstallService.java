package com.nt.cms.install.service;

import com.nt.cms.common.config.CmsProperties;
import com.nt.cms.common.exception.BusinessException;
import com.nt.cms.common.exception.ErrorCode;
import com.nt.cms.install.dto.AdminAccountRequest;
import com.nt.cms.install.dto.DatabaseConfigRequest;
import com.nt.cms.install.dto.SiteConfigRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 설치 서비스 구현체
 * 
 * @author CMS Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultInstallService implements InstallService {

    private final CmsProperties cmsProperties;
    private final PasswordEncoder passwordEncoder;

    /**
     * 세션 대신 사용하는 임시 저장소
     * 실제 운영에서는 세션이나 Redis를 사용 권장
     */
    private DatabaseConfigRequest tempDatabaseConfig;
    private SiteConfigRequest tempSiteConfig;

    @Override
    public boolean isInstalled() {
        return cmsProperties.isInstalled();
    }

    @Override
    public boolean testDatabaseConnection(DatabaseConfigRequest request) {
        String jdbcUrl = request.toJdbcUrl();
        log.info("데이터베이스 연결 테스트: {}", jdbcUrl);

        try (Connection conn = DriverManager.getConnection(
                jdbcUrl, request.getUsername(), request.getPassword())) {
            log.info("데이터베이스 연결 성공");
            return true;
        } catch (SQLException e) {
            log.error("데이터베이스 연결 실패: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public void saveDatabaseConfig(DatabaseConfigRequest request) {
        this.tempDatabaseConfig = request;
        log.info("데이터베이스 설정 임시 저장 완료");
    }

    @Override
    public DatabaseConfigRequest getDatabaseConfig() {
        return this.tempDatabaseConfig;
    }

    @Override
    public boolean executeSchema() {
        return executeSchema("schema/schema-mariadb.sql");
    }

    @Override
    public boolean executeSchema(String schemaPath) {
        if (tempDatabaseConfig == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "데이터베이스 설정이 없습니다.");
        }

        try (Connection conn = getConnection()) {
            String schema = readResourceFile(schemaPath);
            executeSchemaStatements(conn, schema);
            log.info("스키마 실행 완료");
            return true;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("스키마 실행 실패", e);
            return false;
        }
    }

    /**
     * 스키마 SQL 문장 실행
     * - 블록/라인 주석 제거 후 실행 (주석으로 인한 DDL 스킵 방지)
     * - DDL 실패 시 예외 전파 (성공으로 처리되지 않도록)
     */
    private void executeSchemaStatements(Connection conn, String schema) throws SQLException {
        String[] statements = schema.split(";");
        try (Statement stmt = conn.createStatement()) {
            for (String sql : statements) {
                sql = sql.trim();
                if (sql.isEmpty()) {
                    continue;
                }
                // 주석 제거 후 실제 SQL만 추출 (스킵 판단용)
                String sqlForCheck = stripSqlComments(sql).trim();
                if (sqlForCheck.isEmpty()) {
                    continue;
                }
                try {
                    stmt.execute(sql);
                } catch (SQLException e) {
                    log.warn("SQL 실행 실패: {} - {}", sql.substring(0, Math.min(80, sql.length())), e.getMessage());
                    String upper = sqlForCheck.toUpperCase();
                    if (upper.startsWith("DROP") || upper.startsWith("CREATE") || upper.startsWith("ALTER")
                            || upper.startsWith("SET")) {
                        log.error("스키마 실행 실패 - DDL 오류: {}", e.getMessage());
                        throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR,
                                "테이블 생성 실패: " + e.getMessage());
                    }
                }
            }
        }
    }

    /**
     * SQL에서 블록 주석(/* *\/)과 라인 주석(--) 제거
     */
    private String stripSqlComments(String sql) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        int len = sql.length();
        while (i < len) {
            if (i + 1 < len && sql.charAt(i) == '-' && sql.charAt(i + 1) == '-') {
                // 라인 주석: -- 부터 줄 끝까지 스킵
                while (i < len && sql.charAt(i) != '\n') {
                    i++;
                }
                if (i < len) i++; // \n 포함
            } else if (i + 1 < len && sql.charAt(i) == '/' && sql.charAt(i + 1) == '*') {
                // 블록 주석: /* */ 까지 스킵
                i += 2;
                while (i + 1 < len && !(sql.charAt(i) == '*' && sql.charAt(i + 1) == '/')) {
                    i++;
                }
                if (i + 1 < len) i += 2;
            } else if (sql.charAt(i) == '\'' || sql.charAt(i) == '"') {
                // 문자열 내부는 주석으로 처리하지 않음
                char quote = sql.charAt(i);
                sb.append(sql.charAt(i++));
                while (i < len && sql.charAt(i) != quote) {
                    if (sql.charAt(i) == '\\') i++;
                    sb.append(sql.charAt(i++));
                }
                if (i < len) sb.append(sql.charAt(i++));
            } else {
                sb.append(sql.charAt(i++));
            }
        }
        return sb.toString();
    }

    /**
     * 초기 데이터 SQL 문장 실행
     * - 블록/라인 주석 제거 후 판단 (주석으로 인한 INSERT 스킵 방지)
     */
    private void executeInitialDataStatements(Connection conn, String data) throws SQLException {
        String[] statements = data.split(";");
        try (Statement stmt = conn.createStatement()) {
            for (String sql : statements) {
                sql = sql.trim();
                if (sql.isEmpty()) continue;
                String sqlForCheck = stripSqlComments(sql).trim();
                if (sqlForCheck.isEmpty()) continue;
                try {
                    stmt.execute(sql);
                } catch (SQLException e) {
                    log.warn("초기 데이터 삽입 실패: {} - {}", sql.substring(0, Math.min(80, sql.length())), e.getMessage());
                    String upper = sqlForCheck.toUpperCase();
                    if (upper.startsWith("INSERT") || upper.startsWith("SELECT")) {
                        log.error("초기 데이터 삽입 실패: {}", e.getMessage());
                        throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR,
                                "초기 데이터 삽입 실패: " + e.getMessage());
                    }
                }
            }
        }
    }

    @Override
    public boolean insertInitialData() {
        if (tempDatabaseConfig == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "데이터베이스 설정이 없습니다.");
        }

        try (Connection conn = getConnection()) {
            // 초기 데이터 파일 읽기
            String data = readResourceFile("schema/data-init.sql");
            executeInitialDataStatements(conn, data);
            log.info("초기 데이터 삽입 완료");
            return true;
        } catch (Exception e) {
            log.error("초기 데이터 삽입 실패", e);
            return false;
        }
    }

    @Override
    public boolean createAdminAccount(AdminAccountRequest request) {
        if (tempDatabaseConfig == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "데이터베이스 설정이 없습니다.");
        }

        try (Connection conn = getConnection()) {
            // ADMIN 역할 ID 조회
            Long adminRoleId = 1L; // 기본값 (data-init.sql에서 첫 번째로 생성됨)
            Long anonymousRoleId = getRoleIdByCode(conn, "ANONYMOUS");

            // H2는 user가 예약어이므로 "user", MariaDB는 `user` 사용
            String userTable = tempDatabaseConfig.toJdbcUrl().toLowerCase().contains("h2")
                    ? "\"user\"" : "`user`";

            // 관리자 계정 삽입
            String sql = String.format("""
                INSERT INTO %s (username, password, name, email, role_id, status, created_at, deleted)
                VALUES (?, ?, ?, ?, ?, 'ACTIVE', ?, 0)
                """, userTable);

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, request.getUsername());
                pstmt.setString(2, passwordEncoder.encode(request.getPassword()));
                pstmt.setString(3, request.getName());
                pstmt.setString(4, request.getEmail());
                pstmt.setLong(5, adminRoleId);
                pstmt.setString(6, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                pstmt.executeUpdate();
            }

            // 비회원(익명) 시스템 계정 생성 (게시판 익명 글쓰기용)
            if (anonymousRoleId != null) {
                String anonymousSql = String.format("""
                    INSERT INTO %s (username, password, name, email, role_id, status, created_at, deleted)
                    VALUES ('anonymous', ?, '비회원', 'anonymous@system.local', ?, 'ACTIVE', ?, 0)
                    """, userTable);
                try (PreparedStatement pstmt = conn.prepareStatement(anonymousSql)) {
                    pstmt.setString(1, passwordEncoder.encode(java.util.UUID.randomUUID().toString()));
                    pstmt.setLong(2, anonymousRoleId);
                    pstmt.setString(3, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    pstmt.executeUpdate();
                }
                log.info("비회원 시스템 계정 생성 완료");
            }

            log.info("관리자 계정 생성 완료: {}", request.getUsername());
            return true;
        } catch (Exception e) {
            log.error("관리자 계정 생성 실패", e);
            return false;
        }
    }

    /**
     * 역할 코드로 역할 ID 조회
     */
    private Long getRoleIdByCode(Connection conn, String roleCode) throws SQLException {
        String sql = "SELECT id FROM role WHERE role_code = ? AND deleted = 0";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, roleCode);
            var rs = pstmt.executeQuery();
            return rs.next() ? rs.getLong("id") : null;
        }
    }

    @Override
    public void saveSiteConfig(SiteConfigRequest request) {
        this.tempSiteConfig = request;
        log.info("사이트 설정 임시 저장 완료");
    }

    @Override
    public boolean completeInstallation() {
        if (tempDatabaseConfig == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "데이터베이스 설정이 없습니다.");
        }

        try {
            // cms-config.yml 파일 생성
            String configContent = generateConfigYaml();
            
            // UTF-8로 저장 (시스템 기본 인코딩 사용 시 MalformedInputException 방지)
            Files.writeString(Path.of("cms-config.yml"), configContent, StandardCharsets.UTF_8);

            log.info("설치 완료 - cms-config.yml 생성됨");
            return true;
        } catch (IOException e) {
            log.error("설정 파일 생성 실패", e);
            return false;
        }
    }

    @Override
    public void resetInstallation() {
        this.tempDatabaseConfig = null;
        this.tempSiteConfig = null;
        log.info("설치 상태 초기화 완료");
    }

    /**
     * 데이터베이스 연결 획득
     */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                tempDatabaseConfig.toJdbcUrl(),
                tempDatabaseConfig.getUsername(),
                tempDatabaseConfig.getPassword()
        );
    }

    /**
     * 리소스 파일 읽기
     */
    private String readResourceFile(String path) throws IOException {
        ClassPathResource resource = new ClassPathResource(path);
        return FileCopyUtils.copyToString(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)
        );
    }

    /**
     * cms-config.yml 내용 생성
     */
    private String generateConfigYaml() {
        String siteName = tempSiteConfig != null ? tempSiteConfig.getSiteName() : "CMS Core";
        String uploadPath = tempSiteConfig != null ? tempSiteConfig.getUploadPath() : "C:/cms/files";

        String dbPassword = tempDatabaseConfig.getPassword() != null ? tempDatabaseConfig.getPassword() : "";

        return String.format("""
            # CMS Core Platform 설정 파일
            # 이 파일은 설치 마법사에 의해 자동 생성되었습니다.
            # 생성일시: %s
            # 설치 후 서버 재시작 시 이 DB로 로그인됩니다.
            
            cms:
              installed: true
              
              database:
                host: %s
                port: %d
                name: %s
                username: %s
                password: %s
              
              site:
                name: %s
              
              file:
                upload-path: %s
            """,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                tempDatabaseConfig.getHost(),
                tempDatabaseConfig.getPort(),
                tempDatabaseConfig.getDatabaseName(),
                tempDatabaseConfig.getUsername(),
                dbPassword,
                siteName,
                uploadPath.replace("\\", "/")
        );
    }
}
