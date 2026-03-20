# CMS Core 프로젝트 컨텍스트

> AI/개발자가 프로젝트를 빠르게 이해하기 위한 통합 컨텍스트 문서입니다.

---

## 1. 프로젝트 한 줄 정의

Spring Boot + MyBatis 기반의 재사용 가능한 CMS Core 플랫폼이며, 관리자 웹과 사용자 사이트 API를 함께 제공한다.

---

## 2. 기술/아키텍처 핵심

- 패키지: `com.nt.cms`
- 계층: Controller -> Service -> Mapper(XML)
- 보안: Spring Security + JWT + 세션 분리 운영
- 데이터: Soft Delete 기본
- API 버전: `/api/v1`
- 코딩 규칙: 본 문서의 규칙 요약 기준

---

## 3. 도메인 맵

- 공통: `common`, `install`, `auth`
- 운영: `user`, `role`, `board`, `file`, `audit`, `menu`, `commoncode`, `popup`
- 대관: `rental`(place/room/pricing/calendar/reservation)
- 표현: `admin`, `publicapi`, `/site/**`

---

## 4. 문서 체계 (현재 기준)

- 아키텍처: `docs/ARCHITECTURE.md`
- DB 구조: `docs/EDR.md`
- API: `docs/API.md`
- 사용법: `docs/MANUAL.md`
- 설치/설정: `docs/SETTING.md`
- Postman: `docs/postman/*`
- 규칙 : .cursorrules
- 렌탈 상세: `docs/RENTAL_MODULE_STRUCTURE.md`, `docs/RENTAL_PRICING_LOGIC.md`, `docs/RENTAL_FILES_REFERENCE.md`

---

## 5. 개발 시 우선 참조 파일

1. `docs/CONTEXT.md`
2. `docs/ARCHITECTURE.md`
3. `docs/API.md`
4. `docs/MANUAL.md`
5. `docs/SETTING.md`
6. `docs/EDR.md`
7. `docs/.cursorrules`

---

## 6. 사용자 사이트 동기화 기준

- CMS `/site/**`와 `cms_user_react`는 동일 API를 기준으로 동작
- 라우팅/권한/XSS 정책 변경 시 양쪽 동시 반영
- XSS 허용 태그는 React sanitizer 기준으로 관리

---

## 7. 문서 재정비 이력 (2026-03-20)

- 구 문서 루트 체계를 `docs` 단일 체계로 이관
- 기존 문서(`api`, `user_map`, `REFACTORING_CHECKLIST`, `PUBLIC_POST_CREATE_IMPL_EXAMPLE`, `ARCHITECTURE`, `DB_ERD`) 내용을 목적별로 통합
- Postman 자산을 `docs/postman`으로 이전
- 프로젝트 내 문서 참조 경로를 `docs/...` 기준으로 정리

---

## 8. 루트 문서 이관 맵

아래 루트 파일의 핵심 내용을 `docs` 하위 문서로 통합했다.

| 기존 파일 | 통합 대상 |
|------|------|
| `.cursorrules` | `docs/CONTEXT.md` (아키텍처/코딩 규칙) |
| `README.md` | `docs/MANUAL.md`, `docs/SETTING.md` |
| `PROJECT_CONTEXT.md` | `docs/CONTEXT.md` |
| `USER_CONTEXT.md` | `docs/CONTEXT.md`, `docs/MANUAL.md` |
| `user_project.md` | `docs/CONTEXT.md`, `docs/MANUAL.md` |
| `all_project.md` | `docs/CONTEXT.md` |

---

## 9. 핵심 규칙 요약(.cursorrules 이관)

- 계층: Controller / Service / Mapper 분리, 비즈니스 로직은 Service 전용
- MyBatis XML 강제, `SELECT *` 금지, soft delete 조건 필수
- 보안: JWT(1시간/14일), BCrypt, 로그인 5회 실패 잠금
- 권한: RBAC, `Permission` 기반, 하드코딩 금지
- API: `/api/v1` 버전, `ApiResponse` 형식, check/code는 `ApiResponse<Boolean>`
- 사용자 사이트: `/site/**` 전용, admin 세션과 분리, cms/cms_user_react 동기화 유지

---

## 10. 진행 현황/Phase 요약(all_project + PROJECT_CONTEXT 이관)

### 10.1 완료

- Phase 1: 백엔드 API 모듈
- Phase 2: 관리자 화면 기본/핵심 화면
- Phase 3: 사용자단 공개 API
- 사용자 사이트 UX 동기화(Lucide, 다중 테마, 레이아웃 정렬)

### 10.2 다음 우선순위

1. 관리자 설정 화면 실구현 점검
2. 감사 로그 기능 상세 검증
3. 사용자 사이트/React 동기화 회귀 검증
4. 대관 모듈 예약/정산 시나리오 강화

---

## 11. 사용자 사이트 컨텍스트(USER_CONTEXT + user_project 이관)

- 렌더링: cms는 Thymeleaf Page/Submit, cms_user_react는 API fetch 기반
- 공통 경로: `/site/**`
- 핵심 공개 API: 메뉴/페이지/팝업/게시판/최신글
- 게시판 URL: boardCode, boardId 모두 지원
- 도움말: `/site/help`에서 Swagger 링크 제공
- XSS 허용 태그는 React sanitizer 기준으로 맞춘다

---

## 12. 새 세션 시작 가이드

```text
docs/CONTEXT.md
docs/ARCHITECTURE.md
docs/API.md`
docs/SETTING.md
docs/MANUAL.md
docs/.cursorrules
```

위 순서로 읽고 작업을 시작하면 기존 루트 문서 없이도 동일한 컨텍스트를 확보할 수 있다.
