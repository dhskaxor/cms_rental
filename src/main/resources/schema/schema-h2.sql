-- ==============================================
-- CMS Core Platform - H2 스키마 (테스트/개발용)
-- MariaDB 호환 모드
-- 기존 테이블이 있으면 모두 삭제 후 재생성
-- ==============================================

SET REFERENTIAL_INTEGRITY FALSE;

-- 기존 테이블 DROP (의존성 역순)
DROP TABLE IF EXISTS site_config;
DROP TABLE IF EXISTS site_popup;
DROP TABLE IF EXISTS common_code;
DROP TABLE IF EXISTS common_code_group;
DROP TABLE IF EXISTS audit_log;
DROP TABLE IF EXISTS file;
DROP TABLE IF EXISTS board_comment;
DROP TABLE IF EXISTS board_post;
DROP TABLE IF EXISTS board_permission;
DROP TABLE IF EXISTS board;
DROP TABLE IF EXISTS board_group;
DROP TABLE IF EXISTS jwt_refresh_token;
DROP TABLE IF EXISTS "user";
DROP TABLE IF EXISTS role_permission;
DROP TABLE IF EXISTS permission;
DROP TABLE IF EXISTS role;

SET REFERENTIAL_INTEGRITY TRUE;

-- =========================
-- 역할 테이블
-- =========================
CREATE TABLE IF NOT EXISTS role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_code VARCHAR(50) NOT NULL UNIQUE,
    role_name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    created_at DATETIME NOT NULL,
    created_by BIGINT,
    updated_at DATETIME,
    updated_by BIGINT,
    deleted TINYINT DEFAULT 0
);
CREATE INDEX IF NOT EXISTS idx_role_deleted ON role(deleted);


-- =========================
-- 권한 테이블
-- =========================
CREATE TABLE IF NOT EXISTS permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    permission_code VARCHAR(100) NOT NULL UNIQUE,
    permission_name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    created_at DATETIME NOT NULL,
    created_by BIGINT,
    updated_at DATETIME,
    updated_by BIGINT,
    deleted TINYINT DEFAULT 0
);
CREATE INDEX IF NOT EXISTS idx_permission_deleted ON permission(deleted);


-- =========================
-- 역할-권한 매핑 테이블
-- =========================
CREATE TABLE IF NOT EXISTS role_permission (
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_rp_role FOREIGN KEY (role_id) REFERENCES role(id),
    CONSTRAINT fk_rp_permission FOREIGN KEY (permission_id) REFERENCES permission(id)
);


-- =========================
-- 사용자 테이블 (H2에서 user는 예약어이므로 쌍따옴표로 감쌈)
-- =========================
CREATE TABLE IF NOT EXISTS "user" (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(100),
    email VARCHAR(150),
    role_id BIGINT NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    login_fail_count INT DEFAULT 0,
    last_login_at DATETIME,
    password_reset_token VARCHAR(255),
    password_reset_expires_at DATETIME,
    created_at DATETIME NOT NULL,
    created_by BIGINT,
    updated_at DATETIME,
    updated_by BIGINT,
    deleted TINYINT DEFAULT 0,
    CONSTRAINT fk_user_role FOREIGN KEY (role_id) REFERENCES role(id)
);
CREATE INDEX IF NOT EXISTS idx_user_role ON "user"(role_id);
CREATE INDEX IF NOT EXISTS idx_user_status ON "user"(status);
CREATE INDEX IF NOT EXISTS idx_user_deleted ON "user"(deleted);
CREATE INDEX IF NOT EXISTS idx_user_email ON "user"(email);


-- =========================
-- JWT 리프레시 토큰 테이블
-- =========================
CREATE TABLE IF NOT EXISTS jwt_refresh_token (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    refresh_token VARCHAR(500) NOT NULL,
    expires_at DATETIME NOT NULL,
    revoked TINYINT DEFAULT 0,
    user_agent VARCHAR(255),
    ip_address VARCHAR(100),
    created_at DATETIME NOT NULL,
    deleted TINYINT DEFAULT 0,
    CONSTRAINT fk_jwt_user FOREIGN KEY (user_id) REFERENCES "user"(id)
);
CREATE INDEX IF NOT EXISTS idx_jwt_user ON jwt_refresh_token(user_id);
CREATE INDEX IF NOT EXISTS idx_jwt_expires ON jwt_refresh_token(expires_at);


-- =========================
-- 게시판 그룹 테이블
-- =========================
CREATE TABLE IF NOT EXISTS board_group (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    group_code VARCHAR(50) NOT NULL UNIQUE,
    group_name VARCHAR(100) NOT NULL,
    sort_order INT DEFAULT 0,
    created_at DATETIME NOT NULL,
    created_by BIGINT,
    updated_at DATETIME,
    updated_by BIGINT,
    deleted TINYINT DEFAULT 0
);
CREATE INDEX IF NOT EXISTS idx_board_group_deleted ON board_group(deleted);


