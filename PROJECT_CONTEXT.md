# CMS Core Platform - 프로젝트 컨텍스트

> 이 파일은 AI 어시스턴트가 프로젝트를 이해하는 데 필요한 핵심 정보를 담고 있습니다.
> 새로운 개발 세션 시작 시 이 파일을 참조하세요.
> 이 프로젝트는 CMS 관리자 프로젝트이며, 여기에 개발된 API를 이용하여 사용자단은 여러 사이트에서 API를 호출하여 사용합니다.
> 이 프로젝트가 기본이 되고, 여기에 기능이 추가되어 쇼핑몰 / 홈페이지 등 여러 사이트가 개발될 예정입니다.

---

## 1. 프로젝트 핵심 정보

| 항목 | 값 |
|------|-----|
| 프로젝트명 | CMS Core Platform |
| 패키지명 | `com.nt.cms` |
| Java 버전 | 17 |
| Spring Boot 버전 | 3.2.x |
| 주 데이터베이스 | MariaDB (PostgreSQL 추후 지원) |
| 테스트 DB | H2 In-Memory |

---

## 2. 확정된 기술적 결정 사항

### 2.1 아키텍처
- **계층 구조**: Controller → Service (인터페이스) → Mapper
- **ORM**: MyBatis XML Mapper (JPA 사용 금지)
- **구현체 명명**: `Default` 접두어 (예: `DefaultUserService`)

### 2.2 네이밍 컨벤션
- **데이터 객체**: VO (예: `UserVO`, `BoardVO`)
- **DB 접근**: mapper 패키지 (예: `UserMapper`)
- **DTO**: `UserCreateRequest`, `UserResponse`
- **패키지**: 소문자, 단수형 (예: `user`, `board`)

### 2.3 보안
- **JWT Access Token**: 1시간
- **JWT Refresh Token**: 14일
- **비밀번호 암호화**: BCrypt
- **비밀번호 정책**: 영문/숫자/특수문자 포함, 6~12자
- **로그인 실패**: 5회 이상 시 계정 잠금 (LOCKED)
- **멀티 디바이스**: 불허 (새 로그인 시 기존 토큰 무효화)

### 2.4 회원가입 / 이메일
- **회원가입**: 누구나 가능 (공개)
- **이메일 인증**: 불필요
- **비밀번호 찾기**: 재설정 링크 발송 방식

### 2.5 게시판
- **대댓글**: 1단계까지 (댓글 → 대댓글)
- **검색**: 제목/내용, 작성자, 기간별

### 2.6 파일
- **저장 경로**: `C:/cms/files` (설정 가능)
- **허용 확장자**: 이미지(jpg, png, gif, webp), 문서(pdf, doc, docx, xls, xlsx, hwp)
- **최대 크기**: 3MB

### 2.7 캐싱
- **솔루션**: Redis (선택)
- **미설정 시**: 인메모리 캐시로 대체
- **대상**: 세션, 권한 정보

### 2.8 API
- **버전**: `/api/v1/...`
- **문서화**: Swagger/OpenAPI
- **CORS**: 필요 (외부 프론트엔드 호출 대비)

### 2.9 설치 기능
- **UI**: Thymeleaf (백엔드 포함)
- **설정 저장**: `cms-config.yml`
- **상태 관리**: `installed: true` 플래그
- **재설치**: 기능 포함 (추후용)

### 2.10 로깅
- **로그 위치**: 설정 가능
- **보관 기간**: 설정 가능

---

## 3. API 응답 포맷

### 기본 응답
```json
{
  "success": true,
  "data": { },
  "error": null
}
```

### 에러 응답
```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "ERROR_CODE",
    "message": "에러 메시지"
  }
}
```

### 페이징 응답
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

## 4. 데이터베이스 테이블

| 테이블 | 설명 |
|--------|------|
| `role` | 역할 마스터 (ADMIN, MANAGER, USER) |
| `permission` | 권한 마스터 (USER_CREATE, USER_READ 등) |
| `role_permission` | 역할-권한 N:M 매핑 |
| `user` | 사용자 정보 |
| `jwt_refresh_token` | JWT 리프레시 토큰 관리 |
| `board_group` | 게시판 그룹 |
| `board` | 게시판 설정 |
| `board_permission` | 게시판별 역할별 CRUD 권한 |
| `board_post` | 게시글 |
| `board_comment` | 댓글 (대댓글 지원) |
| `file` | 파일 (ref_type, ref_id 다형적 연결) |
| `audit_log` | 감사 로그 |
| `site_menu` | 사이트 메뉴 (계층형, 노출 위치/기간 제어) |
| `site_page` | 정적 페이지 (page_code, content, is_published) |
| `common_code_group` | 공통 코드 그룹 (그룹 코드/명, 시스템 코드 여부) |
| `common_code` | 공통 코드 상세 (그룹별 코드, 계층, 정렬, 기간) |
| `site_popup` | 사용자 사이트 팝업 (노출 위치, 디바이스, 기간, 오늘 하루 보지 않기 등) |

---

## 5. 개발 현황

### 5.1 Phase 1: API 모듈 (백엔드) — 완료

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

### 5.2 Phase 2: 관리자 화면 — 완료

