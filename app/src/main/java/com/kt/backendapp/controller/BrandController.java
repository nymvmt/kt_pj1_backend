package com.kt.backendapp.controller;

import com.kt.backendapp.dto.common.ApiResponse;

import com.kt.backendapp.dto.response.brand.BrandDetailResponse;
import com.kt.backendapp.dto.response.brand.BrandListResponse;
import com.kt.backendapp.dto.response.brand.CategoryResponse;
import com.kt.backendapp.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class BrandController {
    
    private final BrandService brandService;
    
    /**
     * 전체 브랜드 목록 조회
     * GET /api/v1/brands
     */
    @GetMapping("/brands")
    public ResponseEntity<ApiResponse<List<BrandListResponse>>> getAllBrands(
            @RequestHeader(value = "User-Id", required = false) Long userId) {
        try {
            List<BrandListResponse> response = brandService.getAllBrands(userId);
            return ResponseEntity.ok(ApiResponse.success(response, "전체 브랜드 목록 조회가 완료되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("INTERNAL_ERROR", "서버 오류가 발생했습니다."));
        }
    }
    
    /**
     * 브랜드 상세 조회 + 조회수 증가
     * GET /api/v1/brands/{id}
     */
    @GetMapping("/brands/{id}")
    public ResponseEntity<ApiResponse<BrandDetailResponse>> getBrandDetail(
            @PathVariable Long id,
            @RequestHeader(value = "User-Id", required = false) Long userId) {
        try {
            BrandDetailResponse response = brandService.getBrandDetail(id, userId);
            return ResponseEntity.ok(ApiResponse.success(response, "브랜드 상세 조회가 완료되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("INVALID_REQUEST", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("INTERNAL_ERROR", "서버 오류가 발생했습니다."));
        }
    }
    
    /**
     * 브랜드 찜하기
     * POST /api/v1/brands/{id}/save
     */
    @PostMapping("/brands/{id}/save")
    public ResponseEntity<ApiResponse<String>> saveBrand(
            @PathVariable Long id,
            @RequestHeader("User-Id") Long userId) {
        try {
            brandService.saveBrand(id, userId);
            return ResponseEntity.ok(ApiResponse.success("브랜드 찜하기가 완료되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("SAVE_FAILED", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("INTERNAL_ERROR", "서버 오류가 발생했습니다."));
        }
    }
    
    /**
     * 브랜드 찜 취소
     * DELETE /api/v1/brands/{id}/save
     */
    @DeleteMapping("/brands/{id}/save")
    public ResponseEntity<ApiResponse<String>> unsaveBrand(
            @PathVariable Long id,
            @RequestHeader("User-Id") Long userId) {
        try {
            brandService.unsaveBrand(id, userId);
            return ResponseEntity.ok(ApiResponse.success("브랜드 찜 취소가 완료되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("UNSAVE_FAILED", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("INTERNAL_ERROR", "서버 오류가 발생했습니다."));
        }
    }
    
    /**
     * 내 찜 목록 조회
     * GET /api/v1/users/saved-brands
     */
    @GetMapping("/users/saved-brands")
    public ResponseEntity<ApiResponse<List<BrandListResponse>>> getSavedBrands(
            @RequestHeader("User-Id") Long userId) {
        try {
            List<BrandListResponse> response = brandService.getSavedBrands(userId);
            return ResponseEntity.ok(ApiResponse.success(response, "내 찜 목록 조회가 완료되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("INVALID_REQUEST", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("INTERNAL_ERROR", "서버 오류가 발생했습니다."));
        }
    }

    /**
     * 카테고리 목록 조회
     * GET /api/v1/categories
     */
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getCategories() {
        try {
            List<CategoryResponse> response = brandService.getCategories();
            return ResponseEntity.ok(ApiResponse.success(response, "카테고리 목록 조회가 완료되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("INTERNAL_ERROR", "서버 오류가 발생했습니다."));
        }
    }
}