-- =========================
-- 게시판 테이블
-- =========================
CREATE TABLE IF NOT EXISTS board (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    board_code VARCHAR(50) NOT NULL UNIQUE,
    board_name VARCHAR(100) NOT NULL,
    group_id BIGINT,
    description VARCHAR(500),
    use_comment TINYINT DEFAULT 1,
    use_file TINYINT DEFAULT 1,
    max_file_count INT DEFAULT 5,
    use_secret TINYINT DEFAULT 0,
    page_size INT DEFAULT 10,
    template_code VARCHAR(50) DEFAULT 'default',
    use_editor TINYINT DEFAULT 0,
    sort_order INT DEFAULT 0,
    created_at DATETIME NOT NULL,
    created_by BIGINT,
    updated_at DATETIME,
    updated_by BIGINT,
    deleted TINYINT DEFAULT 0,
    CONSTRAINT fk_board_group FOREIGN KEY (group_id) REFERENCES board_group(id)
);
CREATE INDEX IF NOT EXISTS idx_board_group ON board(group_id);
CREATE INDEX IF NOT EXISTS idx_board_deleted ON board(deleted);


-- =========================
-- 게시판 권한 테이블
-- =========================
CREATE TABLE IF NOT EXISTS board_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    board_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    can_list TINYINT DEFAULT 1,
    can_read TINYINT DEFAULT 1,
    can_create TINYINT DEFAULT 0,
    can_update TINYINT DEFAULT 0,
    can_delete TINYINT DEFAULT 0,
    created_at DATETIME NOT NULL,
    CONSTRAINT uk_board_role UNIQUE (board_id, role_id),
    CONSTRAINT fk_bp_board FOREIGN KEY (board_id) REFERENCES board(id),
    CONSTRAINT fk_bp_role FOREIGN KEY (role_id) REFERENCES role(id)
);
CREATE INDEX IF NOT EXISTS idx_board_perm_role ON board_permission(role_id);


-- =========================
-- 게시글 테이블
-- =========================
CREATE TABLE IF NOT EXISTS board_post (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    board_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    content CLOB,
    writer_id BIGINT NOT NULL,
    view_count INT DEFAULT 0,
    is_notice TINYINT DEFAULT 0,
    is_secret TINYINT DEFAULT 0,
    created_at DATETIME NOT NULL,
    created_by BIGINT,
    updated_at DATETIME,
    updated_by BIGINT,
    deleted TINYINT DEFAULT 0,
    CONSTRAINT fk_post_board FOREIGN KEY (board_id) REFERENCES board(id),
    CONSTRAINT fk_post_writer FOREIGN KEY (writer_id) REFERENCES "user"(id)
);
CREATE INDEX IF NOT EXISTS idx_post_board_created ON board_post(board_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_post_writer ON board_post(writer_id);
CREATE INDEX IF NOT EXISTS idx_post_deleted ON board_post(deleted);


-- =========================
-- 댓글 테이블
-- =========================
CREATE TABLE IF NOT EXISTS board_comment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    post_id BIGINT NOT NULL,
    parent_id BIGINT,
    content CLOB NOT NULL,
    writer_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    deleted TINYINT DEFAULT 0,
    CONSTRAINT fk_comment_post FOREIGN KEY (post_id) REFERENCES board_post(id),
    CONSTRAINT fk_comment_parent FOREIGN KEY (parent_id) REFERENCES board_comment(id),
    CONSTRAINT fk_comment_writer FOREIGN KEY (writer_id) REFERENCES "user"(id)
);
CREATE INDEX IF NOT EXISTS idx_comment_post ON board_comment(post_id);
CREATE INDEX IF NOT EXISTS idx_comment_parent ON board_comment(parent_id);
CREATE INDEX IF NOT EXISTS idx_comment_deleted ON board_comment(deleted);


-- =========================
-- 파일 테이블
-- =========================
CREATE TABLE IF NOT EXISTS file (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    ref_type VARCHAR(50),
    ref_id BIGINT,
    original_name VARCHAR(255),
    stored_name VARCHAR(255),
    file_path VARCHAR(500),
    file_size BIGINT,
    mime_type VARCHAR(100),
    file_extension VARCHAR(20),
    created_at DATETIME NOT NULL,
    created_by BIGINT,
    deleted TINYINT DEFAULT 0
);
CREATE INDEX IF NOT EXISTS idx_file_ref ON file(ref_type, ref_id);
CREATE INDEX IF NOT EXISTS idx_file_deleted ON file(deleted);


-- =========================
-- 감사 로그 테이블
-- =========================
CREATE TABLE IF NOT EXISTS audit_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    username VARCHAR(100),
    action VARCHAR(100),
    target_type VARCHAR(50),
    target_id BIGINT,
    before_data CLOB,
    after_data CLOB,
    ip_address VARCHAR(100),
    user_agent VARCHAR(255),
    created_at DATETIME NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_audit_user ON audit_log(user_id);
CREATE INDEX IF NOT EXISTS idx_audit_target ON audit_log(target_type, target_id);
CREATE INDEX IF NOT EXISTS idx_audit_action ON audit_log(action);
CREATE INDEX IF NOT EXISTS idx_audit_created ON audit_log(created_at);


