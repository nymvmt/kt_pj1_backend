#!/bin/bash

# 프랜차이즈 상담 플랫폼 데이터베이스 초기화 스크립트
# 실행 전 PostgreSQL 컨테이너가 실행 중이어야 함

set -e  # 에러 발생 시 스크립트 중단

# 색상 정의
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 데이터베이스 연결 정보
DB_HOST="localhost"
DB_PORT="5433"
DB_NAME="fullstack_db"
DB_USER="kt_user"
DB_PASSWORD="kt_password"

echo -e "${YELLOW}프랜차이즈 상담 플랫폼 데이터베이스 초기화 시작${NC}"

# PostgreSQL 컨테이너 상태 확인
echo -e "${YELLOW}PostgreSQL 컨테이너 상태 확인 중...${NC}"
if ! docker ps | grep -q "kt_pj1_postgres"; then
    echo -e "${RED}PostgreSQL 컨테이너가 실행 중이지 않습니다.${NC}"
    echo -e "${YELLOW}Docker Compose로 컨테이너를 시작합니다...${NC}"
    docker-compose up -d postgres
    echo -e "${YELLOW}PostgreSQL 컨테이너 시작 대기 중 (30초)...${NC}"
    sleep 30
fi

# 데이터베이스 연결 테스트
echo -e "${YELLOW}데이터베이스 연결 테스트 중...${NC}"
export PGPASSWORD=$DB_PASSWORD
if ! psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -c "SELECT 1;" > /dev/null 2>&1; then
    echo -e "${RED}데이터베이스 연결에 실패했습니다.${NC}"
    echo -e "${YELLOW}연결 정보를 확인하고 다시 시도해주세요.${NC}"
    exit 1
fi

echo -e "${GREEN}데이터베이스 연결 성공${NC}"

# 스키마 생성
echo -e "${YELLOW}데이터베이스 스키마 생성 중...${NC}"
if psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -f schema.sql; then
    echo -e "${GREEN}스키마 생성 완료${NC}"
else
    echo -e "${RED}스키마 생성 실패${NC}"
    exit 1
fi

# 초기 데이터 삽입
echo -e "${YELLOW}초기 데이터 삽입 중...${NC}"
if psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -f init-data.sql; then
    echo -e "${GREEN}초기 데이터 삽입 완료${NC}"
else
    echo -e "${RED}초기 데이터 삽입 실패${NC}"
    exit 1
fi

# 테이블 확인
echo -e "${YELLOW}생성된 테이블 확인 중...${NC}"
psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -c "
SELECT table_name 
FROM information_schema.tables 
WHERE table_schema = 'public' 
ORDER BY table_name;
"

echo -e "${GREEN}데이터베이스 초기화 완료!${NC}"
echo -e "${YELLOW}데이터베이스 정보:${NC}"
echo -e "  Host: $DB_HOST"
echo -e "  Port: $DB_PORT"
echo -e "  Database: $DB_NAME"
echo -e "  User: $DB_USER"
echo -e ""
echo -e "${YELLOW}데이터베이스 접속 명령어:${NC}"
echo -e "  psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME"
