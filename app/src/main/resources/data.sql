-- 중복 실행 방지를 위한 데이터 존재 확인 후 삽입

-- 상담 상태 데이터 (중복 방지)
INSERT INTO consultation_status (status_code, status_name) 
SELECT 1, 'PENDING' WHERE NOT EXISTS (SELECT 1 FROM consultation_status WHERE status_code = 1);

INSERT INTO consultation_status (status_code, status_name) 
SELECT 2, 'RESCHEDULE_REQUEST' WHERE NOT EXISTS (SELECT 1 FROM consultation_status WHERE status_code = 2);

INSERT INTO consultation_status (status_code, status_name) 
SELECT 3, 'CONFIRMED' WHERE NOT EXISTS (SELECT 1 FROM consultation_status WHERE status_code = 3);



INSERT INTO consultation_status (status_code, status_name) 
SELECT 5, 'CANCELLED' WHERE NOT EXISTS (SELECT 1 FROM consultation_status WHERE status_code = 5);

-- 카테고리 데이터 (중복 방지)
INSERT INTO brand_category (category_name) 
SELECT '외식' WHERE NOT EXISTS (SELECT 1 FROM brand_category WHERE category_name = '외식');

INSERT INTO brand_category (category_name) 
SELECT '교육' WHERE NOT EXISTS (SELECT 1 FROM brand_category WHERE category_name = '교육');

INSERT INTO brand_category (category_name) 
SELECT '뷰티' WHERE NOT EXISTS (SELECT 1 FROM brand_category WHERE category_name = '뷰티');

INSERT INTO brand_category (category_name) 
SELECT '편의점' WHERE NOT EXISTS (SELECT 1 FROM brand_category WHERE category_name = '편의점');

-- 매니저 데이터 (중복 방지)
INSERT INTO brand_manager (name, email, password, phone) 
SELECT '김치킨', 'chicken@test.com', 'password123', '010-1234-5678'
WHERE NOT EXISTS (SELECT 1 FROM brand_manager WHERE email = 'chicken@test.com');

INSERT INTO brand_manager (name, email, password, phone) 
SELECT '이교육', 'edu@test.com', 'password123', '010-2345-6789'
WHERE NOT EXISTS (SELECT 1 FROM brand_manager WHERE email = 'edu@test.com');

INSERT INTO brand_manager (name, email, password, phone) 
SELECT '박뷰티', 'beauty@test.com', 'password123', '010-3456-7890'
WHERE NOT EXISTS (SELECT 1 FROM brand_manager WHERE email = 'beauty@test.com');

INSERT INTO brand_manager (name, email, password, phone) 
SELECT '최편의', 'conv@test.com', 'password123', '010-4567-8901'
WHERE NOT EXISTS (SELECT 1 FROM brand_manager WHERE email = 'conv@test.com');

-- 브랜드 데이터 (중복 방지)
INSERT INTO brand (brand_name, category_id, manager_id) 
SELECT '황금치킨', 
       (SELECT category_id FROM brand_category WHERE category_name = '외식'), 
       (SELECT manager_id FROM brand_manager WHERE email = 'chicken@test.com')
WHERE NOT EXISTS (SELECT 1 FROM brand WHERE brand_name = '황금치킨');

INSERT INTO brand (brand_name, category_id, manager_id) 
SELECT '맛있는치킨', 
       (SELECT category_id FROM brand_category WHERE category_name = '외식'), 
       (SELECT manager_id FROM brand_manager WHERE email = 'chicken@test.com')
WHERE NOT EXISTS (SELECT 1 FROM brand WHERE brand_name = '맛있는치킨');

INSERT INTO brand (brand_name, category_id, manager_id) 
SELECT '스마트학원', 
       (SELECT category_id FROM brand_category WHERE category_name = '교육'), 
       (SELECT manager_id FROM brand_manager WHERE email = 'edu@test.com')
WHERE NOT EXISTS (SELECT 1 FROM brand WHERE brand_name = '스마트학원');

INSERT INTO brand (brand_name, category_id, manager_id) 
SELECT '뷰티살롱', 
       (SELECT category_id FROM brand_category WHERE category_name = '뷰티'), 
       (SELECT manager_id FROM brand_manager WHERE email = 'beauty@test.com')
WHERE NOT EXISTS (SELECT 1 FROM brand WHERE brand_name = '뷰티살롱');

INSERT INTO brand (brand_name, category_id, manager_id) 
SELECT '24시편의점', 
       (SELECT category_id FROM brand_category WHERE category_name = '편의점'), 
       (SELECT manager_id FROM brand_manager WHERE email = 'conv@test.com')
WHERE NOT EXISTS (SELECT 1 FROM brand WHERE brand_name = '24시편의점');

-- 브랜드 상세정보 (중복 방지)
INSERT INTO brand_detail (brand_id, view_count, save_count, initial_cost, total_investment, avg_monthly_revenue, store_count, brand_description) 
SELECT (SELECT brand_id FROM brand WHERE brand_name = '황금치킨'), 150, 25, 5000000, 50000000, 8000000, 120, '전국 최고의 치킨 프랜차이즈입니다.'
WHERE NOT EXISTS (SELECT 1 FROM brand_detail WHERE brand_id = (SELECT brand_id FROM brand WHERE brand_name = '황금치킨'));

INSERT INTO brand_detail (brand_id, view_count, save_count, initial_cost, total_investment, avg_monthly_revenue, store_count, brand_description) 
SELECT (SELECT brand_id FROM brand WHERE brand_name = '맛있는치킨'), 89, 12, 3000000, 35000000, 6000000, 85, '합리적인 가격의 치킨 전문점입니다.'
WHERE NOT EXISTS (SELECT 1 FROM brand_detail WHERE brand_id = (SELECT brand_id FROM brand WHERE brand_name = '맛있는치킨'));

INSERT INTO brand_detail (brand_id, view_count, save_count, initial_cost, total_investment, avg_monthly_revenue, store_count, brand_description) 
SELECT (SELECT brand_id FROM brand WHERE brand_name = '스마트학원'), 201, 45, 8000000, 80000000, 12000000, 65, '개인별 맞춤 교육 학원입니다.'
WHERE NOT EXISTS (SELECT 1 FROM brand_detail WHERE brand_id = (SELECT brand_id FROM brand WHERE brand_name = '스마트학원'));

INSERT INTO brand_detail (brand_id, view_count, save_count, initial_cost, total_investment, avg_monthly_revenue, store_count, brand_description) 
SELECT (SELECT brand_id FROM brand WHERE brand_name = '뷰티살롱'), 76, 18, 15000000, 100000000, 9000000, 30, '최신 뷰티 트렌드 살롱입니다.'
WHERE NOT EXISTS (SELECT 1 FROM brand_detail WHERE brand_id = (SELECT brand_id FROM brand WHERE brand_name = '뷰티살롱'));

INSERT INTO brand_detail (brand_id, view_count, save_count, initial_cost, total_investment, avg_monthly_revenue, store_count, brand_description) 
SELECT (SELECT brand_id FROM brand WHERE brand_name = '24시편의점'), 134, 32, 10000000, 60000000, 15000000, 200, '24시간 운영 편의점입니다.'
WHERE NOT EXISTS (SELECT 1 FROM brand_detail WHERE brand_id = (SELECT brand_id FROM brand WHERE brand_name = '24시편의점'));