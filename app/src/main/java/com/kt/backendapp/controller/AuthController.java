package com.kt.backendapp.controller;

import com.kt.backendapp.dto.common.ApiResponse;
import com.kt.backendapp.dto.request.auth.LoginRequest;
import com.kt.backendapp.dto.request.auth.UserCreateRequest;
import com.kt.backendapp.dto.request.brand.BrandManagerCreateRequest;
import com.kt.backendapp.dto.response.auth.LoginResponse;
import com.kt.backendapp.dto.response.auth.UserResponse;
import com.kt.backendapp.dto.response.brand.BrandManagerResponse;
import com.kt.backendapp.service.IAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    
    private final IAuthService authService;
    
    /**
     * 사용자 회원가입
     */
    @PostMapping("/user/register")
    public ResponseEntity<ApiResponse<UserResponse>> userRegister(
            @Valid @RequestBody UserCreateRequest request) {
        try {
            UserResponse response = authService.userRegister(request);
            return ResponseEntity.ok(ApiResponse.success(response, "사용자 회원가입이 완료되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("REGISTRATION_FAILED", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("INTERNAL_ERROR", "서버 오류가 발생했습니다."));
        }
    }
    
    /**
     * 사용자 로그인
     */
    @PostMapping("/user/login")
    public ResponseEntity<ApiResponse<LoginResponse>> userLogin(
            @Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.userLogin(request);
            return ResponseEntity.ok(ApiResponse.success(response, "사용자 로그인이 완료되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("LOGIN_FAILED", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("INTERNAL_ERROR", "서버 오류가 발생했습니다."));
        }
    }
    
    /**
     * 매니저 회원가입 + 브랜드 등록
     */
    @PostMapping("/manager/register")
    public ResponseEntity<ApiResponse<BrandManagerResponse>> managerRegister(
            @Valid @RequestBody BrandManagerCreateRequest request) {
        log.info("매니저 회원가입 요청: email={}, name={}, brandName={}", 
                request.getEmail(), request.getName(), request.getBrandName());
        try {
            BrandManagerResponse response = authService.managerRegister(request);
            log.info("매니저 회원가입 성공: email={}", request.getEmail());
            return ResponseEntity.ok(ApiResponse.success(response, "매니저 회원가입 및 브랜드 등록이 완료되었습니다."));
        } catch (IllegalArgumentException e) {
            log.error("매니저 회원가입 실패 (IllegalArgumentException): {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("REGISTRATION_FAILED", e.getMessage()));
        } catch (Exception e) {
            log.error("매니저 회원가입 실패 (Exception): {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("INTERNAL_ERROR", "서버 오류가 발생했습니다."));
        }
    }
    
    /**
     * 매니저 로그인
     */
    @PostMapping("/manager/login")
    public ResponseEntity<ApiResponse<LoginResponse>> managerLogin(
            @Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.managerLogin(request);
            return ResponseEntity.ok(ApiResponse.success(response, "매니저 로그인이 완료되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("LOGIN_FAILED", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("INTERNAL_ERROR", "서버 오류가 발생했습니다."));
        }
    }
    
    /**
     * 로그아웃 (공통)
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout() {
        try {
            authService.logout();
            return ResponseEntity.ok(ApiResponse.success("로그아웃이 완료되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("INTERNAL_ERROR", "서버 오류가 발생했습니다."));
        }
    }
    
    /**
     * 내 정보 조회 (임시 구현 - 향후 세션 기반으로 변경)
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Object>> getMyInfo(
            @RequestParam String userType,
            @RequestParam Long userId) {
        try {
            Object response = authService.getMyInfo(userType, userId);
            return ResponseEntity.ok(ApiResponse.success(response, "내 정보 조회가 완료되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("USER_NOT_FOUND", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("INTERNAL_ERROR", "서버 오류가 발생했습니다."));
        }
    }
}