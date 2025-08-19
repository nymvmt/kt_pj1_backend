package com.kt.backendapp.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {
    private String errorCode;
    private String message;
    private String path;
    private List<FieldError> fieldErrors;
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FieldError {
        private String field;
        private String message;
        private Object rejectedValue;
    }
    
    // 일반 에러 응답
    public static ErrorResponse of(String errorCode, String message, String path) {
        return ErrorResponse.builder()
            .errorCode(errorCode)
            .message(message)
            .path(path)
            .build();
    }
    
    // 유효성 검증 에러 응답
    public static ErrorResponse of(String errorCode, String message, String path, List<FieldError> fieldErrors) {
        return ErrorResponse.builder()
            .errorCode(errorCode)
            .message(message)
            .path(path)
            .fieldErrors(fieldErrors)
            .build();
    }
}