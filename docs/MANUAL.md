# CMS Core 사용 매뉴얼

> 운영자/개발자가 CMS Core를 실제로 사용하는 절차를 정리한 문서입니다.

---

## 1. 문서 사용 순서

1. 구조 이해: `docs/ARCHITECTURE.md`
2. 설정/설치: `docs/SETTING.md`
3. API 사용: `docs/API.md`
4. DB 확인: `docs/EDR.md`
5. 프로젝트 맥락: `docs/CONTEXT.md`

---

## 2. 프로젝트 개요 (README 이관)

- CMS Core Platform은 재사용 가능한 콘텐츠 관리 코어 시스템이다.
- Spring Boot + MyBatis + JWT + Thymeleaf 기반으로 동작한다.
- 단독 사용 또는 Core import 방식으로 확장 사용이 가능하다.

### 2.1 빠른 시작

```bash
# Windows
gradlew.bat build
gradlew.bat bootRun
```

설치 마법사: `http://localhost:8082/install`

---

## 3. 관리자 화면 사용

### 3.1 로그인

- 경로: `/admin/login`
- 권한 기반 메뉴 노출
- 모든 수정/삭제 기능은 권한(`Permission`) 매핑 기반으로 제어

### 3.2 핵심 운영 메뉴

- 사용자/역할/권한 관리
- 게시판/게시글/댓글 관리
- 사이트 메뉴/페이지/팝업 관리
- 파일 관리
- 감사 로그 조회
- 대관(장소/룸/요금/예약) 관리

---

## 4. 사용자 사이트(`/site/**`) 운영

- 경로: `/site/`
- 데이터는 공개 API 중심으로 제공
- cms 템플릿 사이트와 `cms_user_react`는 동일 API를 기준으로 동작

### 4.1 게시판 URL

- 코드 기반: `/site/board/{boardCode}`
- ID 기반: `/site/board/{boardId}`

### 4.2 비회원 게시판 정책

- 게시판 권한에서 ANONYMOUS 읽기/쓰기 허용 가능
- 비로그인 작성 시 시스템 비회원 계정으로 처리

---

## 5. API 테스트(필수 절차)

### 5.1 Postman 설정

1. `docs/postman/CMS_Core_API.postman_collection.json` Import
2. `docs/postman/CMS_Core_Local.postman_environment.json` Import
3. `baseUrl` 확인 후 로그인 API 실행
4. 저장된 `accessToken`으로 보호 API 테스트

### 5.2 검증 순서 권장

1. Auth(`login`, `refresh`, `logout`)
2. Public API(`menus`, `pages`, `boards`)
3. Admin API(`users`, `roles`, `boards`)
4. Rental API(`public/rentals`, `rental`)

---

## 6. 운영 체크리스트

- 권한 문자열 하드코딩 금지(상수/enum 사용)
- Soft Delete 데이터 노출 여부 점검
- API 응답 포맷(`ApiResponse`) 일관성 점검
- 관리자/사이트 세션 분리 동작 점검
- 게시판/대관 핵심 플로우에 대한 회귀 테스트 수행

---

## 7. 사용자 사이트 Phase 운영 요약 (user_project 이관)

- Phase 1~8 완료 항목 기준으로 `/site/**` 운영
- 메뉴/페이지/게시판/로그인/팝업/에러 페이지/디자인 동기화 포함
- Phase 변경 시 `docs/CONTEXT.md`와 본 문서를 함께 업데이트

---

## 8. 자주 확인하는 경로

- Swagger: `/swagger-ui.html`
- 설치 마법사: `/install` (설치 완료 후 차단)
- 사용자 도움말: `/site/help`
