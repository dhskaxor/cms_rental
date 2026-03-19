-- ==============================================
-- CMS Core Platform - 초기 데이터
-- ==============================================

-- =========================
-- 기본 역할 (Role)
-- =========================
INSERT INTO role (role_code, role_name, description, created_at, deleted) VALUES
('ADMIN', '시스템 관리자', '모든 권한을 가진 시스템 관리자', NOW(), 0),
('MANAGER', '매니저', '일부 관리 권한을 가진 매니저', NOW(), 0),
('USER', '일반 사용자', '기본 권한을 가진 일반 사용자', NOW(), 0),
('ANONYMOUS', '비회원', '로그인하지 않은 사용자 (게시판 권한 설정용)', NOW(), 0);


-- =========================
-- 기본 권한 (Permission)
-- =========================
INSERT INTO permission (permission_code, permission_name, description, created_at, deleted) VALUES
-- 사용자 관련
('USER_CREATE', '사용자 생성', '사용자를 생성할 수 있는 권한', NOW(), 0),
('USER_READ', '사용자 조회', '사용자를 조회할 수 있는 권한', NOW(), 0),
('USER_UPDATE', '사용자 수정', '사용자를 수정할 수 있는 권한', NOW(), 0),
('USER_DELETE', '사용자 삭제', '사용자를 삭제할 수 있는 권한', NOW(), 0),

-- 역할 관련
('ROLE_CREATE', '역할 생성', '역할을 생성할 수 있는 권한', NOW(), 0),
('ROLE_READ', '역할 조회', '역할을 조회할 수 있는 권한', NOW(), 0),
('ROLE_UPDATE', '역할 수정', '역할을 수정할 수 있는 권한', NOW(), 0),
('ROLE_DELETE', '역할 삭제', '역할을 삭제할 수 있는 권한', NOW(), 0),

-- 게시판 관련
('BOARD_CREATE', '게시판 생성', '게시판을 생성할 수 있는 권한', NOW(), 0),
('BOARD_READ', '게시판 조회', '게시판을 조회할 수 있는 권한', NOW(), 0),
('BOARD_UPDATE', '게시판 수정', '게시판을 수정할 수 있는 권한', NOW(), 0),
('BOARD_DELETE', '게시판 삭제', '게시판을 삭제할 수 있는 권한', NOW(), 0),

-- 게시글 관련
('POST_CREATE', '게시글 작성', '게시글을 작성할 수 있는 권한', NOW(), 0),
('POST_READ', '게시글 조회', '게시글을 조회할 수 있는 권한', NOW(), 0),
('POST_UPDATE', '게시글 수정', '게시글을 수정할 수 있는 권한', NOW(), 0),
('POST_DELETE', '게시글 삭제', '게시글을 삭제할 수 있는 권한', NOW(), 0),

-- 댓글 관련
('COMMENT_CREATE', '댓글 작성', '댓글을 작성할 수 있는 권한', NOW(), 0),
('COMMENT_READ', '댓글 조회', '댓글을 조회할 수 있는 권한', NOW(), 0),
('COMMENT_UPDATE', '댓글 수정', '댓글을 수정할 수 있는 권한', NOW(), 0),
('COMMENT_DELETE', '댓글 삭제', '댓글을 삭제할 수 있는 권한', NOW(), 0),

-- 파일 관련
('FILE_CREATE', '파일 업로드', '파일을 업로드할 수 있는 권한', NOW(), 0),
('FILE_READ', '파일 다운로드', '파일을 다운로드할 수 있는 권한', NOW(), 0),
('FILE_DELETE', '파일 삭제', '파일을 삭제할 수 있는 권한', NOW(), 0),

-- 감사 로그 관련
('AUDIT_READ', '감사로그 조회', '감사 로그를 조회할 수 있는 권한', NOW(), 0),

