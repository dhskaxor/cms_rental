# 사용자 사이트 API 사용 문서

> 사용자 사이트(/site/**)에서 호출하는 API 및 사용 방법을 정리합니다.
> 상세 API 명세는 [api.md](api.md), cms/site ↔ cms_user_react 동기화 체크리스트는 [REFACTORING_CHECKLIST.md](REFACTORING_CHECKLIST.md)를 참조하세요.

---

## 1. 개요

- **cms site**: Thymeleaf SSR (Page/Submit). SiteViewController/SiteFormController. 메뉴·팝업·최신글·게시판 링크는 서버 렌더링.
- **cms_user_react**: 클라이언트 React에서 fetch로 API를 호출하여 데이터를 조회·표시한다.

| 구분 | Base URL | 인증 |
|------|----------|------|
| 공개 API | /api/v1/public/** | 불필요 (단, `/api/v1/public/rentals/**` 제외) |
| 인증 API | /api/v1/auth/** | JWT 또는 세션 |
| 공개 대관 예약 API | /api/v1/public/rentals/** | 필요 (인증 사용자) |

---

## 2. 공개 API (인증 불필요)

### 2.1 사이트 로그인 사용자 조회

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | /api/v1/public/site/me | 현재 사이트 세션 사용자 (미로그인 시 null) |

**Response:** `{ success: true, data: { id, username, name, email, roleCode } }` 또는 `data: null`

### 2.2 사이트 설정

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | /api/v1/public/site/config | 사이트명, favicon, SEO, 회사정보, 기본 테마 조회 |

**Response:** `{ id, siteName, faviconUrl, seoTitle, seoDescription, seoKeywords, companyAddress, companyPhone, adminEmail, siteTheme }`

**Fetch 예시 (cms_user_react):**
```ts
const res = await publicSiteApi.getSiteConfig()
const siteName = res.data?.siteName ?? 'CMS Core'
```

### 2.3 메뉴

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | /api/v1/public/menus | 노출 가능 메뉴 계층 조회 |

**Query:** includeLoginRequired (boolean, default: false) - true면 로그인 필수 메뉴 포함

### 2.4 페이지

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | /api/v1/public/pages/{pageCode} | 페이지 코드로 게시된 페이지 조회 |

**Response (public):** `{ id, pageCode, pageTitle, content, isPublished, templateCode }` — `templateCode`는 사용자 사이트 렌더링 시 적용할 템플릿 코드 (default, page_01 등)

**관리자 API (PAGE_READ 권한):**

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | /api/v1/pages/templates | 사용 가능한 페이지 템플릿 코드 목록 (templates/site/page/ 하위 폴더명) |

### 2.5 팝업

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | /api/v1/public/popups | 노출 대상 팝업 조회 |

### 2.5 공통코드

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | /api/v1/public/common-codes/{groupCode} | 그룹 코드로 활성 코드 조회 |

### 2.7 게시판

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | /api/v1/public/board-groups | 게시판 그룹 목록 |
| GET | /api/v1/public/boards | 게시판 목록 |
| GET | /api/v1/public/boards/code/{boardCode} | 게시판 상세 (코드로 조회) |
| GET | /api/v1/public/boards/{boardId} | 게시판 상세 (ID로 조회) |
| GET | /api/v1/public/boards/{boardId}/posts | 게시글 목록 (페이징, page, size, searchType, keyword 등) |
| GET | /api/v1/public/boards/{boardId}/posts/{postId} | 게시글 상세 |
| GET | /api/v1/public/boards/{boardId}/posts/{postId}/comments | 댓글 목록 |
| POST | /api/v1/public/boards/{boardId}/posts | 게시글 작성 (세션, 비로그인 시 ANONYMOUS, attachedFileIds 포함) |
| PUT | /api/v1/public/boards/{boardId}/posts/{postId} | 게시글 수정 |
| DELETE | /api/v1/public/boards/{boardId}/posts/{postId} | 게시글 삭제 |
| POST | /api/v1/public/boards/{boardId}/posts/{postId}/files | 게시글 첨부파일 업로드 (multipart, board.useFile 시) |
| POST | /api/v1/public/boards/{boardId}/files/upload/editor | 에디터 본문 이미지 업로드 (board.useEditor 시, multipart) |
| DELETE | /api/v1/public/boards/{boardId}/posts/{postId}/files/{fileId} | 게시글 첨부파일 삭제 (canUpdate 권한 필요) |

### 2.7 최신글

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | /api/v1/public/posts/latest | 여러 게시판의 최신글 조회 |

### 2.8 대관 공개 API

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | /api/v1/rental/search | 대관 캘린더 가용 슬롯 조회 (공개) |

**Query 파라미터:**

| 파라미터 | 타입 | 필수 | 기본값 | 설명 |
|----------|------|------|--------|------|
| boardIds | string | O | - | 게시판 ID 목록 (콤마 구분, 예: 1,2,3) |
| size | int | X | 10 | 조회할 게시글 수 (최대 50) |
| sortField | string | X | createdAt | 정렬 필드 (createdAt, viewCount, title) |
| sortOrder | string | X | DESC | 정렬 방식 (ASC, DESC) |

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": 123,
      "boardId": 1,
      "boardCode": "notice",
      "boardName": "공지사항",
      "title": "게시글 제목",
      "writerName": "홍길동",
      "viewCount": 100,
      "commentCount": 5,
      "fileCount": 2,
      "createdAt": "2026-02-27T10:30:00"
    }
  ]
}
```

**사용 예시 (JavaScript):**
```javascript
// 게시판 1, 2, 3의 최신글 10개 조회 (최신순)
const res = await fetch('/api/v1/public/posts/latest?boardIds=1,2,3&size=10', {
  credentials: 'same-origin'
});
const json = await res.json();
const posts = json.data;

// 조회수 높은 순으로 정렬
const popularRes = await fetch('/api/v1/public/posts/latest?boardIds=1,2&size=5&sortField=viewCount&sortOrder=DESC');
```

**CMS Site (서버 렌더링):**
- SiteViewController.index()에서 getLatestPosts, collectBoardLinks로 최신글·게시판 링크를 서버에서 렌더링
- /site/ 접속 시 바로 HTML에 반영됨 (JS 불필요)

---

## 3. 사용자 사이트 전용 엔드포인트

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | /site/ | 사용자 사이트 메인 (대문) |
| GET | /site/page/{pageCode} | 페이지 뷰 (SPA, index.html 후 클라이언트에서 pages API로 렌더링) |
| GET | /site/board/{boardCode} | 게시판 목록 (boardCode 또는 boardId, SPA, 권한에 따라 글쓰기 버튼 표시) |
| GET | /site/board/{boardCode}/write | 글쓰기 폼 |
| GET | /site/board/{boardCode}/post/{postId} | 게시글 상세 (SPA, 권한에 따라 수정/삭제 버튼 표시) |
| GET | /site/board/{boardCode}/post/{postId}/edit | 글 수정 폼 |
| GET | /site/auth/login | 로그인 폼 (?redirect= 리다이렉트 URL) |
| POST | /site/auth/login | 로그인 처리 (세션 생성, redirect 파라미터 지원) |
| POST | /site/auth/logout | 로그아웃 처리 |
| - | (에러 시) site/error | 404, 500 등 /site/** 전용 에러 페이지 (status, message 표시) |

### 3.1 인증 사용자 대관 예약 엔드포인트

| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | /api/v1/public/rentals/rooms/{roomId}/reservations | 예약 생성 |
| GET | /api/v1/public/rentals/reservations/my | 내 예약 목록 조회 |
| GET | /api/v1/public/rentals/reservations/{id} | 내 예약 상세 조회 |
| DELETE | /api/v1/public/rentals/reservations/{id} | 내 예약 취소 |

---

## 4. fetch 호출 예시

```javascript
// 사이트 로그인 사용자 조회 (credentials로 세션 쿠키 전송)
const meRes = await fetch('/api/v1/public/site/me', { credentials: 'same-origin' });
const meJson = await meRes.json();
const user = meJson.data;

// 메뉴 조회
const res = await fetch('/api/v1/public/menus?includeLoginRequired=true', { credentials: 'same-origin' });
const json = await res.json();
const menus = json.data;

// 페이지 조회 (Phase 3)
const pageRes = await fetch('/api/v1/public/pages/about', { credentials: 'same-origin' });
const pageJson = await pageRes.json();
const page = pageJson.data; // { pageCode, pageTitle, content, ... }

// 게시글 목록 (페이징)
const listRes = await fetch('/api/v1/public/boards/1/posts?page=1&size=10', { credentials: 'same-origin' });
const listJson = await listRes.json();
const listData = listJson.data; // { content, page, size, totalElements, totalPages }

// 게시글 상세
const postRes = await fetch('/api/v1/public/boards/1/posts/1', { credentials: 'same-origin' });

// 팝업 조회 (Phase 4)
const deviceType = window.innerWidth < 768 ? 'MOBILE' : 'PC';
const popupRes = await fetch('/api/v1/public/popups?positionType=MAIN&deviceType=' + deviceType + '&isLogin=' + (user ? 'true' : 'false'), { credentials: 'same-origin' });
```

---

## 5. 사용자 사이트 스타일 커스터마이징 (Phase 6)

사용자 사이트(`/site/**`)는 `site.css`의 다중 테마 토큰을 사용한다. 기본 테마는 관리자 화면(사이트 관리)에서 선택하며, `html[data-theme="<theme>"]` 기준으로 전체 색상 토큰이 전환된다.

### 5.1 CSS 디자인 시스템

CMS Core 사용자 사이트는 React (`cms_user_react`)와 동일한 CSS 변수 기반 디자인 시스템을 사용합니다.

#### 색상 토큰 (Color Tokens)

| 변수명 | 기본값 | 설명 |
|--------|--------|------|
| `--color-primary` | theme별 상이 | 주요 브랜드 색상 |
| `--color-primary-dark` | theme별 상이 | 호버/활성 상태 |
| `--color-primary-light` | theme별 상이 | 배경 하이라이트 |
| `--color-secondary` | theme별 상이 | 보조 강조 색상 |
| `--color-bg` | theme별 상이 | 기본 배경 |
| `--color-bg-secondary` | theme별 상이 | 보조 배경 |
| `--color-bg-card` | theme별 상이 | 카드 배경 |
| `--color-text` | theme별 상이 | 기본 텍스트 |
| `--color-text-muted` | theme별 상이 | 보조 텍스트 |
| `--color-border` | theme별 상이 | 테두리 |

#### 타이포그래피 (Typography)

| 변수명 | 기본값 | 설명 |
|--------|--------|------|
| `--font-sans` | Inter, system | 기본 폰트 |
| `--font-mono` | JetBrains Mono | 코드 폰트 |
| `--font-size-base` | 1rem | 기본 크기 |
| `--font-size-xl` | 1.25rem | 제목 크기 |

#### 간격 및 레이아웃

| 변수명 | 값 |
|--------|-----|
| `--spacing-1` ~ `--spacing-16` | 0.25rem ~ 4rem |
| `--max-width` | 1200px |
| `--radius-sm/md/lg/xl/2xl` | 0.25rem ~ 1.5rem |

### 5.2 내장 테마셋

- `dark`: 어두운 기본 테마 (폴백)
- `light`: 화이트 기반 고대비 테마
- `sky`: 밝은 하늘색 기반 테마
- `classic`: 기업형 뉴트럴 테마

### 5.3 테마 오버라이드 예시

프로젝트별 `site-custom.css` 등을 추가하고 `site.css` 이후 로드하여 덮어쓸 수 있다.

```html
<link rel="stylesheet" href="/site/css/site.css">
<link rel="stylesheet" href="/site/css/site-custom.css">
```

```css
/* site-custom.css - 라이트 계열 커스텀 예시 */
html[data-theme="light"] {
    --color-primary: #2563eb;
    --color-bg: #ffffff;
    --color-bg-secondary: #f8fafc;
    --color-text: #1e293b;
    --color-border: #e2e8f0;
}
```

---

## 6. 비회원(익명) 게시판 권한

게시판별로 **비회원**이 글 읽기·쓰기를 할 수 있도록 설정할 수 있다.

### 6.1 관리자 설정

1. 관리자 → 게시판 관리 → 게시판 등록/수정
2. **역할별 권한**에서 **비회원** 행에 `읽기`, `쓰기` 체크
3. 저장

### 6.2 동작

- 비로그인 사용자가 사용자 사이트에서 해당 게시판에 접근 시 `ANONYMOUS` 역할로 권한 조회
- `can_read` = true 이면 목록·상세 조회 가능
- `can_read` = false 인 경우 사용자 사이트에서 해당 게시판/게시글 접근 시 "권한이 없습니다" 메시지를 표시하고 `/site/` 로 이동
- `can_create` = true 이면 글 작성 가능 (작성자로 시스템 "비회원" 계정 표시)

### 6.3 기존 설치 환경

이전 버전에서 설치된 환경은 앱 기동 시 `AnonymousUserInitializer`가 ANONYMOUS 역할 및 anonymous 시스템 계정을 자동 생성한다.

### 6.4 사용자 사이트 글쓰기/수정/삭제 UI

- **권한 기반 버튼 표시**: Thymeleaf 템플릿에서 `canCreate`/`canUpdate`/`canDelete` 모델 속성으로 버튼 표시 여부를 결정한다. (SiteViewController가 board.permissions에서 roleCode별 권한 조회)
- **글쓰기 버튼**: 목록 화면 `{{writeButtonHtml}}` 플레이스홀더
- **수정/삭제 버튼**: 상세 화면 `{{actionButtonsHtml}}` 플레이스홀더
- **템플릿 플레이스홀더**: `board-list.html`, `board-post.html`, `post-form.html` 등에 포함

### 6.5 게시판 템플릿 (폴더 기반)

**파일 위치**: `templates/site/board/{templateCode}/`
- `board-list.html` (목록), `board-post.html` (상세), `post-form.html` (폼)
- **자동 등록**: 폴더 추가 시 `GET /api/v1/boards/templates`가 폴더명 목록 반환 → 관리자 게시판 폼 셀렉트에 자동 반영

| 템플릿 코드 | 경로 |
|-------------|------|
| default | `templates/site/board/default/` |
| card | `templates/site/board/card/` |
| news | `templates/site/board/news/` |

---

*마지막 업데이트: 2026-03-10*
