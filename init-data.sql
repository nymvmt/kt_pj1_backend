-- 프랜차이즈 상담 플랫폼 초기 데이터
-- 실행 전 schema.sql을 먼저 실행해야 함

-- 상담 상태 기초 데이터 삽입
INSERT INTO consultation_status (status_code, status_name) VALUES 
(1, 'PENDING'),
(2, 'RESCHEDULE_REQUEST'),
(3, 'CONFIRMED'),
(4, 'COMPLETED'),
(5, 'CANCELLED')
ON CONFLICT (status_code) DO NOTHING;

-- 브랜드 카테고리 기초 데이터 삽입
INSERT INTO brand_category (category_name) VALUES 
('외식'),
('교육'),
('뷰티'),
('편의점'),
('서비스'),
('소매'),
('카페'),
('피트니스'),
('의류'),
('건강')
ON CONFLICT (category_name) DO NOTHING;

-- 테스트용 브랜드 매니저 데이터 삽입
INSERT INTO brand_manager (name, email, password, phone) VALUES 
('김치킨', 'chicken@test.com', 'password123', '010-1234-5678'),
('이교육', 'edu@test.com', 'password123', '010-2345-6789'),
('박뷰티', 'beauty@test.com', 'password123', '010-3456-7890'),
('최편의', 'conv@test.com', 'password123', '010-4567-8901'),
('정카페', 'cafe@test.com', 'password123', '010-5678-9012'),
('김피트', 'fitness@test.com', 'password123', '010-6789-0123')
ON CONFLICT (email) DO NOTHING;

-- 테스트용 브랜드 데이터 삽입
INSERT INTO brand (brand_name, category_id, manager_id) VALUES 
('황금치킨', 
 (SELECT category_id FROM brand_category WHERE category_name = '외식'), 
 (SELECT manager_id FROM brand_manager WHERE email = 'chicken@test.com')),
('맛있는치킨', 
 (SELECT category_id FROM brand_category WHERE category_name = '외식'), 
 (SELECT manager_id FROM brand_manager WHERE email = 'chicken@test.com')),
('스마트학원', 
 (SELECT category_id FROM brand_category WHERE category_name = '교육'), 
 (SELECT manager_id FROM brand_manager WHERE email = 'edu@test.com')),
('뷰티살롱', 
 (SELECT category_id FROM brand_category WHERE category_name = '뷰티'), 
 (SELECT manager_id FROM brand_manager WHERE email = 'beauty@test.com')),
('24시편의점', 
 (SELECT category_id FROM brand_category WHERE category_name = '편의점'), 
 (SELECT manager_id FROM brand_manager WHERE email = 'conv@test.com')),
('스타벅스타일카페', 
 (SELECT category_id FROM brand_category WHERE category_name = '카페'), 
 (SELECT manager_id FROM brand_manager WHERE email = 'cafe@test.com')),
('헬스클럽', 
 (SELECT category_id FROM brand_category WHERE category_name = '피트니스'), 
 (SELECT manager_id FROM brand_manager WHERE email = 'fitness@test.com'))
ON CONFLICT DO NOTHING;

-- 브랜드 상세정보 삽입
INSERT INTO brand_detail (brand_id, view_count, save_count, initial_cost, total_investment, avg_monthly_revenue, store_count, brand_description) VALUES 
((SELECT brand_id FROM brand WHERE brand_name = '황금치킨'), 
 150, 25, 5000000, 50000000, 8000000, 120, '전국 최고의 치킨 프랜차이즈입니다.'),
((SELECT brand_id FROM brand WHERE brand_name = '맛있는치킨'), 
 89, 12, 3000000, 35000000, 6000000, 85, '합리적인 가격의 치킨 전문점입니다.'),
((SELECT brand_id FROM brand WHERE brand_name = '스마트학원'), 
 201, 45, 8000000, 80000000, 12000000, 65, '개인별 맞춤 교육 학원입니다.'),
((SELECT brand_id FROM brand WHERE brand_name = '뷰티살롱'), 
 76, 18, 15000000, 100000000, 9000000, 30, '최신 뷰티 트렌드 살롱입니다.'),
((SELECT brand_id FROM brand WHERE brand_name = '24시편의점'), 
 134, 32, 10000000, 60000000, 15000000, 200, '24시간 운영 편의점입니다.'),
((SELECT brand_id FROM brand WHERE brand_name = '스타벅스타일카페'), 
 98, 22, 12000000, 70000000, 7000000, 95, '프리미엄 커피 전문점입니다.'),
((SELECT brand_id FROM brand WHERE brand_name = '헬스클럽'), 
 67, 15, 20000000, 120000000, 11000000, 45, '최신 운동 기구를 갖춘 헬스클럽입니다.')
ON CONFLICT (brand_id) DO NOTHING;

-- 테스트용 사용자 데이터 삽입
INSERT INTO users (email, password, name, phone) VALUES 
('user1@test.com', 'password123', '김사용자', '010-1111-1111'),
('user2@test.com', 'password123', '이사용자', '010-2222-2222'),
('user3@test.com', 'password123', '박사용자', '010-3333-3333')
ON CONFLICT (email) DO NOTHING;

-- 데이터 삽입 완료 확인
SELECT 'Init data inserted successfully' as result;
