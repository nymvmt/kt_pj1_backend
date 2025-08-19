package com.kt.backendapp.dto.response.auth;

import com.kt.backendapp.dto.response.brand.BrandManagerResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    private UserResponse user;                    // 일반 사용자 정보
    private BrandManagerResponse manager;         // 매니저 정보
    private String userType;                      // "USER" 또는 "MANAGER"
    private String message;                       // 응답 메시지
    
    // 일반 사용자 로그인 성공
    public static LoginResponse userSuccess(UserResponse user) {
        return LoginResponse.builder()
            .user(user)
            .userType("USER")
            .message("사용자 로그인 성공")
            .build();
    }
    
    // 매니저 로그인 성공
    public static LoginResponse managerSuccess(BrandManagerResponse manager) {
        return LoginResponse.builder()
            .manager(manager)
            .userType("MANAGER")
            .message("매니저 로그인 성공")
            .build();
    }
    
    // 실패 응답 생성 메소드  
    public static LoginResponse failure(String message) {
        return LoginResponse.builder()
            .message(message)
            .build();
    }
}