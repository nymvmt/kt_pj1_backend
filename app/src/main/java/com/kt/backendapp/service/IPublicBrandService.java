package com.kt.backendapp.service;

import com.kt.backendapp.dto.response.brand.BrandDetailResponse;
import com.kt.backendapp.dto.response.brand.BrandListResponse;
import com.kt.backendapp.dto.response.brand.CategoryResponse;

import java.util.List;

/**
 * 공개 브랜드 관련 서비스 인터페이스
 */
public interface IPublicBrandService {
    
    /**
     * 공개 브랜드 목록 조회 (기본 정보만)
     */
    List<BrandListResponse> getPublicBrands(Long managerId);
    
    /**
     * 공개 브랜드 상세 조회 (기본 정보만)
     */
    BrandDetailResponse getPublicBrandDetail(Long brandId);
    
    /**
     * 카테고리별 브랜드 목록 조회
     */
    List<BrandListResponse> getBrandsByCategory(Long categoryId, Long managerId);
    
    /**
     * 브랜드 검색
     */
    List<BrandListResponse> searchBrands(String keyword, Long managerId);
    
    /**
     * 카테고리 목록 조회
     */
    List<CategoryResponse> getCategories();
}
