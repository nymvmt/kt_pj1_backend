package com.kt.backendapp.service;

import com.kt.backendapp.dto.response.brand.BrandDetailResponse;
import com.kt.backendapp.dto.response.brand.BrandListResponse;

import java.util.List;
import java.util.Map;

/**
 * 사용자 브랜드 관련 서비스 인터페이스
 */
public interface IUserBrandService {
    
    /**
     * 유저용 브랜드 목록 조회 (찜 상태 포함)
     */
    List<BrandListResponse> getUserBrands(Long userId);
    
    /**
     * 유저용 브랜드 상세 조회 (찜 상태, 관련 브랜드, 카테고리 통계 포함)
     */
    BrandDetailResponse getUserBrandDetail(Long brandId, Long userId);
    
    /**
     * 브랜드 찜하기/찜해제 토글
     */
    boolean toggleSavedBrand(Long brandId, Long userId);
    
    /**
     * 브랜드 찜하기
     */
    void saveBrand(Long brandId, Long userId);
    
    /**
     * 브랜드 찜 해제
     */
    void unsaveBrand(Long brandId, Long userId);
    
    /**
     * 사용자의 찜한 브랜드 목록 조회
     */
    List<BrandListResponse> getSavedBrands(Long userId);
    
    /**
     * 브랜드 찜 상태 조회
     */
    Map<Long, Boolean> getBrandSaveStatus(List<Long> brandIds, Long userId);
}
