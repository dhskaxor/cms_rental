# CMS 리팩토링 및 동기화 체크리스트

> cms / cms_user_react 전체 리팩토링 시 참조용 마스터 체크리스트

---

## 1. 라우팅·URL

| 영역 | CMS (site) | cms_user_react |
|------|------------|----------------|
| 메인 | `/site/` | `/site` |
| 도움말 | `/site/help` | `/site/help` |
| 정적 페이지 | `/site/page/{pageCode}` | `/site/page/:pageCode` |
| 게시판 목록 | `/site/board/{boardCode}` | `/site/board/:boardId` (boardCode 지원) |
| 게시글 상세 | `/site/board/{boardCode}/post/{postId}` | `/site/board/:boardId/post/:postId` |
| 글쓰기 | `/site/board/{boardCode}/write` | `/site/board/:boardId/write` |
| 글 수정 | `/site/board/{boardCode}/post/{postId}/edit` | `/site/board/:boardId/post/:postId/edit` |
| 로그인 | `/site/auth/login` | `/site/auth/login` |
| 연락처 | `/site/contact` | `/site/contact` |

---

## 2. API 엔드포인트

| 기능 | 경로 |
|------|------|
| 게시판 (코드) | `GET /api/v1/public/boards/code/{boardCode}` |
| 게시판 (ID) | `GET /api/v1/public/boards/{boardId}` |
| 메뉴 | `GET /api/v1/public/menus` |
| 정적 페이지 | `GET /api/v1/public/pages/{pageCode}` |
| 사이트 설정 | `GET /api/v1/public/site/config` |
| Favicon | `GET /api/v1/public/site/favicon` |
| 팝업 | `GET /api/v1/public/popups` |
| 최신글 | `GET /api/v1/public/posts/latest` |
| 대관 캘린더 조회 | `GET /api/v1/rental/search` |

---

## 3. XSS 허용 태그 (동기화 유지)

**단일 소스**: cms_user_react `src/utils/sanitizer.ts` ALLOWED_TAGS를 XSS 허용 태그 마스터로 사용. cms는 Thymeleaf th:utext로 서버 렌더링.
변경 시 양쪽 모두 반영. 이 문서가 마스터 목록.

```
p, br, strong, b, em, i, u, a, ul, ol, li, h2, h3, h4, div, span,
blockquote, pre, code, hr, table, thead, tbody, tr, th, td, img
```

---

## 4. 기능 대응표

| 기능 | cms site | cms_user_react |
|------|----------|----------------|
| 홈 | site/index (Thymeleaf, 서버 렌더링) | HomePage |
| 도움말 | site/help (Thymeleaf) | HelpPage |
| 연락처 | site/contact (Thymeleaf, siteConfig 기반) | ContactPage |
| Swagger 링크 | site/help 내 /swagger-ui.html | HelpPage config.swaggerUrl |
| 정적 페이지 | site/page (Thymeleaf, SiteViewController) | StaticPage |
| 게시판 목록 | site/board/list (Thymeleaf) | BoardListPage |
| 게시글 상세 | site/board/post-detail (Thymeleaf) | PostDetailPage |
| 글쓰기/수정 | site/board/post-form (Thymeleaf, SiteFormController POST) | PostFormPage |
| 로그인/회원가입 | site/auth | LoginPage, RegisterPage |
| 최신글 | SiteViewController.index, latestPosts 서버 렌더링 | LatestPosts |
| 팝업 | SiteModelAttributeAdvice sitePopups, layout HTML | PopupContainer |

---

## 5. 게시판 템플릿 (폴더 기반)

- **경로**: `templates/site/board/` 하위 폴더
- **자동 등록**: 폴더 추가 시 관리자 템플릿 셀렉트에 자동 반영 (코드 수정 불필요)
- **파일 규칙**: `board-list.html` (목록), `board-post.html` (상세), `post-form.html` (폼)
- **API**: `GET /api/v1/boards/templates` → 폴더명 목록 반환

---

## 6. 문서 체인

```
cms/README.md           → 프로젝트 전체 개요
cms/PROJECT_CONTEXT.md  → 기술 결정, API, Phase
cms/USER_CONTEXT.md     → 사용자 사이트(site) 규칙
cms/user_project.md     → 사용자 사이트 상세 명세
cms/Doc/api.md          → API 명세
cms/Doc/user_map.md     → 사용자 사이트 API 사용
cms/all_project.md      → 전체 기능·구조 요약

cms_user_react/README.md         → React 프로젝트 개요
cms_user_react/PROJECT_CONTEXT.md → 아키텍처, cms 참조
```
