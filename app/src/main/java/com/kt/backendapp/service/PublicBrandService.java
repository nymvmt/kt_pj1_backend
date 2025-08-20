package com.kt.backendapp.service;

import com.kt.backendapp.dto.response.brand.BrandDetailResponse;
import com.kt.backendapp.dto.response.brand.BrandListResponse;
import com.kt.backendapp.entity.Brand;
import com.kt.backendapp.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PublicBrandService {
    
    private final BrandRepository brandRepository;
    
    /**
     * 공개 브랜드 목록 조회 (기본 정보만)
     */
    public List<BrandListResponse> getPublicBrands() {
        log.info("=== 공개 브랜드 목록 조회 시작 ===");
        
        try {
            // 전체 브랜드 목록 조회 (연관 엔티티 포함)
            List<Brand> brands = brandRepository.findAllWithDetails();
            log.info("Repository에서 조회된 브랜드 수: {}", brands != null ? brands.size() : "null");
            
            if (brands != null && !brands.isEmpty()) {
                log.debug("첫 번째 브랜드 정보: {}", brands.get(0));
            }
            
            // Brand → BrandListResponse 변환 (찜 상태 제외)
            List<BrandListResponse> responses = brands.stream()
                .map(brand -> BrandListResponse.from(brand, false)) // 공개 API는 찜 상태 false
                .collect(Collectors.toList());
            
            log.info("변환 완료된 응답 수: {}", responses.size());
            log.info("=== 공개 브랜드 목록 조회 완료 ===");
            return responses;
            
        } catch (Exception e) {
            log.error("공개 브랜드 목록 조회 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * 공개 브랜드 상세 조회 (기본 정보만)
     */
    public BrandDetailResponse getPublicBrandDetail(Long brandId) {
        log.info("=== 공개 브랜드 상세 조회 시작: brandId={} ===", brandId);
        
        try {
            // 브랜드 기본 정보 조회
            Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 브랜드입니다."));
            
            // 조회수 증가 (시스템 관리값)
            // 공개 API에서는 조회수 증가하지 않음 (유저 API에서만)
            
            // 공개용 응답 생성 (찜 상태, 관련 브랜드, 카테고리 통계 제외)
            return BrandDetailResponse.from(brand, false, null, null);
            
        } catch (Exception e) {
            log.error("공개 브랜드 상세 조회 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
    }
}
