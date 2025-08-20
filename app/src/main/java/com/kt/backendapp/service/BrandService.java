package com.kt.backendapp.service;


import com.kt.backendapp.dto.response.brand.*;
import com.kt.backendapp.entity.*;
import com.kt.backendapp.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class BrandService {
    
    private final BrandRepository brandRepository;
    private final BrandCategoryRepository brandCategoryRepository;
    private final BrandDetailRepository brandDetailRepository;
    private final SavedBrandRepository savedBrandRepository;
    private final UserRepository userRepository;
    
    /**
     * 전체 브랜드 목록 조회
     */
    public List<BrandListResponse> getAllBrands(Long userId) {
        log.info("=== 전체 브랜드 목록 조회 시작 ===");
        log.info("요청 사용자 ID: {}", userId);
        
        try {
            // 전체 브랜드 목록 조회 (연관 엔티티 포함)
            log.debug("Repository 호출 시작: findAllWithDetails");
            List<Brand> brands = brandRepository.findAllWithDetails();
            log.info("Repository에서 조회된 브랜드 수: {}", brands != null ? brands.size() : "null");
            
            if (brands != null && !brands.isEmpty()) {
                log.debug("첫 번째 브랜드 정보: {}", brands.get(0));
            }
            
            // Brand → BrandListResponse 변환
            log.debug("DTO 변환 시작");
            List<BrandListResponse> responses = brands.stream()
                .map(brand -> {
                    try {
                        boolean isSaved = userId != null && 
                            savedBrandRepository.existsByUserUserIdAndBrandBrandId(userId, brand.getBrandId());
                        return BrandListResponse.from(brand, isSaved);
                    } catch (Exception e) {
                        log.error("브랜드 변환 중 오류 발생: brandId={}, error={}", 
                                brand.getBrandId(), e.getMessage(), e);
                        throw e;
                    }
                })
                .collect(Collectors.toList());
            
            log.info("변환 완료된 응답 수: {}", responses.size());
            log.info("=== 전체 브랜드 목록 조회 완료 ===");
            return responses;
            
        } catch (Exception e) {
            log.error("전체 브랜드 목록 조회 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * 브랜드 상세 조회 + 조회수 증가
     * 매니저 입력값 + 시스템 관리값 + 계산값 모두 포함
     */
    @Transactional
    public BrandDetailResponse getBrandDetail(Long brandId, Long userId) {
        // 브랜드 기본 정보 조회 (단순한 findById 사용)
        Brand brand = brandRepository.findById(brandId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 브랜드입니다."));
        
        // 조회수 증가 (시스템 관리값)
        brandDetailRepository.incrementViewCount(brandId);
        
        // 찜 상태 확인
        boolean isSaved = userId != null && 
            savedBrandRepository.existsByUserUserIdAndBrandBrandId(userId, brandId);
        
        // 카테고리 통계 계산 (계산값) - null 체크 추가
        BrandDetailResponse.CategoryStats categoryStats = null;
        if (brand.getCategory() != null) {
            try {
                categoryStats = calculateCategoryStats(brand.getCategory().getCategoryId());
            } catch (Exception e) {
                log.warn("카테고리 통계 계산 중 오류 발생: {}", e.getMessage());
                categoryStats = null;
            }
        }
        
        // 관련 브랜드 조회 (계산값 - 같은 카테고리, 최대 5개) - null 체크 추가
        List<BrandListResponse> relatedBrandResponses = new ArrayList<>();
        if (brand.getCategory() != null) {
            try {
                List<Brand> relatedBrands = brandRepository.findRelatedBrands(
                    brand.getCategory().getCategoryId(), 
                    brandId
                );
                
                relatedBrandResponses = relatedBrands.stream()
                    .map(relatedBrand -> {
                        boolean isRelatedSaved = userId != null && 
                            savedBrandRepository.existsByUserUserIdAndBrandBrandId(userId, relatedBrand.getBrandId());
                        return BrandListResponse.from(relatedBrand, isRelatedSaved);
                    })
                    .collect(Collectors.toList());
            } catch (Exception e) {
                log.warn("관련 브랜드 조회 중 오류 발생: {}", e.getMessage());
                relatedBrandResponses = new ArrayList<>();
            }
        }
        
        return BrandDetailResponse.from(brand, isSaved, categoryStats, relatedBrandResponses);
    }
    
    /**
     * 브랜드 찜하기
     */
    @Transactional
    public void saveBrand(Long brandId, Long userId) {
        // 사용자 존재 확인
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        
        // 브랜드 존재 확인
        Brand brand = brandRepository.findById(brandId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 브랜드입니다."));
        
        // 이미 찜한 브랜드인지 확인
        if (savedBrandRepository.existsByUserUserIdAndBrandBrandId(userId, brandId)) {
            throw new IllegalArgumentException("이미 찜한 브랜드입니다.");
        }
        
        // SavedBrand 생성 및 저장
        SavedBrand savedBrand = SavedBrand.builder()
            .user(user)
            .brand(brand)
            .build();
        savedBrandRepository.save(savedBrand);
        
        // 찜 수 증가 (시스템 관리값)
        brandDetailRepository.incrementSaveCount(brandId);
    }
    
    /**
     * 브랜드 찜 취소
     */
    @Transactional
    public void unsaveBrand(Long brandId, Long userId) {
        // 찜한 브랜드인지 확인
        if (!savedBrandRepository.existsByUserUserIdAndBrandBrandId(userId, brandId)) {
            throw new IllegalArgumentException("찜하지 않은 브랜드입니다.");
        }
        
        // SavedBrand 삭제
        savedBrandRepository.deleteByUserUserIdAndBrandBrandId(userId, brandId);
        
        // 찜 수 감소 (시스템 관리값)
        brandDetailRepository.decrementSaveCount(brandId);
    }
    
    /**
     * 내 찜 목록 조회
     */
    public List<BrandListResponse> getSavedBrands(Long userId) {
      List<SavedBrand> savedBrands = savedBrandRepository.findByUserUserIdWithBrand(userId);
      
      return savedBrands.stream()
          .map(savedBrand -> BrandListResponse.from(savedBrand.getBrand(), true))
          .collect(Collectors.toList());
  }
    
    /**
     * 카테고리 목록 조회
     */
    public List<CategoryResponse> getCategories() {
        List<BrandCategory> categories = brandCategoryRepository.findAll();
        
        return categories.stream()
            .map(category -> {
                Long brandCount = brandRepository.countByCategoryCategoryId(category.getCategoryId());
                return CategoryResponse.from(category, brandCount);
            })
            .collect(Collectors.toList());
    }
    
    // === 유틸리티 메소드들 ===
    /**
     * 카테고리 통계 계산 (실제 구현)
     * 같은 카테고리 브랜드들의 평균값과 경쟁력 점수
     */
    private BrandDetailResponse.CategoryStats calculateCategoryStats(Long categoryId) {
        // 카테고리 통계 데이터 조회
        Object[] stats = brandDetailRepository.calculateCategoryStats(categoryId);
        
        if (stats[0] == null) {
            // 해당 카테고리에 브랜드가 없는 경우 기본값 반환
            return BrandDetailResponse.CategoryStats.builder()
                .avgInitialCost(BigDecimal.ZERO)
                .avgTotalInvestment(BigDecimal.ZERO)
                .avgMonthlyRevenue(BigDecimal.ZERO)
                .avgStoreCount(0.0)
                .competitiveness(BrandDetailResponse.CompetitivenessScore.builder()
                    .initialCostScore(50.0)
                    .revenueScore(50.0)
                    .investmentScore(50.0)
                    .storeCountScore(50.0)
                    .overallScore(50.0)
                    .build())
                .build();
        }
        
        // 통계 데이터 추출
        BigDecimal avgInitialCost = (BigDecimal) stats[0];
        BigDecimal avgTotalInvestment = (BigDecimal) stats[1];
        BigDecimal avgMonthlyRevenue = (BigDecimal) stats[2];
        Double avgStoreCount = (Double) stats[3];
        BigDecimal maxInitialCost = (BigDecimal) stats[4];
        BigDecimal minInitialCost = (BigDecimal) stats[5];
        BigDecimal maxMonthlyRevenue = (BigDecimal) stats[6];
        BigDecimal minMonthlyRevenue = (BigDecimal) stats[7];
        Long totalBrands = (Long) stats[8];
        
        // 경쟁력 점수 계산 (현재 브랜드 vs 카테고리 평균)
        BrandDetailResponse.CompetitivenessScore competitiveness = 
            calculateCompetitivenessScore(avgInitialCost, avgTotalInvestment, avgMonthlyRevenue, avgStoreCount);
        
        return BrandDetailResponse.CategoryStats.builder()
            .avgInitialCost(avgInitialCost != null ? avgInitialCost : BigDecimal.ZERO)
            .avgTotalInvestment(avgTotalInvestment != null ? avgTotalInvestment : BigDecimal.ZERO)
            .avgMonthlyRevenue(avgMonthlyRevenue != null ? avgMonthlyRevenue : BigDecimal.ZERO)
            .avgStoreCount(avgStoreCount != null ? avgStoreCount : 0.0)
            .competitiveness(competitiveness)
            .build();
    }
    
    /**
     * 경쟁력 점수 계산
     */
    private BrandDetailResponse.CompetitivenessScore calculateCompetitivenessScore(
            BigDecimal avgInitialCost, BigDecimal avgTotalInvestment, 
            BigDecimal avgMonthlyRevenue, Double avgStoreCount) {
        
        // 실제 경쟁력 점수 계산 로직
        // 현재는 샘플 값으로 반환 (추후 실제 브랜드 데이터와 비교하여 계산)
        return BrandDetailResponse.CompetitivenessScore.builder()
            .initialCostScore(75.0)    // 가맹비 경쟁력 (낮을수록 좋음, 평균 대비 25% 좋음)
            .revenueScore(80.0)        // 매출 경쟁력 (높을수록 좋음, 평균 대비 20% 좋음)
            .investmentScore(70.0)     // 투자비 경쟁력 (낮을수록 좋음, 평균 대비 15% 좋음)
            .storeCountScore(85.0)     // 매장수 경쟁력 (많을수록 좋음, 평균 대비 35% 좋음)
            .overallScore(77.5)        // 종합 점수
            .build();
    }
}