-- ==============================================
-- CMS Core Platform - MariaDB 스키마
-- ==============================================
-- 기존 테이블이 있으면 모두 삭제 후 재생성 (FOREIGN_KEY_CHECKS=0으로 순서 무관)

SET FOREIGN_KEY_CHECKS = 0;

-- 기존 테이블 DROP (의존성 역순)
DROP TABLE IF EXISTS site_config;
DROP TABLE IF EXISTS site_popup;
DROP TABLE IF EXISTS common_code;
DROP TABLE IF EXISTS common_code_group;
DROP TABLE IF EXISTS site_page;
DROP TABLE IF EXISTS site_menu;
DROP TABLE IF EXISTS audit_log;
DROP TABLE IF EXISTS file;
DROP TABLE IF EXISTS board_comment;
DROP TABLE IF EXISTS board_post;
DROP TABLE IF EXISTS board_permission;
DROP TABLE IF EXISTS board;
DROP TABLE IF EXISTS board_group;
DROP TABLE IF EXISTS jwt_refresh_token;
DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS role_permission;
DROP TABLE IF EXISTS permission;
DROP TABLE IF EXISTS role;

-- =========================
-- 역할 테이블
-- =========================
CREATE TABLE IF NOT EXISTS role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'Primary Key',
    role_code VARCHAR(50) NOT NULL UNIQUE COMMENT '역할 고유 코드 (ADMIN, MANAGER, USER)',
    role_name VARCHAR(100) NOT NULL COMMENT '역할 표시명',
    description VARCHAR(255) COMMENT '역할 설명',
    created_at DATETIME NOT NULL COMMENT '생성 일시',
    created_by BIGINT COMMENT '생성자 ID',
    updated_at DATETIME COMMENT '수정 일시',
    updated_by BIGINT COMMENT '수정자 ID',
    deleted TINYINT(1) DEFAULT 0 COMMENT 'Soft Delete 플래그 (0:활성, 1:삭제)',
    INDEX idx_role_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='역할 마스터 테이블';


-- =========================
-- 권한 테이블
-- =========================
CREATE TABLE IF NOT EXISTS permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'Primary Key',
    permission_code VARCHAR(100) NOT NULL UNIQUE COMMENT '권한 고유 코드',
    permission_name VARCHAR(100) NOT NULL COMMENT '권한명',
    description VARCHAR(255) COMMENT '권한 설명',
    created_at DATETIME NOT NULL COMMENT '생성 일시',
    created_by BIGINT COMMENT '생성자 ID',
    updated_at DATETIME COMMENT '수정 일시',
    updated_by BIGINT COMMENT '수정자 ID',
    deleted TINYINT(1) DEFAULT 0 COMMENT 'Soft Delete 플래그',
    INDEX idx_permission_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='권한 마스터 테이블';


