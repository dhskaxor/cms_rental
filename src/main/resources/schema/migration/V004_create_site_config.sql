-- 사이트 설정 테이블 생성
-- 사이트명, favicon, SEO, 회사정보 등 관리자 사이트 설정 저장용 (단일 행)

-- MariaDB
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
    deleted TINYINT DEFAULT 0 COMMENT 'Soft Delete 플래그'
);

-- 권한 추가 (없을 때만)
INSERT INTO permission (permission_code, permission_name, description, created_at, deleted)
SELECT 'SITE_READ', '사이트 설정 조회', '사이트 설정을 조회할 수 있는 권한', NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM permission WHERE permission_code = 'SITE_READ');
INSERT INTO permission (permission_code, permission_name, description, created_at, deleted)
SELECT 'SITE_UPDATE', '사이트 설정 수정', '사이트 설정을 수정할 수 있는 권한', NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM permission WHERE permission_code = 'SITE_UPDATE');

-- ADMIN 역할에 SITE_READ, SITE_UPDATE 부여 (중복 시 스킵)
INSERT IGNORE INTO role_permission (role_id, permission_id)
SELECT r.id, p.id FROM role r, permission p
WHERE r.role_code = 'ADMIN' AND p.permission_code IN ('SITE_READ', 'SITE_UPDATE');

-- 초기 행 삽입 (id=1, 중복 시 스킵)
INSERT INTO site_config (id, site_name, created_at, deleted)
SELECT 1, 'CMS Core', NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM site_config WHERE id = 1);
