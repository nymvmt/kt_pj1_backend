package com.kt.backendapp.dto.common;

public class ErrorCode {
    // 공통 에러
    public static final String INTERNAL_SERVER_ERROR = "COMMON_001";
    public static final String INVALID_REQUEST = "COMMON_002";
    public static final String VALIDATION_ERROR = "COMMON_003";
    
    // 사용자 관련 에러
    public static final String USER_NOT_FOUND = "USER_001";
    public static final String EMAIL_ALREADY_EXISTS = "USER_002";
    public static final String INVALID_CREDENTIALS = "USER_003";
    
    // 브랜드 관련 에러
    public static final String BRAND_NOT_FOUND = "BRAND_001";
    public static final String BRAND_NAME_ALREADY_EXISTS = "BRAND_002";
    public static final String CATEGORY_NOT_FOUND = "BRAND_003";
    
    // 찜 관련 에러
    public static final String ALREADY_SAVED = "SAVED_001";
    public static final String NOT_SAVED = "SAVED_002";
    
    // 상담 관련 에러
    public static final String CONSULTATION_NOT_FOUND = "CONSULTATION_001";
    public static final String ACTIVE_CONSULTATION_EXISTS = "CONSULTATION_002";
    public static final String INVALID_STATUS_TRANSITION = "CONSULTATION_003";
    
    // 매니저 관련 에러
    public static final String MANAGER_NOT_FOUND = "MANAGER_001";
    public static final String MANAGER_ALREADY_ASSIGNED = "MANAGER_002";
    
    // 알림 관련 에러
    public static final String NOTIFICATION_NOT_FOUND = "NOTIFICATION_001";
}
