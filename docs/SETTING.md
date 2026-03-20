# CMS Core 설치/설정 가이드

> 설치 방법, 필수 설정값, 운영 전 점검 항목을 정리합니다.

---

## 1. 실행 환경

| 항목 | 권장 |
|------|------|
| Java | 17 |
| Spring Boot | 3.2.x |
| DB | MariaDB 10.6+ |
| 캐시 | Redis(선택, 미설정 시 인메모리) |

---

## 1.1 시스템 요구사항 (README 이관)

- OS: Windows 10/11, Ubuntu 20.04+, CentOS 8+
- JDK: 17 이상
- 메모리: 최소 512MB (권장 1GB+)
- 디스크: 최소 1GB + 파일 저장 여유 공간

---

## 2. 설치 절차

1. 애플리케이션 실행
2. `/install` 접속
3. DB 연결 테스트
4. 관리자 계정/사이트 기본정보 입력
5. 설치 완료(`installed: true`)
6. `cms-config.yml` 생성 확인

---

## 3. 주요 설정 파일

- `application.yml`
- `application-dev.yml`
- `application-prod.yml`
- `cms-config.yml` (설치 후 생성)

---

## 4. 핵심 설정 항목

### 4.1 JWT

- Access Token 만료: 1시간
- Refresh Token 만료: 14일

### 4.2 비밀번호 정책

- 영문/숫자/특수문자 포함
- 길이 6~12자
- 로그인 실패 5회 시 계정 잠금

### 4.3 파일 업로드

- 기본 저장 경로: `C:/cms/files` (환경별 변경 가능)
- 허용 확장자: 이미지/문서 규칙 준수
- 최대 크기: 3MB

### 4.4 CORS

- 외부 프론트엔드 연동 시 Origin 허용 목록 설정

### 4.5 로그

- 로그 파일 위치/보관 기간 운영 정책에 맞게 설정

---

## 4.6 인증/권한 필수 정책 (.cursorrules + README 이관)

- RBAC(Role/Permission/RolePermission) 구조 유지
- 수정/삭제 API에 권한 검증 적용
- 권한 문자열 하드코딩 금지(`Permission` 상수/enum 활용)
- 멀티 디바이스 로그인 불허(새 로그인 시 기존 refresh token 무효화)

---

## 5. 배포 전 점검

- DB 스키마 적용 여부
- 관리자 초기 계정 생성 여부
- Swagger 노출 정책 확인
- Public API/Protected API 접근 제어 확인
- Redis 미사용 시 인메모리 캐시 fallback 동작 확인

---

## 6. 문제 해결 포인트

- `/install` 재접근 가능 여부: 설치 상태 플래그 확인
- 로그인 문제: 토큰 만료/시계 오차/JWT 시크릿 확인
- 파일 업로드 실패: 경로 권한, 최대 용량, 확장자 확인
- API 403: Permission 매핑 누락 여부 확인
