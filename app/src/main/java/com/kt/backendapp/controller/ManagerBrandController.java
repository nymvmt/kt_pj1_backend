package com.kt.backendapp.controller;

import com.kt.backendapp.dto.common.ApiResponse;
import com.kt.backendapp.dto.request.brand.BrandCreateRequest;
import com.kt.backendapp.dto.request.brand.BrandUpdateRequest;
import com.kt.backendapp.dto.response.brand.BrandDetailResponse;
import com.kt.backendapp.dto.response.brand.BrandListResponse;
import com.kt.backendapp.dto.response.brand.CategoryResponse;
import com.kt.backendapp.service.IManagerBrandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manager")
@RequiredArgsConstructor
@Slf4j
public class ManagerBrandController {
    
    private final IManagerBrandService managerBrandService;
    
    /**
     * 카테고리 목록 조회
     * GET /api/manager/categories
     */
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getCategories() {
        try {
            List<CategoryResponse> response = managerBrandService.getCategories();
            return ResponseEntity.ok(ApiResponse.success(response, "카테고리 목록 조회가 완료되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("INTERNAL_ERROR", "서버 오류가 발생했습니다."));
        }
    }
    
    /**
     * 전체 브랜드 목록 조회 (매니저용 - 브랜드 추가 가능)
     * GET /api/manager/brands/public
     */
    @GetMapping("/brands/public")
    public ResponseEntity<ApiResponse<List<BrandListResponse>>> getAllBrands(
            @RequestHeader(value = "Manager-Id", required = false) Long managerId) {
        try {
            if (managerId == null) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("MISSING_HEADER", "Manager-Id 헤더가 필요합니다."));
            }
            
            List<BrandListResponse> response = managerBrandService.getAllBrands(managerId);
            return ResponseEntity.ok(ApiResponse.success(response, "전체 브랜드 목록 조회가 완료되었습니다."));
        } catch (Exception e) {
            log.error("전체 브랜드 목록 조회 실패: managerId={}, error={}", managerId, e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("INTERNAL_ERROR", "서버 오류가 발생했습니다."));
        }
    }
    
    /**
     * 내 브랜드 목록 조회 (전체 목록)
     * GET /api/v1/manager/brands
     */
    @GetMapping("/brands")
    public ResponseEntity<ApiResponse<List<BrandListResponse>>> getManagerBrands(
            @RequestHeader("Manager-Id") Long managerId) {
        try {
            List<BrandListResponse> response = managerBrandService.getManagerBrands(managerId);
            return ResponseEntity.ok(ApiResponse.success(response, "내 브랜드 목록 조회가 완료되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("INVALID_REQUEST", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("INTERNAL_ERROR", "서버 오류가 발생했습니다."));
        }
    }
    
    /**
     * 내 브랜드 상세 조회
     * GET /api/v1/manager/brands/{id}
     */
    @GetMapping("/brands/{id}")
    public ResponseEntity<ApiResponse<BrandDetailResponse>> getManagerBrandDetail(
            @PathVariable Long id,
            @RequestHeader("Manager-Id") Long managerId) {
        try {
            BrandDetailResponse response = managerBrandService.getManagerBrandDetail(id, managerId);
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
     * 브랜드 등록 (추가 브랜드)
     * POST /api/v1/manager/brands
     */
    @PostMapping("/brands")
    public ResponseEntity<ApiResponse<BrandDetailResponse>> createBrand(
            @RequestHeader("Manager-Id") Long managerId,
            @Valid @RequestBody BrandCreateRequest request) {
        try {
            BrandDetailResponse response = managerBrandService.createBrand(request, managerId);
            return ResponseEntity.ok(ApiResponse.success(response, "브랜드 등록이 완료되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("REGISTRATION_FAILED", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("INTERNAL_ERROR", "서버 오류가 발생했습니다."));
        }
    }
    
    /**
     * 브랜드 수정
     * PUT /api/v1/manager/brands/{id}
     */
    @PutMapping("/brands/{id}")
    public ResponseEntity<ApiResponse<BrandDetailResponse>> updateBrand(
            @PathVariable Long id,
            @RequestHeader("Manager-Id") Long managerId,
            @Valid @RequestBody BrandUpdateRequest request) {
        try {
            BrandDetailResponse response = managerBrandService.updateBrand(id, request, managerId);
            return ResponseEntity.ok(ApiResponse.success(response, "브랜드 수정이 완료되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("UPDATE_FAILED", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("INTERNAL_ERROR", "서버 오류가 발생했습니다."));
        }
    }
    
    /**
     * 브랜드 삭제
     * DELETE /api/v1/manager/brands/{id}
     */
    @DeleteMapping("/brands/{id}")
    public ResponseEntity<ApiResponse<String>> deleteBrand(
            @PathVariable Long id,
            @RequestHeader("Manager-Id") Long managerId) {
        try {
            managerBrandService.deleteBrand(id, managerId);
            return ResponseEntity.ok(ApiResponse.success("브랜드 삭제가 완료되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("DELETE_FAILED", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("INTERNAL_ERROR", "서버 오류가 발생했습니다."));
        }
    }
}
