# 데이터베이스 설정 및 관리

## 파일 구조

```
kt_pj1_backend/
├── schema.sql          # 데이터베이스 스키마 (DDL)
├── init-data.sql       # 초기 데이터 삽입
├── init-db.sh         # 데이터베이스 초기화 스크립트
├── docker-compose.yml  # PostgreSQL 컨테이너 설정
└── DATABASE.md        # 이 파일
```

## 데이터베이스 초기화 방법

### 1. Docker Compose 사용 (권장)

```bash
# PostgreSQL 컨테이너 시작 (자동으로 스키마 및 데이터 생성)
docker-compose up -d postgres

# 로그 확인
docker-compose logs postgres
```

### 2. 수동 초기화

```bash
# 1. PostgreSQL 컨테이너만 시작
docker-compose up -d postgres

# 2. 데이터베이스 초기화 스크립트 실행
./init-db.sh
```

### 3. 개별 파일 실행

```bash
# 스키마 생성
psql -h localhost -p 5433 -U kt_user -d fullstack_db -f schema.sql

# 초기 데이터 삽입
psql -h localhost -p 5433 -U kt_user -d fullstack_db -f init-data.sql
```

## 데이터베이스 연결 정보

- **Host**: localhost
- **Port**: 5433
- **Database**: fullstack_db
- **Username**: kt_user
- **Password**: kt_password

## 데이터베이스 접속

```bash
# psql 접속
psql -h localhost -p 5433 -U kt_user -d fullstack_db

# 또는 환경변수 설정 후
export PGPASSWORD=kt_password
psql -h localhost -p 5433 -U kt_user -d fullstack_db
```

## 테이블 구조

### 1. 사용자 관련
- `users` - 사용자 정보
- `brand_manager` - 브랜드 매니저 정보

### 2. 브랜드 관련
- `brand_category` - 브랜드 카테고리
- `brand` - 브랜드 기본 정보
- `brand_detail` - 브랜드 상세 정보
- `saved_brand` - 사용자가 저장한 브랜드

### 3. 상담 관련
- `consultation_status` - 상담 상태 코드
- `consultation` - 상담 정보
- `notification` - 알림 정보

## 주요 명령어

### 데이터베이스 리셋
```bash
# 컨테이너 중지 및 볼륨 삭제
docker-compose down -v

# 다시 시작 (자동으로 스키마 및 데이터 재생성)
docker-compose up -d postgres
```

### 테이블 확인
```sql
-- 모든 테이블 조회
SELECT table_name FROM information_schema.tables 
WHERE table_schema = 'public' ORDER BY table_name;

-- 테이블 구조 확인
\d table_name
```

### 데이터 확인
```sql
-- 카테고리별 브랜드 수
SELECT c.category_name, COUNT(b.brand_id) as brand_count
FROM brand_category c
LEFT JOIN brand b ON c.category_id = b.category_id
GROUP BY c.category_name;

-- 상담 상태별 통계
SELECT s.status_name, COUNT(c.consultation_id) as count
FROM consultation_status s
LEFT JOIN consultation c ON s.status_code = c.status_code
GROUP BY s.status_name;
```

## 문제 해결

### 1. 연결 실패 시
- PostgreSQL 컨테이너가 실행 중인지 확인: `docker ps`
- 포트가 사용 중인지 확인: `netstat -an | grep 5433`

### 2. 권한 오류 시
```bash
# 스크립트 실행 권한 부여
chmod +x init-db.sh
```

### 3. 데이터 중복 오류 시
- `schema.sql`에서 `DROP TABLE IF EXISTS` 구문이 있어 기존 데이터를 안전하게 삭제
- `init-data.sql`에서 `ON CONFLICT DO NOTHING` 구문으로 중복 방지
