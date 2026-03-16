# CMS Core - 사용자 사이트 명세서

## 1. 개요

사용자 사이트는 `/site/` 경로 하위에서 API 호출 기반으로 동작한다.
관리자가 등록한 메뉴·페이지·게시판·팝업 등을 활용하여 CMS를 소개하고 기능을 확인할 수 있는 페이지이다.

---

## 2. 전제 조건

| 항목 | 결정 |
|------|------|
| URL | `/site/` 프리픽스 (예: /site/, /site/page/about, /site/board/1) |
| 데이터 소스 | API만 (`/api/v1/public/**`, 인증 시 기존 API) |
| 인증 | Spring 세션 (관리자와 별도) |
| 반응형 | PC: 호버 드롭다운 / 모바일: 햄버거 + 아코디언 |
| 스타일 | 깔끔한 테크니컬 페이지 |
| 홈(대문) | /site/ → index.html, main 페이지에 CMS 기능 설명 |
| 로그인 분리 | ADMIN→/admin, USER/MANAGER→/site, 동시 로그인 가능 |
| API 인증 | AdminSessionAuthenticationFilter 확장 (Admin/Site 세션 구분) |
| 에러 처리 | 글로벌 404/500 페이지, 메시지+status 코드 표시 |

---

## 3. 아키텍처

- **cms site**: Thymeleaf 서버 렌더링 (Page/Submit). SiteViewController (GET), SiteFormController (POST). Layout/메뉴/팝업/최신글/게시판 링크는 서버에서 렌더링.
- **cms_user_react**: 클라이언트 React가 API 응답을 받아 렌더링. Layout/메뉴: `fetch('/api/v1/public/menus')` 후 컴포넌트 렌더링.
- **로그인**: `POST /site/auth/login` → 세션 생성 → 리다이렉트

---

## 4. 단계별 작업 (순차 실행)

### Phase 1: 사전 구성 ✅ 완료

| 순서 | 작업 | 대상 | 검증 |
|------|------|------|------|
| 1-1 | Site 패키지 생성 | `com.nt.cms.site` | ✅ |
| 1-2 | SiteViewController | `/site/**` → Thymeleaf 뷰 (Page/Submit) | ✅ /site/ 접근 가능 |
| 1-3 | SecurityConfig | `/site/**` permitAll 추가 | ✅ /site/ 인증 없이 접근 |
| 1-4 | SiteAuthController | GET/POST /site/auth/login | ✅ 로그인 폼, 세션 생성 |
| 1-5 | AdminSessionAuthenticationFilter 확장 | Admin/Site 세션 구분, 동시 로그인 지원 | ✅ SITE_CURRENT_USER 처리 |
| 1-6 | 정적 리소스 | static/site/css/site.css, templates/site/ | ✅ Thymeleaf 기반 |

**요청 예시:** `@USER_CONTEXT.md @user_project.md 사용자 사이트 Phase 1을 실행해줘.`

---

### Phase 2: Layout 및 메뉴 ✅ 완료

| 순서 | 작업 | 대상 | 검증 |
|------|------|------|------|
| 2-1 | 메뉴 | SiteModelAttributeAdvice siteMenus | ✅ 서버 주입, Thymeleaf 렌더링 |
| 2-2 | 1Depth~Last Depth 호버 드롭다운 | nav + ul 계층, CSS/JS | ✅ PC에서 호버 시 서브메뉴 표시 |
| 2-3 | 모바일 햄버거 + 아코디언 | aside/오버레이, children 클릭 시 펼침 | ✅ 모바일에서 메뉴 오버레이·아코디언 동작 |
| 2-4 | 메뉴 URL 매핑 | menuType=PAGE/BOARD/LINK | ✅ PAGE→/site/page/{pageCode}, BOARD→/site/board/{boardId}, LINK→외부링크 이동 |

**요청 예시:** `@USER_CONTEXT.md @user_project.md 사용자 사이트 Phase 2를 실행해줘.`

---

### Phase 3: 페이지 및 게시판 ✅ 완료

| 순서 | 작업 | 대상 | 검증 |
|------|------|------|------|
| 3-1 | 대문(main) 페이지 | index.html 내 main 영역, CMS 기능 설명 | ✅ /site/ 접속 시 표시 |
| 3-2 | 정적 페이지 렌더링 | /site/page/{pageCode} | ✅ Thymeleaf site/page, SitePageService |
| 3-3 | 게시판 목록/상세/글쓰기·수정 | site/board/list, post-detail, post-form | ✅ SiteViewController GET, SiteFormController POST |
| 3-4 | XSS 대응 | innerHTML 사용 시 허용 태그 기반 sanitize | ✅ 안전한 렌더링 |
| 3-5 | CMS 소개용 페이지 데이터 | 관리자에서 site_page, site_menu 등록 | 메뉴 연동, 소개·기능 페이지 등 (관리자에서 데이터 등록) |

