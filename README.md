# CMS Core Platform

재사용 가능한 CMS(Content Management System) Core 플랫폼입니다.
Spring Boot + MyBatis 기반으로 설계되었으며, 프로젝트별 커스터마이징을 전제로 합니다.

---

## 목차

1. [개요](#1-개요)
2. [기술 스택](#2-기술-스택)
3. [시스템 요구사항](#3-시스템-요구사항)
4. [빠른 시작](#4-빠른-시작)
5. [프로젝트 구조](#5-프로젝트-구조)
6. [설치 가이드](#6-설치-가이드)
7. [설정](#7-설정)
8. [API 명세](#8-api-명세)
9. [인증 및 권한](#9-인증-및-권한)
10. [회원가입 및 이메일](#10-회원가입-및-이메일)
11. [핵심 기능](#11-핵심-기능)
12. [캐싱](#12-캐싱)
13. [CORS](#13-cors)
14. [로깅](#14-로깅)
15. [데이터베이스](#15-데이터베이스)
16. [테스트](#16-테스트)
17. [배포](#17-배포)
18. [라이선스](#18-라이선스)
19. [구현 이력](#19-구현-이력)

---

## 1. 개요

### 1.1 프로젝트 소개

CMS Core Platform은 장기적으로 유지보수되는 콘텐츠 관리 시스템의 핵심 모듈입니다.

**주요 특징:**
- 재사용 가능한 Core 시스템
- 프로젝트별 커스터마이징 지원
- 웹 기반 설치 마법사
- RBAC 기반 권한 시스템
- 확장 가능한 게시판 모듈
- 계층형 사이트 메뉴 및 정적 페이지 관리

### 1.2 사용 방법

1. **단독 사용**: 이 프로젝트를 직접 실행하여 CMS로 사용
2. **Core 모듈로 사용**: 다른 프로젝트에서 이 프로젝트를 Import하여 확장

---

## 2. 기술 스택

### 2.1 Backend

| 기술 | 버전 | 설명 |
|------|------|------|
| Java | 17 | LTS 버전 |
| Spring Boot | 3.2.x | 메인 프레임워크 |
| Spring Security | 6.x | 보안 프레임워크 |
| MyBatis | 3.x | SQL Mapper (XML 기반) |
| JWT | - | 토큰 기반 인증 |
| Lombok | - | 보일러플레이트 코드 감소 |
| Thymeleaf | - | 설치 마법사 UI (백엔드 포함) |
| Swagger/OpenAPI | - | API 문서 자동 생성 |

### 2.2 Database

| 기술 | 용도 |
|------|------|
| MariaDB | 운영 데이터베이스 (주) |
| PostgreSQL | 운영 데이터베이스 (추후 지원) |
| H2 | 테스트용 인메모리 DB |

### 2.3 Cache

| 기술 | 설명 |
|------|------|
| Redis | 선택 사항. 세션·권한 등 캐싱 (미설정 시 인메모리 캐시로 대체) |

### 2.4 Build & Tools

| 기술 | 설명 |
|------|------|
| Gradle | 빌드 도구 |
| JUnit 5 | 테스트 프레임워크 |

---

## 3. 시스템 요구사항

### 3.1 필수 요구사항

- **JDK**: 17 이상
- **MariaDB**: 10.6 이상
- **메모리**: 최소 512MB (권장 1GB 이상)
- **디스크**: 최소 1GB (파일 저장 공간 별도)

### 3.2 권장 환경

```
OS: Windows 10/11, Ubuntu 20.04+, CentOS 8+
JDK: OpenJDK 17 또는 Amazon Corretto 17
MariaDB: 10.11 LTS
```

---

## 4. 빠른 시작

### 4.1 프로젝트 클론

```bash
git clone https://github.com/your-org/cms-core.git
cd cms-core
```

### 4.2 빌드

```bash
# Windows
gradlew.bat build

# Linux/Mac
./gradlew build
```

### 4.3 실행

```bash
# Windows
gradlew.bat bootRun

# Linux/Mac
./gradlew bootRun
```

### 4.4 설치 마법사 접속

브라우저에서 `http://localhost:8082/install` 접속하여 설치를 진행합니다.

---

## 5. 프로젝트 구조

```
cms-core/
├── src/
│   ├── main/
│   │   ├── java/com/nt/cms/
│   │   │   ├── CmsApplication.java          # 메인 애플리케이션
│   │   │   ├── common/                      # 공통 모듈
│   │   │   │   ├── config/                  # 설정 클래스
│   │   │   │   ├── exception/               # 전역 예외 처리
│   │   │   │   ├── response/                # API 응답 포맷
│   │   │   │   ├── util/                    # 유틸리티
│   │   │   │   └── vo/                      # 공통 VO (BaseVO)
│   │   │   ├── install/                     # 설치 마법사
│   │   │   ├── auth/                        # 인증/인가
│   │   │   ├── user/                        # 사용자 관리
│   │   │   ├── role/                        # 역할 관리
│   │   │   ├── permission/                  # 권한 관리
│   │   │   ├── board/                       # 게시판
│   │   │   ├── file/                        # 파일 관리
│   │   │   ├── audit/                       # 감사 로그
│   │   │   ├── menu/                        # 사이트 메뉴, 정적 페이지
│   │   │   ├── commoncode/                  # 공통 코드
│   │   │   ├── popup/                       # 팝업 관리
│   │   │   ├── admin/                       # 관리자 화면 (Thymeleaf)
│   │   │   └── publicapi/                   # 사용자단 공개 API
│   │   └── resources/
│   │       ├── application.yml              # 기본 설정
│   │       ├── application-dev.yml          # 개발 환경 설정
│   │       ├── application-prod.yml         # 운영 환경 설정
│   │       ├── mapper/                      # MyBatis XML Mapper
│   │       │   ├── UserMapper.xml
│   │       │   ├── RoleMapper.xml
│   │       │   ├── BoardMapper.xml
│   │       │   └── ...
│   │       ├── schema/                      # DB 스키마
│   │       │   └── schema-mariadb.sql
│   │       └── data/                        # 초기 데이터
│   │           └── data-init.sql
│   └── test/
│       └── java/com/nt/cms/                 # 테스트 코드
├── Doc/                                     # 프로젝트 문서
│   ├── api.md                               # API 명세서
│   ├── user_map.md                          # 사용자 사이트 API/스타일 가이드
│   ├── REFACTORING_CHECKLIST.md             # 리팩토링·동기화 체크리스트 (cms ↔ cms_user_react)
│   ├── PUBLIC_POST_CREATE_IMPL_EXAMPLE.md   # 익명 게시글 작성 API 구현 예시
│   ├── DB_ERD.md                            # DB ERD (Mermaid)
│   └── postman/                             # Postman Collection
├── cms-config.yml                           # CMS 설정 파일 (설치 후 생성)
├── build.gradle                             # Gradle 빌드 설정
├── settings.gradle                          # Gradle 설정
├── .cursorrules                             # 코딩 규칙
├── all_project.md                           # 프로젝트 명세서
├── PROJECT_CONTEXT.md                       # 프로젝트 컨텍스트 (AI 참조)
└── README.md                                # 이 파일
```

### 5.1 공통 모듈 상세 (common)

공통 모듈은 전체 애플리케이션에서 재사용되는 핵심 컴포넌트를 포함합니다.

```
com.nt.cms.common/
├── config/                          # 설정 클래스
│   ├── CmsProperties.java           # CMS 설정 프로퍼티 (@ConfigurationProperties)
│   ├── SecurityConfig.java          # Spring Security 설정
│   ├── CorsConfig.java              # CORS 설정
│   ├── CacheConfig.java             # 캐시 설정 (Redis/인메모리 자동 전환)
│   ├── SwaggerConfig.java           # Swagger/OpenAPI 설정
│   └── WebConfig.java               # 웹 MVC 설정
├── exception/                       # 예외 처리
│   ├── ErrorCode.java               # 에러 코드 enum (60+ 정의)
│   ├── BusinessException.java       # 비즈니스 예외 클래스
│   └── GlobalExceptionHandler.java  # 전역 예외 처리 핸들러
├── response/                        # API 응답
│   ├── ApiResponse.java             # 표준 API 응답 포맷
│   ├── ErrorResponse.java           # 에러 응답
│   └── PageResponse.java            # 페이징 응답
├── util/                            # 유틸리티
│   ├── DateTimeUtil.java            # 날짜/시간 유틸리티
│   └── PasswordUtil.java            # 비밀번호 정책 검증
└── vo/                              # 공통 VO
    └── BaseVO.java                  # 공통 필드 정의
```

#### BaseVO 공통 필드

모든 VO는 `BaseVO`를 상속받아 다음 필드를 공통으로 가집니다:

| 필드 | 타입 | 설명 |
|------|------|------|
| id | Long | Primary Key |
| createdAt | LocalDateTime | 생성 일시 |
| createdBy | Long | 생성자 ID |
| updatedAt | LocalDateTime | 수정 일시 |
| updatedBy | Long | 수정자 ID |
| deleted | Boolean | Soft Delete 플래그 |

#### ErrorCode 카테고리

| 범위 | 카테고리 | 예시 |
|------|----------|------|
| 1000번대 | 공통 에러 | INTERNAL_SERVER_ERROR, INVALID_INPUT_VALUE |
| 2000번대 | 인증 에러 | UNAUTHORIZED, INVALID_TOKEN, ACCOUNT_LOCKED |
| 2100번대 | 권한 에러 | ACCESS_DENIED, PERMISSION_DENIED |
| 3000번대 | 사용자 에러 | USER_NOT_FOUND, DUPLICATE_USERNAME |
| 3100번대 | 역할 에러 | ROLE_NOT_FOUND, ROLE_IN_USE |
| 4000번대 | 게시판 에러 | BOARD_NOT_FOUND, POST_NOT_FOUND |
| 5000번대 | 파일 에러 | FILE_NOT_FOUND, FILE_SIZE_EXCEEDED |
| 6000번대 | 설치 에러 | ALREADY_INSTALLED, DATABASE_CONNECTION_FAILED |

#### CmsProperties 설정 바인딩

`application.yml`의 `cms.*` 설정을 Java 객체로 바인딩:

```java
@Autowired
private CmsProperties cmsProperties;

// JWT 설정
long accessTokenValidity = cmsProperties.getJwt().getAccessTokenValidity();

// 파일 설정
String uploadPath = cmsProperties.getFile().getUploadPath();
String[] allowedExtensions = cmsProperties.getFile().getAllAllowedExtensions();

// 보안 설정
int maxLoginAttempts = cmsProperties.getSecurity().getMaxLoginAttempts();
```

#### 캐시 설정 (자동 전환)

```yaml
# Redis 사용 시
spring:
  cache:
    type: redis

# 인메모리 캐시 사용 시 (기본값)
spring:
  cache:
    type: simple
```

사용 가능한 캐시 이름:
- `user` - 사용자 정보 (TTL: 30분)
- `permission` - 권한 정보 (TTL: 1시간)
- `role` - 역할 정보 (TTL: 1시간)
- `board` - 게시판 정보 (TTL: 30분)

### 5.2 설치 모듈 상세 (install)

설치 모듈은 웹 기반 설치 마법사를 제공합니다. Tailwind CSS를 사용한 깔끔한 UI로 구성되어 있습니다.

```
com.nt.cms.install/
├── controller/
│   └── InstallController.java      # 설치 마법사 컨트롤러
├── service/
│   ├── InstallService.java         # 설치 서비스 인터페이스
│   └── DefaultInstallService.java  # 설치 서비스 구현체
└── dto/
    ├── DatabaseConfigRequest.java  # DB 연결 설정 요청
    ├── AdminAccountRequest.java    # 관리자 계정 생성 요청
    └── SiteConfigRequest.java      # 사이트 설정 요청

templates/install/
├── layout.html                     # 공통 레이아웃 (Progress Bar 포함)
├── step1-database.html             # Step 1: DB 연결 정보 입력
├── step2-schema.html               # Step 2: 테이블 생성
├── step3-admin.html                # Step 3: 관리자 계정 생성
├── step4-config.html               # Step 4: 사이트 기본 설정
├── step5-complete.html             # Step 5: 설치 완료 확인
├── success.html                    # 설치 성공 페이지
└── already-installed.html          # 이미 설치됨 안내 페이지
```

#### 설치 흐름

| Step | 화면 | 기능 |
|------|------|------|
| 1 | DB 연결 | 호스트, 포트, DB명, 사용자 정보 입력 + 연결 테스트 |
| 2 | 테이블 생성 | 스키마 실행 + 초기 데이터 삽입 |
| 3 | 관리자 계정 | 아이디, 비밀번호, 이름, 이메일 입력 (비밀번호 정책 실시간 검증) |
| 4 | 사이트 설정 | 사이트명, 파일 업로드 경로 설정 |
| 5 | 완료 | 설정 요약 확인 + `cms-config.yml` 생성 |

#### 주요 기능

**연결 테스트 API**
```
POST /install/api/test-connection
Content-Type: application/json

{
  "host": "localhost",
  "port": 3306,
  "databaseName": "cms",
  "username": "root",
  "password": "password"
}
```

**비밀번호 정책 실시간 검증**
- Step 3에서 비밀번호 입력 시 JavaScript로 정책 충족 여부 실시간 표시
- 정책: 영문/숫자/특수문자 포함, 6~12자

**설치 상태 관리**
- `cms.installed=true` 설정 시 `/install` 경로 접근 차단
- 재설치가 필요한 경우 `cms-config.yml` 삭제 또는 `installed: false` 설정

#### 생성되는 설정 파일 (cms-config.yml)

```yaml
cms:
  installed: true
  
  database:
    host: localhost
    port: 3306
    name: cms
    username: root
    password: ****
  
  site:
    name: CMS Core
  
  file:
    upload-path: C:/cms/files
```

### 5.3 인증/인가 모듈 상세 (auth)

인증/인가 모듈은 **JWT 기반 API 인증**과 **세션 기반 웹 인증**을 모두 지원합니다.

#### 관리자 웹 페이지

```
templates/
├── auth/
│   └── login.html              # 로그인 페이지 (Tailwind CSS)
└── admin/
    ├── layout.html             # 관리자 공통 레이아웃 (사이드바, 헤더)
    ├── dashboard.html          # 대시보드 페이지
    ├── post/                   # 게시글 관리 (board-select, list, detail, form)
    ├── file/                   # 파일 관리 (list.html)
    ├── role/, user/, menu/, page/, board/, commoncode/, popup/, audit/ 등
    └── settings.html           # 설정 (placeholder)

com.nt.cms.auth.controller/
└── WebAuthController.java      # 웹 로그인/로그아웃 처리

com.nt.cms.admin.controller/
├── AdminController.java        # 대시보드, 설정 placeholder
├── AdminPostController.java    # 게시글 관리 (파일 첨부 포함)
├── AdminFileController.java    # 파일 관리 (다중 업로드 포함)
├── AdminRoleController.java
├── AdminUserController.java
├── AdminMenuController.java
├── AdminBoardController.java
├── AdminCommonCodeController.java
├── AdminPopupController.java
└── AdminAuditLogController.java
```

#### 접근 흐름

```
[루트 /] → [사용자 사이트 /site/]
[관리자] /auth/login → /admin
```

| URL | 설명 | 인증 |
|-----|------|------|
| `/` | 사용자 사이트(/site/)로 리다이렉트 | ❌ |
| `/site/` | 사용자 사이트 메인 | ❌ |
| `/auth/login` | 관리자 로그인 페이지 | ❌ |
| `/auth/logout` | 로그아웃 처리 | ❌ |
| `/admin` | 대시보드 | 세션 |
| `/admin/*` | 관리자 페이지들 (역할, 사용자, 메뉴, 페이지, 게시판, 게시글, 공통코드, 팝업, 파일, 감사로그 등) | 세션 |
| `/api/v1/*` | REST API | JWT |

#### JWT 기반 API 인증

```
com.nt.cms.auth/
├── controller/
│   └── AuthController.java           # 인증 API 컨트롤러
├── service/
│   ├── AuthService.java              # 인증 서비스 인터페이스
│   └── DefaultAuthService.java       # 인증 서비스 구현체
├── dto/
│   ├── LoginRequest.java             # 로그인 요청
│   ├── LoginResponse.java            # 로그인 응답 (토큰 + 사용자 정보)
│   ├── TokenRefreshRequest.java      # 토큰 갱신 요청
│   └── TokenRefreshResponse.java     # 토큰 갱신 응답
├── jwt/
│   ├── JwtTokenProvider.java         # JWT 토큰 생성/검증
│   └── JwtAuthenticationFilter.java  # JWT 인증 필터
├── security/
│   ├── CustomUserDetails.java        # UserDetails 구현체
│   └── CustomUserDetailsService.java # UserDetailsService 구현체
├── vo/
│   ├── RefreshTokenVO.java           # Refresh Token VO
│   └── UserAuthVO.java               # 사용자 인증 정보 VO
└── mapper/
    └── AuthMapper.java               # 인증 관련 Mapper

resources/mapper/auth/
└── AuthMapper.xml                    # 인증 SQL 매핑
```

#### 인증 API

| Method | URL | 설명 | 인증 필요 |
|--------|-----|------|----------|
| POST | `/api/v1/auth/login` | 로그인 | ❌ |
| POST | `/api/v1/auth/refresh` | 토큰 갱신 | ❌ |
| POST | `/api/v1/auth/logout` | 로그아웃 | ❌ |
| POST | `/api/v1/auth/logout-all` | 전체 로그아웃 (모든 디바이스) | ✅ |
| GET | `/api/v1/auth/me` | 내 정보 조회 | ✅ |

#### 로그인 예시

**요청**
```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "Admin123!"
}
```

**응답**
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIs...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
    "tokenType": "Bearer",
    "expiresIn": 3600,
    "user": {
      "id": 1,
      "username": "admin",
      "name": "관리자",
      "email": "admin@example.com",
      "roleCode": "ADMIN"
    }
  },
  "error": null
}
```

#### 토큰 갱신 예시

**요청**
```http
POST /api/v1/auth/refresh
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzI1NiIs..."
}
```

**응답**
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIs...",
    "tokenType": "Bearer",
    "expiresIn": 3600
  },
  "error": null
}
```

#### JWT 설정

| 항목 | 기본값 | 설명 |
|------|--------|------|
| Access Token 유효 시간 | 3600초 (1시간) | 짧은 유효 기간으로 보안 강화 |
| Refresh Token 유효 시간 | 1209600초 (14일) | 사용자 편의성 고려 |

#### 보안 정책

- **단일 디바이스 로그인**: 새 로그인 시 기존 Refresh Token 모두 폐기
- **계정 잠금**: 5회 연속 로그인 실패 시 계정 잠금 (status = LOCKED)
- **비밀번호 정책**: 영문/숫자/특수문자 포함, 6~12자

#### 인증 사용 예시

```java
// Authorization 헤더에 Bearer 토큰 포함
Authorization: Bearer eyJhbGciOiJIUzI1NiIs...

// 컨트롤러에서 인증된 사용자 정보 접근
@GetMapping("/me")
public ApiResponse<UserInfo> me(@AuthenticationPrincipal CustomUserDetails userDetails) {
    Long userId = userDetails.getUserId();
    String username = userDetails.getUsername();
    String roleCode = userDetails.getRoleCode();
    // ...
}

// 권한 체크
@PreAuthorize("hasRole('ADMIN')")
public void adminOnly() { }

@PreAuthorize("hasAuthority('USER_CREATE')")
public void createUser() { }
```

### 5.4 관리자 화면 구현 현황 (Phase 2)

| 메뉴 | 경로 | 컨트롤러 | 상태 |
|------|------|----------|------|
| 대시보드 | `/admin` | AdminController | ✅ |
| 역할 관리 | `/admin/roles` | AdminRoleController | ✅ |
| 사용자 관리 | `/admin/users` | AdminUserController | ✅ |
| 메뉴 관리 | `/admin/menus` | AdminMenuController | ✅ |
| 페이지 관리 | `/admin/pages` | AdminMenuController | ✅ |
| 게시판 관리 | `/admin/boards` | AdminBoardController | ✅ |
| **게시글 관리** | `/admin/posts`, `/admin/boards/{id}/posts` | AdminPostController | ✅ (파일 첨부 포함) |
| 공통 코드 | `/admin/common-codes` | AdminCommonCodeController | ✅ |
| 팝업 관리 | `/admin/popups` | AdminPopupController | ✅ |
| **파일 관리** | `/admin/files` | AdminFileController | ✅ (다중 업로드, refType/refId 필터) |
| 감사 로그 | `/admin/audit-logs` | AdminAuditLogController | ✅ |
| 설정 | `/admin/settings` | AdminController | 🔲 placeholder |

**추가 구현 사항**
- 게시글 관리: `board.useFile` 시 파일 첨부, 등록/수정 시 batch 업로드, maxFileCount 제한
- 파일 관리: multiple + batch API로 다중 업로드 지원
- XSS 대응: `data-*` + `onclick` 패턴, `credentials: 'same-origin'` 공통 적용

### 5.5 사용자단 API (Phase 3) — 완료

인증 없이 호출 가능한 공개 API (`/api/v1/public/**`):

| 구분 | API | 설명 |
|------|-----|------|
| Public Site | `GET /api/v1/public/menus` | 노출 메뉴 |
| | `GET /api/v1/public/pages/{pageCode}` | 정적 페이지 |
| | `GET /api/v1/public/popups` | 노출 팝업 |
| | `GET /api/v1/public/common-codes/{groupCode}` | 공통코드 |
| Public Board | `GET /api/v1/public/board-groups` | 게시판 그룹 |
| | `GET /api/v1/public/boards`, `boards/{id}/posts` 등 | 게시판·게시글·댓글 조회 |

상세: [Doc/api.md](Doc/api.md) 공개 API 섹션, [all_project.md](all_project.md) 섹션 16

---

## 6. 설치 가이드

### 6.1 설치 마법사

CMS는 웹 기반 설치 마법사를 제공합니다.
- **UI 구현**: Thymeleaf (백엔드 프로젝트에 포함, 서버 사이드 렌더링)

**설치 단계:**

#### Step 1: DB 연결 정보 입력
```
- Host: localhost
- Port: 3306
- Database: cms_db
- Username: cms_user
- Password: ********
```
연결 테스트 버튼으로 DB 연결을 확인합니다.

#### Step 2: 테이블 생성
스키마가 자동으로 실행되어 필요한 테이블이 생성됩니다.

#### Step 3: 관리자 계정 생성
```
- Username: admin
- Password: ********
- Email: admin@example.com
```

#### Step 4: 기본 설정
```
- 사이트명: My CMS
- 파일 저장 경로: C:/cms/files
```

#### Step 5: 완료
설치가 완료되면 로그인 페이지로 이동합니다.

### 6.2 수동 설치

설치 마법사를 사용하지 않고 수동으로 설치하려면:

1. **데이터베이스 생성**
```sql
CREATE DATABASE cms_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'cms_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON cms_db.* TO 'cms_user'@'localhost';
FLUSH PRIVILEGES;
```

2. **스키마 실행**
```bash
mysql -u cms_user -p cms_db < src/main/resources/schema/schema-mariadb.sql
```

3. **설정 파일 생성**
`cms-config.yml` 파일을 프로젝트 루트에 생성:
```yaml
cms:
  installed: true
  database:
    host: localhost
    port: 3306
    name: cms_db
    username: cms_user
    password: your_password
  site:
    name: My CMS
  file:
    upload-path: C:/cms/files
```

4. **애플리케이션 실행**
```bash
./gradlew bootRun
```

### 6.3 재설치

재설치가 필요한 경우:

1. `cms-config.yml`에서 `installed: false`로 변경
2. 애플리케이션 재시작
3. `/install` 페이지에서 재설치 진행

> ⚠️ **주의**: 재설치 시 기존 데이터가 삭제될 수 있습니다.

---

## 7. 설정

### 7.1 application.yml 기본 구조

```yaml
server:
  port: 8082

spring:
  profiles:
    active: dev

# MyBatis 설정
mybatis:
  mapper-locations: classpath:mapper/**/*.xml
  type-aliases-package: com.nt.cms
  configuration:
    map-underscore-to-camel-case: true
```

### 7.2 CMS 설정 (cms-config.yml)

설치 완료 후 생성되는 설정 파일:

```yaml
cms:
  # 설치 상태
  installed: true
  
  # 데이터베이스 설정
  database:
    host: localhost
    port: 3306
    name: cms_db
    username: cms_user
    password: encrypted_password
  
  # 사이트 설정
  site:
    name: My CMS
  
  # JWT 설정
  jwt:
    secret: your-secret-key
    access-token-validity: 3600      # 1시간 (초)
    refresh-token-validity: 1209600  # 14일 (초)
  
  # 파일 설정
  file:
    upload-path: C:/cms/files
    max-size: 3145728                # 3MB (바이트)
    allowed-extensions:
      image: jpg,jpeg,png,gif,webp
      document: pdf,doc,docx,xls,xlsx,hwp
  
  # 로깅 설정 (위치·보관기간 설정 가능)
  logging:
    file:
      path: logs/cms.log              # 로그 파일 위치
    retention-days: 30                # 로그 보관 기간 (일)
```

### 7.3 환경별 설정

| 프로파일 | 파일 | 용도 |
|---------|------|------|
| dev | application-dev.yml | 개발 환경 |
| prod | application-prod.yml | 운영 환경 |
| test | application-test.yml | 테스트 환경 |

```bash
# 개발 환경 실행
./gradlew bootRun --args='--spring.profiles.active=dev'

# 운영 환경 실행
./gradlew bootRun --args='--spring.profiles.active=prod'
```

---

## 8. API 명세

### 8.1 기본 정보

- **Base URL**: `http://localhost:8082/api/v1`
- **Content-Type**: `application/json`
- **인증**: Bearer Token (JWT) — 공개 API는 인증 불필요
- **API 문서**: Swagger/OpenAPI (`/swagger-ui.html`)

### 8.2 문서 및 도구

| 문서 | 설명 |
|------|------|
| [Doc/api.md](Doc/api.md) | 전체 API 명세 (인증, 공개 API, 게시판 등) |
| [Doc/user_map.md](Doc/user_map.md) | 사용자 사이트 API·스타일 가이드 (템플릿, CSS 변수, 비회원 권한) |
| [Doc/PUBLIC_POST_CREATE_IMPL_EXAMPLE.md](Doc/PUBLIC_POST_CREATE_IMPL_EXAMPLE.md) | 익명 게시글 작성 API 구현 예시 |
| [Doc/DB_ERD.md](Doc/DB_ERD.md) | DB ERD (Mermaid 다이어그램) |
| [Doc/ARCHITECTURE.md](Doc/ARCHITECTURE.md) | 아키텍처 (계층, 패키지, 요청 흐름) |
| Doc/postman/ | Postman Collection (CMS_Core_API.postman_collection.json) |

### 8.3 응답 포맷

#### 성공 응답
```json
{
  "success": true,
  "data": {
    "id": 1,
    "username": "admin"
  },
  "error": null
}
```

#### 에러 응답
```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "USER_NOT_FOUND",
    "message": "사용자를 찾을 수 없습니다."
  }
}
```

#### 페이징 응답
```json
{
  "success": true,
  "data": {
    "content": [ ],
    "page": 1,
    "size": 10,
    "totalElements": 100,
    "totalPages": 10
  },
  "error": null
}
```

#### 컨트롤러에서의 사용 예시

```java
import com.nt.cms.common.response.ApiResponse;
import com.nt.cms.common.response.PageResponse;
import com.nt.cms.common.exception.BusinessException;
import com.nt.cms.common.exception.ErrorCode;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    // 성공 응답 (데이터 포함)
    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getUser(@PathVariable Long id) {
        UserResponse user = userService.findById(id);
        return ApiResponse.success(user);
    }

    // 성공 응답 (데이터 없음)
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return ApiResponse.success();
    }

    // 페이징 응답
    @GetMapping
    public ApiResponse<PageResponse<UserResponse>> getUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<UserResponse> users = userService.findAll(page, size);
        return ApiResponse.success(users);
    }

    // 비즈니스 예외 발생 시 (GlobalExceptionHandler가 자동 처리)
    // throw new BusinessException(ErrorCode.USER_NOT_FOUND);
}
```

### 8.4 주요 API 엔드포인트

#### 인증 API

| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/api/v1/auth/login` | 로그인 |
| POST | `/api/v1/auth/logout` | 로그아웃 |
| POST | `/api/v1/auth/refresh` | 토큰 갱신 |

#### 사용자 API

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/api/v1/users` | 사용자 목록 조회 |
| GET | `/api/v1/users/{id}` | 사용자 상세 조회 |
| POST | `/api/v1/users` | 사용자 생성 |
| PUT | `/api/v1/users/{id}` | 사용자 수정 |
| DELETE | `/api/v1/users/{id}` | 사용자 삭제 |

#### 역할 API

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/api/v1/roles` | 역할 목록 조회 |
| GET | `/api/v1/roles/{id}` | 역할 상세 조회 |
| POST | `/api/v1/roles` | 역할 생성 |
| PUT | `/api/v1/roles/{id}` | 역할 수정 |
| DELETE | `/api/v1/roles/{id}` | 역할 삭제 |

#### 게시판 API

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/api/v1/boards` | 게시판 목록 조회 |
| GET | `/api/v1/boards/{code}/posts` | 게시글 목록 조회 |
| GET | `/api/v1/boards/{code}/posts/{id}` | 게시글 상세 조회 |
| POST | `/api/v1/boards/{code}/posts` | 게시글 작성 |
| PUT | `/api/v1/boards/{code}/posts/{id}` | 게시글 수정 |
| DELETE | `/api/v1/boards/{code}/posts/{id}` | 게시글 삭제 |

#### 파일 API

| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/api/v1/files/upload` | 단일 파일 업로드 |
| POST | `/api/v1/files/upload/batch` | 다중 파일 업로드 |
| GET | `/api/v1/files/{id}/download` | 파일 다운로드 |
| GET | `/api/v1/files/{id}` | 파일 정보 조회 |
| GET | `/api/v1/files?refType=&refId=` | 참조별 파일 목록 조회 |
| DELETE | `/api/v1/files/{id}` | 파일 삭제 |

#### 감사 로그 API

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/api/v1/audit-logs` | 감사 로그 목록 조회 (페이징, 검색) |
| GET | `/api/v1/audit-logs/{id}` | 감사 로그 상세 조회 |

---

## 9. 인증 및 권한

### 9.1 JWT 인증

#### 토큰 구조
- **Access Token**: 1시간 유효, API 인증에 사용
- **Refresh Token**: 14일 유효, Access Token 갱신에 사용

#### 멀티 디바이스
- **정책**: 불허. 새 로그인 시 기존 Refresh Token 무효화 (단일 디바이스만 유효)

#### 인증 헤더
```
Authorization: Bearer {access_token}
```

#### 로그인 요청
```json
POST /api/v1/auth/login
{
  "username": "admin",
  "password": "password123"
}
```

#### 로그인 응답
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIs...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
    "expiresIn": 3600
  },
  "error": null
}
```

### 9.2 RBAC 권한 시스템

#### 권한 구조
```
Role (역할)
  └── Permission (권한)
       └── Action (행위)
```

#### 기본 역할
| 역할 | 코드 | 설명 |
|------|------|------|
| 관리자 | ADMIN | 모든 권한 보유 |
| 매니저 | MANAGER | 일부 관리 권한 |
| 사용자 | USER | 기본 권한 |

#### 기본 권한 코드
```
USER_CREATE, USER_READ, USER_UPDATE, USER_DELETE
ROLE_CREATE, ROLE_READ, ROLE_UPDATE, ROLE_DELETE
BOARD_CREATE, BOARD_READ, BOARD_UPDATE, BOARD_DELETE
POST_CREATE, POST_READ, POST_UPDATE, POST_DELETE
COMMENT_CREATE, COMMENT_READ, COMMENT_UPDATE, COMMENT_DELETE
FILE_CREATE, FILE_READ, FILE_DELETE
AUDIT_READ
```

#### 권한 체크
```java
@PreAuthorize("hasAuthority('USER_CREATE')")
public ApiResponse<UserResponse> createUser(...) { }
```

#### 파일 구조

```
com.nt.cms.role/
├── controller/
│   ├── RoleController.java           # 역할 REST API
│   ├── PermissionController.java     # 권한 REST API
│   └── AdminRoleController.java      # 관리자 웹 페이지
├── service/
│   ├── RoleService.java              # 역할 서비스 인터페이스
│   ├── DefaultRoleService.java       # 역할 서비스 구현체
│   ├── PermissionService.java        # 권한 서비스 인터페이스
│   └── DefaultPermissionService.java # 권한 서비스 구현체
├── dto/
│   ├── RoleCreateRequest.java        # 역할 생성 요청
│   ├── RoleUpdateRequest.java        # 역할 수정 요청
│   ├── RoleResponse.java             # 역할 응답
│   ├── PermissionCreateRequest.java  # 권한 생성 요청
│   ├── PermissionUpdateRequest.java  # 권한 수정 요청
│   └── PermissionResponse.java       # 권한 응답
├── vo/
│   ├── RoleVO.java                   # 역할 VO
│   └── PermissionVO.java             # 권한 VO
└── mapper/
    ├── RoleMapper.java               # 역할 Mapper 인터페이스
    └── PermissionMapper.java         # 권한 Mapper 인터페이스
```

#### REST API 엔드포인트

**역할 API**

| Method | URL | 설명 | 권한 |
|--------|-----|------|------|
| GET | `/api/v1/roles` | 역할 목록 조회 | ROLE_READ |
| GET | `/api/v1/roles/{id}` | 역할 상세 조회 | ROLE_READ |
| POST | `/api/v1/roles` | 역할 생성 | ROLE_CREATE |
| PUT | `/api/v1/roles/{id}` | 역할 수정 | ROLE_UPDATE |
| DELETE | `/api/v1/roles/{id}` | 역할 삭제 | ROLE_DELETE |
| GET | `/api/v1/roles/check/code` | 역할 코드 중복 확인 | - |

**권한 API**

| Method | URL | 설명 | 권한 |
|--------|-----|------|------|
| GET | `/api/v1/permissions` | 권한 목록 조회 | ROLE_READ |
| GET | `/api/v1/permissions/{id}` | 권한 상세 조회 | ROLE_READ |
| GET | `/api/v1/permissions/role/{roleId}` | 역할별 권한 목록 | ROLE_READ |
| POST | `/api/v1/permissions` | 권한 생성 | ROLE_CREATE |
| PUT | `/api/v1/permissions/{id}` | 권한 수정 | ROLE_UPDATE |
| DELETE | `/api/v1/permissions/{id}` | 권한 삭제 | ROLE_DELETE |
| GET | `/api/v1/permissions/check/code` | 권한 코드 중복 확인 | - |

#### 관리자 웹 페이지

| URL | 설명 |
|-----|------|
| `/admin/roles` | 역할 목록 |
| `/admin/roles/{id}` | 역할 상세 |
| `/admin/roles/new` | 역할 등록 |
| `/admin/roles/{id}/edit` | 역할 수정 |

#### API 사용 예시

**역할 생성**
```bash
POST /api/v1/roles
Authorization: Bearer {access_token}
Content-Type: application/json

{
  "roleCode": "EDITOR",
  "roleName": "편집자",
  "description": "게시글 편집 권한",
  "permissionIds": [1, 2, 3]
}
```

**역할 수정 (권한 포함)**
```bash
PUT /api/v1/roles/1
Authorization: Bearer {access_token}
Content-Type: application/json

{
  "roleName": "수정된 역할명",
  "description": "수정된 설명",
  "permissionIds": [1, 2, 3, 4, 5]
}
```

### 9.3 로그인 보안

| 항목 | 값 |
|------|-----|
| 로그인 실패 제한 | 5회 이상 실패 시 계정 잠금 (status = LOCKED) |
| 비밀번호 정책 | 영문/숫자/특수문자 포함, 최소 6자 최대 12자 |

---

## 10. 회원가입 및 이메일

| 항목 | 값 |
|------|-----|
| 회원가입 | 누구나 가능 (공개) |
| 회원가입 이메일 인증 | 불필요 |
| 비밀번호 찾기 | 비밀번호 재설정 링크 발송 → 링크 클릭 후 새 비밀번호 설정 |
| SMTP 설정 | 설치 마법사에 미포함 (application.yml 등 별도 설정) |

---

## 11. 핵심 기능

### 11.1 사용자 관리

#### 기능 목록

| 기능 | 설명 |
|------|------|
| 사용자 목록 조회 | 검색(아이디/이름/이메일), 상태, 역할별 필터링 + 페이징 |
| 사용자 상세 조회 | ID로 사용자 정보 조회 |
| 사용자 생성 | 관리자가 새 사용자 생성 |
| 사용자 수정 | 이름, 이메일, 역할, 상태 수정 |
| 사용자 삭제 | Soft Delete (자기 자신 삭제 불가) |
| 회원가입 | 공개 회원가입 (기본 역할: USER) |
| 비밀번호 변경 | 현재 비밀번호 확인 후 변경 |
| 비밀번호 초기화 | 관리자가 사용자 비밀번호 초기화 |
| 계정 잠금/해제 | 계정 상태 변경 (ACTIVE ↔ LOCKED) |
| 중복 확인 | 아이디/이메일 중복 여부 확인 |

#### 파일 구조

```
com.nt.cms.user/
├── controller/
│   ├── UserController.java        # REST API 컨트롤러
│   └── RegisterController.java    # 회원가입 API 컨트롤러
├── service/
│   ├── UserService.java           # 서비스 인터페이스
│   └── DefaultUserService.java    # 서비스 구현체
├── dto/
│   ├── UserCreateRequest.java     # 사용자 생성 요청
│   ├── UserUpdateRequest.java     # 사용자 수정 요청
│   ├── UserResponse.java          # 사용자 응답
│   ├── UserSearchRequest.java     # 검색 조건
│   ├── RegisterRequest.java       # 회원가입 요청
│   └── PasswordChangeRequest.java # 비밀번호 변경 요청
├── vo/
│   └── UserVO.java                # 사용자 VO
└── mapper/
    └── UserMapper.java            # Mapper 인터페이스

resources/mapper/user/
└── UserMapper.xml                  # MyBatis XML Mapper

templates/admin/user/
├── list.html                       # 사용자 목록 페이지
├── detail.html                     # 사용자 상세 페이지
└── form.html                       # 사용자 등록/수정 폼
```

#### REST API 엔드포인트

| Method | URL | 설명 | 권한 |
|--------|-----|------|------|
| GET | `/api/v1/users` | 사용자 목록 조회 | USER_READ |
| GET | `/api/v1/users/{id}` | 사용자 상세 조회 | USER_READ |
| POST | `/api/v1/users` | 사용자 생성 | USER_CREATE |
| PUT | `/api/v1/users/{id}` | 사용자 수정 | USER_UPDATE |
| DELETE | `/api/v1/users/{id}` | 사용자 삭제 | USER_DELETE |
| PUT | `/api/v1/users/{id}/password/reset` | 비밀번호 초기화 | USER_UPDATE |
| PUT | `/api/v1/users/{id}/lock` | 계정 잠금 | USER_UPDATE |
| PUT | `/api/v1/users/{id}/unlock` | 계정 잠금 해제 | USER_UPDATE |
| GET | `/api/v1/users/check/username` | 아이디 중복 확인 | 공개 |
| GET | `/api/v1/users/check/email` | 이메일 중복 확인 | 공개 |
| PUT | `/api/v1/users/me` | 내 정보 수정 | 인증 필요 |
| PUT | `/api/v1/users/me/password` | 내 비밀번호 변경 | 인증 필요 |
| POST | `/api/v1/auth/register` | 회원가입 | 공개 |

#### 관리자 웹 페이지

| URL | 설명 |
|-----|------|
| `/admin/users` | 사용자 목록 |
| `/admin/users/{id}` | 사용자 상세 |
| `/admin/users/new` | 사용자 등록 |
| `/admin/users/{id}/edit` | 사용자 수정 |

#### API 사용 예시

**사용자 목록 조회**
```bash
GET /api/v1/users?keyword=test&status=ACTIVE&page=1&size=10
Authorization: Bearer {access_token}
```

**회원가입**
```bash
POST /api/v1/auth/register
Content-Type: application/json

{
  "username": "newuser",
  "password": "Test123!",
  "passwordConfirm": "Test123!",
  "name": "신규 사용자",
  "email": "newuser@example.com"
}
```

**사용자 생성 (관리자)**
```bash
POST /api/v1/users
Authorization: Bearer {access_token}
Content-Type: application/json

{
  "username": "admin2",
  "password": "Admin123!",
  "passwordConfirm": "Admin123!",
  "name": "관리자2",
  "email": "admin2@example.com",
  "roleId": 1
}
```

**비밀번호 변경**
```bash
PUT /api/v1/users/me/password
Authorization: Bearer {access_token}
Content-Type: application/json

{
  "currentPassword": "OldPass123!",
  "newPassword": "NewPass123!",
  "newPasswordConfirm": "NewPass123!"
}
```

### 11.2 게시판

#### 게시판 기능
- 다중 게시판 지원
- 게시판 그룹화
- 게시판별 역할별 권한 설정 (읽기/쓰기/수정/삭제)
- **비회원(ANONYMOUS) 권한**: 관리자에서 비회원 행에 읽기·쓰기 체크 시, 로그인 없이 게시글 조회·작성 가능
- 댓글/파일 첨부 ON/OFF

#### 게시글 기능
- CRUD
- 조회수
- 공지사항
- 비밀글
- 파일 첨부 (게시판 설정 `useFile` 시, batch 업로드, maxFileCount 제한)
- **최신글 API**: 여러 게시판의 최신글을 한 번에 조회 (`GET /api/v1/public/posts/latest`)

#### 댓글 기능
- 댓글 작성/수정/삭제
- 대댓글 지원 (1단계까지)

#### 게시글 검색
- 제목/내용 검색
- 작성자 검색
- 기간별 검색

### 11.3 사이트 메뉴/페이지 관리

#### 사이트 메뉴 (site_menu)
- 계층형 메뉴 구조 (parent_id, depth)
- 메뉴 유형: PAGE(정적 페이지), BOARD(게시판), LINK(외부 링크)
- 게시판/페이지 연결 (board_id, page_id)
- 노출 제어 (is_visible, start_at, end_at)
- 로그인 필요 여부 설정
- SEO 메타 정보 (seo_title, seo_description)
- 정렬 순서 및 아이콘 설정

#### 정적 페이지 (site_page)
- 고유 코드 기반 페이지 관리
- HTML 콘텐츠 (LONGTEXT) — 관리자 폼에서 Toast UI Editor(WYSIWYG) 사용
- 게시 여부 설정
- 템플릿 코드 (template_code): 사용자 사이트 렌더링 시 적용할 템플릿 (default, page_01 등)

### 11.4 파일 관리

- 파일 업로드/다운로드 (단일/다중 batch 업로드 지원)
- 허용 확장자: 이미지(jpg, png, gif, webp), 문서(pdf, doc, docx, xls, xlsx, hwp)
- 최대 파일 크기: 3MB
- 다형적 연결 (ref_type, ref_id)
- 관리자 화면: `/admin/files` - refType/refId 필터, 다중 업로드 UI

### 11.5 감사 로그

- 수정/삭제 작업 자동 기록
- before/after 데이터 JSON 저장
- 사용자, 대상, 시간 기록

### 11.6 팝업 관리 (site_popup)

- 사용자 사이트 팝업 관리
- 팝업 유형: LAYER, WINDOW, MODAL
- 노출 위치: MAIN, SUB, ALL
- 디바이스: PC, MOBILE, ALL
- 기간 제어 (start_at, end_at)
- 오늘 하루 보지 않기 (쿠키 기반)
- 로그인 사용자 전용 옵션

### 11.7 게시판 템플릿 세팅

사용자 사이트(`/site/**`)에서 게시판 목록·상세 페이지의 UI를 게시판별로 다르게 적용할 수 있습니다.

#### 템플릿 설정 방법

1. **관리자 > 게시판 관리** (`/admin/boards`) 접속
2. 게시판 **등록** 또는 **수정** 화면에서 **"사용자 사이트 템플릿"** 선택
3. 저장

| 옵션 | 코드 | 설명 |
|------|------|------|
| 테이블형 (default) | `default` | 테이블 형태의 목록, 기본 상세 레이아웃 |
| 카드형 (card) | `card` | 카드 형태의 목록, 카드형 상세 레이아웃 |

#### 기본 제공 템플릿

| 템플릿 코드 | 목록 템플릿 | 상세 템플릿 | 폼 템플릿 |
|-------------|-------------|-------------|-----------|
| `default` | `board-list.html` | `board-post.html` | `post-form.html` |
| `card` | `board-list.html` | `board-post.html` | `post-form.html` |
| `news` | `board-list.html` | `board-post.html` | `post-form.html` |

#### 템플릿 파일 위치

```
src/main/resources/templates/site/board/
├── list.html               # 공통 목록 (fallback)
├── post-detail.html        # 공통 상세 (fallback)
├── post-form.html          # 공통 폼 (fallback)
├── default/                # 템플릿 코드: default
│   ├── board-list.html     # 목록: 테이블형
│   ├── board-post.html     # 상세: 기본형
│   └── post-form.html      # 글쓰기/수정 폼
├── card/                   # 템플릿 코드: card
│   ├── board-list.html     # 목록: 카드형
│   ├── board-post.html     # 상세: 카드형
│   └── post-form.html
└── news/                   # 템플릿 코드: news
    ├── board-list.html     # 목록 (default 위임)
    ├── board-post.html     # 상세 (default 위임)
    └── post-form.html
```

**자동 템플릿 등록**: `templates/site/board/` 하위에 폴더와 HTML 파일을 추가하면, 별도 코드 수정 없이 관리자 게시판 등록/수정 화면의 "사용자 사이트 템플릿" 셀렉트 박스에 자동으로 옵션이 추가됩니다. 서버 재시작 후 반영됩니다.

#### 새 템플릿 추가 방법

1. `templates/site/board/{코드}/` 폴더 생성
2. `board-list.html` (목록), `board-post.html` (상세), `post-form.html` (폼) 파일 생성
3. Thymeleaf 문법(`th:text`, `th:utext` 등) 사용, 기존 default/card 폴더 참고
4. 관리자 > 게시판 등록/수정 시 새 템플릿이 셀렉트에 자동 노출됨

### 11.8 페이지 템플릿 세팅

사용자 사이트(`/site/page/{pageCode}`)에서 정적 페이지의 UI를 페이지별로 다르게 적용할 수 있습니다.

#### 템플릿 설정 방법

1. **관리자 > 페이지 관리** (`/admin/pages`) 접속
2. 페이지 **등록** 또는 **수정** 화면에서 **"템플릿"** 선택
3. 저장

#### 템플릿 파일 위치

```
src/main/resources/templates/site/page/
├── page.html               # fallback (미지정/미존재 시)
├── default/                # 템플릿 코드: default
│   └── page.html           # 기본 페이지 템플릿
├── page_01/                # 템플릿 코드: page_01
│   └── page.html
└── page_02/                # 템플릿 코드: page_02
    └── page.html
```

**자동 템플릿 등록**: `templates/site/page/` 하위에 폴더와 `page.html`을 추가하면 관리자 페이지 폼의 템플릿 셀렉트에 자동으로 옵션이 추가됩니다. 서버 재시작 후 반영됩니다.

#### API

| Method | URL | 설명 |
|--------|-----|------|
| GET | `/api/v1/pages/templates` | 사용 가능한 페이지 템플릿 코드 목록 (권한: PAGE_READ) |

**목록 템플릿 플레이스홀더**

| 플레이스홀더 | 설명 |
|--------------|------|
| `{{boardName}}` | 게시판명 |
| `{{listHtml}}` | 게시글 목록 HTML (내부에서 제목·작성자·조회·작성일 등 렌더링) |
| `{{pagerHtml}}` | 페이징 HTML |

**상세 템플릿 플레이스홀더**

| 플레이스홀더 | 설명 |
|--------------|------|
| `{{listUrl}}` | 목록으로 돌아가기 URL |
| `{{title}}` | 게시글 제목 |
| `{{writer}}` | 작성자 |
| `{{createdAt}}` | 작성일 |
| `{{viewCount}}` | 조회수 |
| `{{content}}` | 본문 HTML (XSS 정제됨) |
| `{{filesHtml}}` | 첨부파일 목록 HTML |
| `{{actionButtonsHtml}}` | 수정/삭제 버튼 HTML (권한에 따라) |

**폼 템플릿 플레이스홀더** (`post-form.html`)

| 플레이스홀더 | 설명 |
|--------------|------|
| `{{formTitle}}` | 글쓰기 / 글 수정 |
| `{{listUrl}}` | 목록 URL |
| `{{boardId}}` | 게시판 ID |
| `{{postId}}` | 게시글 ID (신규 시 빈 문자열) |
| `{{title}}` | 제목 (수정 시 기존 값) |
| `{{content}}` | 내용 (수정 시 기존 값) |
| `{{fileUploadHtml}}` | 첨부파일 업로드 영역 (board.useFile=true 시) |
| `{{secretField}}` | 비밀글 체크박스 HTML (useSecret=false 시 빈 문자열) |
| `{{submitLabel}}` | 등록 / 수정 |

### 11.9 사용자 사이트 디자인 수정 가이드

사용자 사이트(`/site/**`)의 디자인은 CSS와 정적 HTML 템플릿으로 구성되어 있으며, 프로젝트별 커스터마이징이 가능합니다.

#### 주요 파일 위치

| 구분 | 경로 |
|------|------|
| 메인 | `src/main/resources/templates/site/index.html` (Thymeleaf) |
| 스타일 | `src/main/resources/static/site/css/site.css` |
| 사이트 설정 | Thymeleaf 서버 렌더링 (메뉴·팝업·최신글·게시판 링크), layout 인라인 스크립트 (모바일 메뉴·팝업 닫기) |
| 게시판 템플릿 | `templates/site/board/` (폴더별 board-list.html, board-post.html, post-form.html) |
| 페이지 템플릿 | `templates/site/page/` (폴더별 page.html, fallback: page.html) |

#### CSS 디자인 시스템 (React 동기화)

`site.css`는 React 프로젝트(`cms_user_react/src/index.css`)와 동일한 CSS 변수 기반 다크 테마 디자인 시스템을 사용합니다. `:root` 변수만 수정하면 전체 테마가 변경됩니다.

**색상 토큰**

| 변수명 | 기본값 | 설명 |
|--------|--------|------|
| --color-primary | #00d4ff | 주 강조 색상 (시안) |
| --color-primary-dark | #00a8cc | 호버/활성 상태 |
| --color-secondary | #8b5cf6 | 보조 강조 (보라) |
| --color-bg | #0a0a0f | 배경색 (다크 테마) |
| --color-bg-card | #1a1a24 | 카드 배경 |
| --color-text | #f1f5f9 | 기본 텍스트 |
| --color-text-muted | #94a3b8 | 보조 텍스트 |
| --color-border | #2a2a3a | 테두리 색상 |

**타이포그래피 & 레이아웃**

| 변수명 | 기본값 | 설명 |
|--------|--------|------|
| --font-sans | Inter, system | 기본 폰트 |
| --font-mono | JetBrains Mono | 코드 폰트 |
| --max-width | 1200px | 콘텐츠 최대 너비 |
| --spacing-1 ~ --spacing-16 | 0.25rem ~ 4rem | 여백 스케일 |
| --radius-sm/md/lg/xl/2xl | 0.25rem ~ 1.5rem | 모서리 둥글기 |

**테마 오버라이드 예시** (라이트 테마)
```css
/* site-custom.css */
:root {
    --color-primary: #2563eb;
    --color-bg: #ffffff;
    --color-bg-card: #f8fafc;
    --color-text: #1e293b;
    --color-border: #e2e8f0;
}
```

> 상세 가이드는 `Doc/user_map.md` 5절 참조.

#### 사용자 사이트 사용법 페이지

상단 우측 **"사용법"** 버튼 클릭 시 `/site/help` 로 이동합니다. 목차 클릭으로 해당 섹션으로 스크롤됩니다.

- 1. 사용자 사이트 개요
- 2. 메뉴 사용 방법
- 3. 페이지 타입 메뉴 사용
- 4. 게시판 템플릿 사용
- 5. 디자인·템플릿 커스터마이징

#### 404 에러 페이지

존재하지 않는 URL 접근 시 모두 `site/error` 템플릿으로 통합 표시됩니다. 상태 코드와 메시지를 함께 보여줍니다.

#### 주요 클래스명 규칙

| 접두어 | 용도 | 예시 |
|--------|------|------|
| `.site-header` | 헤더 영역 | `.site-header-inner`, `.site-logo`, `.site-nav-desktop` |
| `.site-main` | 메인 콘텐츠 영역 | `#site-main-content` |
| `.site-board` | 게시판 목록 | `.site-board-title`, `.site-board-table`, `.site-board-card` |
| `.site-post` | 게시글 상세 | `.site-post-head`, `.site-post-content`, `.site-post-files` |
| `.site-pager` | 페이징 | `.site-pager-prev`, `.site-pager-current`, `.site-pager-next` |

#### 커스터마이징 방법

1. **색상 변경**: `site.css`의 `:root` 변수 수정
2. **레이아웃 변경**: `site.css`에서 해당 `.site-*` 클래스 스타일 수정
3. **템플릿 구조 변경**: `site/templates/` 하위 HTML 파일 수정 (플레이스홀더 유지)
4. **새 템플릿 추가**: `board-list.html`, `board-post.html`, `post-form.html` 추가 후 게시판 설정에서 선택

> **참고**: 사용자 사이트는 Thymeleaf 서버 렌더링(SSR) 방식으로 동작합니다. 메뉴·팝업·최신글·게시판 링크는 서버에서 렌더링되며 JS 없이 콘텐츠 확인이 가능합니다.

---

## 12. 캐싱

| 항목 | 값 |
|------|-----|
| 캐시 솔루션 | Redis (선택) |
| Redis 미사용 시 | 인메모리 캐시로 대체 (동작 유지) |
| 캐싱 대상 | 세션, 권한 정보 등 |

---

## 13. CORS

- **설정 필요**: 외부 프론트엔드에서 API 호출 시 대비
- **비고**: 추후 이 CMS 기반으로 사용자 페이지(프론트엔드) 개발 예정이므로 CORS 설정 포함

---

## 14. 로깅

| 항목 | 값 |
|------|-----|
| 로그 파일 위치 | 설정 가능 (cms-config.yml 등) |
| 로그 보관 기간 | 설정 가능 (일 단위 등) |

---

## 15. 데이터베이스

### 15.1 ERD 요약

```
┌─────────┐     ┌────────────────┐     ┌────────────┐
│  role   │────<│ role_permission │>────│ permission │
└─────────┘     └────────────────┘     └────────────┘
     │
     │ 1:N
     ▼
┌─────────┐     ┌───────────────────┐
│  user   │────<│ jwt_refresh_token │
└─────────┘     └───────────────────┘
     │
     │
     ▼
┌─────────────┐     ┌─────────┐     ┌──────────────────┐
│ board_group │────<│  board  │────<│ board_permission │
└─────────────┘     └─────────┘     └──────────────────┘
                         │
                         │ 1:N
                         ▼
                    ┌────────────┐     ┌───────────────┐
                    │ board_post │────<│ board_comment │
                    └────────────┘     └───────────────┘
                         │
                         │
                         ▼
                    ┌─────────┐
                    │  file   │ (ref_type, ref_id로 연결)
                    └─────────┘

┌─────────────┐
│  audit_log  │ (독립 테이블)
└─────────────┘

┌──────────────────────┐     ┌───────────────┐
│ common_code_group    │────<│ common_code   │ (parent_id로 계층)
└──────────────────────┘     └───────────────┘

┌─────────────┐
│ site_popup  │ (독립 테이블, 사용자 사이트 팝업)
└─────────────┘
```

### 15.2 공통 컬럼

모든 테이블은 다음 공통 컬럼을 포함:

| 컬럼 | 타입 | 설명 |
|------|------|------|
| id | BIGINT | Primary Key |
| created_at | DATETIME | 생성 일시 |
| created_by | BIGINT | 생성자 ID |
| updated_at | DATETIME | 수정 일시 |
| updated_by | BIGINT | 수정자 ID |
| deleted | TINYINT(1) | Soft Delete 플래그 |

### 15.3 Soft Delete

- 모든 테이블은 Soft Delete 사용
- `deleted = 0`: 활성 데이터
- `deleted = 1`: 삭제된 데이터
- 모든 조회 쿼리에 `WHERE deleted = 0` 조건 포함

---

## 16. 테스트

### 16.1 테스트 실행

```bash
# 전체 테스트
./gradlew test

# 특정 테스트 클래스
./gradlew test --tests "com.nt.cms.user.service.UserServiceTest"

# 테스트 리포트 생성
./gradlew test jacocoTestReport
```

### 16.2 테스트 환경

- **테스트 DB**: H2 In-Memory
- **프로파일**: test
- **설정 파일**: application-test.yml

### 16.3 테스트 구조

```
src/test/java/com/nt/cms/
├── common/
│   └── response/
├── auth/
│   ├── controller/
│   └── service/
├── user/
│   ├── controller/
│   ├── service/
│   └── mapper/
├── board/
└── ...
```

---

## 17. 배포

### 17.1 JAR 빌드

```bash
./gradlew clean build -x test
```

빌드된 JAR 파일: `build/libs/cms-core-{version}.jar`

### 17.2 실행

```bash
java -jar cms-core-{version}.jar --spring.profiles.active=prod
```

### 17.3 시스템 서비스 등록 (Linux)

```bash
# /etc/systemd/system/cms-core.service
[Unit]
Description=CMS Core Platform
After=network.target

[Service]
Type=simple
User=cms
ExecStart=/usr/bin/java -jar /opt/cms/cms-core.jar --spring.profiles.active=prod
Restart=always

[Install]
WantedBy=multi-user.target
```

```bash
sudo systemctl enable cms-core
sudo systemctl start cms-core
```

---

## 18. 라이선스

이 프로젝트의 라이선스는 추후 결정됩니다.

---

## 19. 구현 이력

사용자 사이트(`/site/**`) 관련 주요 구현 작업 내역입니다. Phase 2/3 이후 게시판 템플릿, 페이징, 첨부파일, 동적 템플릿 로딩 등이 순차적으로 추가되었습니다.

### 19.1 Phase 2/3: 사용자 사이트 (Page/Submit 방식)

| 구분 | 내용 |
|------|------|
| **SiteViewController** | Thymeleaf GET 처리: /site/, /site/help, /site/page/{pageCode}, /site/board/{boardCode}, 게시글 상세·글쓰기·수정 폼 |
| **SiteFormController** | POST 처리: 게시글 작성·수정·삭제 (form 제출) |
| **SiteModelAttributeAdvice** | siteUser, siteMenus 모델 자동 주입 |
| **서버 렌더링** | 메뉴(SiteModelAttributeAdvice), 팝업(sitePopups), 최신글/게시판 링크(SiteViewController.index) |
| **XSS 대응** | Thymeleaf th:utext (동적 컨텐츠), REFACTORING_CHECKLIST.md 참조 |

**관련 파일**
- `src/main/java/.../site/controller/SiteViewController.java`
- `src/main/java/.../site/controller/SiteFormController.java`
- `src/main/java/.../site/config/SiteModelAttributeAdvice.java`
- `src/main/resources/templates/site/` (layout, index, help, page, board/list·post-detail·post-form)
- `src/main/resources/templates/site/layout.html` (인라인 스크립트: 모바일 메뉴, 팝업, 로그인필요 링크)

---

### 19.2 게시판 템플릿 시스템

| 구분 | 내용 |
|------|------|
| **DB** | `board` 테이블에 `template_code` 컬럼 추가 (MariaDB, H2, schema) |
| **VO/DTO** | `BoardVO`, `BoardResponse`, `BoardCreateRequest`, `BoardUpdateRequest`에 `templateCode` 필드 반영 |
| **BoardMapper.xml** | `template_code` SELECT/INSERT/UPDATE 포함 |
| **DefaultBoardService** | 생성·수정 시 `templateCode` 처리 (미지정 시 `default`) |
| **관리자 폼** | 게시판 등록/수정 화면에 "사용자 사이트 템플릿" select box 추가 |

**템플릿 파일 구조**
```
templates/site/board/
├── default/
│   ├── board-list.html      # 목록 템플릿 (테이블형)
│   ├── board-post.html      # 상세 템플릿
│   └── post-form.html       # 글쓰기/수정 폼
└── card/
    ├── board-list.html      # 목록 템플릿 (카드형)
    ├── board-post.html      # 상세 템플릿
    └── post-form.html
```

**템플릿 경로**
- 목록: `site/board/{templateCode}/board-list.html`
- 상세: `site/board/{templateCode}/board-post.html`
- 폼: `site/board/{templateCode}/post-form.html`

**관련 파일**
- `src/main/resources/schema/schema-mariadb.sql`, `schema-h2.sql`
- `src/main/resources/mapper/board/BoardMapper.xml`
- `src/main/java/.../board/` (VO, DTO, Service)
- `src/main/resources/templates/admin/board/form.html`

---

### 19.3 템플릿 동적 로딩 (폴더 기반)

| 구분 | 내용 |
|------|------|
| **API** | `GET /api/v1/boards/templates` - 사용 가능한 게시판 템플릿 코드 목록 반환 |
| **권한** | `BOARD_READ` 필요 |
| **BoardService** | `getAvailableTemplateCodes()` - `templates/site/board/*/*.html` 패턴으로 폴더 스캔, 하위 폴더명을 템플릿 코드로 추출 |
| **관리자 폼** | 페이지 로드 시 API 호출 후 select box option 동적 생성 |
| **fallback** | API 실패 또는 템플릿 없음 시 `default`, `card` 고정 option 표시 |

**추가 이유**: `templates/board/` 하위 폴더에 새 템플릿을 추가해도 관리자 화면에서 자동으로 선택 가능하도록 함.

**관련 파일**
- `src/main/java/.../board/controller/BoardController.java` (GET /api/v1/boards/templates)
- `src/main/java/.../board/service/DefaultBoardService.java` (getAvailableTemplateCodes)
- `src/main/resources/templates/admin/board/form.html` (fetch + option 동적 생성)

---

### 19.4 페이지 템플릿 관리

| 구분 | 내용 |
|------|------|
| **DB** | `site_page` 테이블에 `template_code VARCHAR(50) DEFAULT 'default'` 추가 |
| **VO/DTO** | `SitePageVO`, `SitePageResponse`, `SitePageCreateRequest`, `SitePageUpdateRequest`에 `templateCode` 반영 |
| **SitePageMapper.xml** | `template_code` SELECT/INSERT/UPDATE 포함 |
| **SitePageService** | `getAvailablePageTemplateCodes()`, `resolvePageView(templateCode)` 추가 |
| **API** | `GET /api/v1/pages/templates` - PAGE_READ 권한, templates/site/page/* 폴더 스캔 |
| **관리자 폼** | 페이지 등록/수정 화면에 템플릿 셀렉트 (API 호출 후 option 동적 생성) |
| **SiteViewController** | page()에서 page.templateCode로 `site/page/{code}/page` 해석, 미존재 시 `site/page` fallback |

**템플릿 파일 구조**
```
templates/site/page/
├── page.html           # fallback
├── default/page.html   # 기본 템플릿
├── page_01/page.html
└── page_02/page.html
```

**관련 파일**
- `src/main/resources/schema/migration/V003_add_template_code_to_site_page.sql`
- `src/main/java/.../menu/` (VO, DTO, Service, Controller)
- `src/main/resources/templates/admin/page/form.html`
- `src/main/java/.../site/controller/SiteViewController.java`
- `src/main/resources/templates/site/page/default/`, `page_01/`, `page_02/`

---

### 19.5 페이징 강화

| 구분 | 내용 |
|------|------|
| **buildPager()** | 이전/다음 외에 **페이지 번호 링크**(1, 2, 3...) 표시 |
| **표시 범위** | 현재 페이지 기준 ±2 페이지, 처음/끝 링크, 생략 구간(…) 표시 |
| **총 건수** | `(총 N건)` 표시 |
| **조건** | `totalPages <= 1`이면 페이지 링크 숨김, 빈 div만 반환 |

**site.css 스타일**
- `.site-pager-current` - 현재 페이지
- `.site-pager-ellipsis` - 생략 구간
- `.site-pager-prev`, `.site-pager-next`

**관련 파일**
- `src/main/resources/templates/site/board/*/` (Thymeleaf 템플릿)
- `src/main/resources/static/site/css/site.css`

---

### 19.6 게시글 상세 첨부파일 처리

| 구분 | 내용 |
|------|------|
| **PublicFileController** | 사용자 사이트용 공개 파일 API (인증 불필요) |
| **파일 목록** | `GET /api/v1/public/boards/{boardId}/posts/{postId}/files` |
| **다운로드** | `GET /api/v1/public/files/{id}/download?postId={postId}` - postId로 소속 검증 |
| **Thymeleaf** | 게시글 상세 시 파일 목록을 model.addAttribute("files")로 전달, 템플릿에서 렌더링 |
| **플레이스홀더** | 상세 템플릿 `{{filesHtml}}` 치환 |
| **유틸** | `buildFilesHtml()`, `formatFileSize()`, `escapeAttr()` |

**site.css 스타일**
- `.site-post-files` - 첨부파일 영역
- `.site-post-file-link` - 다운로드 링크

**관련 파일**
- `src/main/java/.../publicapi/controller/PublicFileController.java`
- `src/main/java/.../site/controller/SiteViewController.java` (postDetail)
- `src/main/resources/templates/site/board/*/board-post.html` ({{filesHtml}})
- `src/main/resources/static/site/css/site.css`

---

### 19.7 디자인 수정 가이드 및 문서화

| 구분 | 내용 |
|------|------|
| **README 11.7** | 게시판 템플릿 세팅 방법 (설정 방법, 파일 위치, 플레이스홀더, 새 템플릿 추가) |
| **README 11.8** | 페이지 템플릿 세팅 (site_page.template_code, templates/site/page/{code}/page.html) |
| **README 11.9** | 사용자 사이트 디자인 수정 가이드 (파일 위치, CSS 변수, 클래스 규칙, 커스터마이징 방법) |
| **CSS 변수** | `:root` - `--site-primary`, `--site-bg`, `--site-text`, `--site-text-muted` |
| **클래스 규칙** | `.site-header`, `.site-board`, `.site-post`, `.site-pager`, `.site-post-files` 등 |

**관련 파일**
- `README.md` (11.7, 11.8 섹션)

---

### 19.8 비회원 게시판 권한 (ANONYMOUS)

| 구분 | 내용 |
|------|------|
| **역할** | `ANONYMOUS`(비회원) - data-init.sql, AnonymousUserInitializer |
| **시스템 계정** | `anonymous` - 익명 글쓰기 시 writer_id로 사용 |
| **관리자 설정** | 게시판 등록/수정 시 역할별 권한에서 "비회원" 행에 읽기·쓰기 체크 |
| **PostService** | roleId=null → ANONYMOUS 역할로 권한 조회, userId=null → anonymous 계정 사용 |
| **구현 예시** | `Doc/PUBLIC_POST_CREATE_IMPL_EXAMPLE.md` - 익명 게시글 작성 API 구현 |

**관련 파일**
- `src/main/resources/schema/data-init.sql` (ANONYMOUS 역할)
- `src/main/java/.../common/config/AnonymousUserInitializer.java`
- `src/main/java/.../board/service/DefaultPostService.java` (resolveRoleId, resolveWriterId)
- `Doc/user_map.md` 6절

---

### 19.9 React 디자인 동기화

| 구분 | 내용 |
|------|------|
| **CSS 디자인 시스템** | React `index.css`와 동일한 CSS 변수 기반 다크 테마 |
| **index.html 개편** | Hero 섹션, Tech Stack, 기능 그리드, API Info, 최신글/게시판 바로가기 |
| **헤더/네비게이션** | 그라데이션 로고, hover 배경, 그라데이션 로그인 버튼, 모바일 토글 아이콘 |
| **게시판 템플릿** | 아이콘 헤더, 검색 폼, 번호 컬럼, 카드형 상세 |
| **최신글 API** | `GET /api/v1/public/posts/latest?boardIds=1,2&size=10` |
| **SiteViewController** | `index()`에서 `getLatestPosts`, `collectBoardLinks`로 최신글·게시판 링크 서버 렌더링 |

**관련 파일**
- `src/main/resources/static/site/css/site.css` (CSS 변수 디자인 시스템)
- `src/main/resources/static/site/index.html` (메인 페이지 레이아웃)
- `src/main/resources/templates/site/index.html` (최신글·게시판 링크 Thymeleaf)
- `src/main/resources/templates/site/board/*/` (board-list.html, board-post.html, post-form.html)

---

### 19.10 요약

| 작업 | 날짜(개념) | 주요 변경 |
|------|------------|-----------|
| Phase 2/3 | - | 사용자 사이트 SPA 라우팅, XSS 처리 |
| 게시판 템플릿 | - | `template_code`, 목록/상세 템플릿 분리 |
| 템플릿 폴더 구조 | - | `board/{code}/board-list.html`, `board-post.html`, `post-form.html` |
| 템플릿 동적 로딩 | - | GET /api/v1/boards/templates, 관리자 select 동적 생성 |
| 페이징 | - | 페이지 번호 링크, 총 건수 표시 |
| 첨부파일 | - | PublicFileController, `{{filesHtml}}`, buildFilesHtml |
| 루트 경로 | - | `/` → `/site/` 리다이렉트 (사용자 사이트 우선) |
| 404 에러 | - | 모든 에러 → site/error 템플릿 통합 |
| 사용법 페이지 | - | `/site/help` - 목차, 사용 가이드 |
| 비회원 권한 | - | ANONYMOUS 역할, 익명 게시글 작성 |
| React 동기화 | - | CSS 변수 다크 테마, index.html 개편, 최신글 API |
| 문서화 | - | user_map.md, PUBLIC_POST_CREATE_IMPL_EXAMPLE.md |

---

## 문의

- **이슈 리포트**: GitHub Issues
- **이메일**: support@example.com
