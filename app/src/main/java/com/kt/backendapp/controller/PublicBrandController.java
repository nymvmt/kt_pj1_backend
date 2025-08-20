package com.kt.backendapp.controller;

import com.kt.backendapp.dto.common.ApiResponse;
import com.kt.backendapp.dto.response.brand.BrandDetailResponse;
import com.kt.backendapp.dto.response.brand.BrandListResponse;
import com.kt.backendapp.service.PublicBrandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public")
@RequiredArgsConstructor
@Slf4j
public class PublicBrandController {
    
    private final PublicBrandService publicBrandService;
    
    /**
     * 공개 브랜드 목록 조회 (인증 불필요)
     * GET /api/v1/public/brands
     */
    @GetMapping("/brands")
    public ResponseEntity<ApiResponse<List<BrandListResponse>>> getPublicBrands() {
        try {
            List<BrandListResponse> response = publicBrandService.getPublicBrands();
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
}
