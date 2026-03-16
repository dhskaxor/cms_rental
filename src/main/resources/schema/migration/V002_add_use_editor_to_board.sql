-- 기존 board 테이블에 use_editor 컬럼 추가
-- 이미 컬럼이 있으면 에러 발생. 필요 시 수동으로 DROP 후 재실행.

-- MariaDB
ALTER TABLE board ADD COLUMN use_editor TINYINT(1) DEFAULT 0 COMMENT '리치 에디터(WYSIWYG) 사용 여부' AFTER template_code;
