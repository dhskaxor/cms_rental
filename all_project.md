# CMS Core Platform - 프로젝트 명세서

## 1. 프로젝트 개요

이 프로젝트는 장기적으로 유지보수되는 **CMS(Content Management System) Core 플랫폼**이다.
이 프로젝트를 기반으로 추가적인 기능을 개발하여 사용하거나, 해당 프로젝트를 Core로 다른 프로젝트에서 Import해서 사용할 수 있다.

---

## 2. 기술 스택

| 구분 | 기술 | 비고 |
|------|------|------|
| Language | Java 17 | LTS 버전 |
| Framework | Spring Boot 3.2.x | 최신 안정 버전 |
| ORM | MyBatis (XML Mapper) | JPA 사용 금지 |
| Security | Spring Security + JWT | Access/Refresh Token |
| Database | MariaDB (주), PostgreSQL (추후) | Soft Delete 적용 |
| Test DB | H2 In-Memory | 단위 테스트용 |
| Build | Gradle | Groovy DSL |
| API | REST API (JSON) | /api/v1 버전 관리 |
| API 문서 | Swagger/OpenAPI | API 자동 문서화 |
| 설치 UI | Thymeleaf | 백엔드에 포함된 웹 페이지 |
| Cache | Redis (선택) | 없으면 인메모리 캐시로 대체 (세션, 권한 등) |

---

## 3. 패키지 구조

```
com.nt.cms
 ├── CmsApplication.java
 │
 ├── common                     # 공통 모듈
 │   ├── config                 # 설정 클래스
 │   ├── exception              # 예외 처리
 │   ├── response               # API 응답 포맷
 │   ├── util                   # 유틸리티
 │   └── vo                     # BaseVO 등 공통 VO
 │
 ├── install                    # CMS 설치 모듈
 │   ├── controller             # 설치 마법사 컨트롤러
 │   ├── service                # 설치 로직
 │   └── dto                    # 설치 관련 DTO
 │
 ├── auth                       # 인증/인가 (JWT)
 │   ├── controller             # 로그인/로그아웃 API
 │   ├── service                # 인증 서비스
 │   ├── mapper                 # JWT 토큰 Mapper
 │   ├── vo                     # JWT 관련 VO
 │   ├── dto                    # 로그인 요청/응답 DTO
 │   ├── security               # Security 설정, Filter
 │   └── util                   # JWT 유틸리티
 │
 ├── user                       # 사용자 관리
 │   ├── controller             # 사용자 CRUD API
 │   ├── service                # 사용자 서비스 (인터페이스 + Default 구현체)
 │   ├── mapper                 # 사용자 Mapper
 │   ├── vo                     # UserVO
 │   └── dto                    # 사용자 요청/응답 DTO
 │
 ├── role                       # 역할 관리
 │   ├── controller
 │   ├── service
 │   ├── mapper
 │   ├── vo
 │   └── dto
 │
 ├── permission                 # 권한 관리
 │   ├── controller
 │   ├── service
 │   ├── mapper
 │   ├── vo
 │   └── dto
 │
 ├── board                      # 게시판 모듈
 │   ├── controller             # 게시판/게시글/댓글 API
 │   ├── service                # 게시판 서비스
 │   ├── mapper                 # 게시판 관련 Mapper
 │   ├── vo                     # BoardVO, PostVO, CommentVO
 │   ├── dto                    # 요청/응답 DTO
 │   └── permission             # 게시판 권한 처리
 │
 ├── menu                       # 사이트 메뉴/페이지 모듈
 │   ├── controller             # 메뉴/페이지 API
 │   ├── service                # 메뉴/페이지 서비스
 │   ├── mapper                 # 메뉴/페이지 Mapper
 │   ├── vo                     # SiteMenuVO, SitePageVO
 │   └── dto                    # 요청/응답 DTO
 │
 ├── file                       # 파일 관리
 │   ├── controller             # 파일 업로드/다운로드 API
 │   ├── service                # 파일 서비스
 │   ├── mapper                 # 파일 Mapper
 │   ├── vo                     # FileVO
 │   └── dto                    # 파일 DTO
 │
 ├── audit                      # 감사 로그
 │   ├── service                # 감사 로그 서비스
 │   ├── mapper                 # 감사 로그 Mapper
 │   └── vo                     # AuditLogVO
 │
 ├── commoncode                 # 공통 코드
 │   ├── controller             # 공통코드 API
 │   ├── service                # 공통코드 서비스
 │   ├── mapper                 # CommonCodeMapper, CommonCodeGroupMapper
 │   ├── vo                     # CommonCodeVO, CommonCodeGroupVO
 │   └── dto                    # 요청/응답 DTO
 │
 ├── popup                      # 팝업 관리
 │   ├── controller             # 팝업 API
 │   ├── service                # 팝업 서비스
 │   ├── mapper                 # PopupMapper
 │   ├── vo                     # SitePopupVO
 │   └── dto                    # 요청/응답 DTO
 │
 └── admin                      # 관리자 화면 (Thymeleaf 웹)
     ├── controller             # AdminController, AdminRoleController 등
     ├── config                 # AdminModelAttributeAdvice
     └── interceptor            # AdminSessionInterceptor
```

---

## 4. 네이밍 컨벤션

| 구분 | 규칙 | 예시 |
|------|------|------|
| 패키지 | 소문자, 단수형 | `user`, `board`, `file` |
| 인터페이스 | 동사 + 명사 | `UserService` |
| 구현체 | Default + 인터페이스명 | `DefaultUserService` |
| VO | 테이블명 + VO | `UserVO`, `BoardVO` |
| DTO | 용도 + Request/Response | `UserCreateRequest`, `UserResponse` |
| Mapper | 도메인 + Mapper | `UserMapper`, `BoardMapper` |
| Controller | 도메인 + Controller | `UserController` |

---

## 5. API 설계

### 5.1 기본 응답 포맷

**성공 응답:**
```json
{
  "success": true,
  "data": { },
  "error": null
}
```

**에러 응답:**
```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "USER_NOT_FOUND",
    "message": "사용자를 찾을 수 없습니다."
  }
}
```

### 5.2 API 버전 관리
- 기본 경로: `/api/v1/...`
- 예시: `/api/v1/users`, `/api/v1/boards`

### 5.3 페이징 응답 포맷
```json
{
  "success": true,
  "data": {
    "content": [ ],
    "page": 1,
    "size": 10,
    "totalElements": 100,
    "totalPages": 10
  },
  "error": null
}
```

---

## 6. 보안 설정

