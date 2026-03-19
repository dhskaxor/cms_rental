-- 사이트 설정 기본 테마 컬럼 추가
-- 기존 설치본의 site_config에 기본 테마 값을 추가한다.

ALTER TABLE site_config
    ADD COLUMN site_theme VARCHAR(50) NOT NULL DEFAULT 'dark';

UPDATE site_config
SET site_theme = 'dark'
WHERE site_theme IS NULL OR TRIM(site_theme) = '';
