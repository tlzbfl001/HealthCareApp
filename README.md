# HealthCareApp

**HealthCareApp**은 Kotlin을 기반으로 한 의료/헬스케어 관련 애플리케이션입니다. Gradle 빌드 시스템과 `app` 폴더 구조로, 서버 API, 백엔드 서비스를 활용한 앱입니다.

---

##  주요 기능
- 사용자 데이터 관리
- 건강 데이터 분석 또는 기록 기능
- RESTful API 제공 (`/health`, `/patients`, `/records` 등)
- 인증 및 보안 (JWT, OAuth 등)
- DB 연동 (PostgreSQL, MySQL)

---

##  기술 스택
- 빌드/관리: **Gradle**
- 데이터베이스: sqlLite (in-memory), MySQL/PostgreSQL
- API 문서화: Swagger/OpenAPI

---

##  요구사항 & 실행 가이드
1. 클론:
   ```sh
   git clone https://github.com/yej431/HealthCareApp.git
   cd HealthCareApp