| 항목 | 값 |
|------|-----|
| JWT Access Token 만료 | 1시간 |
| JWT Refresh Token 만료 | 14일 |
| 비밀번호 암호화 | BCrypt |
| 권한 시스템 | RBAC (Role-Based Access Control) |
| 로그인 실패 제한 | 5회 이상 실패 시 계정 잠금 (LOCKED) |
| 비밀번호 정책 | 영문/숫자/특수문자 포함, 최소 6자 최대 12자 |
| 멀티 디바이스 로그인 | 불허 (새 로그인 시 기존 Refresh Token 무효화) |

---

## 7. 파일 설정

| 항목 | 값 |
|------|-----|
| 저장 경로 | `C:/cms/files` |
| 허용 확장자 | 이미지 (jpg, png, gif, webp), 문서 (pdf, doc, docx, xls, xlsx, hwp) |
| 최대 파일 크기 | 3MB |

---

## 8. 게시판 설정

| 항목 | 값 |
|------|-----|
| 대댓글 depth | 1단계 (댓글 → 대댓글까지만) |
| 게시판 권한 | 게시판별 역할별 CRUD 권한 설정 가능 |
| 게시글 검색 | 제목/내용 검색, 작성자 검색, 기간별 검색 지원 |

---

## 9. 회원가입 및 이메일

| 항목 | 값 |
|------|-----|
| 회원가입 | 누구나 가능 (공개) |
| 회원가입 이메일 인증 | 불필요 |
| 비밀번호 찾기 | 비밀번호 재설정 링크 발송 → 링크 클릭 후 새 비밀번호 설정 |
| SMTP 설정 | 설치 마법사에 미포함 (별도 application 설정) |

---

## 10. 캐싱 / CORS / 로깅

| 구분 | 항목 | 값 |
|------|------|-----|
| 캐싱 | 솔루션 | Redis (선택), 미설정 시 인메모리 캐시 |
| 캐싱 | 대상 | 세션, 권한 정보 |
| CORS | 설정 | 필요 (외부 프론트엔드 호출 대비) |
| 로깅 | 파일 위치 | 설정 가능 |
| 로깅 | 보관 기간 | 설정 가능 |

---

## 11. 설치 기능

### 11.1 설치 마법사 UI
- **구현 방식**: Thymeleaf (백엔드 프로젝트에 포함)
- 설치 웹 페이지는 서버 사이드 렌더링으로 제공

### 11.2 설치 마법사 흐름

```
[Step 1] DB 연결 정보 입력
  - Host, Port, Database Name
  - Username, Password
  - 연결 테스트 버튼

[Step 2] 테이블 생성
  - 스키마 자동 실행

[Step 3] 관리자 계정 생성
  - Username, Password, Email

[Step 4] 기본 설정
  - 사이트명, 파일 저장 경로 등

[Step 5] 완료
```

### 11.3 설정 파일

- 설치 완료 후 설정은 `cms-config.yml`에 저장
- `installed: true` 플래그로 설치 상태 관리
- 설치 완료 후 `/install` 접근 차단

### 11.4 재설치 기능

- 추후 지원을 위해 기능만 구현
- 기존 데이터 삭제 후 재설치 가능

---

## 12. 데이터베이스 테이블

### 12.1 테이블 목록

| 테이블 | 설명 |
|--------|------|
| `role` | 역할 마스터 |
| `permission` | 권한 마스터 |
| `role_permission` | 역할-권한 매핑 |
| `user` | 사용자 정보 |
| `jwt_refresh_token` | JWT 리프레시 토큰 |
| `board_group` | 게시판 그룹 |
| `board` | 게시판 설정 |
| `board_permission` | 게시판별 권한 |
| `board_post` | 게시글 |
| `board_comment` | 댓글 |
| `file` | 파일 정보 |
| `audit_log` | 감사 로그 |
| `site_menu` | 사이트 메뉴 (계층형, 노출 위치/기간 제어) |
| `site_page` | 정적 페이지 (page_code, content, is_published) |
| `common_code_group` | 공통 코드 그룹 |
| `common_code` | 공통 코드 상세 (그룹별, 계층 지원) |
| `site_popup` | 사용자 사이트 팝업 |

### 12.2 공통 코드 관리 테이블 상세

| 테이블 | 설명 |
|--------|------|
| **common_code_group** | 코드 그룹 마스터. `group_code`(고유), `group_name`, `description`, `is_system`(수정 제한), `is_active`. 공통 컬럼(created_at, created_by, updated_at, updated_by, deleted) 적용. |
| **common_code** | 그룹별 코드 상세. `group_id`(FK → common_code_group), `parent_id`(FK → common_code, 계층), `depth`, `code`(그룹 내 유일), `code_name`, `code_value`, `sort_order`, `is_active`, `is_system`, `start_at`/`end_at`(사용 기간). UNIQUE(group_id, code). |

- **계층**: `parent_id`, `depth`로 트리 구조 지원.
- **시스템 코드**: `is_system = 1`인 경우 수정/삭제 제한 용도.
- **기간 제어**: `start_at`, `end_at`으로 노출 기간 제한 가능.

### 12.3 팝업 관리 테이블 상세

| 테이블 | 설명 |
|--------|------|
| **site_popup** | 사용자 사이트 팝업. `popup_code`(고유), `popup_name`, `popup_type`(LAYER/WINDOW/MODAL), `position_type`(MAIN/SUB/ALL), `device_type`(PC/MOBILE/ALL), `width`/`height`, `pos_x`/`pos_y`, `content`(HTML), `link_url`/`link_target`, `is_login_required`, `is_today_close_enabled`, `start_at`/`end_at`, `sort_order`, `is_active`, `is_published`. 공통 컬럼 적용. |

- **팝업 유형**: LAYER(레이어), WINDOW(창), MODAL(모달).
- **노출 위치**: MAIN(메인), SUB(서브), ALL(전체).
- **디바이스**: PC, MOBILE, ALL.
- **오늘 하루 보지 않기**: `is_today_close_enabled`로 쿠키 기반 재노출 제어.

---

## 13. 초기 데이터

프로젝트 설치 시 자동 생성되는 기본 데이터:

### 13.1 기본 Role
- `ADMIN`: 시스템 관리자
- `MANAGER`: 매니저
- `USER`: 일반 사용자

### 13.2 기본 Permission
- `USER_CREATE`, `USER_READ`, `USER_UPDATE`, `USER_DELETE`
- `ROLE_CREATE`, `ROLE_READ`, `ROLE_UPDATE`, `ROLE_DELETE`
- `BOARD_CREATE`, `BOARD_READ`, `BOARD_UPDATE`, `BOARD_DELETE`
- `POST_CREATE`, `POST_READ`, `POST_UPDATE`, `POST_DELETE`
- `FILE_CREATE`, `FILE_READ`, `FILE_DELETE`
- `AUDIT_READ`

