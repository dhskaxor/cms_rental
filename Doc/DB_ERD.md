# CMS Core DB 구조 (ERD)

> Mermaid ER 다이어그램. GitHub, VS Code, Cursor에서 렌더링됩니다.
> 스키마 원본: [cms_schema](../cms_schema)

---

## ER 다이어그램

```mermaid
erDiagram
    ROLE {
        bigint id PK
        varchar role_code UK
        varchar role_name
        varchar description
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
        varchar password
        varchar name
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
        varchar board_name
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
        tinyint can_delete
    }

    BOARD_POST {
        bigint id PK
        bigint board_id FK
        varchar title
        text content
        bigint writer_id FK
        tinyint is_notice
        tinyint is_secret
        tinyint deleted
    }

    BOARD_COMMENT {
        bigint id PK
        bigint post_id FK
        bigint parent_id FK
        text content
        bigint writer_id FK
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
        text before_data
        text after_data
        datetime created_at
    }

    SITE_MENU {
        bigint id PK
        bigint parent_id FK
        int depth
        varchar menu_name
        varchar menu_code UK
        varchar menu_type
        bigint board_id FK
        bigint page_id
        int sort_order
        tinyint is_visible
        tinyint is_login_required
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
        tinyint is_system
        tinyint is_active
        tinyint deleted
    }

    COMMON_CODE {
        bigint id PK
        bigint group_id FK
        bigint parent_id FK
        int depth
        varchar code
        varchar code_name
        int sort_order
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
        tinyint is_published
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
    BOARD ||--o| SITE_MENU : "board_id"
    SITE_MENU ||--o| SITE_MENU : "parent_id"
    COMMON_CODE_GROUP ||--o{ COMMON_CODE : "group_id"
    COMMON_CODE ||--o| COMMON_CODE : "parent_id"
```

---

## 테이블 목록 (16개)

| 구분 | 테이블 | 설명 |
|------|--------|------|
| 권한 | role | 역할 마스터 (ADMIN, MANAGER, USER) |
| 권한 | permission | 권한 마스터 (USER_CREATE, USER_READ 등) |
| 권한 | role_permission | 역할-권한 N:M 매핑 |
| 사용자 | user | 사용자 정보 |
| 인증 | jwt_refresh_token | JWT 리프레시 토큰 |
| 게시판 | board_group | 게시판 그룹 |
| 게시판 | board | 게시판 설정 |
| 게시판 | board_permission | 게시판별 역할별 CRUD 권한 |
| 게시판 | board_post | 게시글 |
| 게시판 | board_comment | 댓글 (대댓글 1단계) |
| 파일 | file | ref_type, ref_id 다형적 연결 |
| 감사 | audit_log | 변경 이력 로그 |
| 사이트 | site_menu | 계층형 메뉴 |
| 사이트 | site_page | 정적 페이지 |
| 코드 | common_code_group | 공통코드 그룹 |
| 코드 | common_code | 공통코드 상세 (계층) |
| 팝업 | site_popup | 사용자 사이트 팝업 |

---

## 공통 컬럼 (BaseVO)

대부분의 테이블은 다음 공통 컬럼을 포함합니다.

| 컬럼 | 타입 | 설명 |
|------|------|------|
| id | BIGINT | Primary Key |
| created_at | DATETIME | 생성 일시 |
| created_by | BIGINT | 생성자 ID |
| updated_at | DATETIME | 수정 일시 |
| updated_by | BIGINT | 수정자 ID |
| deleted | TINYINT(1) | Soft Delete 플래그 (0:활성, 1:삭제) |

---

## Soft Delete 규칙

- 모든 테이블은 **Soft Delete** 사용 (하드 Delete 금지)
- `deleted = 0`: 활성 데이터
- `deleted = 1`: 삭제된 데이터
- 모든 조회 쿼리에 `WHERE deleted = false` (또는 `deleted = 0`) 조건 포함
