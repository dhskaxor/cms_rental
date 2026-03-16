-- site_page 테이블에 template_code 컬럼 추가
-- 페이지별 사용자 사이트 템플릿 선택용 (templates/site/page/{code}/page.html)

-- MariaDB
ALTER TABLE site_page ADD COLUMN template_code VARCHAR(50) DEFAULT 'default'
    COMMENT '사용자 사이트 페이지 템플릿 코드 (default, page_01 등)' AFTER is_published;