-- =========================
-- 사이트 설정 테이블 (단일 행)
-- =========================
CREATE TABLE IF NOT EXISTS site_config (
    id BIGINT PRIMARY KEY,
    site_name VARCHAR(200),
    favicon_file_id BIGINT NULL,
    seo_title VARCHAR(200),
    seo_description VARCHAR(500),
    seo_keywords VARCHAR(500),
    company_address VARCHAR(500),
    company_phone VARCHAR(50),
    admin_email VARCHAR(200),
    created_at DATETIME NOT NULL,
    created_by BIGINT,
    updated_at DATETIME,
    updated_by BIGINT,
    deleted TINYINT DEFAULT 0,
    CONSTRAINT fk_site_config_file FOREIGN KEY (favicon_file_id) REFERENCES file(id)
);
CREATE INDEX IF NOT EXISTS idx_site_config_deleted ON site_config(deleted);


-- =========================
-- 공통 코드 그룹 테이블
-- =========================
CREATE TABLE IF NOT EXISTS common_code_group (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    group_code VARCHAR(100) NOT NULL UNIQUE,
    group_name VARCHAR(200) NOT NULL,
    description VARCHAR(500),
    is_system TINYINT DEFAULT 0,
    is_active TINYINT DEFAULT 1,
    created_at DATETIME NOT NULL,
    created_by BIGINT,
    updated_at DATETIME,
    updated_by BIGINT,
    deleted TINYINT DEFAULT 0
);
CREATE INDEX IF NOT EXISTS idx_ccg_active ON common_code_group(is_active);
CREATE INDEX IF NOT EXISTS idx_ccg_deleted ON common_code_group(deleted);

-- =========================
-- 공통 코드 테이블
-- =========================
CREATE TABLE IF NOT EXISTS common_code (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    group_id BIGINT NOT NULL,
    parent_id BIGINT NULL,
    depth INT NOT NULL DEFAULT 1,
    code VARCHAR(100) NOT NULL,
    code_name VARCHAR(200) NOT NULL,
    code_value VARCHAR(500),
    sort_order INT NOT NULL DEFAULT 0,
    is_active TINYINT DEFAULT 1,
    is_system TINYINT DEFAULT 0,
    start_at DATETIME,
    end_at DATETIME,
    created_at DATETIME NOT NULL,
    created_by BIGINT,
    updated_at DATETIME,
    updated_by BIGINT,
    deleted TINYINT DEFAULT 0,
    CONSTRAINT uk_cc_group_code UNIQUE (group_id, code),
    CONSTRAINT fk_cc_group FOREIGN KEY (group_id) REFERENCES common_code_group(id),
    CONSTRAINT fk_cc_parent FOREIGN KEY (parent_id) REFERENCES common_code(id)
);
CREATE INDEX IF NOT EXISTS idx_cc_group ON common_code(group_id);
CREATE INDEX IF NOT EXISTS idx_cc_parent ON common_code(parent_id);
CREATE INDEX IF NOT EXISTS idx_cc_sort ON common_code(sort_order);
CREATE INDEX IF NOT EXISTS idx_cc_active ON common_code(is_active);
CREATE INDEX IF NOT EXISTS idx_cc_deleted ON common_code(deleted);


-- =========================
-- 사용자 사이트 팝업 테이블
-- =========================
CREATE TABLE IF NOT EXISTS site_popup (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    popup_code VARCHAR(100) NOT NULL UNIQUE,
    popup_name VARCHAR(200) NOT NULL,
    popup_type VARCHAR(30) NOT NULL DEFAULT 'LAYER',
    position_type VARCHAR(30) NOT NULL DEFAULT 'MAIN',
    device_type VARCHAR(30) NOT NULL DEFAULT 'ALL',
    width INT,
    height INT,
    pos_x INT DEFAULT 0,
    pos_y INT DEFAULT 0,
    content CLOB,
    link_url VARCHAR(500),
    link_target VARCHAR(20) DEFAULT '_self',
    is_login_required TINYINT DEFAULT 0,
    is_today_close_enabled TINYINT DEFAULT 1,
    start_at TIMESTAMP,
    end_at TIMESTAMP,
    sort_order INT NOT NULL DEFAULT 0,
    is_active TINYINT DEFAULT 1,
    is_published TINYINT DEFAULT 1,
    created_at TIMESTAMP NOT NULL,
    created_by BIGINT,
    updated_at TIMESTAMP,
    updated_by BIGINT,
    deleted TINYINT DEFAULT 0
);
CREATE INDEX IF NOT EXISTS idx_popup_active ON site_popup(is_active);
CREATE INDEX IF NOT EXISTS idx_popup_period ON site_popup(start_at, end_at);
CREATE INDEX IF NOT EXISTS idx_popup_device ON site_popup(device_type);
CREATE INDEX IF NOT EXISTS idx_popup_deleted ON site_popup(deleted);
