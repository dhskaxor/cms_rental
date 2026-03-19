# CMS Core API 명세서

## 목차

1. [개요](#개요)
2. [인증](#인증)
3. [공통 응답 형식](#공통-응답-형식)
4. [에러 코드](#에러-코드)
5. [API 목록](#api-목록)
   - [공개 API (Public)](#공개-api-public)
   - [인증 API](#인증-api)
   - [사용자 API](#사용자-api)
   - [역할 API](#역할-api)
   - [권한 API](#권한-api)
   - [게시판 그룹 API](#게시판-그룹-api)
   - [게시판 API](#게시판-api)
   - [게시글 API](#게시글-api)
   - [댓글 API](#댓글-api)
   - [파일 API](#파일-api)
   - [감사 로그 API](#감사-로그-api)
   - [공통 코드 API](#공통-코드-api)
   - [팝업 API](#팝업-api)
   - [설치 API](#설치-api)

---

## 개요

| 항목 | 값 |
|------|-----|
| Base URL | `http://localhost:8080` |
| API Version | `v1` |
| Content-Type | `application/json` |
| 인증 방식 | JWT Bearer Token |

---

## 인증

### JWT 토큰 사용

인증이 필요한 API는 HTTP 헤더에 JWT Access Token을 포함해야 합니다.

```
Authorization: Bearer {access_token}
```

### 토큰 만료 시간

| 토큰 | 만료 시간 |
|------|----------|
| Access Token | 1시간 |
| Refresh Token | 14일 |

---

## 공통 응답 형식

### 성공 응답

```json
{
  "success": true,
  "data": { ... },
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
    "message": "에러 메시지",
    "details": null
  }
}
```

### 페이징 응답

```json
{
  "success": true,
  "data": {
    "content": [...],
    "page": 1,
    "size": 10,
    "totalElements": 100,
    "totalPages": 10
  },
  "error": null
}
```

### 코드 중복 체크 API (check/code)

코드 중복 체크 엔드포인트는 `ApiResponse<Boolean>` 형식을 사용합니다.

- **URL 패턴**: `GET /api/v1/{리소스}/check/code`
- **응답**: `data`가 `true`면 중복, `false`면 사용 가능
- **예시**: `GET /api/v1/menus/check/code?menuCode=about`, `GET /api/v1/pages/check/code?pageCode=about&excludeId=1`

```json
{
  "success": true,
  "data": true,
  "error": null
}
```

---

## 에러 코드

### 공통 에러 (1000번대)

| 코드 | 메시지 | HTTP Status |
|------|--------|-------------|
| COMMON_1000 | 서버 내부 오류가 발생했습니다. | 500 |
| COMMON_1001 | 잘못된 요청입니다. | 400 |
| COMMON_1002 | 유효하지 않은 입력값입니다. | 400 |
| COMMON_1003 | 요청한 리소스를 찾을 수 없습니다. | 404 |
| COMMON_1004 | 지원하지 않는 HTTP 메서드입니다. | 405 |

### 인증 에러 (2000번대)

| 코드 | 메시지 | HTTP Status |
|------|--------|-------------|
| AUTH_2000 | 인증에 실패했습니다. | 401 |
| AUTH_2001 | 유효하지 않은 토큰입니다. | 401 |
| AUTH_2002 | 토큰이 만료되었습니다. | 401 |
| AUTH_2003 | 접근 권한이 없습니다. | 403 |
| AUTH_2004 | 아이디 또는 비밀번호가 올바르지 않습니다. | 401 |
| AUTH_2005 | 계정이 잠겼습니다. | 403 |
| AUTH_2006 | 리프레시 토큰이 유효하지 않습니다. | 401 |

### 사용자 에러 (3000번대)

| 코드 | 메시지 | HTTP Status |
|------|--------|-------------|
| USER_3000 | 사용자를 찾을 수 없습니다. | 404 |
| USER_3001 | 이미 존재하는 사용자명입니다. | 409 |
| USER_3002 | 이미 존재하는 이메일입니다. | 409 |
| USER_3007 | 비밀번호가 일치하지 않습니다. | 400 |
| USER_3008 | 비밀번호 형식이 올바르지 않습니다. | 400 |
| USER_3009 | 현재 비밀번호가 올바르지 않습니다. | 400 |
| USER_3010 | 자기 자신은 삭제할 수 없습니다. | 400 |

### 역할 에러 (3100번대)

| 코드 | 메시지 | HTTP Status |
|------|--------|-------------|
| ROLE_3100 | 역할을 찾을 수 없습니다. | 404 |
| ROLE_3101 | 이미 존재하는 역할입니다. | 409 |
| ROLE_3102 | 사용 중인 역할은 삭제할 수 없습니다. | 409 |

### 권한 에러 (3200번대)

| 코드 | 메시지 | HTTP Status |
|------|--------|-------------|
| PERM_3200 | 권한을 찾을 수 없습니다. | 404 |
| PERM_3201 | 이미 존재하는 권한입니다. | 409 |
| PERM_3202 | 사용 중인 권한은 삭제할 수 없습니다. | 409 |

### 설치 에러 (9000번대)

| 코드 | 메시지 | HTTP Status |
|------|--------|-------------|
| INSTALL_9000 | 이미 설치가 완료되었습니다. | 400 |
| INSTALL_9001 | 데이터베이스 연결에 실패했습니다. | 400 |
| INSTALL_9002 | 테이블 생성에 실패했습니다. | 500 |
| INSTALL_9003 | 초기 데이터 생성에 실패했습니다. | 500 |

---

## API 목록

---

## 공개 API (Public)

사용자 사이트(쇼핑몰, 홈페이지 등)에서 인증 없이 호출하는 API입니다. `/api/v1/public/**` 경로로 통합 제공됩니다.

### Public Site API (사이트 기초)

#### 1. 사이트 설정 조회

- **URL**: `GET /api/v1/public/site/config`
- **인증**: 불필요
- **설명**: 관리자 사이트 관리에서 설정한 사이트명, favicon, SEO, 회사정보, 기본 테마를 조회
- **Response**: `id`, `siteName`, `faviconUrl`, `seoTitle`, `seoDescription`, `seoKeywords`, `companyAddress`, `companyPhone`, `adminEmail`, `siteTheme`
- **비고**: faviconUrl은 설정된 favicon이 있을 때 `/api/v1/public/site/favicon` 경로
  - `siteTheme` 지원값: `dark`, `light`, `sky`, `classic` (미설정/잘못된 값은 `dark` 폴백)

#### 2. Favicon

- **URL**: `GET /api/v1/public/site/favicon`
- **인증**: 불필요
- **설명**: 설정된 favicon 파일 바이너리 반환. 없으면 204 No Content
- **비고**: `/favicon.ico` 요청 시 이 경로로 302 리다이렉트

#### 3. 메뉴 목록 조회

- **URL**: `GET /api/v1/public/menus`
- **인증**: 불필요

| Query | 타입 | 기본값 | 설명 |
|-------|------|--------|------|
| includeLoginRequired | boolean | false | true면 로그인 필수 메뉴 포함 |

#### 4. 페이지 조회

- **URL**: `GET /api/v1/public/pages/{pageCode}`
- **인증**: 불필요
- **비고**: is_published=true 페이지만 반환 (미게시 시 404)
- **Response**: `id`, `pageCode`, `pageTitle`, `content`, `isPublished`, `templateCode` (페이지 템플릿 코드)

**관리자 API (PAGE_READ 권한):**
- **URL**: `GET /api/v1/pages/templates`
- **설명**: 사용 가능한 페이지 템플릿 코드 목록 (templates/site/page/ 하위 폴더명)
- **Response**: `["default", "page_01", "page_02", ...]`

#### 5. 팝업 목록 조회

- **URL**: `GET /api/v1/public/popups`
- **인증**: 불필요

| Query | 타입 | 기본값 | 설명 |
|-------|------|--------|------|
| positionType | String | MAIN | MAIN, SUB, ALL |
| deviceType | String | PC | PC, MOBILE, ALL |
| isLogin | boolean | false | 로그인 여부 |

#### 6. 공통코드 조회

- **URL**: `GET /api/v1/public/common-codes/{groupCode}`
- **인증**: 불필요

#### 7. 대관 캘린더 공개 조회

- **URL**: `GET /api/v1/rental/search`
- **인증**: 불필요
- **설명**: roomId, yearMonth 기준으로 예약 가능 슬롯/일자 조회

#### 8. 공개 경로 중 인증 필요 대관 예약 API

- **URL**: `POST /api/v1/public/rentals/rooms/{roomId}/reservations`
- **인증**: 필요 (로그인 사용자)
- **설명**: 대관 예약 생성

- **URL**: `GET /api/v1/public/rentals/reservations/my`
- **인증**: 필요 (로그인 사용자)
- **설명**: 로그인 사용자의 내 예약 목록 조회

- **URL**: `GET /api/v1/public/rentals/reservations/{id}`
- **인증**: 필요 (로그인 사용자)
- **설명**: 로그인 사용자의 내 예약 상세 조회

- **URL**: `DELETE /api/v1/public/rentals/reservations/{id}`
- **인증**: 필요 (로그인 사용자)
- **설명**: 로그인 사용자의 내 예약 취소

---

### Public Board API (게시판/게시글)

#### 1. 게시판 그룹 목록

- **URL**: `GET /api/v1/public/board-groups`
- **인증**: 불필요

#### 2. 게시판 목록

- **URL**: `GET /api/v1/public/boards`
- **인증**: 불필요

#### 3. 그룹별 게시판 목록

- **URL**: `GET /api/v1/public/boards/group/{groupId}`
- **인증**: 불필요

#### 4. 게시판 상세 (코드)

- **URL**: `GET /api/v1/public/boards/code/{boardCode}`
- **인증**: 불필요
- **Response**: `useEditor` (Boolean) 포함 - true면 리치 에디터(WYSIWYG) 사용, false면 textarea

#### 5. 게시글 목록

- **URL**: `GET /api/v1/public/boards/{boardId}/posts`
- **인증**: 불필요

| Query | 타입 | 기본값 | 설명 |
|-------|------|--------|------|
| page | int | 1 | 페이지 번호 |
| size | int | 10 | 페이지 크기 |
| searchType | String | - | title, content, writer, all |
| keyword | String | - | 검색어 |
| startDate | String | - | yyyy-MM-dd |
| endDate | String | - | yyyy-MM-dd |

#### 6. 공지글 목록

- **URL**: `GET /api/v1/public/boards/{boardId}/posts/notices`
- **인증**: 불필요

#### 7. 게시글 상세

- **URL**: `GET /api/v1/public/boards/{boardId}/posts/{postId}`
- **인증**: 불필요
- **비고**: 비밀글은 익명 접근 시 403 (POST_4102)

#### 8. 이전/다음 게시글

- **URL**: `GET /api/v1/public/boards/{boardId}/posts/{postId}/prev`
- **URL**: `GET /api/v1/public/boards/{boardId}/posts/{postId}/next`
- **인증**: 불필요

#### 9. 댓글 목록

- **URL**: `GET /api/v1/public/boards/{boardId}/posts/{postId}/comments`
- **인증**: 불필요

#### 10. 게시글 작성 (공개)

- **URL**: `POST /api/v1/public/boards/{boardId}/posts`
- **인증**: 선택 (세션, 비로그인 시 ANONYMOUS 역할로 처리)
- **Content-Type**: `application/json`
- **Request Body**: `{ title, content?, isNotice?, isSecret?, attachedFileIds? }` (PostCreateRequest)
- **비고**: 게시판 역할별 권한에서 canCreate=true인 역할(비회원 포함)만 호출 가능. board.useEditor=true인 경우 content는 HTML, attachedFileIds는 에디터 본문에 삽입된 이미지 파일 ID 목록.

#### 11. 게시글 수정 (공개)

- **URL**: `PUT /api/v1/public/boards/{boardId}/posts/{postId}`
- **인증**: 선택 (세션, 비로그인 시 ANONYMOUS)
- **Request Body**: `{ title, content?, isNotice?, isSecret?, attachedFileIds? }` (PostUpdateRequest)

#### 12. 게시글 삭제 (공개)

- **URL**: `DELETE /api/v1/public/boards/{boardId}/posts/{postId}`
- **인증**: 선택 (세션, 비로그인 시 ANONYMOUS)

#### 13. 게시글 첨부파일 업로드 (공개)

- **URL**: `POST /api/v1/public/boards/{boardId}/posts/{postId}/files`
- **Content-Type**: `multipart/form-data`
- **인증**: 불필요
- **Request**: `files` (multipart), `maxCount` (선택, 기본값 board.maxFileCount)
- **비고**: board.useFile=true인 게시판에서만 가능. 게시글 생성·수정 후 호출.

#### 14. 에디터 본문 이미지 업로드 (공개)

- **URL**: `POST /api/v1/public/boards/{boardId}/files/upload/editor`
- **Content-Type**: `multipart/form-data`
- **인증**: 선택 (세션, 비로그인 시 ANONYMOUS 가능)
- **Query**: `postId` (선택) - 수정 시 게시글 ID, 신규 작성 시 미전달 또는 0
- **Request**: `file` (multipart) - 이미지 파일 1개
- **비고**: board.useEditor=true인 게시판에서 Toast UI Editor 등 리치 에디터 본문에 이미지 삽입 시 사용. 업로드된 파일 ID를 게시글 작성·수정 시 attachedFileIds에 포함하여 전송.

#### 15. 최신글 목록 조회 (공개)

- **URL**: `GET /api/v1/public/posts/latest`
- **인증**: 불필요

**Query Parameters:**

| 파라미터 | 타입 | 필수 | 기본값 | 설명 |
|----------|------|------|--------|------|
| boardIds | string | O | - | 게시판 ID 목록 (콤마 구분) |
| size | int | X | 10 | 조회할 개수 (최대 50) |
| sortField | string | X | createdAt | 정렬 필드 (createdAt, viewCount, title) |
| sortOrder | string | X | DESC | 정렬 방식 (ASC, DESC) |

**Response:**

```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "boardId": 1,
      "boardCode": "notice",
      "boardName": "공지사항",
      "title": "게시글 제목",
      "writerName": "홍길동",
      "viewCount": 100,
      "isNotice": false,
      "isSecret": false,
      "commentCount": 5,
      "fileCount": 2,
      "createdAt": "2026-02-27T10:30:00"
    }
  ]
}
```

**사용 예시:**

```bash
# 게시판 1, 2, 3의 최신글 10개 조회
GET /api/v1/public/posts/latest?boardIds=1,2,3&size=10

# 조회수 높은 순으로 정렬
GET /api/v1/public/posts/latest?boardIds=1,2&size=5&sortField=viewCount&sortOrder=DESC
```

---

### 공개 API 에러 코드

| 코드 | 메시지 | HTTP Status |
|------|--------|-------------|
| POST_4102 | 비밀글은 작성자만 볼 수 있습니다. | 403 |
| PAGE_4400 | 페이지를 찾을 수 없습니다. | 404 |
| POST_4100 | 게시글을 찾을 수 없습니다. | 404 |
| BOARD_4000 | 게시판을 찾을 수 없습니다. | 404 |

---

## 인증 API

### 1. 로그인

사용자 로그인 후 JWT 토큰을 발급합니다.

- **URL**: `POST /api/v1/auth/login`
- **인증**: 불필요
- **Content-Type**: `application/json`

#### Request Body

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| username | String | O | 사용자 아이디 |
| password | String | O | 비밀번호 |

#### Request Example

```json
{
  "username": "admin",
  "password": "Admin123!"
}
```

#### Response Example (성공)

```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 3600,
    "tokenType": "Bearer"
  },
  "error": null
}
```

#### Response Example (실패 - 계정 잠김)

```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "AUTH_2005",
    "message": "계정이 잠겼습니다.",
    "details": null
  }
}
```

---

### 2. 토큰 갱신

Refresh Token을 사용하여 새로운 Access Token을 발급합니다.

- **URL**: `POST /api/v1/auth/refresh`
- **인증**: 불필요
- **Content-Type**: `application/json`

#### Request Body

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| refreshToken | String | O | 리프레시 토큰 |

#### Request Example

```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### Response Example (성공)

```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 3600,
    "tokenType": "Bearer"
  },
  "error": null
}
```

---

### 3. 로그아웃

사용자 로그아웃 처리 (Refresh Token 무효화)

- **URL**: `POST /api/v1/auth/logout`
- **인증**: 필요
- **Content-Type**: `application/json`

#### Request Headers

```
Authorization: Bearer {access_token}
```

#### Response Example (성공)

```json
{
  "success": true,
  "data": null,
  "error": null
}
```

---

## 사용자 API

### 1. 사용자 목록 조회

페이징된 사용자 목록을 조회합니다.

- **URL**: `GET /api/v1/users`
- **인증**: 필요
- **권한**: `USER_READ`

#### Query Parameters

| 필드 | 타입 | 필수 | 기본값 | 설명 |
|------|------|------|--------|------|
| page | Integer | X | 1 | 페이지 번호 |
| size | Integer | X | 10 | 페이지 크기 |
| username | String | X | - | 사용자명 검색 |
| email | String | X | - | 이메일 검색 |
| status | String | X | - | 상태 필터 (ACTIVE/LOCKED) |
| roleId | Long | X | - | 역할 ID 필터 |

#### Request Example

```
GET /api/v1/users?page=1&size=10&status=ACTIVE
Authorization: Bearer {access_token}
```

#### Response Example (성공)

```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "username": "admin",
        "email": "admin@example.com",
        "name": "관리자",
        "status": "ACTIVE",
        "roleId": 1,
        "roleName": "관리자",
        "createdAt": "2026-02-22T10:00:00"
      }
    ],
    "page": 1,
    "size": 10,
    "totalElements": 1,
    "totalPages": 1
  },
  "error": null
}
```

---

### 2. 사용자 상세 조회

특정 사용자의 상세 정보를 조회합니다.

- **URL**: `GET /api/v1/users/{id}`
- **인증**: 필요
- **권한**: `USER_READ`

#### Path Parameters

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| id | Long | O | 사용자 ID |

#### Response Example (성공)

```json
{
  "success": true,
  "data": {
    "id": 1,
    "username": "admin",
    "email": "admin@example.com",
    "name": "관리자",
    "status": "ACTIVE",
    "roleId": 1,
    "roleName": "관리자",
    "createdAt": "2026-02-22T10:00:00",
    "updatedAt": "2026-02-22T10:00:00"
  },
  "error": null
}
```

---

### 3. 사용자 생성 (관리자)

새로운 사용자를 생성합니다.

- **URL**: `POST /api/v1/users`
- **인증**: 필요
- **권한**: `USER_CREATE`
- **Content-Type**: `application/json`

#### Request Body

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| username | String | O | 사용자 아이디 (4-20자, 영문/숫자) |
| password | String | O | 비밀번호 (6-12자, 영문/숫자/특수문자) |
| email | String | O | 이메일 |
| name | String | O | 이름 |
| roleId | Long | O | 역할 ID |

#### Request Example

```json
{
  "username": "newuser",
  "password": "User123!",
  "email": "newuser@example.com",
  "name": "신규 사용자",
  "roleId": 3
}
```

#### Response Example (성공)

```json
{
  "success": true,
  "data": {
    "id": 2,
    "username": "newuser",
    "email": "newuser@example.com",
    "name": "신규 사용자",
    "status": "ACTIVE",
    "roleId": 3,
    "roleName": "사용자",
    "createdAt": "2026-02-22T11:00:00"
  },
  "error": null
}
```

---

### 4. 사용자 수정

사용자 정보를 수정합니다.

- **URL**: `PUT /api/v1/users/{id}`
- **인증**: 필요
- **권한**: `USER_UPDATE`
- **Content-Type**: `application/json`

#### Path Parameters

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| id | Long | O | 사용자 ID |

#### Request Body

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| email | String | O | 이메일 |
| name | String | O | 이름 |
| roleId | Long | O | 역할 ID |

#### Request Example

```json
{
  "email": "updated@example.com",
  "name": "수정된 이름",
  "roleId": 2
}
```

#### Response Example (성공)

```json
{
  "success": true,
  "data": {
    "id": 2,
    "username": "newuser",
    "email": "updated@example.com",
    "name": "수정된 이름",
    "status": "ACTIVE",
    "roleId": 2,
    "roleName": "매니저",
    "updatedAt": "2026-02-22T12:00:00"
  },
  "error": null
}
```

---

### 5. 사용자 삭제

사용자를 삭제합니다 (Soft Delete).

- **URL**: `DELETE /api/v1/users/{id}`
- **인증**: 필요
- **권한**: `USER_DELETE`

#### Path Parameters

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| id | Long | O | 사용자 ID |

#### Response Example (성공)

```json
{
  "success": true,
  "data": null,
  "error": null
}
```

---

### 6. 비밀번호 변경

사용자 본인의 비밀번호를 변경합니다.

- **URL**: `PUT /api/v1/users/{id}/password`
- **인증**: 필요
- **권한**: 본인만 가능
- **Content-Type**: `application/json`

#### Path Parameters

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| id | Long | O | 사용자 ID |

#### Request Body

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| currentPassword | String | O | 현재 비밀번호 |
| newPassword | String | O | 새 비밀번호 (6-12자, 영문/숫자/특수문자) |

#### Request Example

```json
{
  "currentPassword": "OldPass1!",
  "newPassword": "NewPass1!"
}
```

#### Response Example (성공)

```json
{
  "success": true,
  "data": null,
  "error": null
}
```

---

### 7. 비밀번호 초기화 (관리자)

관리자가 사용자의 비밀번호를 초기화합니다.

- **URL**: `PUT /api/v1/users/{id}/password/reset`
- **인증**: 필요
- **권한**: `USER_UPDATE`
- **Content-Type**: `application/json`

#### Path Parameters

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| id | Long | O | 사용자 ID |

#### Request Body

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| newPassword | String | O | 새 비밀번호 (6-12자, 영문/숫자/특수문자) |

#### Request Example

```json
{
  "newPassword": "Reset123!"
}
```

#### Response Example (성공)

```json
{
  "success": true,
  "data": null,
  "error": null
}
```

---

### 8. 계정 잠금

사용자 계정을 잠금 처리합니다.

- **URL**: `PUT /api/v1/users/{id}/lock`
- **인증**: 필요
- **권한**: `USER_UPDATE`

#### Path Parameters

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| id | Long | O | 사용자 ID |

#### Response Example (성공)

```json
{
  "success": true,
  "data": null,
  "error": null
}
```

---

### 9. 계정 잠금 해제

사용자 계정의 잠금을 해제합니다.

- **URL**: `PUT /api/v1/users/{id}/unlock`
- **인증**: 필요
- **권한**: `USER_UPDATE`

#### Path Parameters

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| id | Long | O | 사용자 ID |

#### Response Example (성공)

```json
{
  "success": true,
  "data": null,
  "error": null
}
```

---

### 10. 사용자명 중복 확인

사용자명 중복 여부를 확인합니다.

- **URL**: `GET /api/v1/users/check/username`
- **인증**: 불필요

#### Query Parameters

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| username | String | O | 확인할 사용자명 |

#### Response Example (성공)

```json
{
  "success": true,
  "data": false,
  "error": null
}
```

> `data: true` = 중복, `data: false` = 사용 가능

---

### 11. 이메일 중복 확인

이메일 중복 여부를 확인합니다.

- **URL**: `GET /api/v1/users/check/email`
- **인증**: 불필요

#### Query Parameters

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| email | String | O | 확인할 이메일 |

#### Response Example (성공)

```json
{
  "success": true,
  "data": false,
  "error": null
}
```

---

### 12. 회원가입 (공개)

신규 사용자가 회원가입합니다.

- **URL**: `POST /api/v1/register`
- **인증**: 불필요
- **Content-Type**: `application/json`

#### Request Body

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| username | String | O | 사용자 아이디 (4-20자) |
| password | String | O | 비밀번호 (6-12자) |
| email | String | O | 이메일 |
| name | String | O | 이름 |

#### Request Example

```json
{
  "username": "newmember",
  "password": "Member1!",
  "email": "member@example.com",
  "name": "신규회원"
}
```

#### Response Example (성공)

```json
{
  "success": true,
  "data": {
    "id": 3,
    "username": "newmember",
    "email": "member@example.com",
    "name": "신규회원",
    "status": "ACTIVE",
    "roleId": 3,
    "roleName": "사용자",
    "createdAt": "2026-02-22T13:00:00"
  },
  "error": null
}
```

---

## 역할 API

### 1. 역할 목록 조회

모든 역할 목록을 조회합니다.

- **URL**: `GET /api/v1/roles`
- **인증**: 필요
- **권한**: `ROLE_READ`

#### Response Example (성공)

```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "roleCode": "ADMIN",
      "roleName": "관리자",
      "description": "시스템 관리자",
      "userCount": 1,
      "permissions": null,
      "createdAt": "2026-02-22T10:00:00"
    },
    {
      "id": 2,
      "roleCode": "MANAGER",
      "roleName": "매니저",
      "description": "일부 관리 권한",
      "userCount": 5,
      "permissions": null,
      "createdAt": "2026-02-22T10:00:00"
    },
    {
      "id": 3,
      "roleCode": "USER",
      "roleName": "사용자",
      "description": "기본 권한",
      "userCount": 100,
      "permissions": null,
      "createdAt": "2026-02-22T10:00:00"
    }
  ],
  "error": null
}
```

---

### 2. 역할 상세 조회

특정 역할의 상세 정보를 조회합니다 (권한 목록 포함).

- **URL**: `GET /api/v1/roles/{id}`
- **인증**: 필요
- **권한**: `ROLE_READ`

#### Path Parameters

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| id | Long | O | 역할 ID |

#### Response Example (성공)

```json
{
  "success": true,
  "data": {
    "id": 1,
    "roleCode": "ADMIN",
    "roleName": "관리자",
    "description": "시스템 관리자",
    "userCount": 1,
    "permissions": [
      {
        "id": 1,
        "permissionCode": "USER_CREATE",
        "permissionName": "사용자 생성",
        "description": "사용자 생성 권한"
      },
      {
        "id": 2,
        "permissionCode": "USER_READ",
        "permissionName": "사용자 조회",
        "description": "사용자 조회 권한"
      }
    ],
    "createdAt": "2026-02-22T10:00:00",
    "updatedAt": "2026-02-22T10:00:00"
  },
  "error": null
}
```

---

### 3. 역할 생성

새로운 역할을 생성합니다.

- **URL**: `POST /api/v1/roles`
- **인증**: 필요
- **권한**: `ROLE_CREATE`
- **Content-Type**: `application/json`

#### Request Body

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| roleCode | String | O | 역할 코드 (영문 대문자, 언더스코어) |
| roleName | String | O | 역할명 |
| description | String | X | 설명 |
| permissionIds | Long[] | X | 권한 ID 목록 |

#### Request Example

```json
{
  "roleCode": "EDITOR",
  "roleName": "편집자",
  "description": "게시글 편집 권한",
  "permissionIds": [1, 2, 3, 4]
}
```

#### Response Example (성공)

```json
{
  "success": true,
  "data": {
    "id": 4,
    "roleCode": "EDITOR",
    "roleName": "편집자",
    "description": "게시글 편집 권한",
    "userCount": 0,
    "permissions": [
      {
        "id": 1,
        "permissionCode": "USER_CREATE",
        "permissionName": "사용자 생성",
        "description": null
      }
    ],
    "createdAt": "2026-02-22T14:00:00"
  },
  "error": null
}
```

---

### 4. 역할 수정

역할 정보를 수정합니다.

- **URL**: `PUT /api/v1/roles/{id}`
- **인증**: 필요
- **권한**: `ROLE_UPDATE`
- **Content-Type**: `application/json`

#### Path Parameters

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| id | Long | O | 역할 ID |

#### Request Body

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| roleName | String | O | 역할명 |
| description | String | X | 설명 |
| permissionIds | Long[] | X | 권한 ID 목록 |

#### Request Example

```json
{
  "roleName": "수정된 역할명",
  "description": "수정된 설명",
  "permissionIds": [1, 2, 3, 4, 5]
}
```

#### Response Example (성공)

```json
{
  "success": true,
  "data": {
    "id": 4,
    "roleCode": "EDITOR",
    "roleName": "수정된 역할명",
    "description": "수정된 설명",
    "userCount": 0,
    "permissions": [...],
    "updatedAt": "2026-02-22T15:00:00"
  },
  "error": null
}
```

---

### 5. 역할 삭제

역할을 삭제합니다.

- **URL**: `DELETE /api/v1/roles/{id}`
- **인증**: 필요
- **권한**: `ROLE_DELETE`

#### Path Parameters

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| id | Long | O | 역할 ID |

#### Response Example (성공)

```json
{
  "success": true,
  "data": null,
  "error": null
}
```

> 사용자가 할당된 역할은 삭제할 수 없습니다.

---

### 6. 역할 코드 중복 확인

역할 코드 중복 여부를 확인합니다.

- **URL**: `GET /api/v1/roles/check/code`
- **인증**: 불필요

#### Query Parameters

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| roleCode | String | O | 확인할 역할 코드 |

#### Response Example (성공)

```json
{
  "success": true,
  "data": false,
  "error": null
}
```

---

## 권한 API

### 1. 권한 목록 조회

모든 권한 목록을 조회합니다.

- **URL**: `GET /api/v1/permissions`
- **인증**: 필요
- **권한**: `ROLE_READ`

#### Response Example (성공)

```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "permissionCode": "USER_CREATE",
      "permissionName": "사용자 생성",
      "description": "사용자 생성 권한",
      "createdAt": "2026-02-22T10:00:00"
    },
    {
      "id": 2,
      "permissionCode": "USER_READ",
      "permissionName": "사용자 조회",
      "description": "사용자 조회 권한",
      "createdAt": "2026-02-22T10:00:00"
    }
  ],
  "error": null
}
```

---

### 2. 권한 상세 조회

특정 권한의 상세 정보를 조회합니다.

- **URL**: `GET /api/v1/permissions/{id}`
- **인증**: 필요
- **권한**: `ROLE_READ`

#### Path Parameters

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| id | Long | O | 권한 ID |

#### Response Example (성공)

```json
{
  "success": true,
  "data": {
    "id": 1,
    "permissionCode": "USER_CREATE",
    "permissionName": "사용자 생성",
    "description": "사용자 생성 권한",
    "createdAt": "2026-02-22T10:00:00",
    "updatedAt": null
  },
  "error": null
}
```

---

### 3. 역할별 권한 목록 조회

특정 역할에 부여된 권한 목록을 조회합니다.

- **URL**: `GET /api/v1/permissions/role/{roleId}`
- **인증**: 필요
- **권한**: `ROLE_READ`

#### Path Parameters

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| roleId | Long | O | 역할 ID |

#### Response Example (성공)

```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "permissionCode": "USER_CREATE",
      "permissionName": "사용자 생성",
      "description": "사용자 생성 권한",
      "createdAt": "2026-02-22T10:00:00"
    }
  ],
  "error": null
}
```

---

### 4. 권한 생성

새로운 권한을 생성합니다.

- **URL**: `POST /api/v1/permissions`
- **인증**: 필요
- **권한**: `ROLE_CREATE`
- **Content-Type**: `application/json`

#### Request Body

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| permissionCode | String | O | 권한 코드 (영문 대문자, 언더스코어) |
| permissionName | String | O | 권한명 |
| description | String | X | 설명 |

#### Request Example

```json
{
  "permissionCode": "REPORT_CREATE",
  "permissionName": "보고서 생성",
  "description": "보고서 생성 권한"
}
```

#### Response Example (성공)

```json
{
  "success": true,
  "data": {
    "id": 20,
    "permissionCode": "REPORT_CREATE",
    "permissionName": "보고서 생성",
    "description": "보고서 생성 권한",
    "createdAt": "2026-02-22T16:00:00"
  },
  "error": null
}
```

---

### 5. 권한 수정

권한 정보를 수정합니다.

- **URL**: `PUT /api/v1/permissions/{id}`
- **인증**: 필요
- **권한**: `ROLE_UPDATE`
- **Content-Type**: `application/json`

#### Path Parameters

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| id | Long | O | 권한 ID |

#### Request Body

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| permissionName | String | O | 권한명 |
| description | String | X | 설명 |

#### Request Example

```json
{
  "permissionName": "수정된 권한명",
  "description": "수정된 설명"
}
```

#### Response Example (성공)

```json
{
  "success": true,
  "data": {
    "id": 20,
    "permissionCode": "REPORT_CREATE",
    "permissionName": "수정된 권한명",
    "description": "수정된 설명",
    "updatedAt": "2026-02-22T17:00:00"
  },
  "error": null
}
```

---

### 6. 권한 삭제

권한을 삭제합니다.

- **URL**: `DELETE /api/v1/permissions/{id}`
- **인증**: 필요
- **권한**: `ROLE_DELETE`

#### Path Parameters

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| id | Long | O | 권한 ID |

#### Response Example (성공)

```json
{
  "success": true,
  "data": null,
  "error": null
}
```

> 역할에 매핑된 권한은 삭제할 수 없습니다.

---

### 7. 권한 코드 중복 확인

권한 코드 중복 여부를 확인합니다.

- **URL**: `GET /api/v1/permissions/check/code`
- **인증**: 불필요

#### Query Parameters

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| permissionCode | String | O | 확인할 권한 코드 |

#### Response Example (성공)

```json
{
  "success": true,
  "data": false,
  "error": null
}
```

---

## 파일 API

파일 업로드, 다운로드, 삭제를 제공합니다. ref_type, ref_id로 게시글, 페이지 등 다양한 엔티티에 연결할 수 있습니다.

### 파일 설정

| 항목 | 값 |
|------|-----|
| 저장 경로 | `C:/cms/files` (설정 가능) |
| 최대 파일 크기 | 3MB |
| 허용 확장자 | 이미지: jpg, jpeg, png, gif, webp / 문서: pdf, doc, docx, xls, xlsx, hwp |

### 1. 단일 파일 업로드

- **URL**: `POST /api/v1/files/upload`
- **인증**: 필요
- **권한**: `FILE_CREATE`
- **Content-Type**: `multipart/form-data`

#### Form Parameters

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| file | File | O | 업로드할 파일 |
| refType | String | X | 참조 타입 (기본: GENERAL, 예: POST, PAGE) |
| refId | Long | X | 참조 ID (기본: 0) |

#### Response Example (성공)

```json
{
  "success": true,
  "data": {
    "id": 1,
    "refType": "POST",
    "refId": 1,
    "originalName": "document.pdf",
    "fileSize": 102400,
    "mimeType": "application/pdf",
    "createdAt": "2026-02-23T09:00:00",
    "downloadUrl": "/api/v1/files/1/download"
  },
  "error": null
}
```

---

### 2. 다중 파일 업로드

- **URL**: `POST /api/v1/files/upload/batch`
- **인증**: 필요
- **권한**: `FILE_CREATE`
- **Content-Type**: `multipart/form-data`

#### Form Parameters

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| files | File[] | O | 업로드할 파일 목록 |
| refType | String | X | 참조 타입 (기본: POST) |
| refId | Long | O | 참조 ID |
| maxCount | int | X | 최대 업로드 개수 (기본: 5) |

---

### 3. 파일 다운로드

- **URL**: `GET /api/v1/files/{id}/download`
- **인증**: 필요
- **권한**: `FILE_READ`

#### Path Parameters

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| id | Long | O | 파일 ID |

#### Response

파일 바이너리 스트림 (Content-Disposition: attachment)

---

### 4. 파일 정보 조회

- **URL**: `GET /api/v1/files/{id}`
- **인증**: 필요
- **권한**: `FILE_READ`

---

### 5. 참조별 파일 목록 조회

- **URL**: `GET /api/v1/files?refType={refType}&refId={refId}`
- **인증**: 필요
- **권한**: `FILE_READ`

---

### 6. 파일 삭제

- **URL**: `DELETE /api/v1/files/{id}`
- **인증**: 필요
- **권한**: `FILE_DELETE`

### 파일 에러 코드 (5000번대)

| 코드 | 메시지 | HTTP Status |
|------|--------|-------------|
| FILE_5000 | 파일을 찾을 수 없습니다. | 404 |
| FILE_5001 | 파일 업로드에 실패했습니다. | 500 |
| FILE_5002 | 파일 크기가 제한을 초과했습니다. (최대 3MB) | 400 |
| FILE_5003 | 허용되지 않는 파일 확장자입니다. | 400 |
| FILE_5004 | 파일 개수가 제한을 초과했습니다. | 400 |

---

## 감사 로그 API

수정/삭제 이력을 조회합니다. 사용자, 게시글 등 주요 엔티티 변경 시 자동 기록됩니다.

### 1. 감사 로그 목록 조회

- **URL**: `GET /api/v1/audit-logs`
- **인증**: 필요
- **권한**: `AUDIT_READ`

#### Query Parameters

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| page | int | X | 페이지 (기본: 1) |
| size | int | X | 페이지 크기 (기본: 20) |
| userId | Long | X | 사용자 ID |
| action | String | X | 액션 (CREATE, UPDATE, DELETE) |
| targetType | String | X | 대상 타입 (USER, POST 등) |
| targetId | Long | X | 대상 ID |
| startDate | String | X | 시작일 (yyyy-MM-dd) |
| endDate | String | X | 종료일 (yyyy-MM-dd) |

#### Response Example (성공)

```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "userId": 1,
        "username": "admin",
        "action": "UPDATE",
        "targetType": "USER",
        "targetId": 2,
        "beforeData": "{\"name\":\"홍길동\"}",
        "afterData": "{\"name\":\"김철수\"}",
        "ipAddress": "127.0.0.1",
        "createdAt": "2026-02-23T10:00:00"
      }
    ],
    "page": 1,
    "size": 20,
    "totalElements": 100,
    "totalPages": 5
  },
  "error": null
}
```

---

### 2. 감사 로그 상세 조회

- **URL**: `GET /api/v1/audit-logs/{id}`
- **인증**: 필요
- **권한**: `AUDIT_READ`

#### Path Parameters

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| id | Long | O | 감사 로그 ID |

### 감사 로그 에러 코드 (4500번대)

| 코드 | 메시지 | HTTP Status |
|------|--------|-------------|
| AUDIT_4500 | 감사 로그를 찾을 수 없습니다. | 404 |

---

## 공통 코드 API

코드 그룹 및 코드를 관리합니다. 계층 구조(parent_id)를 지원하며, 그룹별 코드는 드롭다운 등에서 활용됩니다.

### 1. 코드 그룹 목록 조회

- **URL**: `GET /api/v1/common-code-groups`
- **인증**: 필요
- **권한**: `COMMON_CODE_READ`

#### Response Example (성공)

```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "groupCode": "GENDER",
      "groupName": "성별",
      "description": "성별 코드",
      "isSystem": false,
      "isActive": true,
      "codeCount": 3,
      "createdAt": "2026-02-23T10:00:00",
      "updatedAt": null
    }
  ],
  "error": null
}
```

### 2. 코드 그룹 상세 조회

- **URL**: `GET /api/v1/common-code-groups/{id}`
- **인증**: 필요
- **권한**: `COMMON_CODE_READ`

### 3. 코드 그룹 코드로 조회

- **URL**: `GET /api/v1/common-code-groups/code/{groupCode}`
- **인증**: 필요
- **권한**: `COMMON_CODE_READ`

### 4. 코드 그룹 생성

- **URL**: `POST /api/v1/common-code-groups`
- **인증**: 필요
- **권한**: `COMMON_CODE_CREATE`

#### Request Body

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| groupCode | String | O | 그룹 코드 (영문, 숫자, 언더스코어) |
| groupName | String | O | 그룹명 |
| description | String | X | 설명 |
| isSystem | Boolean | X | 시스템 코드 여부 (기본: false) |
| isActive | Boolean | X | 활성 여부 (기본: true) |

### 5. 코드 그룹 수정

- **URL**: `PUT /api/v1/common-code-groups/{id}`
- **인증**: 필요
- **권한**: `COMMON_CODE_UPDATE`

#### Request Body

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| groupName | String | X | 그룹명 |
| description | String | X | 설명 |
| isActive | Boolean | X | 활성 여부 |

### 6. 코드 그룹 삭제

- **URL**: `DELETE /api/v1/common-code-groups/{id}`
- **인증**: 필요
- **권한**: `COMMON_CODE_DELETE`

> 코드가 있는 그룹은 삭제할 수 없습니다.

### 7. 그룹별 코드 목록 조회 (평면)

- **URL**: `GET /api/v1/common-codes/group/{groupId}`
- **인증**: 필요
- **권한**: `COMMON_CODE_READ`

### 8. 그룹별 코드 목록 조회 (계층)

- **URL**: `GET /api/v1/common-codes/group/{groupId}/hierarchy`
- **인증**: 필요
- **권한**: `COMMON_CODE_READ`

### 9. 활성 코드 조회 (공개 API)

현재 유효한(활성+기간) 코드 목록을 조회합니다. 인증 없이 사용 가능하며, 드롭다운 등에서 활용됩니다.

- **URL**: `GET /api/v1/common-codes/active/{groupCode}`
- **인증**: 불필요

#### Response Example (성공)

```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "groupId": 1,
      "groupCode": "GENDER",
      "parentId": null,
      "depth": 1,
      "code": "M",
      "codeName": "남성",
      "codeValue": null,
      "sortOrder": 1,
      "isActive": true,
      "children": null
    }
  ],
  "error": null
}
```

### 10. 코드 생성

- **URL**: `POST /api/v1/common-codes`
- **인증**: 필요
- **권한**: `COMMON_CODE_CREATE`

#### Request Body

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| groupId | Long | O | 코드 그룹 ID |
| parentId | Long | X | 상위 코드 ID (최상위면 생략) |
| code | String | O | 코드 (그룹 내 유일) |
| codeName | String | O | 코드명 |
| codeValue | String | X | 실제 저장 값 |
| sortOrder | Integer | X | 정렬 순서 (기본: 0) |
| isActive | Boolean | X | 사용 여부 (기본: true) |
| startAt | DateTime | X | 사용 시작일 |
| endAt | DateTime | X | 사용 종료일 |

### 11. 코드 수정

- **URL**: `PUT /api/v1/common-codes/{id}`
- **인증**: 필요
- **권한**: `COMMON_CODE_UPDATE`

### 12. 코드 삭제

- **URL**: `DELETE /api/v1/common-codes/{id}`
- **인증**: 필요
- **권한**: `COMMON_CODE_DELETE`

> 하위 코드가 있는 코드는 삭제할 수 없습니다.

### 공통 코드 에러 코드 (4600번대)

| 코드 | 메시지 | HTTP Status |
|------|--------|-------------|
| CC_4600 | 공통 코드 그룹을 찾을 수 없습니다. | 404 |
| CC_4601 | 이미 존재하는 코드 그룹입니다. | 409 |
| CC_4602 | 코드가 있는 그룹은 삭제할 수 없습니다. | 409 |
| CC_4603 | 시스템 코드 그룹은 수정/삭제할 수 없습니다. | 403 |
| CC_4610 | 공통 코드를 찾을 수 없습니다. | 404 |
| CC_4611 | 같은 그룹 내에 이미 존재하는 코드입니다. | 409 |
| CC_4612 | 시스템 코드는 수정/삭제할 수 없습니다. | 403 |
| CC_4613 | 하위 코드가 있는 코드는 삭제할 수 없습니다. | 409 |

---

## 팝업 API

사이트 팝업을 관리합니다. 노출 위치, 디바이스, 기간별 제어 및 오늘 하루 보지 않기 지원.

### 1. 팝업 목록 조회

- **URL**: `GET /api/v1/popups`
- **인증**: 필요
- **권한**: `POPUP_READ`

#### Query Parameters

| 필드 | 타입 | 설명 |
|------|------|------|
| keyword | String | 팝업 코드/명 검색 |
| popupType | String | LAYER, WINDOW, MODAL |
| positionType | String | MAIN, SUB, ALL |
| deviceType | String | PC, MOBILE, ALL |
| page | int | 페이지 (기본: 1) |
| size | int | 페이지 크기 (기본: 20) |

### 2. 팝업 상세 조회

- **URL**: `GET /api/v1/popups/{id}`
- **인증**: 필요
- **권한**: `POPUP_READ`

### 3. 노출 대상 팝업 조회 (공개 API)

사용자 페이지에서 현재 노출할 팝업 목록 조회. 인증 불필요.

- **URL**: `GET /api/v1/popups/display`
- **인증**: 불필요

#### Query Parameters

| 필드 | 타입 | 기본값 | 설명 |
|------|------|--------|------|
| positionType | String | MAIN | MAIN, SUB, ALL |
| deviceType | String | PC | PC, MOBILE, ALL |
| isLogin | boolean | false | 로그인 여부 |

### 4. 팝업 생성

- **URL**: `POST /api/v1/popups`
- **인증**: 필요
- **권한**: `POPUP_CREATE`

#### Request Body

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| popupCode | String | O | 팝업 코드 (고유) |
| popupName | String | O | 팝업명 |
| popupType | String | X | LAYER, WINDOW, MODAL (기본: LAYER) |
| positionType | String | X | MAIN, SUB, ALL (기본: MAIN) |
| deviceType | String | X | PC, MOBILE, ALL (기본: ALL) |
| width | Integer | X | 너비(px) |
| height | Integer | X | 높이(px) |
| posX | Integer | X | X 좌표 (기본: 0) |
| posY | Integer | X | Y 좌표 (기본: 0) |
| content | String | X | HTML 내용 |
| linkUrl | String | X | 클릭 시 이동 URL |
| linkTarget | String | X | _self, _blank |
| isLoginRequired | Boolean | X | 로그인 사용자 전용 |
| isTodayCloseEnabled | Boolean | X | 오늘 하루 보지 않기 (기본: true) |
| startAt | DateTime | X | 노출 시작일 |
| endAt | DateTime | X | 노출 종료일 |
| isActive | Boolean | X | 활성 (기본: true) |
| isPublished | Boolean | X | 게시 (기본: true) |

### 5. 팝업 수정 / 삭제

- **수정**: `PUT /api/v1/popups/{id}` (권한: POPUP_UPDATE)
- **삭제**: `DELETE /api/v1/popups/{id}` (권한: POPUP_DELETE)

### 팝업 에러 코드 (4700번대)

| 코드 | 메시지 | HTTP Status |
|------|--------|-------------|
| POPUP_4700 | 팝업을 찾을 수 없습니다. | 404 |
| POPUP_4701 | 이미 존재하는 팝업 코드입니다. | 409 |

---

## 설치 API

### 1. 설치 상태 확인

CMS 설치 상태를 확인합니다.

- **URL**: `GET /api/v1/install/status`
- **인증**: 불필요

#### Response Example (성공)

```json
{
  "success": true,
  "data": {
    "installed": false,
    "version": null
  },
  "error": null
}
```

---

### 2. 데이터베이스 연결 테스트

데이터베이스 연결을 테스트합니다.

- **URL**: `POST /api/v1/install/database/test`
- **인증**: 불필요
- **Content-Type**: `application/json`

#### Request Body

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| host | String | O | DB 호스트 |
| port | Integer | O | DB 포트 |
| database | String | O | DB명 |
| username | String | O | DB 사용자 |
| password | String | O | DB 비밀번호 |

#### Request Example

```json
{
  "host": "localhost",
  "port": 3306,
  "database": "cms",
  "username": "root",
  "password": "password"
}
```

#### Response Example (성공)

```json
{
  "success": true,
  "data": {
    "connected": true,
    "message": "데이터베이스 연결 성공"
  },
  "error": null
}
```

---

### 3. 설치 실행

CMS를 설치합니다.

- **URL**: `POST /api/v1/install`
- **인증**: 불필요
- **Content-Type**: `application/json`

#### Request Body

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| database | Object | O | 데이터베이스 설정 |
| site | Object | O | 사이트 설정 |
| admin | Object | O | 관리자 계정 정보 |

#### Request Example

```json
{
  "database": {
    "host": "localhost",
    "port": 3306,
    "database": "cms",
    "username": "root",
    "password": "password"
  },
  "site": {
    "name": "My CMS",
    "url": "http://localhost:8080"
  },
  "admin": {
    "username": "admin",
    "password": "Admin123!",
    "email": "admin@example.com",
    "name": "관리자"
  }
}
```

#### Response Example (성공)

```json
{
  "success": true,
  "data": {
    "installed": true,
    "message": "설치가 완료되었습니다."
  },
  "error": null
}
```

---

---

## 사용자 사이트 (Site)

사용자 사이트(`/site/**`)는 정적 HTML + API 기반으로 동작하며, 관리자(`/admin/**`)와 세션을 분리합니다.

### Site 로그인

| 메서드 | URL | 설명 |
|--------|-----|------|
| GET | `/site/auth/login` | 로그인 폼 페이지 (Thymeleaf) |
| POST | `/site/auth/login` | 로그인 처리 (username, password 폼 파라미터) |
| POST | `/site/auth/logout` | 로그아웃 (SITE_CURRENT_USER 세션 제거) |

**POST /site/auth/login**
- **Content-Type**: `application/x-www-form-urlencoded`
- **파라미터**: `username`, `password`
- **성공 시**: 302 Redirect → `/site/`
- **실패 시**: 302 Redirect → `/site/auth/login?error=...`
- **세션 키**: `SITE_CURRENT_USER` (Admin의 `CURRENT_USER`와 분리)

### Site 정적 리소스

| 경로 | 설명 |
|------|------|
| `GET /site/` | index.html (메인) |
| `GET /site/css/site.css` | 사이트 스타일 |
| (제거됨) | site.js, router.js 제거. 사이트는 Thymeleaf 서버 렌더링 |

---

## 변경 이력

| 버전 | 날짜 | 변경 내용 |
|------|------|----------|
| 1.0.0 | 2026-02-22 | 최초 작성 |
| 1.1.0 | 2026-02-24 | 공개 API (Public Site, Public Board) 섹션 추가 |
| 1.2.0 | 2026-02-24 | 사용자 사이트 Phase 1 (Site 로그인/정적 리소스) 추가 |
