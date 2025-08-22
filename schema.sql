-- 프랜차이즈 상담 플랫폼 데이터베이스 스키마
-- PostgreSQL 15 기준

-- 기존 테이블이 있으면 삭제 (개발 환경용)
DROP TABLE IF EXISTS notification CASCADE;
DROP TABLE IF EXISTS saved_brand CASCADE;
DROP TABLE IF EXISTS consultation CASCADE;
DROP TABLE IF EXISTS brand_detail CASCADE;
DROP TABLE IF EXISTS brand CASCADE;
DROP TABLE IF EXISTS brand_manager CASCADE;
DROP TABLE IF EXISTS brand_category CASCADE;
DROP TABLE IF EXISTS consultation_status CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- 1. 사용자 테이블
CREATE TABLE users (
    user_id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20)
);

-- 2. 브랜드 카테고리 테이블
CREATE TABLE brand_category (
    category_id BIGSERIAL PRIMARY KEY,
    category_name VARCHAR(50) NOT NULL UNIQUE
);

-- 3. 브랜드 매니저 테이블
CREATE TABLE brand_manager (
    manager_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(20),
    password VARCHAR(255) NOT NULL
);

-- 4. 브랜드 테이블
CREATE TABLE brand (
    brand_id BIGSERIAL PRIMARY KEY,
    brand_name VARCHAR(200) NOT NULL,
    category_id BIGINT,
    manager_id BIGINT,
    FOREIGN KEY (category_id) REFERENCES brand_category(category_id),
    FOREIGN KEY (manager_id) REFERENCES brand_manager(manager_id)
);

-- 5. 브랜드 상세정보 테이블
CREATE TABLE brand_detail (
    detail_id BIGSERIAL PRIMARY KEY,
    brand_id BIGINT UNIQUE,
    view_count BIGINT DEFAULT 0 NOT NULL,
    save_count BIGINT DEFAULT 0 NOT NULL,
    initial_cost DECIMAL(15,2),
    total_investment DECIMAL(15,2),
    avg_monthly_revenue DECIMAL(15,2),
    store_count INTEGER,
    brand_description VARCHAR(200),
    FOREIGN KEY (brand_id) REFERENCES brand(brand_id)
);

-- 6. 상담 상태 테이블
CREATE TABLE consultation_status (
    status_code BIGSERIAL PRIMARY KEY,
    status_name VARCHAR(50) NOT NULL
);

-- 7. 상담 테이블
CREATE TABLE consultation (
    consultation_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    brand_id BIGINT,
    status_code BIGINT,
    preferred_date DATE,
    preferred_time TIME,
    manager_note TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    confirmed_at TIMESTAMP,
    adjusted_date DATE,
    adjusted_time TIME,
    adjustment_reason TEXT,
    adjustment_requested_at TIMESTAMP,
    user_response VARCHAR(50),
    user_response_at TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (brand_id) REFERENCES brand(brand_id),
    FOREIGN KEY (status_code) REFERENCES consultation_status(status_code)
);

-- 8. 저장된 브랜드 테이블
CREATE TABLE saved_brand (
    save_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    brand_id BIGINT,
    saved_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (brand_id) REFERENCES brand(brand_id),
    UNIQUE(user_id, brand_id)
);

-- 9. 알림 테이블
CREATE TABLE notification (
    notification_id BIGSERIAL PRIMARY KEY,
    recipient_id BIGINT,
    recipient_type VARCHAR(50),
    consultation_id BIGINT,
    status_code BIGINT,
    message VARCHAR(200),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_read BOOLEAN DEFAULT FALSE NOT NULL,
    FOREIGN KEY (consultation_id) REFERENCES consultation(consultation_id),
    FOREIGN KEY (status_code) REFERENCES consultation_status(status_code)
);

-- 인덱스 추가
CREATE INDEX idx_brand_category ON brand(category_id);
CREATE INDEX idx_brand_manager ON brand(manager_id);
CREATE INDEX idx_consultation_user ON consultation(user_id);
CREATE INDEX idx_consultation_brand ON consultation(brand_id);
CREATE INDEX idx_consultation_status ON consultation(status_code);
CREATE INDEX idx_consultation_active ON consultation(is_active);
CREATE INDEX idx_saved_brand_user ON saved_brand(user_id);
CREATE INDEX idx_notification_recipient ON notification(recipient_id, recipient_type);
CREATE INDEX idx_notification_unread ON notification(is_read);

-- 코멘트 추가
COMMENT ON TABLE users IS '사용자 정보';
COMMENT ON TABLE brand_category IS '브랜드 카테고리';
COMMENT ON TABLE brand_manager IS '브랜드 매니저';
COMMENT ON TABLE brand IS '브랜드 정보';
COMMENT ON TABLE brand_detail IS '브랜드 상세정보';
COMMENT ON TABLE consultation_status IS '상담 상태';
COMMENT ON TABLE consultation IS '상담 정보';
COMMENT ON TABLE saved_brand IS '사용자가 저장한 브랜드';
COMMENT ON TABLE notification IS '알림';
