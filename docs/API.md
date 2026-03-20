# CMS Core API 문서

> 전체 API 구조와 대표 호출 방법을 정리한 통합 문서입니다.  
> Postman 컬렉션은 `docs/postman`을 사용합니다.

---

## 1. 기본 정보

| 항목 | 값 |
|------|-----|
| Base URL | `http://localhost:8080` |
| Prefix | `/api/v1` |
| Content-Type | `application/json` |
| 인증 | JWT Bearer + 일부 세션 기반 |

---

## 2. 공통 응답 형식

### 2.1 성공

```json
{
  "success": true,
  "data": {},
  "error": null
}
```

### 2.2 오류

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

### 2.3 페이징

```json
{
  "success": true,
  "data": {
    "content": [],
    "page": 1,
    "size": 10,
    "totalElements": 100,
    "totalPages": 10
  },
  "error": null
}
```

### 2.4 코드 중복 체크 규칙

- 엔드포인트: `GET /{resource}/check/code`
- 응답: `ApiResponse<Boolean>`
  - `true`: 중복
  - `false`: 사용 가능

---

## 3. 인증 정책

- Access Token: 1시간
- Refresh Token: 14일
- Authorization 헤더

```text
Authorization: Bearer {accessToken}
```

---

## 4. API 영역

### 4.1 공개 API

- `GET /public/menus`
- `GET /public/pages/{pageCode}`
- `GET /public/popups`
- `GET /public/common-codes/{groupCode}`
- `GET /public/board-groups`
- `GET /public/boards`
- `GET /public/boards/code/{boardCode}`
- `GET /public/boards/{boardId}`
- `GET /public/boards/{boardId}/posts`
- `GET /public/boards/{boardId}/posts/{postId}`
- `GET /public/boards/{boardId}/posts/{postId}/comments`
- `POST|PUT|DELETE /public/boards/{boardId}/posts/**`
- `GET /public/posts/latest`

### 4.2 인증 API

- `POST /auth/login`
- `POST /auth/refresh`
- `POST /auth/logout`
- `GET /auth/me`
- `POST /auth/register`

### 4.3 관리자/도메인 API (JWT)

- 사용자: `/users/**`
- 역할/권한: `/roles/**`, `/permissions/**`
- 게시판: `/board-groups/**`, `/boards/**`
- 파일: `/files/**`
- 공통코드: `/common-code-groups/**`, `/common-codes/**`
- 팝업: `/popups/**`
- 감사로그: `/audit-logs/**`
- 설치: `/install/**`

### 4.4 대관 API

- 공개: `/public/rentals/**`
- 인증 기반 조회/예약: `/rental/**`

---

## 5. 사용자 사이트(`/site/**`) 연계 API 가이드

### 5.1 공개 조회

| 기능 | Endpoint |
|------|----------|
| 사이트 로그인 사용자 | `GET /public/site/me` |
| 사이트 설정 | `GET /public/site/config` |
| 메뉴 | `GET /public/menus` |
| 페이지 | `GET /public/pages/{pageCode}` |
| 팝업 | `GET /public/popups` |
| 최신글 | `GET /public/posts/latest` |

### 5.2 게시판

| 기능 | Endpoint |
|------|----------|
| 게시판 상세(코드) | `GET /public/boards/code/{boardCode}` |
| 게시판 상세(ID) | `GET /public/boards/{boardId}` |
| 게시글 목록 | `GET /public/boards/{boardId}/posts` |
| 게시글 상세 | `GET /public/boards/{boardId}/posts/{postId}` |
| 게시글 작성 | `POST /public/boards/{boardId}/posts` |
| 게시글 수정 | `PUT /public/boards/{boardId}/posts/{postId}` |
| 게시글 삭제 | `DELETE /public/boards/{boardId}/posts/{postId}` |

### 5.3 대관 예약(사이트)

| 기능 | Endpoint |
|------|----------|
| 예약 생성 | `POST /public/rentals/rooms/{roomId}/reservations` |
| 내 예약 목록 | `GET /public/rentals/reservations/my` |
| 내 예약 상세 | `GET /public/rentals/reservations/{id}` |
| 내 예약 취소 | `DELETE /public/rentals/reservations/{id}` |

---

## 6. fetch 예시

```javascript
// 메뉴 조회
const menuRes = await fetch('/api/v1/public/menus', { credentials: 'same-origin' });
const menuJson = await menuRes.json();

// 게시글 목록 조회
const postRes = await fetch('/api/v1/public/boards/1/posts?page=1&size=10', { credentials: 'same-origin' });
const postJson = await postRes.json();
```

---

## 7. Postman 사용

- 컬렉션: `docs/postman/CMS_Core_API.postman_collection.json`
- 환경: `docs/postman/CMS_Core_Local.postman_environment.json`
- 상세 사용법: `docs/MANUAL.md`
