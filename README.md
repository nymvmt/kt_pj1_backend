# KT 프로젝트 백엔드

## 개발 환경 설정

### 1. 데이터베이스 설정
- PostgreSQL 15+ 필요
- 데이터베이스: `fullstack_db`
- 사용자: `kt_user`
- 비밀번호: `kt_password`
- 포트: `5433`

### 2. 애플리케이션 실행
```bash
./gradlew bootRun
```

### 3. 초기 데이터 설정 (개발 환경)
현재 `ddl-auto: update` 모드로 설정되어 있어 테이블은 자동으로 생성되지만 초기 데이터는 수동으로 입력해야 합니다.

```bash
# PostgreSQL에 연결
psql -h localhost -p 5433 -U kt_user -d fullstack_db

# 초기 데이터 실행
\i src/main/resources/init-dev-data.sql
```

### 4. API 테스트
- 서버: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html` (구현 시)

## 주요 기능

### 인증
- 사용자 회원가입/로그인
- 매니저 회원가입/로그인 (브랜드 정보 포함)

### 브랜드 관리
- 브랜드 목록 조회
- 브랜드 상세 정보
- 매니저별 브랜드 관리

### 상담
- 브랜드 상담 신청
- 상담 상태 관리

## 데이터베이스 스키마

### 주요 엔티티
- `User`: 일반 사용자
- `BrandManager`: 브랜드 매니저
- `Brand`: 브랜드 기본 정보
- `BrandDetail`: 브랜드 상세 정보
- `BrandCategory`: 브랜드 카테고리
- `Consultation`: 상담 정보

### 관계
- `Brand` ↔ `BrandManager`: Many-to-One
- `Brand` ↔ `BrandCategory`: Many-to-One
- `Brand` ↔ `BrandDetail`: One-to-One
- `Brand` ↔ `Consultation`: One-to-Many

