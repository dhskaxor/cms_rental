# 사용자 사이트 익명 게시글 작성 API 구현 예시

> 비회원 권한이 설정된 게시판에서 사용자 사이트에서 글쓰기 API를 호출할 수 있도록 하는 구현 예시입니다.

---

## 1. 개요

| 항목 | 내용 |
|------|------|
| 대상 | 사용자 사이트 (`/site/**`) |
| 엔드포인트 | `POST /api/v1/public/boards/{boardId}/posts` |
| 인증 | 선택 (로그인 시 userId/roleId 사용, 비로그인 시 ANONYMOUS) |
| 선행 조건 | 관리자에서 해당 게시판에 비회원 `쓰기` 권한 설정 |

---

## 2. API 설계

### 2.1 요청

| Method | URL | 설명 |
|--------|-----|------|
| POST | `/api/v1/public/boards/{boardId}/posts` | 게시글 생성 |

**Path Variable**

| 이름 | 타입 | 설명 |
|------|------|------|
| boardId | Long | 게시판 ID |

**Request Body** (application/json)

```json
{
  "title": "제목",
  "content": "내용",
  "isNotice": false,
  "isSecret": false
}
```

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| title | String | O | 제목 (255자 이하) |
| content | String | | 내용 |
| isNotice | Boolean | | 공지 여부 (기본 false) |
| isSecret | Boolean | | 비밀글 여부 (기본 false, 게시판 useSecret=true일 때만) |

### 2.2 응답

**성공 (200)**

```json
{
  "success": true,
  "data": {
    "id": 1,
    "boardId": 1,
    "title": "제목",
    "content": "내용",
    "writerName": "비회원",
    "createdAt": "2026-02-26T10:00:00"
  },
  "error": null
}
```

**에러 예시**

- `BOARD_NOT_FOUND`: 게시판 없음
- `BOARD_PERMISSION_DENIED`: 비회원/해당 역할에 쓰기 권한 없음

---

## 3. 백엔드 구현 예시

### 3.1 PublicBoardController에 추가

```java
/**
 * 게시글 작성 (인증 선택: 로그인 시 사용자, 비로그인 시 ANONYMOUS 권한으로 처리)
 */
@Operation(summary = "게시글 작성", description = "게시글을 작성합니다. 비로그인 시 비회원 권한으로 처리됩니다.")
@PostMapping("/boards/{boardId}/posts")
public ApiResponse<PostResponse> createPost(
        @PathVariable Long boardId,
        @Valid @RequestBody PostCreateRequest request,
        HttpSession session) {
    SessionUser user = (SessionUser) session.getAttribute(SessionConstants.SITE_CURRENT_USER);
    Long userId = user != null ? user.getId() : null;
    Long roleId = resolveRoleId(user);
    PostResponse response = postService.createPost(boardId, request, userId, roleId);
    return ApiResponse.success(response);
}

/**
 * SessionUser에서 roleId 추출 (roleCode로 조회)
 */
private Long resolveRoleId(SessionUser user) {
    if (user == null || user.getRoleCode() == null) return null;
    var role = roleMapper.findByRoleCode(user.getRoleCode());
    return role != null ? role.getId() : null;
}
```

### 3.2 필요한 import 및 의존성

```java
import com.nt.cms.auth.dto.SessionUser;
import com.nt.cms.common.constant.SessionConstants;
import com.nt.cms.role.mapper.RoleMapper;
import com.nt.cms.role.vo.RoleVO;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
```

`PublicBoardController` 생성자에 `RoleMapper` 주입 필요.

### 3.3 SessionUser 구조

`SessionUser`는 `id`, `username`, `name`, `email`, `roleCode` 필드를 갖습니다. `roleId`는 `RoleMapper.findByRoleCode(roleCode)`로 조회합니다.

---

## 4. 프론트엔드 fetch 예시 (cms_user_react 또는 별도 폼)

```javascript
function createPost(boardId, formData) {
    return fetch('/api/v1/public/boards/' + boardId + '/posts', {
        method: 'POST',
        credentials: 'same-origin',
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        },
        body: JSON.stringify({
            title: formData.title,
            content: formData.content || '',
            isNotice: false,
            isSecret: formData.isSecret || false
        })
    })
    .then(function(res) {
        if (!res.ok) {
            return res.json().then(function(body) {
                throw new Error(body.error?.message || '게시글 작성에 실패했습니다.');
            });
        }
        return res.json();
    })
    .then(function(body) {
        if (body.success && body.data) {
            // 작성 성공 시 상세 페이지로 이동
            window.location.href = '/site/board/' + boardId + '/post/' + body.data.id;
        }
    });
}
```

### 4.1 사용 예 (글쓰기 폼 제출)

```javascript
document.getElementById('postForm').addEventListener('submit', function(e) {
    e.preventDefault();
    var boardId = parseInt(document.getElementById('boardId').value, 10);
    var formData = {
        title: document.getElementById('title').value.trim(),
        content: document.getElementById('content').value
    };
    createPost(boardId, formData).catch(function(err) {
        alert(err.message);
    });
});
```

---

## 5. SecurityConfig

`/api/v1/public/**` 는 이미 `PUBLIC_URLS` 에 포함되어 `permitAll()` 처리되므로, 별도 Security 수정 없이 POST 요청 가능합니다.

---

## 6. 추가 구현 시 고려사항

| 항목 | 설명 |
|------|------|
| 댓글 작성 | `CommentService.createComment` 에 roleId 파라미터 추가 후, 동일 패턴으로 Public API 구현 |
| CSRF | REST API + form 혼용 시 CSRF 처리 (현재 csrf 비활성화 상태) |
| Rate Limiting | 익명 글쓰기 남용 방지를 위한 IP 기반 제한 검토 |
| 작성자 표시 | 비로그인 시 `writerName` = "비회원" (시스템 익명 계정 이름) |

---

## 7. 참조

- `PostService.createPost(boardId, request, userId, roleId)` – userId/roleId null 시 ANONYMOUS·anonymous 계정 사용
- `Doc/user_map.md` 6절 – 비회원 게시판 권한 설정
- `PostCreateRequest` – 기존 DTO 재사용
