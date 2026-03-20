# CMS Core DB 구조 (EDR)

> 기존 ERD 문서를 `docs` 체계로 이관한 기준 문서입니다.  
> 스키마 원본은 루트 `cms_schema` 및 `src/main/resources/schema/*.sql`을 참조합니다.

---

## 1. ER 다이어그램

```mermaid
erDiagram
    ROLE {
        bigint id PK
        varchar role_code UK
        varchar role_name
        tinyint deleted
    }
    PERMISSION {
        bigint id PK
        varchar permission_code UK
        varchar permission_name
        tinyint deleted
    }
    ROLE_PERMISSION {
        bigint role_id PK,FK
        bigint permission_id PK,FK
    }
    USER {
        bigint id PK
        varchar username UK
        varchar email
        bigint role_id FK
        varchar status
        tinyint deleted
    }
    JWT_REFRESH_TOKEN {
        bigint id PK
        bigint user_id FK
        varchar refresh_token
        datetime expires_at
        tinyint deleted
    }
    BOARD_GROUP {
        bigint id PK
        varchar group_code UK
        varchar group_name
        tinyint deleted
    }
    BOARD {
        bigint id PK
        varchar board_code UK
        bigint group_id FK
        tinyint use_comment
        tinyint use_secret
        tinyint deleted
    }
    BOARD_PERMISSION {
        bigint id PK
        bigint board_id FK
        bigint role_id FK
        tinyint can_create
        tinyint can_read
        tinyint can_update
        tinyint can_delete
    }
    BOARD_POST {
        bigint id PK
        bigint board_id FK
        bigint writer_id FK
        varchar title
        text content
        tinyint deleted
    }
    BOARD_COMMENT {
        bigint id PK
        bigint post_id FK
        bigint parent_id FK
        bigint writer_id FK
        text content
        tinyint deleted
    }
    FILE {
        bigint id PK
        varchar ref_type
        bigint ref_id
        varchar original_name
        varchar stored_name
        bigint file_size
        tinyint deleted
    }
    AUDIT_LOG {
        bigint id PK
        bigint user_id
        varchar action
        varchar target_type
        bigint target_id
        datetime created_at
    }
    SITE_MENU {
        bigint id PK
        bigint parent_id FK
        varchar menu_code UK
        varchar menu_type
        bigint board_id FK
        bigint page_id
        tinyint is_visible
        tinyint deleted
    }
    SITE_PAGE {
        bigint id PK
        varchar page_code UK
        varchar page_title
        longtext content
        tinyint is_published
        tinyint deleted
    }
    COMMON_CODE_GROUP {
        bigint id PK
        varchar group_code UK
        varchar group_name
        tinyint is_active
        tinyint deleted
    }
    COMMON_CODE {
        bigint id PK
        bigint group_id FK
        bigint parent_id FK
        varchar code
        varchar code_name
        tinyint is_active
        tinyint deleted
    }
    SITE_POPUP {
        bigint id PK
        varchar popup_code UK
        varchar popup_name
        varchar popup_type
        varchar position_type
        varchar device_type
        datetime start_at
        datetime end_at
        tinyint is_active
        tinyint deleted
    }

    ROLE ||--o{ ROLE_PERMISSION : "role_id"
    PERMISSION ||--o{ ROLE_PERMISSION : "permission_id"
    ROLE ||--o{ USER : "role_id"
    USER ||--o{ JWT_REFRESH_TOKEN : "user_id"
    BOARD_GROUP ||--o{ BOARD : "group_id"
    BOARD ||--o{ BOARD_PERMISSION : "board_id"
    ROLE ||--o{ BOARD_PERMISSION : "role_id"
    BOARD ||--o{ BOARD_POST : "board_id"
    USER ||--o{ BOARD_POST : "writer_id"
    BOARD_POST ||--o{ BOARD_COMMENT : "post_id"
    BOARD_COMMENT ||--o| BOARD_COMMENT : "parent_id"
    USER ||--o{ BOARD_COMMENT : "writer_id"
    SITE_MENU ||--o| SITE_MENU : "parent_id"
    COMMON_CODE_GROUP ||--o{ COMMON_CODE : "group_id"
    COMMON_CODE ||--o| COMMON_CODE : "parent_id"
```

---

## 2. 공통 데이터 규칙

- 모든 핵심 테이블은 Soft Delete(`deleted`) 사용
- 공통 필드는 `BaseVO` 기준으로 관리
  - `id`, `createdAt`, `createdBy`, `updatedAt`, `updatedBy`, `deleted`
- 하드 삭제 금지

---

## 3. 테이블 분류

- 인증/권한: `role`, `permission`, `role_permission`, `user`, `jwt_refresh_token`
- 콘텐츠: `board_group`, `board`, `board_permission`, `board_post`, `board_comment`
- 사이트: `site_menu`, `site_page`, `site_popup`
- 공통/운영: `common_code_group`, `common_code`, `file`, `audit_log`
- 대관: `rental_place`, `rental_room`, `rental_reservation`, 요금/달력 관련 테이블

---

## 4. 대관 모듈 상세 참고

- 구조: `docs/RENTAL_MODULE_STRUCTURE.md`
- 요금: `docs/RENTAL_PRICING_LOGIC.md`
- 파일 참조: `docs/RENTAL_FILES_REFERENCE.md`
