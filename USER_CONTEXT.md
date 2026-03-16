# CMS Core - 사용자 사이트 컨텍스트

> 이 파일은 사용자 사이트(/site/**) 개발 시 AI 어시스턴트가 참조하는 핵심 정보입니다.
> user_project.md와 함께 사용자 페이지 관련 컨텍스트를 관리합니다.

---

## 1. 사용자 사이트 핵심 정보

| 항목 | 값 |
|------|-----|
| URL 프리픽스 | /site/ |
| 데이터 소스 | **cms site**: Thymeleaf SSR (Page/Submit, 메뉴·팝업·최신글·게시판 링크 서버 렌더링) / **cms_user_react**: API fetch |
| 인증 방식 | Spring 세션 (관리자와 별도) |
| 반응형 | PC: 호버 드롭다운 / 모바일: 햄버거 + 아코디언 |

---

## 2. 확정된 기술적 결정

### 2.1 렌더링
- **cms site**: Thymeleaf 서버 렌더링 (Page/Submit 방식). SiteViewController + SiteFormController. 메뉴·팝업·최신글·게시판 링크는 서버 렌더링.
- **cms_user_react**: 클라이언트(React)에서 /api/v1/public/** 등 API를 fetch하여 렌더링한다.

### 2.2 경로 및 세션
- /site/** 경로는 사용자 사이트 전용이다.
- Site 세션과 Admin 세션을 반드시 분리 유지한다.

### 2.3 로그인 분리
- 같은 user 테이블, 로그인 시 역할(ADMIN/USER/MANAGER)에 따라 리다이렉트
  - ADMIN → /admin
  - USER, MANAGER → /site
- 한 브라우저에서 관리자와 사용자를 동시에 로그인 가능 (다른 탭)
  - Admin 세션과 Site 세션을 쿠키 경로 또는 세션 키로 분리

### 2.4 홈(대문) 페이지
- /site/ 접속 시 index.html을 표시한다.
- index.html 내부에 대문(사용자 main) 페이지를 구성한다.
- main 페이지에는 해당 CMS의 기능을 설명하는 내용을 표시한다.

### 2.5 API 인증
- 사용자 로그인(세션) 후 API 호출 시: 기존 AdminSessionAuthenticationFilter 확장
  - Admin 세션과 Site 세션을 구분하여 SecurityContext에 반영

### 2.6 에러 페이지
- 사용자 사이트(/site/**) 전용 404, 500 에러 페이지
  - 해당 페이지로 이동, 적절한 메시지와 status 코드 표시

### 2.7 참조 API
- 메뉴: GET /api/v1/public/menus
- 페이지: GET /api/v1/public/pages/{pageCode}
- 팝업: GET /api/v1/public/popups
- 공통코드: GET /api/v1/public/common-codes/{groupCode}
- 게시판: GET /api/v1/public/board-groups, boards, boards/{id}/posts 등

---

## 3. 참조 파일

| 파일 | 설명 |
|------|------|
| user_project.md | 사용자 사이트 상세 명세 및 순차 실행 계획 |
| Doc/user_map.md | 사용자 사이트 API 사용 문서 |
| Doc/REFACTORING_CHECKLIST.md | 리팩토링·동기화 체크리스트 (cms/site ↔ cms_user_react) |
| PROJECT_CONTEXT.md | 전체 프로젝트 컨텍스트 (API, DB 등) |
| .cursorrules | 코딩 규칙 (섹션 15. 사용자 사이트 규칙) |

---

## 4. 다음 개발 세션 시작 방법

```
@.cursorrules @USER_CONTEXT.md @user_project.md
파일들을 읽고 사용자 사이트 컨텍스트를 파악한 후,
[단계명]을 순차 실행해줘.
```

**요청 예시:**
```
@USER_CONTEXT.md @user_project.md
사용자 사이트 Phase 1을 실행해줘.
```

---

---

## 5. Phase 진행 현황

- Phase 1: 사전 구성 ✅
  - Site 패키지, SiteController, SiteAuthController, 정적 리소스 구현 완료
  - Admin/Site 세션 분리 (CURRENT_USER / SITE_CURRENT_USER)
  - Doc/api.md에 Site 로그인/정적 리소스 명세 추가
- Phase 2: Layout 및 메뉴 ✅
  - /api/v1/public/menus 연동, 데스크탑/모바일 메뉴 렌더링
  - PC: 호버 드롭다운, 모바일: 햄버거 + 아코디언 동작
  - menuType=PAGE/BOARD/LINK에 따른 기본 URL 매핑 (PAGE→/site/page/{pageCode}, BOARD→/site/board/{boardId}, LINK→linkUrl)
- Phase 3: 페이지 및 게시판 ✅
  - **cms site (Page/Submit)**: SiteViewController GET 처리, SiteFormController POST 처리. Thymeleaf 템플릿 site/index, site/help, site/page, site/board. 메뉴·팝업·최신글·게시판 링크 서버 렌더링 (JS 최소화)
  - **cms_user_react**: API fetch 기반 SPA (HomePage, BoardListPage, PostDetailPage, PostFormPage 등)
  - 페이지/게시글 본문 HTML 허용 태그 기반 sanitize (XSS 대응, REFACTORING_CHECKLIST.md 동기화)
- Phase 4: 사용자 로그인 및 부가 기능 ✅
  - 로그인/로그아웃 UI: 헤더에 세션 기반 표시 (비로그인: 로그인 링크, 로그인: 사용자명 + 로그아웃)
  - GET /api/v1/public/site/me: 현재 사이트 로그인 사용자 조회
  - 인증 필요 메뉴: isLoginRequired 메뉴 클릭 시 비로그인 → /site/auth/login?redirect= 목표 URL
  - 팝업: fetch /api/v1/public/popups → LAYER/MODAL 오버레이, WINDOW 새 창, 오늘 하루 보지 않기 쿠키
- Phase 5: 에러 페이지 ✅
  - SiteErrorViewResolver: /site/** 경로 에러 시 site/error 템플릿 (status, message 표시)
  - SiteExceptionHandler: /site/** HTML 요청 예외 시 site/error (404, 500 등)
  - templates/site/error.html, site.css .site-error-* 스타일
- Phase 6: 디자인 및 문서 ✅
  - 테크니컬 스타일: 폰트, 색상, 여백, 카드형 블록 적용
  - CSS 변수: --primary-color, --font-family 등으로 테마 오버라이드 가능
  - Doc/user_map.md: 사용자 사이트 API 사용 문서 및 CSS 커스터마이징 가이드 갱신
- Phase 7: 게시판 권한 기반 UI ✅
  - 글쓰기/수정/삭제 버튼: board.permissions + site/me 역할로 canCreate/canUpdate/canDelete 판단
  - 비회원(ANONYMOUS) 권한 시 비로그인에서도 글쓰기 버튼 노출
  - Public API: POST/PUT/DELETE /api/v1/public/boards/{boardId}/posts (세션 기반)
  - 라우트: /site/board/{boardId}/write, /site/board/{boardId}/post/{postId}/edit
- Phase 8: React 디자인 동기화 ✅
  - **site.css 전면 개편**: CSS 변수 기반 다크 테마 디자인 시스템 (React `index.css`와 동일)
  - **index.html 메인 페이지 개편**: Hero 섹션, Tech Stack, 기능 그리드, API Info, 최신글/게시판 바로가기
  - **헤더/네비게이션**: React와 동일한 그라데이션 로고, hover 배경 효과, 로그인 버튼 스타일
  - **게시판 템플릿**: 아이콘 헤더, 검색 폼, 번호 컬럼, 카드형 게시글 상세
  - **최신글 컴포넌트**: SiteViewController.index()에서 getLatestPosts로 서버 렌더링

---

## 6. CSS 디자인 시스템

### 6.1 CSS 변수 (:root)

| 구분 | 변수 예시 | 설명 |
|------|-----------|------|
| 색상 | `--color-primary`, `--color-bg`, `--color-text` | 테마 색상 |
| 타이포그래피 | `--font-sans`, `--font-size-base` | 폰트 설정 |
| 간격 | `--spacing-1` ~ `--spacing-16` | 여백 스케일 |
| 효과 | `--shadow-md`, `--glow-primary` | 그림자/글로우 |
| 전환 | `--transition-normal` | 애니메이션 시간 |

### 6.2 테마 변경

`site.css`의 `:root` 변수만 수정하면 전체 테마가 변경됩니다.

```css
:root {
    --color-primary: #00d4ff;  /* 주요 색상 변경 */
    --color-bg: #0a0a0f;       /* 배경색 변경 */
}
```

---

*마지막 업데이트: 2026-03-10*
