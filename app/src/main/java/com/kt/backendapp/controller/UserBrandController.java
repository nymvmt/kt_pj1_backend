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
import java.util.Map;

@RestController
@RequestMapping("/api/user")
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
     * 사용자의 찜한 브랜드 목록 조회 (User-Id 헤더 필요)
     * GET /api/user/brands/saved
     */
    @GetMapping("/brands/saved")
    public ResponseEntity<ApiResponse<List<BrandListResponse>>> getSavedBrands(
            @RequestHeader("User-Id") Long userId) {
        try {
            List<BrandListResponse> response = userBrandService.getSavedBrands(userId);
            return ResponseEntity.ok(ApiResponse.success(response, "찜한 브랜드 목록 조회가 완료되었습니다."));
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
     * 브랜드 찜하기/찜해제 토글 (User-Id 헤더 필요)
     * POST /api/user/brands/{brandId}/save
     */
    @PostMapping("/brands/{brandId}/save")
    public ResponseEntity<ApiResponse<Boolean>> toggleSavedBrand(
            @PathVariable Long brandId,
            @RequestHeader("User-Id") Long userId) {
        try {
            boolean isSaved = userBrandService.toggleSavedBrand(brandId, userId);
            String message = isSaved ? "브랜드를 찜했습니다." : "브랜드 찜을 해제했습니다.";
            return ResponseEntity.ok(ApiResponse.success(isSaved, message));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("INVALID_REQUEST", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("INTERNAL_ERROR", "서버 오류가 발생했습니다."));
        }
    }
    
    /**
     * 브랜드 찜 상태 조회 (User-Id 헤더 필요)
     * POST /api/user/brands/save-status
     */
    @PostMapping("/brands/save-status")
    public ResponseEntity<ApiResponse<Map<Long, Boolean>>> getBrandSaveStatus(
            @RequestBody List<Long> brandIds,
            @RequestHeader("User-Id") Long userId) {
        try {
            Map<Long, Boolean> saveStatus = userBrandService.getBrandSaveStatus(brandIds, userId);
            return ResponseEntity.ok(ApiResponse.success(saveStatus, "브랜드 찜 상태 조회가 완료되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("INTERNAL_ERROR", "서버 오류가 발생했습니다."));
        }
    }
}