---

## 14. 완료 작업 기록

### 14.1 API 모듈 (Phase 1) — 완료

| 순번 | 모듈 | 패키지 | 완료 내용 |
|------|------|--------|----------|
| 1 | 공통 모듈 | `common` | config, exception, response, BaseVO, CmsProperties, WebConfig |
| 2 | 설치 모듈 | `install` | InstallController, InstallService, DB연결/스키마/관리자계정/설정 마법사 |
| 3 | 인증/인가 | `auth` | JWT, Spring Security, WebAuthController, 로그인/로그아웃/비밀번호찾기 |
| 4 | 사용자 관리 | `user` | UserController API, UserService, CRUD, 회원가입, 프로필 |
| 5 | 권한 시스템 | `role`, `permission` | RoleController, PermissionController, RBAC |
| 6 | 게시판 | `board` | BoardGroupController, BoardController, PostController, CommentController |
| 7 | 파일 관리 | `file` | FileController, 업로드/다운로드 |
| 8 | 감사 로그 | `audit` | AuditLogController, AuditLogService |
| 9 | 사이트 메뉴/페이지 | `menu` | SiteMenuController, SitePageController, 계층형 메뉴, 정적 페이지 |
| 10 | 공통 코드 | `commoncode` | CommonCodeController, CommonCodeGroupController, 계층 지원 |
| 11 | 팝업 관리 | `popup` | PopupController, PopupService |

### 14.2 관리자 Layout (Phase 2-1) — 완료

| 순번 | 작업 | 대상 | 완료 내용 |
|------|------|------|----------|
| 1 | Layout 골격 | `admin/layout.html` | 상단 헤더, 좌측 메뉴, `layout:fragment="content"` |
| 2 | 대시보드 | `admin/dashboard.html` | `layout:decorate` 사용, 메인 영역 |
| 3 | 헤더 스타일 | layout.html | 그라데이션, 제목 클릭 → 대시보드, 로그인 사용자, 로그아웃 |
| 4 | 좌측 메뉴 | layout.html | 15.2 메뉴 순서, currentMenu 하이라이트, 아이콘 |
| 5 | 대시보드 컨텐츠 | dashboard.html | 환영 메시지, 빠른 링크 |

### 14.3 현재 관리자 컨텐츠 상태

| 메뉴 | 템플릿 | 컨트롤러 | Layout 패턴 | 상태 |
|------|--------|----------|-------------|------|
| 대시보드 | dashboard.html | AdminController | layout:decorate | ✅ |
| 역할 관리 | role/list, detail, form | AdminRoleController | layout:decorate | ✅ |
| 사용자 관리 | user/list, detail, form | AdminUserController | layout:decorate | ✅ |
| 메뉴 관리 | menu/list, form | AdminMenuController | layout:decorate | ✅ |
| 페이지 관리 | page/list, form | AdminMenuController | layout:decorate | ✅ |
| 게시판 관리 | board/list, detail, form, group-* | AdminBoardController | layout:decorate | ✅ |
| 게시글 관리 | post/board-select, list, detail, form | AdminPostController | layout:decorate | ✅ (파일 첨부 포함) |
| 공통 코드 | commoncode/list, detail, group-form, code-form | AdminCommonCodeController | layout:decorate | ✅ |
| 팝업 관리 | popup/list, detail, form | AdminPopupController | layout:decorate | ✅ |
| 파일 관리 | file/list.html | AdminFileController | layout:decorate | ✅ (다중 업로드 포함) |
| 감사 로그 | audit-logs, audit-log-detail | AdminAuditLogController | layout:decorate | ✅ |
| 설정 | settings.html | AdminController | layout:decorate | 🔲 미구현 (Phase별 구현 예정) |

### 14.4 Layout 패턴 (1단계 완료 — 통일됨)

| 패턴 | 사용 템플릿 |
|------|-------------|
| `layout:decorate="~{admin/layout}"` + `layout:fragment="content"` | dashboard, role, user, menu, page, board, commoncode, popup, audit, posts, files, settings |

### 14.5 404 발생 경로 (0단계 완료로 해소됨)

