package com.kt.backendapp.entity;

/**
 * 사용자 상담 응답 타입
 */
public enum UserResponseType {
    ACCEPT("수락"),
    REJECT("거절");
    
    private final String description;
    
    UserResponseType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