-- =========================
-- 역할-권한 매핑 테이블
-- =========================
CREATE TABLE IF NOT EXISTS role_permission (
    role_id BIGINT NOT NULL COMMENT '역할 ID',
    permission_id BIGINT NOT NULL COMMENT '권한 ID',
    PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_rp_role FOREIGN KEY (role_id) REFERENCES role(id),
    CONSTRAINT fk_rp_permission FOREIGN KEY (permission_id) REFERENCES permission(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='역할-권한 매핑 테이블';


-- =========================
-- 사용자 테이블
-- =========================
CREATE TABLE IF NOT EXISTS user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'Primary Key',
    username VARCHAR(100) NOT NULL UNIQUE COMMENT '로그인 아이디',
    password VARCHAR(255) NOT NULL COMMENT '암호화된 비밀번호',
    name VARCHAR(100) COMMENT '사용자 이름',
    email VARCHAR(150) COMMENT '이메일',
    role_id BIGINT NOT NULL COMMENT '역할 ID',
    status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '상태 (ACTIVE/LOCKED)',
    login_fail_count INT DEFAULT 0 COMMENT '로그인 실패 횟수',
    last_login_at DATETIME COMMENT '마지막 로그인 일시',
    password_reset_token VARCHAR(255) COMMENT '비밀번호 재설정 토큰',
    password_reset_expires_at DATETIME COMMENT '비밀번호 재설정 토큰 만료 일시',
    created_at DATETIME NOT NULL COMMENT '생성 일시',
    created_by BIGINT COMMENT '생성자 ID',
    updated_at DATETIME COMMENT '수정 일시',
    updated_by BIGINT COMMENT '수정자 ID',
    deleted TINYINT(1) DEFAULT 0 COMMENT 'Soft Delete 플래그',
    INDEX idx_user_role (role_id),
    INDEX idx_user_status (status),
    INDEX idx_user_deleted (deleted),
    INDEX idx_user_email (email),
    CONSTRAINT fk_user_role FOREIGN KEY (role_id) REFERENCES role(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='사용자 테이블';


-- =========================
-- JWT 리프레시 토큰 테이블
-- =========================
CREATE TABLE IF NOT EXISTS jwt_refresh_token (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'Primary Key',
    user_id BIGINT NOT NULL COMMENT '사용자 ID',
    refresh_token VARCHAR(500) NOT NULL COMMENT '리프레시 토큰',
    expires_at DATETIME NOT NULL COMMENT '만료 일시',
    revoked TINYINT(1) DEFAULT 0 COMMENT '폐기 여부',
    user_agent VARCHAR(255) COMMENT '클라이언트 User Agent',
    ip_address VARCHAR(100) COMMENT '클라이언트 IP',
    created_at DATETIME NOT NULL COMMENT '생성 일시',
    deleted TINYINT(1) DEFAULT 0 COMMENT 'Soft Delete 플래그',
    INDEX idx_jwt_user (user_id),
    INDEX idx_jwt_expires (expires_at),
    INDEX idx_jwt_token (refresh_token(255)),
    CONSTRAINT fk_jwt_user FOREIGN KEY (user_id) REFERENCES user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='JWT 리프레시 토큰 테이블';


-- =========================
-- 게시판 그룹 테이블
-- =========================
CREATE TABLE IF NOT EXISTS board_group (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'Primary Key',
    group_code VARCHAR(50) NOT NULL UNIQUE COMMENT '그룹 코드',
    group_name VARCHAR(100) NOT NULL COMMENT '그룹명',
    sort_order INT DEFAULT 0 COMMENT '정렬 순서',
    created_at DATETIME NOT NULL COMMENT '생성 일시',
    created_by BIGINT COMMENT '생성자 ID',
    updated_at DATETIME COMMENT '수정 일시',
    updated_by BIGINT COMMENT '수정자 ID',
    deleted TINYINT(1) DEFAULT 0 COMMENT 'Soft Delete 플래그',
    INDEX idx_board_group_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='게시판 그룹 테이블';


-- =========================
-- 게시판 테이블
-- =========================
CREATE TABLE IF NOT EXISTS board (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'Primary Key',
    board_code VARCHAR(50) NOT NULL UNIQUE COMMENT '게시판 코드',
    board_name VARCHAR(100) NOT NULL COMMENT '게시판명',
    group_id BIGINT COMMENT '게시판 그룹 ID',
    description VARCHAR(500) COMMENT '게시판 설명',
    use_comment TINYINT(1) DEFAULT 1 COMMENT '댓글 사용 여부',
    use_file TINYINT(1) DEFAULT 1 COMMENT '파일 첨부 사용 여부',
    max_file_count INT DEFAULT 5 COMMENT '최대 파일 첨부 개수',
    use_secret TINYINT(1) DEFAULT 0 COMMENT '비밀글 사용 여부',
    page_size INT DEFAULT 10 COMMENT '페이지당 게시글 수',
    template_code VARCHAR(50) DEFAULT 'default' COMMENT '사용자 사이트 게시판 템플릿 코드 (default, card 등)',
    use_editor TINYINT(1) DEFAULT 0 COMMENT '리치 에디터(WYSIWYG) 사용 여부',
    sort_order INT DEFAULT 0 COMMENT '정렬 순서',
    created_at DATETIME NOT NULL COMMENT '생성 일시',
    created_by BIGINT COMMENT '생성자 ID',
    updated_at DATETIME COMMENT '수정 일시',
    updated_by BIGINT COMMENT '수정자 ID',
    deleted TINYINT(1) DEFAULT 0 COMMENT 'Soft Delete 플래그',
    INDEX idx_board_group (group_id),
    INDEX idx_board_deleted (deleted),
    CONSTRAINT fk_board_group FOREIGN KEY (group_id) REFERENCES board_group(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='게시판 설정 테이블';


-- =========================
-- 게시판 권한 테이블
-- =========================
CREATE TABLE IF NOT EXISTS board_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'Primary Key',
    board_id BIGINT NOT NULL COMMENT '게시판 ID',
    role_id BIGINT NOT NULL COMMENT '역할 ID',
    can_list TINYINT(1) DEFAULT 1 COMMENT '목록 조회 권한',
    can_read TINYINT(1) DEFAULT 1 COMMENT '상세 조회 권한',
    can_create TINYINT(1) DEFAULT 0 COMMENT '작성 권한',
    can_update TINYINT(1) DEFAULT 0 COMMENT '수정 권한',
    can_delete TINYINT(1) DEFAULT 0 COMMENT '삭제 권한',
    created_at DATETIME NOT NULL COMMENT '생성 일시',
    UNIQUE KEY uk_board_role (board_id, role_id),
    INDEX idx_board_perm_role (role_id),
    CONSTRAINT fk_bp_board FOREIGN KEY (board_id) REFERENCES board(id),
    CONSTRAINT fk_bp_role FOREIGN KEY (role_id) REFERENCES role(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='게시판 권한 테이블';


-- =========================
-- 게시글 테이블
-- =========================
CREATE TABLE IF NOT EXISTS board_post (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'Primary Key',
    board_id BIGINT NOT NULL COMMENT '게시판 ID',
    title VARCHAR(255) NOT NULL COMMENT '제목',
    content TEXT COMMENT '내용',
    writer_id BIGINT NOT NULL COMMENT '작성자 ID',
    view_count INT DEFAULT 0 COMMENT '조회수',
    is_notice TINYINT(1) DEFAULT 0 COMMENT '공지 여부',
    is_secret TINYINT(1) DEFAULT 0 COMMENT '비밀글 여부',
    created_at DATETIME NOT NULL COMMENT '생성 일시',
    created_by BIGINT COMMENT '생성자 ID',
    updated_at DATETIME COMMENT '수정 일시',
    updated_by BIGINT COMMENT '수정자 ID',
    deleted TINYINT(1) DEFAULT 0 COMMENT 'Soft Delete 플래그',
    INDEX idx_post_board_created (board_id, created_at DESC),
    INDEX idx_post_writer (writer_id),
    INDEX idx_post_deleted (deleted),
    INDEX idx_post_notice (is_notice, created_at DESC),
    FULLTEXT INDEX ft_post_title_content (title, content),
    CONSTRAINT fk_post_board FOREIGN KEY (board_id) REFERENCES board(id),
    CONSTRAINT fk_post_writer FOREIGN KEY (writer_id) REFERENCES user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='게시글 테이블';


-- =========================
-- 댓글 테이블
-- =========================
CREATE TABLE IF NOT EXISTS board_comment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'Primary Key',
    post_id BIGINT NOT NULL COMMENT '게시글 ID',
    parent_id BIGINT COMMENT '부모 댓글 ID (대댓글)',
    content TEXT NOT NULL COMMENT '댓글 내용',
    writer_id BIGINT NOT NULL COMMENT '작성자 ID',
    created_at DATETIME NOT NULL COMMENT '생성 일시',
    updated_at DATETIME COMMENT '수정 일시',
    deleted TINYINT(1) DEFAULT 0 COMMENT 'Soft Delete 플래그',
    INDEX idx_comment_post (post_id),
    INDEX idx_comment_parent (parent_id),
    INDEX idx_comment_deleted (deleted),
    CONSTRAINT fk_comment_post FOREIGN KEY (post_id) REFERENCES board_post(id),
    CONSTRAINT fk_comment_parent FOREIGN KEY (parent_id) REFERENCES board_comment(id),
    CONSTRAINT fk_comment_writer FOREIGN KEY (writer_id) REFERENCES user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='댓글 테이블';


-- =========================
-- 파일 테이블
-- =========================
CREATE TABLE IF NOT EXISTS file (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'Primary Key',
    ref_type VARCHAR(50) COMMENT '참조 엔티티 타입 (POST, COMMENT 등)',
    ref_id BIGINT COMMENT '참조 엔티티 ID',
    original_name VARCHAR(255) COMMENT '원본 파일명',
    stored_name VARCHAR(255) COMMENT '저장 파일명',
    file_path VARCHAR(500) COMMENT '파일 경로',
    file_size BIGINT COMMENT '파일 크기 (bytes)',
    mime_type VARCHAR(100) COMMENT 'MIME 타입',
    file_extension VARCHAR(20) COMMENT '파일 확장자',
    created_at DATETIME NOT NULL COMMENT '생성 일시',
    created_by BIGINT COMMENT '업로더 ID',
    deleted TINYINT(1) DEFAULT 0 COMMENT 'Soft Delete 플래그',
    INDEX idx_file_ref (ref_type, ref_id),
    INDEX idx_file_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='파일 테이블';


-- =========================
-- 감사 로그 테이블
-- =========================
CREATE TABLE IF NOT EXISTS audit_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'Primary Key',
    user_id BIGINT COMMENT '사용자 ID',
    username VARCHAR(100) COMMENT '사용자명 (비정규화)',
    action VARCHAR(100) COMMENT '액션 타입 (CREATE, UPDATE, DELETE)',
    target_type VARCHAR(50) COMMENT '대상 엔티티 타입',
    target_id BIGINT COMMENT '대상 엔티티 ID',
    before_data TEXT COMMENT '변경 전 데이터 (JSON)',
    after_data TEXT COMMENT '변경 후 데이터 (JSON)',
    ip_address VARCHAR(100) COMMENT '클라이언트 IP',
    user_agent VARCHAR(255) COMMENT 'User Agent',
    created_at DATETIME NOT NULL COMMENT '생성 일시',
    INDEX idx_audit_user (user_id),
    INDEX idx_audit_target (target_type, target_id),
    INDEX idx_audit_action (action),
    INDEX idx_audit_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='감사 로그 테이블';

/* =========================
   사이트 설정 테이블 (단일 행)
   ========================= */
CREATE TABLE IF NOT EXISTS site_config (
    id BIGINT PRIMARY KEY COMMENT 'Primary Key (항상 1)',
    site_name VARCHAR(200) COMMENT '사이트명',
    favicon_file_id BIGINT NULL COMMENT 'favicon 파일 ID (file 테이블 FK)',
    seo_title VARCHAR(200) COMMENT 'SEO 기본 제목',
    seo_description VARCHAR(500) COMMENT 'SEO 기본 설명',
    seo_keywords VARCHAR(500) COMMENT 'SEO 기본 키워드',
    company_address VARCHAR(500) COMMENT '회사 주소',
    company_phone VARCHAR(50) COMMENT '회사 전화번호',
    admin_email VARCHAR(200) COMMENT '관리자 이메일',
    created_at DATETIME NOT NULL COMMENT '생성 일시',
    created_by BIGINT COMMENT '생성자 ID',
    updated_at DATETIME COMMENT '수정 일시',
    updated_by BIGINT COMMENT '수정자 ID',
    deleted TINYINT(1) DEFAULT 0 COMMENT 'Soft Delete 플래그',
    INDEX idx_site_config_deleted (deleted),
    CONSTRAINT fk_site_config_file FOREIGN KEY (favicon_file_id) REFERENCES file(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='사이트 설정 (단일 행)';

/* =========================
   사용자 사이트 메뉴 테이블
   - board_id FK는 선택적 참조로 제외 (errno 150 방지, 애플리케이션에서 검증)
   - parent_id self-FK는 테이블 생성 후 ALTER로 추가
   ========================= */
CREATE TABLE IF NOT EXISTS site_menu (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'Primary Key',

    parent_id BIGINT NULL COMMENT '상위 메뉴 ID (NULL이면 최상위)',
    depth INT NOT NULL DEFAULT 1 COMMENT '메뉴 깊이',

    menu_name VARCHAR(200) NOT NULL COMMENT '메뉴명',
    menu_code VARCHAR(100) NOT NULL UNIQUE COMMENT '메뉴 고유 코드',

    menu_type VARCHAR(30) NOT NULL DEFAULT 'PAGE'
        COMMENT '메뉴 유형 (PAGE, BOARD, LINK)',

    url_path VARCHAR(255) COMMENT '프론트 URL 경로 (/about, /board/free)',
    link_url VARCHAR(500) COMMENT '외부 링크 URL',

    board_id BIGINT COMMENT '게시판 연결 시 board_id',
    page_id BIGINT COMMENT '페이지 연결 시 page_id',

    icon VARCHAR(100) COMMENT '아이콘 클래스명',
    target VARCHAR(20) DEFAULT '_self'
        COMMENT '링크 타겟 (_self, _blank)',

    sort_order INT NOT NULL DEFAULT 0 COMMENT '정렬 순서',

    is_visible TINYINT(1) DEFAULT 1 COMMENT '노출 여부',
    is_login_required TINYINT(1) DEFAULT 0 COMMENT '로그인 필요 여부',

    start_at DATETIME COMMENT '노출 시작일',
    end_at DATETIME COMMENT '노출 종료일',

    seo_title VARCHAR(255) COMMENT 'SEO 제목',
    seo_description VARCHAR(500) COMMENT 'SEO 설명',

    created_at DATETIME NOT NULL COMMENT '생성일시',
    created_by BIGINT COMMENT '생성자',
    updated_at DATETIME COMMENT '수정일시',
    updated_by BIGINT COMMENT '수정자',
    deleted TINYINT(1) DEFAULT 0 COMMENT '삭제 여부',

    INDEX idx_site_menu_parent (parent_id),
    INDEX idx_site_menu_depth (depth),
    INDEX idx_site_menu_sort (sort_order),
    INDEX idx_site_menu_visible (is_visible),
    INDEX idx_site_menu_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='사용자 사이트 메뉴 관리 테이블';

ALTER TABLE site_menu ADD CONSTRAINT fk_site_menu_parent
    FOREIGN KEY (parent_id) REFERENCES site_menu(id) ON DELETE SET NULL;

/* =========================
   사용자 정적 페이지 테이블
   ========================= */
CREATE TABLE IF NOT EXISTS site_page (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'Primary Key',

    page_code VARCHAR(100) NOT NULL UNIQUE COMMENT '페이지 고유 코드',
    page_title VARCHAR(200) NOT NULL COMMENT '페이지 제목',
    content LONGTEXT COMMENT '페이지 HTML 내용',

    is_published TINYINT(1) DEFAULT 1 COMMENT '게시 여부',
    template_code VARCHAR(50) DEFAULT 'default' COMMENT '사용자 사이트 페이지 템플릿 코드 (default, page_01 등)',

    created_at DATETIME NOT NULL COMMENT '생성일시',
    created_by BIGINT COMMENT '생성자',
    updated_at DATETIME COMMENT '수정일시',
    updated_by BIGINT COMMENT '수정자',
    deleted TINYINT(1) DEFAULT 0 COMMENT '삭제 여부',

    INDEX idx_site_page_deleted (deleted)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='사용자 정적 페이지 테이블';


/* =========================
   공통 코드 그룹 테이블
   ========================= */
CREATE TABLE IF NOT EXISTS common_code_group (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'Primary Key',

    group_code VARCHAR(100) NOT NULL UNIQUE COMMENT '코드 그룹 고유 코드',
    group_name VARCHAR(200) NOT NULL COMMENT '코드 그룹명',
    description VARCHAR(500) COMMENT '설명',

    is_system TINYINT(1) DEFAULT 0 COMMENT '시스템 코드 여부 (수정 제한)',
    is_active TINYINT(1) DEFAULT 1 COMMENT '활성 여부',

    created_at DATETIME NOT NULL COMMENT '생성일시',
    created_by BIGINT COMMENT '생성자',
    updated_at DATETIME COMMENT '수정일시',
    updated_by BIGINT COMMENT '수정자',
    deleted TINYINT(1) DEFAULT 0 COMMENT '삭제 여부',

    INDEX idx_ccg_active (is_active),
    INDEX idx_ccg_deleted (deleted)

) ENGINE=InnoDB COMMENT='공통 코드 그룹 관리 테이블';

/* =========================
   공통 코드 테이블
   ========================= */
CREATE TABLE IF NOT EXISTS common_code (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'Primary Key',

    group_id BIGINT NOT NULL COMMENT '코드 그룹 ID',
    parent_id BIGINT NULL COMMENT '상위 코드 ID (계층 구조 지원)',
    depth INT NOT NULL DEFAULT 1 COMMENT '코드 깊이',

    code VARCHAR(100) NOT NULL COMMENT '코드 값',
    code_name VARCHAR(200) NOT NULL COMMENT '코드명',
    code_value VARCHAR(500) COMMENT '실제 저장 값 (선택)',

    sort_order INT NOT NULL DEFAULT 0 COMMENT '정렬 순서',

    is_active TINYINT(1) DEFAULT 1 COMMENT '사용 여부',
    is_system TINYINT(1) DEFAULT 0 COMMENT '시스템 코드 여부',

    start_at DATETIME COMMENT '사용 시작일',
    end_at DATETIME COMMENT '사용 종료일',

    created_at DATETIME NOT NULL COMMENT '생성일시',
    created_by BIGINT COMMENT '생성자',
    updated_at DATETIME COMMENT '수정일시',
    updated_by BIGINT COMMENT '수정자',
    deleted TINYINT(1) DEFAULT 0 COMMENT '삭제 여부',

    UNIQUE KEY uk_group_code (group_id, code),

    INDEX idx_cc_group (group_id),
    INDEX idx_cc_parent (parent_id),
    INDEX idx_cc_sort (sort_order),
    INDEX idx_cc_active (is_active),
    INDEX idx_cc_deleted (deleted),

    CONSTRAINT fk_cc_group
        FOREIGN KEY (group_id) REFERENCES common_code_group(id),

    CONSTRAINT fk_cc_parent
        FOREIGN KEY (parent_id) REFERENCES common_code(id)

) ENGINE=InnoDB COMMENT='공통 코드 상세 테이블';

/* =========================
   사용자 사이트 팝업 테이블
   ========================= */
CREATE TABLE IF NOT EXISTS site_popup (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'Primary Key',

    popup_code VARCHAR(100) NOT NULL UNIQUE COMMENT '팝업 고유 코드',
    popup_name VARCHAR(200) NOT NULL COMMENT '팝업명',

    popup_type VARCHAR(30) NOT NULL DEFAULT 'LAYER'
        COMMENT '팝업 유형 (LAYER, WINDOW, MODAL)',

    position_type VARCHAR(30) NOT NULL DEFAULT 'MAIN'
        COMMENT '노출 위치 (MAIN, SUB, ALL)',

    device_type VARCHAR(30) NOT NULL DEFAULT 'ALL'
        COMMENT '디바이스 유형 (PC, MOBILE, ALL)',

    width INT COMMENT '팝업 너비(px)',
    height INT COMMENT '팝업 높이(px)',

    pos_x INT DEFAULT 0 COMMENT 'X 좌표',
    pos_y INT DEFAULT 0 COMMENT 'Y 좌표',

    content LONGTEXT COMMENT '팝업 HTML 내용',

    link_url VARCHAR(500) COMMENT '클릭 시 이동 URL',
    link_target VARCHAR(20) DEFAULT '_self'
        COMMENT '링크 타겟 (_self, _blank)',

    is_login_required TINYINT(1) DEFAULT 0 COMMENT '로그인 사용자 전용 여부',
    is_today_close_enabled TINYINT(1) DEFAULT 1 COMMENT '오늘 하루 보지 않기 사용 여부',

    start_at DATETIME COMMENT '노출 시작일',
    end_at DATETIME COMMENT '노출 종료일',

    sort_order INT NOT NULL DEFAULT 0 COMMENT '정렬 순서',

    is_active TINYINT(1) DEFAULT 1 COMMENT '활성 여부',
    is_published TINYINT(1) DEFAULT 1 COMMENT '게시 여부',

    created_at DATETIME NOT NULL COMMENT '생성일시',
    created_by BIGINT COMMENT '생성자',
    updated_at DATETIME COMMENT '수정일시',
    updated_by BIGINT COMMENT '수정자',
    deleted TINYINT(1) DEFAULT 0 COMMENT '삭제 여부',

    INDEX idx_popup_active (is_active),
    INDEX idx_popup_period (start_at, end_at),
    INDEX idx_popup_device (device_type),
    INDEX idx_popup_deleted (deleted)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='사용자 사이트 팝업 관리 테이블';

SET FOREIGN_KEY_CHECKS = 1;