- ~~`/admin/posts`~~ — AdminPostController, post/*.html 완전 구현 완료 ✅
- ~~`/admin/files`~~ — AdminFileController, file/list.html 완전 구현 완료 ✅
- ~~`/admin/settings`~~ — placeholder 템플릿 생성 완료 (실제 폼 미구현) 🔲

### 14.6 추가 구현 사항 및 cms_user_react 동기화

| 구분 | 적용 내용 | 대상 |
|------|----------|------|
| **cms_user_react** | CMS API를 호출하는 별도 React 사용자 사이트. cms /site/**와 동일 기능 제공 | cms_user_react 프로젝트 |
| **게시글 파일 첨부** | board.useFile 시 첨부, 등록/수정 시 batch 업로드, 기존 파일 삭제·추가, maxFileCount 제한 | AdminPostController, post/form.html |
| **리치 에디터(Toast UI)** | board.useEditor 시 WYSIWYG 에디터, 아니면 textarea. 관리자·사이트·React 공통 적용 | admin/post/form, site post-form.html, cms_user_react PostFormPage |
| **cms site Page/Submit** | SPA → Thymeleaf 전환. SiteViewController(GET), SiteFormController(POST). site/layout, board/list·post-detail·post-form | SiteViewController, SiteFormController, SiteModelAttributeAdvice |
| **파일 다중 업로드** | multiple + batch API, ref_type/ref_id 필터 | file/list.html, FileController |
| **XSS 대응** | th:onclick → data-* + onclick 핸들러 | role, user, commoncode, popup, page, board 등 |
| **fetch credentials** | `credentials: 'same-origin'` 공통 적용 | 모든 관리자 템플릿 |
| **라우팅 정리** | AdminController의 posts, files 제거 → AdminPostController, AdminFileController로 분리 | AdminController |
| **메뉴/코드 순환 방지** | findDescendantIds()로 상위 선택 시 자기·자손 제외 | SiteMenuService, CommonCodeService |
| **사용자 부가 기능** | 비밀번호 초기화, 계정 잠금/해제 스크립트 | AdminUserController, user/detail.html |
| **페이지 매퍼 수정** | SitePageMapper 테이블명 cms_user → user | SitePageMapper.xml |
| **check/code API 통일** | ApiResponse<Boolean>, URL /check/code, Permission enum 사용 | SiteMenuController, SitePageController |
| **Permission 하드코딩 수정** | MENU_CREATE → Permission.MENU_CREATE.value | SiteMenuController |

---

## 15. 관리자 화면 작업 (Phase 2)

> 관리자 페이지의 모든 메뉴는 동일한 Layout을 사용한다.
> `@PROJECT_CONTEXT.md @all_project.md` 를 참조하여 단계별로 요청한다.

### 15.1 목표 레이아웃 구조

```
┌─────────────────────────────────────────────────────────────────────────┐
│  [제목 - 좌측]                              [로그인 사용자 - 우측]       │  ← 상단 헤더
├────────────────┬────────────────────────────────────────────────────────┤
│                │                                                        │
│  [메뉴1]       │                                                         │
│  [메뉴2]       │              메인 컨텐츠 영역                             │
│  [메뉴3]       │                                                        │
│  [메뉴4]       │                                                        │
│  ...          │                                                         │
│                │                                                        │
│                │                  푸터                                   │
└────────────────┴────────────────────────────────────────────────────────┘
```

| 영역 | 설명 |
|------|------|
| **상단 헤더** | 좌측: 사이트/관리자 제목 (예: CMS Admin) |
| **상단 헤더** | 우측: 로그인 사용자 이름, 역할, 로그아웃 버튼 |
| **좌측 메뉴** | 메뉴 타이틀 순차 나열, 현재 페이지 활성 표시 |
| **메인 컨텐츠** | `layout:fragment="content"` 영역 |

### 15.2 메뉴 순서

| 순번 | 메뉴명 | 경로 | currentMenu |
|------|--------|------|-------------|
| 1 | 대시보드 | `/admin` | dashboard |
| 2 | 역할 관리 | `/admin/roles` | roles |
| 3 | 사용자 관리 | `/admin/users` | users |
| 4 | 메뉴 관리 | `/admin/menus` | menus |
| 5 | 페이지 관리 | `/admin/pages` | pages |
| 6 | 게시판 관리 | `/admin/boards` | boards |
| 7 | 게시글 관리 | `/admin/posts` | posts |
| 8 | 공통 코드 | `/admin/common-codes` | commoncodes |
| 9 | 팝업 관리 | `/admin/popups` | popups |
| 10 | 파일 관리 | `/admin/files` | files |
| 11 | 감사 로그 | `/admin/audit-logs` | audit |
| 12 | 설정 | `/admin/settings` | settings |

### 15.3 기술 스택

| 항목 | 값 |
|------|-----|
| 템플릿 | Thymeleaf + Layout Dialect (`layout:decorate`, `layout:fragment`) |
| 표준 패턴 | `layout:decorate="~{admin/layout}"` + `layout:fragment="content"` |
| 스타일 | Tailwind CDN |
| 반응형 | 모바일 시 좌측 메뉴 토글/접기 고려 |

### 15.4 단계별 작업 (순차 실행)

#### 0단계: 사전 정리 — ✅ 완료

| 순서 | 작업 | 대상 | 검증 |
|------|------|------|------|
| 0-1 | placeholder 템플릿 생성 | admin/posts.html, files.html, settings.html | layout 적용, "추후 구현" 표시 ✅ |
| 0-2 | AdminController 확인 | AdminController | posts, files, settings가 placeholder 반환 ✅ |

**요청 예시:**
```
@PROJECT_CONTEXT.md @all_project.md
관리자 화면 0단계(사전 정리)를 실행해줘.
```

#### 1단계: Layout 패턴 통일 — ✅ 완료
모든 관리자의 layout은 admin/layout.html을 이용한다.
| 순서 | 작업 | 대상 | 목표 |
|------|------|------|------|
| 1-1 | role, user, popup, commoncode, audit | 해당 *.html | `layout:decorate` 패턴으로 변경 ✅ |
| 1-2 | menu, page | menu/*.html, page/*.html | sidebar → `layout:decorate` 전환 ✅ |
| 1-3 | board | board/*.html | 별도 구조 → `layout:decorate` 적용 ✅ |
| 1-4 | currentMenu, pageTitle | Admin*Controller | 모든 페이지 일관 전달 ✅ |

**요청 예시:**
```
@PROJECT_CONTEXT.md @all_project.md
관리자 화면 1단계(Layout 패턴 통일)를 실행해줘.
```

#### 2단계: 메뉴별 검증 (15.2 순서)

> 하나의 메뉴 작업 완료 후 기능 테스트를 진행하고, 문제 없으면 다음 메뉴로 진행한다.


##### 2-A. 역할 관리 — ✅ 완료
> 각 개발전 관련 스키마 정보를 한번 더 분석 후 정확하게 구조를 파악 후 진행한다.
| 순서 | 작업 | 대상 | 검증 |
|------|------|------|------|
| 2-A-1 | role/list.html → layout:decorate 변환 | role/list.html | 목록 페이지 레이아웃 표시 ✅ |
| 2-A-2 | role/detail.html → layout:decorate 변환 | role/detail.html | 상세 페이지 레이아웃 표시 ✅ |
| 2-A-3 | role/form.html → layout:decorate 변환 | role/form.html | 등록/수정 폼 레이아웃 표시 ✅ |
| 2-A-4 | AdminRoleController currentMenu 확인 | AdminRoleController | `currentMenu="roles"` 일관 전달 ✅ |
| 2-A-5 | **기능 테스트: 목록** | /admin/roles | 역할 목록 조회, 클릭 시 상세 이동 ✅ |
| 2-A-6 | **기능 테스트: 등록** | /admin/roles/new | 역할 등록 폼, API 연동, 저장 후 목록 이동 ✅ |
| 2-A-7 | **기능 테스트: 상세/수정** | /admin/roles/{id}, /admin/roles/{id}/edit | 상세 조회, 수정 폼, 저장 ✅ |
| 2-A-8 | **기능 테스트: 삭제** | detail 또는 list | 삭제 버튼 동작, 목록 갱신 ✅ |

**추가 적용 사항:**
- AdminSessionAuthenticationFilter: 관리자 세션 로그인 시 `/api/v1/*` fetch 호출에 세션 기반 인증 적용
- fetch 호출에 `credentials: 'same-origin'` 추가 (세션 쿠키 전송)
- 역할 등록 후 목록(/admin/roles)으로 이동하도록 수정

**요청 예시:** `@PROJECT_CONTEXT.md @all_project.md  관리자 화면 2단계 [역할 관리]를 순차 구현해줘.`

---

##### 2-B. 사용자 관리 — ✅ 완료
> 각 개발전 관련 스키마 정보를 한번 더 분석 후 정확하게 구조를 파악 후 진행한다.
| 순서 | 작업 | 대상 | 검증 |
|------|------|------|------|
| 2-B-1 | user/list.html → layout:decorate 변환 | user/list.html | 목록 페이지 ✅ |
| 2-B-2 | user/detail.html → layout:decorate 변환 | user/detail.html | 상세 페이지 ✅ |
| 2-B-3 | user/form.html → layout:decorate 변환 | user/form.html | 등록/수정 폼 ✅ |
| 2-B-4 | AdminUserController currentMenu 확인 | AdminUserController | `currentMenu="users"` ✅ |
| 2-B-5 | **기능 테스트: 목록** | /admin/users | 목록 조회, 검색(이름/아이디/이메일) ✅ |
| 2-B-6 | **기능 테스트: 등록** | /admin/users/new | 사용자 등록 ✅ |
| 2-B-7 | **기능 테스트: 상세/수정** | /admin/users/{id}, /admin/users/{id}/edit | 상세, 수정 ✅ |
| 2-B-8 | **기능 테스트: 삭제** | - | 삭제(soft delete) ✅ |

**적용 사항:**
- th:onclick 문자열 변수 → data-* 속성 패턴 (Thymeleaf XSS 제한 대응)
- fetch 호출에 credentials: 'same-origin' 추가
- 역할 선택: RoleService로 동적 로드 (하드코딩 제거)
- 상세 페이지 모달 닫는 div 구조 수정

**요청 예시:** `@PROJECT_CONTEXT.md @all_project.md  관리자 화면 2단계 [사용자 관리]를 순차 구현해줘.`

---

##### 2-C. 메뉴 관리 — ✅ 완료
> 각 개발전 관련 스키마 정보를 한번 더 분석 후 정확하게 구조를 파악 후 진행한다.
| 순서 | 작업 | 대상 | 검증 |
|------|------|------|------|
| 2-C-1 | menu/list.html → layout:decorate 변환 | menu/list.html | 계층형 메뉴 목록 ✅ |
| 2-C-2 | menu/form.html → layout:decorate 변환 | menu/form.html | 메뉴 등록/수정 폼 ✅ |
| 2-C-3 | AdminMenuController currentMenu 확인 | AdminMenuController | `currentMenu="menus"` ✅ |
| 2-C-4 | **기능 테스트: 목록** | /admin/menus | 계층형 메뉴 표시, 정렬 ✅ |
| 2-C-5 | **기능 테스트: 등록/수정** | /admin/menus/new, /admin/menus/{id}/edit | 상위 메뉴 선택, 메뉴 유형(PAGE/BOARD/LINK) ✅ |
| 2-C-6 | **기능 테스트: 삭제** | - | 삭제 동작 ✅ |

**적용 사항:**
- SiteMenuService.findListForAdmin(): 관리자 목록용 계층 순서 flat 목록 제공
- SiteMenuService.findDescendantIds(): 수정 시 상위 메뉴 선택 시 자기 자신·자손 제외(순환 방지)
- AdminMenuController: 등록/수정 폼에 findListForAdmin() 기반 parentMenus 전달
- menu/form.html: 상위 메뉴 option 계층 시각화 (SpEL), credentials, check-code null-safe
- menu/list.html: th:style 수정, credentials, 삭제 확인 메시지 안전 처리

**요청 예시:** `@PROJECT_CONTEXT.md @all_project.md  관리자 화면 2단계 [메뉴 관리]를 순차 구현해줘.`

---

##### 2-D. 페이지 관리 — ✅ 완료
> 각 개발전 관련 스키마 정보를 한번 더 분석 후 정확하게 구조를 파악 후 진행한다.
| 순서 | 작업 | 대상 | 검증 |
|------|------|------|------|
| 2-D-1 | page/list.html → layout:decorate 변환 | page/list.html | 페이지 목록 ✅ |
| 2-D-2 | page/form.html → layout:decorate 변환 | page/form.html | 페이지 등록/수정 폼 ✅ |
| 2-D-3 | AdminMenuController (pages) currentMenu 확인 | AdminMenuController | `currentMenu="pages"` ✅ |
| 2-D-4 | **기능 테스트: 목록** | /admin/pages | 목록 조회 ✅ |
| 2-D-5 | **기능 테스트: 등록/수정** | /admin/pages/new, /admin/pages/{id}/edit | page_code, page_title, content ✅ |
| 2-D-6 | **기능 테스트: 삭제** | - | 삭제 동작 (메뉴 참조 시 PAGE_IN_USE) ✅ |

**적용 사항:**
- page/list.html: credentials: 'same-origin', 삭제 확인 메시지 안전 처리
- page/form.html: credentials, check-code null-safe, 미리보기 모달 유지

**요청 예시:** `@PROJECT_CONTEXT.md @all_project.md  관리자 화면 2단계 [페이지 관리]를 순차 구현해줘.`

---

##### 2-E. 게시판 관리 — ✅ 완료
> 각 개발전 관련 스키마 정보를 한번 더 분석 후 정확하게 구조를 파악 후 진행한다.
| 순서 | 작업 | 대상 | 검증 |
|------|------|------|------|
| 2-E-1 | board/group-list.html → layout:decorate 변환 | board/group-list.html | 게시판 그룹 목록 ✅ |
| 2-E-2 | board/group-form.html → layout:decorate 변환 | board/group-form.html | 그룹 등록/수정 ✅ |
| 2-E-3 | board/list.html → layout:decorate 변환 | board/list.html | 게시판 목록 ✅ |
| 2-E-4 | board/detail.html → layout:decorate 변환 | board/detail.html | 게시판 상세 ✅ |
| 2-E-5 | board/form.html → layout:decorate 변환 | board/form.html | 게시판 등록/수정 ✅ |
| 2-E-6 | AdminBoardController currentMenu 확인 | AdminBoardController | `currentMenu="boards"` ✅ |
| 2-E-7 | **기능 테스트: 그룹** | /admin/boards/groups | 그룹 CRUD ✅ |
| 2-E-8 | **기능 테스트: 게시판** | /admin/boards | 게시판 CRUD, 권한 설정 ✅ |
| 2-E-9 | **기능 테스트: 삭제** | - | 그룹/게시판 삭제 ✅ |

**적용 사항:**
- group-list: layout fragment 닫는 div 추가, boardCount null 처리, 삭제 확인 메시지 안전 처리
- group-form: check-code fetch에 credentials 추가
- list/detail/form: 삭제 확인 메시지 안전 처리, board form check-code credentials 추가
- **게시판 템플릿 폴더 기반**: `templates/site/board/` 하위 폴더 스캔 → `GET /api/v1/boards/templates`, 폴더 추가 시 관리자 셀렉트에 자동 반영. SiteViewController는 `BoardService.resolve*View(templateCode)`로 동적 뷰 반환

**요청 예시:** `@PROJECT_CONTEXT.md @all_project.md  관리자 화면 2단계 [게시판 관리]를 순차 구현해줘.`

---

##### 2-F. 게시글 관리 — ✅ 완료 (신규 구현)
> 각 개발전 관련 스키마 정보를 한번 더 분석 후 정확하게 구조를 파악 후 진행한다.
| 순서 | 작업 | 대상 | 검증 |
|------|------|------|------|
| 2-F-1 | AdminPostController 신규 생성 | AdminPostController.java | /admin/posts, /admin/boards/{id}/posts ✅ |
| 2-F-2 | post/board-select, list.html 신규 생성 | admin/post/ | 게시판 선택, 게시글 목록 ✅ |
| 2-F-3 | post/detail.html 신규 생성 | admin/post/detail.html | 게시글 상세 ✅ |
| 2-F-4 | post/form.html 신규 생성 | admin/post/form.html | 게시글 등록/수정 ✅ |
| 2-F-5 | **기능 테스트: 목록** | /admin/posts, /admin/boards/{id}/posts | 게시판별 목록, 페이징, 검색 ✅ |
| 2-F-6 | **기능 테스트: 상세** | /admin/boards/{id}/posts/{postId} | 상세 조회, 조회수 ✅ |
| 2-F-7 | **기능 테스트: 등록/수정** | - | 게시글 작성, 수정 (API 연동) ✅ |
| 2-F-8 | **기능 테스트: 삭제** | - | 삭제(soft delete) ✅ |
| 2-F-9 | **기능 테스트: 파일 첨부** | - | board.useFile 시 첨부, 등록/수정 시 batch 업로드 ✅ |

**적용 사항:**
- AdminPostController: 게시판 선택(/admin/posts), 게시판별 목록/상세/등록/수정
- board-select.html: 게시판 카드 선택 → 해당 게시판 게시글 목록
- list.html: 페이징, 제목/내용/작성자/기간 검색, credentials
- detail.html: 제목/작성자/조회수/내용, th:utext(content), credentials
- form.html: 제목/내용/공지/비밀글(게시판 설정 시), credentials
- **파일 첨부**: `board.useFile` 시에만 첨부 섹션 노출, 등록/수정 시 batch 업로드(refType=POST, refId=postId), 기존 파일 표시·삭제, 새 파일 batch 업로드, 선택 파일 미리보기·제거 버튼, `maxFileCount` 제한

**요청 예시:** `@PROJECT_CONTEXT.md @all_project.md  관리자 화면 2단계 [게시글 관리]를 순차 구현해줘.`

---

##### 2-G. 공통 코드
> 각 개발전 관련 스키마 정보를 한번 더 분석 후 정확하게 구조를 파악 후 진행한다.
| 순서 | 작업 | 대상 | 검증 |
|------|------|------|------|
| 2-G-1 | commoncode/list.html → layout:decorate 변환 | commoncode/list.html | 그룹 목록 |
| 2-G-2 | commoncode/detail.html → layout:decorate 변환 | commoncode/detail.html | 그룹 상세 |
| 2-G-3 | commoncode/group-form.html → layout:decorate 변환 | commoncode/group-form.html | 그룹 등록/수정 |
| 2-G-4 | commoncode/code-form.html → layout:decorate 변환 | commoncode/code-form.html | 코드 등록/수정 |
| 2-G-5 | AdminCommonCodeController currentMenu 확인 | AdminCommonCodeController | `currentMenu="commoncodes"` |
| 2-G-6 | **기능 테스트: 그룹** | /admin/common-codes | 그룹 CRUD ✅ |
| 2-G-7 | **기능 테스트: 코드** | /admin/common-codes/group/{groupId} | 그룹별 코드 CRUD, 계층 ✅ |
| 2-G-8 | **기능 테스트: 삭제** | - | 그룹/코드 삭제 ✅ |

**적용 사항:**
- list.html: Tailwind 스타일, 그룹 삭제 버튼(시스템 그룹 제외), credentials
- detail.html: data-* + onclick 기반 삭제(XSS 방지), credentials, depth padding
- group-form.html: max-w-3xl UI, credentials
- code-form.html: credentials, 수정 시 parentCodes에서 자기 자신·자손 제외(순환 방지)
- CommonCodeService.findDescendantIds(): 코드 수정 시 상위 선택 순환 방지

**요청 예시:** `@PROJECT_CONTEXT.md @all_project.md  관리자 화면 2단계 [공통 코드]를 순차 구현해줘.`

---

##### 2-H. 팝업 관리
> 각 개발전 관련 스키마 정보를 한번 더 분석 후 정확하게 구조를 파악 후 진행한다.
| 순서 | 작업 | 대상 | 검증 |
|------|------|------|------|
| 2-H-1 | popup/list.html → layout:decorate 변환 | popup/list.html | 팝업 목록 |
| 2-H-2 | popup/detail.html → layout:decorate 변환 | popup/detail.html | 팝업 상세 |
| 2-H-3 | popup/form.html → layout:decorate 변환 | popup/form.html | 팝업 등록/수정 |
| 2-H-4 | AdminPopupController currentMenu 확인 | AdminPopupController | `currentMenu="popups"` |
| 2-H-5 | **기능 테스트: 목록** | /admin/popups | 목록 조회, 검색 ✅ |
| 2-H-6 | **기능 테스트: 등록/수정** | /admin/popups/new, /admin/popups/{id}/edit | 유형, 노출 위치, 기간 ✅ |
| 2-H-7 | **기능 테스트: 삭제** | - | 삭제 동작 ✅ |

**적용 사항:**
- list.html: 검색(키워드, 유형, 노출위치, 디바이스, 상태), 페이징, 삭제 버튼, credentials
- detail.html: max-w 래퍼, 삭제 버튼(data-* + onclick), credentials
- form.html: 유형/노출위치/디바이스, 크기/좌표, 콘텐츠, 링크, 노출기간(startAt/endAt), 정렬순서, 체크박스(활성/게시/로그인전용/오늘하루보지않기), credentials
- PopupSearchRequest: @Setter 추가 (폼 바인딩)

**요청 예시:** `@PROJECT_CONTEXT.md @all_project.md  관리자 화면 2단계 [팝업 관리]를 순차 구현해줘.`

---

##### 2-I. 파일 관리 (신규 구현)
> 각 개발전 관련 스키마 정보를 한번 더 분석 후 정확하게 구조를 파악 후 진행한다.
| 순서 | 작업 | 대상 | 검증 |
|------|------|------|------|
| 2-I-1 | AdminFileController 신규 생성 | AdminFileController.java | /admin/files |
| 2-I-2 | file/list.html 신규 생성 | admin/file/list.html | 파일 목록, 업로드 영역 |
| 2-I-3 | FileController API 연동 | - | 업로드, 다운로드, 삭제 API 호출 |
| 2-I-4 | **기능 테스트: 목록** | /admin/files | 파일 목록, ref_type/ref_id 필터 ✅ |
| 2-I-5 | **기능 테스트: 업로드** | - | 파일 업로드, 목록 갱신 ✅ |
| 2-I-6 | **기능 테스트: 다운로드/삭제** | - | 다운로드, 삭제 ✅ |

**적용 사항:**
- AdminFileController: /admin/files, currentMenu="files", FileSearchRequest 바인딩
- FileSearchRequest, FileMapper.findAllWithFilter/countWithFilter, FileService.getFilesForAdmin
- FileController: GET /api/v1/files/list (refType, refId 선택, 페이징), POST /api/v1/files/batch (다중 파일 업로드)
- list.html: 업로드 영역(multiple + batch API), refType/refId 필터, 목록 테이블, 다운로드/삭제, credentials
- **다중 업로드**: input multiple, batch API 호출로 한 번에 여러 파일 업로드 지원

**요청 예시:** `@PROJECT_CONTEXT.md @all_project.md  관리자 화면 2단계 [파일 관리]를 순차 구현해줘.`

---

##### 2-J. 감사 로그 — ✅ 완료
> 각 개발전 관련 스키마 정보를 한번 더 분석 후 정확하게 구조를 파악 후 진행한다.
| 순서 | 작업 | 대상 | 검증 |
|------|------|------|------|
| 2-J-1 | audit-logs.html → layout:decorate 변환 | audit-logs.html | 감사 로그 목록 ✅ |
| 2-J-2 | audit-log-detail.html → layout:decorate 변환 | audit-log-detail.html | 감사 로그 상세 ✅ |
| 2-J-3 | AdminAuditLogController currentMenu 확인 | AdminAuditLogController | `currentMenu="audit"` ✅ |
| 2-J-4 | **기능 테스트: 목록** | /admin/audit-logs | 목록 조회, 페이징, 검색(액션/대상/사용자ID/기간) ✅ |
| 2-J-5 | **기능 테스트: 상세** | /admin/audit-logs/{id} | 상세 조회, before/after 데이터 ✅ |

**적용 사항:**
- page/size 기본값 설정 (null 방지)
- 검색: action, targetType(USER/POST/BOARD/ROLE/MENU/PAGE/POPUP), userId, startDate, endDate
- max-w-7xl(목록), max-w-4xl(상세) 래퍼 적용

**요청 예시:** `@PROJECT_CONTEXT.md @all_project.md  관리자 화면 2단계 [감사 로그]를 순차 구현해줘.`

---

##### 2-K. 설정
> 각 개발전 관련 스키마 정보를 한번 더 분석 후 정확하게 구조를 파악 후 진행한다.
| 순서 | 작업 | 대상 | 검증 |
|------|------|------|------|
| 2-K-1 | settings.html placeholder → 실제 폼 | admin/settings.html | 사이트명, 파일 저장 경로 등 입력 폼 |
| 2-K-2 | 설정 저장 API/로직 연동 | - | cms-config.yml 또는 DB 저장 |
| 2-K-3 | **기능 테스트: 조회** | /admin/settings | 현재 설정 값 표시 |
| 2-K-4 | **기능 테스트: 저장** | - | 설정 저장, 반영 확인 |

**요청 예시:** `@PROJECT_CONTEXT.md @all_project.md  관리자 화면 2단계 [설정]을 순차 구현해줘.`

---

**공통 요청 예시:**
```
@PROJECT_CONTEXT.md @all_project.md
관리자 화면 2단계 [역할 관리]를 순차 구현해줘.
```

### 15.5 ADMIN 권한

- `data-init.sql`에서 ADMIN은 이미 모든 permission에 매핑됨
- 별도 코드 수정 없이 ADMIN 로그인 시 전체 메뉴 접근 가능

### 15.6 검증 체크리스트

- [ ] Layout 단독으로 빈 페이지가 정상 렌더링되는지
- [ ] 대시보드에서 헤더(제목, 사용자), 좌측 메뉴, 메인 영역이 모두 표시되는지
- [ ] 메뉴 클릭 시 해당 페이지로 이동하는지
- [ ] `currentMenu` 기준으로 현재 메뉴가 활성 표시되는지
- [ ] 로그아웃 버튼이 정상 동작하는지

---

## 16. 사용자단 API 개발 작업 (Phase 3)

> 사용자 사이트(쇼핑몰, 홈페이지 등)에서 호출하는 공개 API를 `/api/v1/public/**` 경로로 통합합니다.
> 기존 Service를 재사용하며, 순차적으로 단계별 요청으로 진행합니다.

### 16.0 현재 상태 및 아키텍처

**이미 공개된 API (PUBLIC_URLS)**  
- `/api/v1/auth/login`, `register`, `refresh`, `password-reset/**`  
- `/api/v1/users/check/**`, `/api/v1/common-codes/active/**`, `/api/v1/popups/display`

**공개 의도이나 인증 필요 (미등록)**  
- `GET /api/v1/menus/public`, `GET /api/v1/pages/public/{pageCode}` — SecurityConfig 미등록

**인증 필요**  
- BoardGroup, Board, Post, Comment — 전부 인증 필요

**아키텍처**: PublicSiteController, PublicBoardController → 기존 Service 호출

---

### 16.1 1단계: SecurityConfig 공개 경로 보완

| 순서 | 작업 | 대상 |
|------|------|------|
| 1-1 | PUBLIC_URLS에 `/api/v1/public/**` 추가 | SecurityConfig.java |

**요청 예시:** `@PROJECT_CONTEXT.md @all_project.md 사용자단 API 1단계를 실행해줘`

---

### 16.2 2단계: PublicSiteController 생성 (사이트 기초 API)

| 순서 | API | 기존 서비스 | 비고 |
|------|-----|-------------|------|
| 2-1 | GET `/api/v1/public/menus` | SiteMenuService.findVisibleMenus(includeLoginRequired) | 쿼리: includeLoginRequired |
| 2-2 | GET `/api/v1/public/pages/{pageCode}` | SitePageService.findByPageCode(pageCode) | is_published 확인 |
| 2-3 | GET `/api/v1/public/popups` | PopupService.getDisplayPopups(positionType, deviceType, isLogin) | |
| 2-4 | GET `/api/v1/public/common-codes/{groupCode}` | CommonCodeService.getActiveCodesByGroupCode(groupCode) | |

**신규 패키지**: `com.nt.cms.publicapi.controller.PublicSiteController` (Java 예약어로 public 대신 publicapi 사용)  
**구현**: Controller에서 기존 Service 호출, 기존 DTO/Response 재사용.

**적용 완료 (2026-02-24)**: PublicSiteController 4개 API 구현, 단위 테스트 작성 및 통과

**요청 예시:** `@PROJECT_CONTEXT.md @all_project.md 사용자단 API 2단계를 실행해줘`

---

### 16.3 3단계: PublicBoardController 생성 (게시판/게시글 공개 조회)

| 순서 | API | 기존 서비스 | 비고 |
|------|-----|-------------|------|
| 3-1 | GET `/api/v1/public/board-groups` | BoardGroupService.getGroups() | |
| 3-2 | GET `/api/v1/public/boards` | BoardService.getBoards() | |
| 3-3 | GET `/api/v1/public/boards/group/{groupId}` | BoardService.getBoardsByGroupId(groupId) | |
| 3-4 | GET `/api/v1/public/boards/code/{boardCode}` | BoardService.getBoardByCode(boardCode) | |
| 3-5 | GET `/api/v1/public/boards/{boardId}/posts` | PostService.getPosts() | 페이징, 검색 |
| 3-6 | GET `/api/v1/public/boards/{boardId}/posts/notices` | PostService.getNotices() | |
| 3-7 | GET `/api/v1/public/boards/{boardId}/posts/{postId}` | PostService.getPost(id, null, null) | 비밀글 시 에러 |
| 3-8 | POST `/api/v1/public/boards/{boardId}/posts` | PostService.createPost() | 세션, 비로그인 시 ANONYMOUS |
| 3-9 | PUT `/api/v1/public/boards/{boardId}/posts/{postId}` | PostService.updatePost() | 세션 기반 |
| 3-10 | DELETE `/api/v1/public/boards/{boardId}/posts/{postId}` | PostService.deletePost() | 세션 기반 |
| 3-8 | GET `.../posts/{postId}/prev`, `.../next` | PostService.getPrevPost(), getNextPost() | |
| 3-9 | GET `.../posts/{postId}/comments` | CommentService.getComments() | |

**권한**: 공개 조회만 인증 없이 허용. 비밀글은 SECRET_POST_ACCESS_DENIED 또는 404.

**적용 완료 (2026-02-24)**: PublicBoardController 9개 API 구현, 단위 테스트 작성 및 통과

**요청 예시:** `@PROJECT_CONTEXT.md @all_project.md 사용자단 API 3단계를 실행해줘`

---

### 16.4 4단계: PostService 비밀글 익명 처리 확인/보완

| 순서 | 작업 | 대상 |
|------|------|------|
| 4-1 | getPost(id, null, null) 호출 시 비밀글 예외 처리 확인 | DefaultPostService |
| 4-2 | 미구현 시 SECRET_POST_ACCESS_DENIED 반환 처리 | ErrorCode, BusinessException |

**권장**: roleId=null일 때 비밀글만 차단, 일반글은 익명 허용.

**적용 완료 (2026-02-24)**: DefaultPostService getPost() null 안전성 보완(Objects.equals), 익명 비밀글 차단 검증 테스트 추가

**요청 예시:** `@PROJECT_CONTEXT.md @all_project.md 사용자단 API 4단계를 실행해줘`

---

### 16.5 5단계: SitePageService is_published 조건 검증

| 순서 | 작업 | 대상 |
|------|------|------|
| 5-1 | findByPageCode 시 is_published=true 페이지만 반환하는지 확인 | SitePageMapper.xml, DefaultSitePageService |
| 5-2 | 미적용 시 Mapper/Service에 조건 추가 | |

**적용 완료 (2026-02-24)**: SitePageMapper.findByPageCode에 `AND sp.is_published = true` 조건 추가, 단위 테스트 보완

**요청 예시:** `@PROJECT_CONTEXT.md @all_project.md 사용자단 API 5단계를 실행해줘`

---

### 16.6 6단계: 문서 및 테스트

| 순서 | 작업 | 대상 |
|------|------|------|
| 6-1 | API 명세서 업데이트 | Doc/api.md — 공개 API 섹션 추가 |
| 6-2 | Postman Collection 업데이트 | Doc/postman/ — Public API 폴더/요청 추가 |
| 6-3 | 단위 테스트 작성 | PublicSiteController, PublicBoardController |

**적용 완료 (2026-02-24)**: Doc/api.md 공개 API 섹션 추가, Postman Public API 폴더 추가, 단위 테스트 통과 확인

**요청 예시:** `@PROJECT_CONTEXT.md @all_project.md 사용자단 API 6단계를 실행해줘`

---

### 16.7 선택 사항 (추가 검토)

| 항목 | 설명 |
|------|------|
| 파일 다운로드 공개 | refType=POST 첨부 파일 인증 없이 다운로드 허용 여부 |
| CORS 검증 | 외부 도메인 프론트엔드 호출 시 설정 점검 |

---

### 16.8 7단계: 최신글 API

| 순서 | API | 기존 서비스 | 비고 |
|------|-----|-------------|------|
| 7-1 | GET `/api/v1/public/posts/latest` | PostService | 여러 게시판의 최신글 조회 |

**쿼리 파라미터**:
- `boardIds` (필수): 게시판 ID 목록 (콤마 구분, 예: `1,2,3`)
- `size` (선택, 기본 10): 조회할 게시글 수 (최대 50)
- `sortField` (선택, 기본 `createdAt`): 정렬 필드 (`createdAt`, `viewCount`, `title`)
- `sortOrder` (선택, 기본 `DESC`): 정렬 방식 (`ASC`, `DESC`)

**응답**: `{ success, data: [ { id, boardId, boardCode, boardName, title, writerName, viewCount, commentCount, fileCount, createdAt } ] }`

**적용 완료 (2026-02-27)**: PublicBoardController에 `/posts/latest` API 구현, PostMapper에 `findLatestByBoardIds` 쿼리 추가

---

### 16.9 참고: 기존 API와의 관계

- `/api/v1/menus/public`, `/api/v1/pages/public/**` — 유지 시 PUBLIC_URLS 추가, 또는 `/public/*` 통합 후 deprecated
- `/api/v1/popups/display`, `/api/v1/common-codes/active/**` — 이미 공개, PublicSiteController에서 `/public/*`로 래핑하여 통일

### 16.10 참고: 로그인 사용자 API (이미 존재)

- PostController, CommentController — 게시글/댓글 CRUD
- FileController — 파일 업로드
- AuthController.me, UserController — 내 정보, 비밀번호 변경

---

*마지막 업데이트: 2026-02-27*
*사용자단 API 개발 계획 재정립: 7단계 순차 적용 구조 (최신글 API 추가)*
