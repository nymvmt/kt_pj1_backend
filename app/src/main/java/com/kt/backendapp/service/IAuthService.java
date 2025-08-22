package com.kt.backendapp.service;

import com.kt.backendapp.dto.request.auth.LoginRequest;
import com.kt.backendapp.dto.request.auth.UserCreateRequest;
import com.kt.backendapp.dto.request.brand.BrandManagerCreateRequest;
import com.kt.backendapp.dto.response.auth.LoginResponse;
import com.kt.backendapp.dto.response.auth.UserResponse;
import com.kt.backendapp.dto.response.brand.BrandManagerResponse;

/**
 * 인증 관련 서비스 인터페이스
 */
public interface IAuthService {
    
    /**
     * 사용자 회원가입
     */
    UserResponse userRegister(UserCreateRequest request);
    
    /**
     * 사용자 로그인
     */
    LoginResponse userLogin(LoginRequest request);
    
    /**
     * 매니저 회원가입 + 브랜드 동시 등록 (상세 정보 포함)
     */
    BrandManagerResponse managerRegister(BrandManagerCreateRequest request);
    
    /**
     * 매니저 로그인
     */
    LoginResponse managerLogin(LoginRequest request);
    
    /**
     * 로그아웃 (단순 응답)
     */
    void logout();
    
    /**
     * 내 정보 조회 (향후 세션 기반으로 구현)
     */
    Object getMyInfo(String userType, Long userId);
}
