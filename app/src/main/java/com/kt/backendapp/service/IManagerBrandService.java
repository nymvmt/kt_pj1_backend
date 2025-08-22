package com.kt.backendapp.service;

import com.kt.backendapp.dto.request.brand.BrandCreateRequest;
import com.kt.backendapp.dto.request.brand.BrandUpdateRequest;
import com.kt.backendapp.dto.response.brand.BrandDetailResponse;
import com.kt.backendapp.dto.response.brand.BrandListResponse;
import com.kt.backendapp.dto.response.brand.CategoryResponse;

import java.util.List;

/**
 * 매니저 브랜드 관리 서비스 인터페이스
 */
public interface IManagerBrandService {
    
    /**
     * 카테고리 목록 조회
     */
    List<CategoryResponse> getCategories();
    
    /**
     * 전체 브랜드 목록 조회 (매니저용 - 브랜드 추가 가능)
     */
    List<BrandListResponse> getAllBrands(Long managerId);
    
    /**
     * 매니저의 브랜드 목록 조회 (전체 목록)
     */
    List<BrandListResponse> getManagerBrands(Long managerId);
    
    /**
     * 매니저의 브랜드 상세 조회
     */
    BrandDetailResponse getManagerBrandDetail(Long brandId, Long managerId);
    
    /**
     * 브랜드 등록
     */
    BrandDetailResponse createBrand(BrandCreateRequest request, Long managerId);
    
    /**
     * 브랜드 수정
     */
    BrandDetailResponse updateBrand(Long brandId, BrandUpdateRequest request, Long managerId);
    
    /**
     * 브랜드 삭제
     */
    void deleteBrand(Long brandId, Long managerId);
}