**요청 예시:** `@USER_CONTEXT.md @user_project.md 사용자 사이트 Phase 3를 실행해줘.`

---

### Phase 4: 사용자 로그인 및 부가 기능 ✅ 완료

| 순서 | 작업 | 대상 | 검증 |
|------|------|------|------|
| 4-1 | 로그인/로그아웃 UI | 헤더: 로그인 버튼, 로그인 후 사용자명+로그아웃 | 세션 기반 표시 | ✅ |
| 4-2 | 인증 필요 메뉴 | isLoginRequired 시 비로그인 → 로그인 페이지 | 리다이렉트 동작 | ✅ |
| 4-3 | API 호출 시 credentials | fetch credentials: 'same-origin' | 세션 쿠키 전송 | ✅ |
| 4-4 | 팝업 | fetch('/api/v1/public/popups') → 모달/레이어 | 노출 조건 만족 시 표시 | ✅ |

**구현 내용**
- PublicSiteController: GET /api/v1/public/site/me (현재 사이트 세션 사용자)
- layout 인라인 스크립트: 모바일 메뉴 토글, 팝업 닫기, 로그인 필요 메뉴 클릭 처리
- SiteAuthController: login redirect 파라미터 처리
- site.css: .site-nav-user, .site-nav-logout, .site-popup-overlay 등

**요청 예시:** `@USER_CONTEXT.md @user_project.md 사용자 사이트 Phase 4를 실행해줘.`

---

### Phase 5: 에러 페이지 및 디자인 ✅ 완료

| 순서 | 작업 | 대상 | 검증 |
|------|------|------|------|
| 5-0 | 에러 페이지 (사용자 사이트 전용) | 404, 500 등 | /site/** 전용, 메시지+status 코드 표시 | ✅ |

**구현 내용**
- SiteErrorViewResolver: ErrorViewResolver 구현, /site/** 원본 경로 시 site/error 뷰 반환
- SiteExceptionHandler: @ControllerAdvice, /site/** HTML 요청의 NoHandlerFoundException·Exception 처리
- templates/site/error.html: statusCode, message, path, 메인 링크
- site.css: .site-error-code, .site-error-message, .site-error-link 등

**요청 예시:** `@USER_CONTEXT.md @user_project.md 사용자 사이트 Phase 5를 실행해줘.` (에러 페이지)  

---

### Phase 6: 디자인 및 문서 ✅ 완료

| 순서 | 작업 | 대상 | 검증 |
|------|------|------|------|
| 6-1 | 테크니컬 스타일 | 폰트, 색상, 여백, 카드형 블록 | 깔끔한 UI | ✅ |
| 6-2 | CSS 변수 (커스터마이징) | --primary-color, --font-family 등 | 테마 오버라이드 가능 | ✅ |
| 6-3 | Doc/user_map.md | 사용자 사이트 API 사용 문서 | 문서 갱신 | ✅ |
| 6-4 | USER_CONTEXT.md, user_project.md | 현황 반영 | 최신화 | ✅ |

**구현 내용**
- site.css: :root에 --primary-color, --font-family, --spacing-*, --radius-* 등 CSS 변수 정의
- 카드형 블록(.feature-card, .site-page, .site-board-card-item 등)에 변수 적용, 호버 효과
- Doc/user_map.md: 5절 CSS 커스터마이징 가이드 추가

**요청 예시:** `@USER_CONTEXT.md @user_project.md 사용자 사이트 Phase 6을 실행해줘.` (디자인·문서)

---

## 5. 파일 구조 (목표)

```
src/main/resources/
├── static/site/
│   └── css/site.css
└── (필요 시 templates/site/ - 로그인 폼 등)

src/main/java/com/nt/cms/site/
├── controller/
│   ├── SiteController.java
│   └── SiteAuthController.java
├── config/
│   └── SiteWebConfig.java (선택)
└── security/
    └── SiteSessionAuthenticationFilter.java (또는 기존 확장)
```

---

## 6. 문서 업데이트 규칙

- 사용자 사이트 관련 API 사용: Doc/user_map.md에 기록
- Phase 완료 시: `USER_CONTEXT.md`, `user_project.md` 현황 갱신
- 규칙: `.cursorrules` 섹션 15, 18 참조

---

*마지막 업데이트: 2026-02-25*
