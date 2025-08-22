package com.kt.backendapp.controller;

import com.kt.backendapp.dto.common.ApiResponse;
import com.kt.backendapp.dto.response.brand.BrandDetailResponse;
import com.kt.backendapp.dto.response.brand.BrandListResponse;
import com.kt.backendapp.dto.response.brand.CategoryResponse;
import com.kt.backendapp.service.IPublicBrandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
@Slf4j
public class PublicBrandController {
    
    private final IPublicBrandService publicBrandService;
    
    /**
     * 공개 브랜드 목록 조회 (인증 불필요, 매니저인 경우 isManaged 필드 포함)
     * GET /api/v1/public/brands
     */
    @GetMapping("/brands")
    public ResponseEntity<ApiResponse<List<BrandListResponse>>> getPublicBrands(
            @RequestHeader(value = "Manager-Id", required = false) Long managerId) {
        log.info("=== 공개 브랜드 목록 조회 요청: Manager-Id 헤더={} ===", managerId);
        try {
            List<BrandListResponse> response = publicBrandService.getPublicBrands(managerId);
            return ResponseEntity.ok(ApiResponse.success(response, "공개 브랜드 목록 조회가 완료되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("INTERNAL_ERROR", "서버 오류가 발생했습니다."));
        }
    }
    
    /**
     * 공개 브랜드 상세 조회 (인증 불필요)
     * GET /api/v1/public/brands/{id}
     */
    @GetMapping("/brands/{id}")
    public ResponseEntity<ApiResponse<BrandDetailResponse>> getPublicBrandDetail(@PathVariable Long id) {
        try {
            BrandDetailResponse response = publicBrandService.getPublicBrandDetail(id);
            return ResponseEntity.ok(ApiResponse.success(response, "공개 브랜드 상세 조회가 완료되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("INVALID_REQUEST", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("INTERNAL_ERROR", "서버 오류가 발생했습니다."));
        }
    }
    
    /**
     * 카테고리별 브랜드 목록 조회 (인증 불필요, 매니저인 경우 isManaged 필드 포함)
     * GET /api/public/brands/category/{categoryId}
     */
    @GetMapping("/brands/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<BrandListResponse>>> getBrandsByCategory(
            @PathVariable Long categoryId,
            @RequestHeader(value = "Manager-Id", required = false) Long managerId) {
        try {
            List<BrandListResponse> response = publicBrandService.getBrandsByCategory(categoryId, managerId);
            return ResponseEntity.ok(ApiResponse.success(response, "카테고리별 브랜드 목록 조회가 완료되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("INVALID_REQUEST", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("INTERNAL_ERROR", "서버 오류가 발생했습니다."));
        }
    }
    
    /**
     * 브랜드 검색 (인증 불필요, 매니저인 경우 isManaged 필드 포함)
     * GET /api/public/brands/search?keyword={keyword}
     */
    @GetMapping("/brands/search")
    public ResponseEntity<ApiResponse<List<BrandListResponse>>> searchBrands(
            @RequestParam String keyword,
            @RequestHeader(value = "Manager-Id", required = false) Long managerId) {
        try {
            List<BrandListResponse> response = publicBrandService.searchBrands(keyword, managerId);
            return ResponseEntity.ok(ApiResponse.success(response, "브랜드 검색이 완료되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("INVALID_REQUEST", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("INTERNAL_ERROR", "서버 오류가 발생했습니다."));
        }
    }
    
    /**
     * 카테고리 목록 조회 (인증 불필요)
     * GET /api/public/categories
     */
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getCategories() {
        try {
            List<CategoryResponse> response = publicBrandService.getCategories();
            return ResponseEntity.ok(ApiResponse.success(response, "카테고리 목록 조회가 완료되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("INTERNAL_ERROR", "서버 오류가 발생했습니다."));
        }
    }
}
