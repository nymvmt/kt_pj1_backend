-- 개발 환경 초기 데이터 (수동 실행용)
-- psql -h localhost -p 5433 -U kt_user -d fullstack_db -f init-dev-data.sql

-- 카테고리 데이터
INSERT INTO brand_category (category_name) VALUES 
('외식'), ('교육'), ('뷰티'), ('편의점'), ('서비스'), ('소매')
ON CONFLICT (category_name) DO NOTHING;

-- 테스트용 매니저 계정 (비밀번호는 실제 환경에서는 해시화 필요)
INSERT INTO brand_manager (name, email, password, phone) VALUES 
('테스트매니저', 'test@manager.com', 'password123', '010-0000-0000')
ON CONFLICT (email) DO NOTHING;
