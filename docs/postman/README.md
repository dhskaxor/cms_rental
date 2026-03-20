# Postman 컬렉션 안내

- `CMS_Core_API.postman_collection.json`: CMS 핵심 API 테스트 컬렉션
- `CMS_Core_Local.postman_environment.json`: 로컬 환경 변수(`baseUrl`, 토큰) 설정

## 사용 방법

1. Postman에 컬렉션/환경 파일을 Import
2. 환경을 `CMS Core - Local`로 선택
3. `인증(Auth) > 로그인` 실행으로 토큰 저장
4. 공개/관리 API 순서로 테스트 수행
