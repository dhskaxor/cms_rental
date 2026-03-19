# CMS 리팩토링 및 동기화 체크리스트

> cms / cms_user_react 전체 리팩토링 시 참조용 마스터 체크리스트

---

## 1. 라우팅·URL

| 영역 | CMS (site) | cms_user_react |
|------|------------|----------------|
| 메인 | `/site/` | `/site` |
| 도움말 | `/site/help` | `/site/help` |
| 예약 달력 | `/site/rental` | `/site/rental` |
| 예약 진행 | `/site/rental/reserve` | `/site/rental/reserve` (인증 필요) |
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
| 대관 장소 목록 | `GET /api/v1/public/rentals/places` |
| 대관 공간 목록 | `GET /api/v1/public/rentals/places/{placeId}/rooms` |
| 대관 캘린더 조회 | `GET /api/v1/public/rentals/rooms/{roomId}/calendar?placeId=&yearMonth=` |
| 대관 예약 생성 | `POST /api/v1/public/rentals/rooms/{roomId}/reservations` (인증 필요) |
| 내 대관 예약 목록 | `GET /api/v1/public/rentals/reservations/my` (인증 필요) |
| 내 대관 예약 상세/취소 | `GET/DELETE /api/v1/public/rentals/reservations/{id}` (인증 필요) |

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
| 예약 달력 | site/rental/calendar (Thymeleaf) | RentalCalendarPage |
| 예약 진행 | site/rental/reserve (Thymeleaf) | RentalReservePage |
| 내 예약 | site/me 내 나의 예약 섹션 | MyPage 내 나의 예약 섹션 |

---

## 5. 게시판 템플릿 (폴더 기반)

- **경로**: `templates/site/board/` 하위 폴더
- **자동 등록**: 폴더 추가 시 관리자 템플릿 셀렉트에 자동 반영 (코드 수정 불필요)
- **파일 규칙**: `board-list.html` (목록), `board-post.html` (상세), `post-form.html` (폼)
- **API**: `GET /api/v1/boards/templates` → 폴더명 목록 반환

---

## 6. UX 통합 디자인 시스템 (2026-03-19 적용)

### 6.1 아이콘 라이브러리: Lucide 통일

| 항목 | cms_rental | cms_user_react |
|------|-----------|----------------|
| 라이브러리 | Lucide CDN (`lucide.min.js` + `lucide.createIcons()`) | `lucide-react` |
| 사용 방식 | `<i data-lucide="icon-name"></i>` → SVG 변환 | `<IconName />` React 컴포넌트 |
| CSS 셀렉터 | `.lucide` (SVG), `width`/`height` 기반 | Tailwind `h-*`/`w-*` 유틸리티 |
| Bootstrap Icons | **완전 제거** (CDN 포함) | 해당 없음 |

### 6.2 폰트 크기: 컴팩트 스케일 통일

| 단계 | CSS 변수 | 값 |
|------|---------|------|
| xs | `--font-size-xs` | 0.7rem |
| sm | `--font-size-sm` | 0.8rem |
| base | `--font-size-base` | 0.9rem |
| lg | `--font-size-lg` | 1rem |
| xl | `--font-size-xl` | 1.125rem |
| 2xl | `--font-size-2xl` | 1.4rem |
| 3xl | `--font-size-3xl` | 1.85rem |

- cms_user_react: `@theme` 지시어로 Tailwind 유틸리티(`text-sm`, `text-base` 등)를 오버라이드

### 6.3 레이아웃 너비: 1200px 통일

- cms_rental: `--max-width: 1200px` (site.css)
- cms_user_react: `max-w-[1200px]` (MainLayout, Header)

### 6.4 색상 테마 시스템

- 모든 색상은 CSS 변수(`--color-*`)로 관리
- `data-theme` 속성 (`dark`, `light`, `sky`, `classic`)으로 테마 전환
- 하드코딩 Tailwind 색상 → CSS 변수 교체 완료
- auth 페이지(login/register): site.css 테마 시스템에 통합 완료

### 6.5 게시글 본문 스타일

- cms_rental: site.css `.site-post-content` 스타일
- cms_user_react: `.site-prose` 클래스 (CSS 변수 기반, Tailwind `prose-invert` 제거)

---

## 7. 문서 체인

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