-- 공통 코드 관련
('COMMON_CODE_CREATE', '공통코드 생성', '공통 코드를 생성할 수 있는 권한', NOW(), 0),
('COMMON_CODE_READ', '공통코드 조회', '공통 코드를 조회할 수 있는 권한', NOW(), 0),
('COMMON_CODE_UPDATE', '공통코드 수정', '공통 코드를 수정할 수 있는 권한', NOW(), 0),
('COMMON_CODE_DELETE', '공통코드 삭제', '공통 코드를 삭제할 수 있는 권한', NOW(), 0),

-- 팝업 관련
('POPUP_CREATE', '팝업 생성', '팝업을 생성할 수 있는 권한', NOW(), 0),
('POPUP_READ', '팝업 조회', '팝업을 조회할 수 있는 권한', NOW(), 0),
('POPUP_UPDATE', '팝업 수정', '팝업을 수정할 수 있는 권한', NOW(), 0),
('POPUP_DELETE', '팝업 삭제', '팝업을 삭제할 수 있는 권한', NOW(), 0),

-- 메뉴 관련
('MENU_CREATE', '메뉴 생성', '메뉴를 생성할 수 있는 권한', NOW(), 0),
('MENU_READ', '메뉴 조회', '메뉴를 조회할 수 있는 권한', NOW(), 0),
('MENU_UPDATE', '메뉴 수정', '메뉴를 수정할 수 있는 권한', NOW(), 0),
('MENU_DELETE', '메뉴 삭제', '메뉴를 삭제할 수 있는 권한', NOW(), 0),

-- 페이지 관련
('PAGE_CREATE', '페이지 생성', '페이지를 생성할 수 있는 권한', NOW(), 0),
('PAGE_READ', '페이지 조회', '페이지를 조회할 수 있는 권한', NOW(), 0),
('PAGE_UPDATE', '페이지 수정', '페이지를 수정할 수 있는 권한', NOW(), 0),
('PAGE_DELETE', '페이지 삭제', '페이지를 삭제할 수 있는 권한', NOW(), 0),

-- 사이트 설정 관련
('SITE_READ', '사이트 설정 조회', '사이트 설정을 조회할 수 있는 권한', NOW(), 0),
('SITE_UPDATE', '사이트 설정 수정', '사이트 설정을 수정할 수 있는 권한', NOW(), 0);


-- =========================
-- 역할-권한 매핑
-- =========================

-- ADMIN: 모든 권한
INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM role r, permission p
WHERE r.role_code = 'ADMIN';

-- MANAGER: 사용자 조회, 역할 조회, 게시판/게시글/댓글/파일 전체, 감사로그, 공통코드 전체
INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM role r, permission p
WHERE r.role_code = 'MANAGER'
  AND p.permission_code IN (
    'USER_READ',
    'ROLE_READ',
    'BOARD_CREATE', 'BOARD_READ', 'BOARD_UPDATE', 'BOARD_DELETE',
    'POST_CREATE', 'POST_READ', 'POST_UPDATE', 'POST_DELETE',
    'COMMENT_CREATE', 'COMMENT_READ', 'COMMENT_UPDATE', 'COMMENT_DELETE',
    'FILE_CREATE', 'FILE_READ', 'FILE_DELETE',
    'AUDIT_READ',
    'COMMON_CODE_CREATE', 'COMMON_CODE_READ', 'COMMON_CODE_UPDATE', 'COMMON_CODE_DELETE',
    'POPUP_CREATE', 'POPUP_READ', 'POPUP_UPDATE', 'POPUP_DELETE',
    'MENU_CREATE', 'MENU_READ', 'MENU_UPDATE', 'MENU_DELETE',
    'PAGE_CREATE', 'PAGE_READ', 'PAGE_UPDATE', 'PAGE_DELETE'
  );

-- USER: 게시글/댓글/파일 기본 권한
INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM role r, permission p
WHERE r.role_code = 'USER'
  AND p.permission_code IN (
    'POST_CREATE', 'POST_READ',
    'COMMENT_CREATE', 'COMMENT_READ',
    'FILE_CREATE', 'FILE_READ'
  );

-- 사이트 설정 초기 행 (id=1)
INSERT INTO site_config (id, site_name, site_theme, created_at, deleted)
VALUES (1, 'CMS Core', 'dark', NOW(), 0);
