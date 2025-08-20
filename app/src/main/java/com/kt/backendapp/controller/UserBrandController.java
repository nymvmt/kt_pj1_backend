package com.kt.backendapp.controller;

import com.kt.backendapp.dto.common.ApiResponse;
import com.kt.backendapp.dto.response.brand.BrandDetailResponse;
import com.kt.backendapp.dto.response.brand.BrandListResponse;
import com.kt.backendapp.service.UserBrandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Slf4j
public class UserBrandController {
    
    private final UserBrandService userBrandService;
    
    /**
     * 유저용 브랜드 목록 조회 (User-Id 헤더 필요)
     * GET /api/v1/user/brands
     */
    @GetMapping("/brands")
    public ResponseEntity<ApiResponse<List<BrandListResponse>>> getUserBrands(
            @RequestHeader("User-Id") Long userId) {
        try {
            List<BrandListResponse> response = userBrandService.getUserBrands(userId);
            return ResponseEntity.ok(ApiResponse.success(response, "유저용 브랜드 목록 조회가 완료되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("INVALID_REQUEST", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("INTERNAL_ERROR", "서버 오류가 발생했습니다."));
        }
    }
    
    /**
     * 유저용 브랜드 상세 조회 (User-Id 헤더 필요)
     * GET /api/v1/user/brands/{id}
     */
    @GetMapping("/brands/{id}")
    public ResponseEntity<ApiResponse<BrandDetailResponse>> getUserBrandDetail(
            @PathVariable Long id,
            @RequestHeader("User-Id") Long userId) {
        try {
            BrandDetailResponse response = userBrandService.getUserBrandDetail(id, userId);
            return ResponseEntity.ok(ApiResponse.success(response, "유저용 브랜드 상세 조회가 완료되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("INVALID_REQUEST", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("INTERNAL_ERROR", "서버 오류가 발생했습니다."));
        }
    }
    
    /**
     * 브랜드 찜하기 (User-Id 헤더 필요)
     * POST /api/v1/user/brands/{id}/save
     */
    @PostMapping("/brands/{id}/save")
    public ResponseEntity<ApiResponse<String>> saveBrand(
            @PathVariable Long id,
            @RequestHeader("User-Id") Long userId) {
        try {
            userBrandService.saveBrand(id, userId);
            return ResponseEntity.ok(ApiResponse.success("success", "브랜드가 찜 목록에 추가되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("INVALID_REQUEST", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("INTERNAL_ERROR", "서버 오류가 발생했습니다."));
        }
    }
    
    /**
     * 브랜드 찜 해제 (User-Id 헤더 필요)
     * DELETE /api/v1/user/brands/{id}/save
     */
    @DeleteMapping("/brands/{id}/save")
    public ResponseEntity<ApiResponse<String>> unsaveBrand(
            @PathVariable Long id,
            @RequestHeader("User-Id") Long userId) {
        try {
            userBrandService.unsaveBrand(id, userId);
            return ResponseEntity.ok(ApiResponse.success("success", "브랜드가 찜 목록에서 제거되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("INVALID_REQUEST", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("INTERNAL_ERROR", "서버 오류가 발생했습니다."));
        }
    }
}