| 구분 | 항목 | 상태 | 비고 |
|------|------|------|------|
| **Layout** | layout.html, 대시보드 | ✅ 완료 | 헤더, 좌측 메뉴, currentMenu 하이라이트 |
| **컨텐츠** | 역할, 사용자, 메뉴, 페이지, 게시판, 공통코드, 팝업, 감사로그 | ✅ Layout 통일 | 1단계 완료: 모든 템플릿 `layout:decorate` 적용 |
| **컨텐츠** | 게시글 관리 (AdminPostController) | ✅ 완료 | post/board-select, list, detail, form, 파일 첨부 포함 |
| **컨텐츠** | 파일 관리 (AdminFileController) | ✅ 완료 | file/list.html, 다중 업로드·refType/refId 필터 |
| **미구현** | 설정 (settings) | 🔲 placeholder | 실제 폼 미구현, placeholder 템플릿만 존재 |

### 5.3 Phase 3: 사용자단 API — 완료

| 단계 | 작업 | 상태 |
|------|------|------|
| 1 | SecurityConfig 공개 경로 보완 | `/api/v1/public/**` permitAll (완료) |
| 2 | PublicSiteController (메뉴, 페이지, 팝업, 공통코드) | 사이트 기초 API (완료) |
| 3 | PublicBoardController (게시판/게시글 공개 조회) | board-groups, boards, posts, comments (완료) |
| 4 | PostService 비밀글 익명 처리 확인/보완 | roleId=null 시 SECRET_POST_ACCESS_DENIED (완료) |
| 5 | SitePageService is_published 조건 검증 | findByPageCode 공개 페이지만 (완료) |
| 6 | 문서 및 테스트 | Doc/api.md, Postman, 단위 테스트 (완료) |
| 7 | UX 통합 디자인 동기화 | Lucide 통일, 다중 테마/폰트/레이아웃 동기화, auth 페이지 리팩터링 (완료) |

---

## 6. 다음 작업 (우선순위)

| 순위 | 작업 | 상세 | 요청 예시 |
|------|------|------|----------|
| 1 | ~~관리자 0단계: 사전 정리~~ | ~~posts, files, settings placeholder~~ | ✅ 완료 |
| 2 | ~~관리자 1단계: Layout 패턴 통일~~ | ~~모든 admin 템플릿 layout:decorate 통일~~ | ✅ 완료 |
| 3 | ~~관리자 2단계: 게시글·파일 관리~~ | ~~AdminPostController, AdminFileController 신규 구현~~ | ✅ 완료 |
| 4 | 관리자 2단계: 감사 로그·설정 검증 | 2-J 감사 로그 기능 검증, 2-K 설정 실제 폼 구현 | `관리자 화면 2단계 [감사 로그]를 순차 구현해줘` |
| 5 | 사용자단 API | 1~6단계 순차 실행 | `@PROJECT_CONTEXT.md @all_project.md 사용자단 API 1단계를 실행해줘` |

> 상세 작업 내용은 `all_project.md` 섹션 15(관리자 화면), 섹션 16(사용자단 API) 참조.

---

## 7. 사용자단 API 요약 (상세: all_project.md)

### 1단계: SecurityConfig
- `/api/v1/public/**` 공개 경로 추가

### 2단계: PublicSiteController
- `GET /api/v1/public/menus`, `pages/{pageCode}`, `popups`, `common-codes/{groupCode}`

### 3단계: 게시판/게시글 공개 API
- `GET /api/v1/public/board-groups`, `boards`, `boards/{id}/posts`, `posts/{id}`, `posts/{id}/comments` 등

### 4~5단계: 페이지 공개 조건, 문서·테스트

### 로그인 사용자 API (이미 존재)
- 게시글/댓글 CRUD, 파일 업로드, `auth/me`, `users/me`, 비밀번호 변경

---

## 8. 참조 파일

| 파일 | 설명 |
|------|------|
| `.cursorrules` | 코딩 규칙 (AI 자동 참조) |
| `all_project.md` | 프로젝트 상세 명세서 및 작업 계획 |
| `README.md` | 프로젝트 문서 |
| `cms_schema` | 데이터베이스 스키마 (MariaDB) |
| `PROJECT_CONTEXT.md` | 이 파일 (핵심 결정 사항 및 현황) |

---

## 9. 다음 개발 세션 시작 방법

```
@.cursorrules @all_project.md @PROJECT_CONTEXT.md @cms_schema 
파일들을 읽고 프로젝트 컨텍스트를 파악한 후, 
[작업명]을 실행해줘.
```

**작업 요청 예시:**
```
@PROJECT_CONTEXT.md @all_project.md
관리자 화면 0단계(사전 정리)를 실행해줘.
```

```
@PROJECT_CONTEXT.md @all_project.md
사용자단 API 1단계를 실행해줘.
```

---

## 10. 주의사항

1. **JPA 사용 금지** - 반드시 MyBatis XML Mapper 사용
2. **SELECT * 금지** - 모든 컬럼 명시적 작성
3. **Soft Delete** - 모든 테이블에 `deleted = 0` 조건 포함
4. **주석 필수** - 모든 주석은 한글로 작성
5. **단위 테스트 필수** - 모든 메서드에 테스트 작성
6. **인터페이스 기반** - 서비스는 인터페이스 + Default 구현체

---

*마지막 업데이트: 2026-03-19*
*사용자 사이트 UX 통합: Lucide 아이콘 통일, 다중 테마/컴팩트 폰트/1200px 레이아웃 동기화, auth 페이지(site.css 기반) 리팩터링 반영*
