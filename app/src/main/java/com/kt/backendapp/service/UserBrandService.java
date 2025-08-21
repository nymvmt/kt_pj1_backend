package com.kt.backendapp.service;

import com.kt.backendapp.dto.response.brand.BrandDetailResponse;
import com.kt.backendapp.dto.response.brand.BrandListResponse;
import com.kt.backendapp.entity.Brand;
import com.kt.backendapp.entity.SavedBrand;
import com.kt.backendapp.repository.BrandRepository;
import com.kt.backendapp.repository.BrandDetailRepository;
import com.kt.backendapp.repository.SavedBrandRepository;
import com.kt.backendapp.repository.UserRepository;
import com.kt.backendapp.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserBrandService {
    
    private final BrandRepository brandRepository;
    private final SavedBrandRepository savedBrandRepository;
    private final BrandDetailRepository brandDetailRepository;
    private final ViewCountService viewCountService;
    private final UserRepository userRepository;
    
    /**
     * 유저용 브랜드 목록 조회 (찜 상태 포함)
     */
    public List<BrandListResponse> getUserBrands(Long userId) {
        log.info("=== 유저용 브랜드 목록 조회 시작: userId={} ===", userId);
        
        try {
            // 전체 브랜드 목록 조회 (연관 엔티티 포함)
            List<Brand> brands = brandRepository.findAllWithDetails();
            log.info("Repository에서 조회된 브랜드 수: {}", brands != null ? brands.size() : "null");
            
            if (brands != null && !brands.isEmpty()) {
                log.debug("첫 번째 브랜드 정보: {}", brands.get(0));
            }
            
            // Brand → BrandListResponse 변환 (찜 상태 포함)
            List<BrandListResponse> responses = brands.stream()
                .map(brand -> {
                    boolean isSaved = savedBrandRepository.existsByUserUserIdAndBrandBrandId(userId, brand.getBrandId());
                    return BrandListResponse.from(brand, isSaved);
                })
                .collect(Collectors.toList());
            
            log.info("변환 완료된 응답 수: {}", responses.size());
            log.info("=== 유저용 브랜드 목록 조회 완료 ===");
            return responses;
            
        } catch (Exception e) {
            log.error("유저용 브랜드 목록 조회 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * 유저용 브랜드 상세 조회 (찜 상태, 관련 브랜드, 카테고리 통계 포함)
     */
    public BrandDetailResponse getUserBrandDetail(Long brandId, Long userId) {
        log.info("=== 유저용 브랜드 상세 조회 시작: brandId={}, userId={} ===", brandId, userId);
        
        try {
            // 브랜드 기본 정보 조회
            Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 브랜드입니다."));
            
            // null check 강화
            if (brand == null) {
                throw new IllegalArgumentException("브랜드 정보를 찾을 수 없습니다.");
            }
            
            log.debug("브랜드 기본 정보 조회 완료: {}", brand.getBrandName());
            
            // 조회수 증가 (시스템 관리값) - 공통 서비스 사용
            viewCountService.incrementViewCount(brandId);
            
            // 찜 상태 확인 - null check 강화
            boolean isSaved = false;
            try {
                isSaved = savedBrandRepository.existsByUserUserIdAndBrandBrandId(userId, brandId);
                log.debug("찜 상태 확인 완료: {}", isSaved);
            } catch (Exception e) {
                log.warn("찜 상태 확인 중 오류 발생: {}", e.getMessage());
                isSaved = false; // 기본값으로 설정
            }
            
            // 카테고리 통계 계산 (계산값) - 임시로 비활성화하여 기본 기능 테스트
            BrandDetailResponse.CategoryStats categoryStats = null;
            log.debug("카테고리 통계 계산을 임시로 비활성화했습니다.");
            // TODO: 카테고리 통계 계산 기능 활성화
            // if (brand.getCategory() != null && brand.getCategory().getCategoryId() != null) {
            //     try {
            //         log.debug("카테고리 통계 계산 시작: categoryId={}", brand.getCategory().getCategoryId());
            //         categoryStats = calculateCategoryStats(brand.getCategory().getCategoryId());
            //         log.debug("카테고리 통계 계산 완료");
            //     } catch (Exception e) {
            //         log.warn("카테고리 통계 계산 중 오류 발생: {}", e.getMessage());
            //         categoryStats = null;
            //     }
            // } else {
            //     log.debug("카테고리 정보가 없어 통계 계산을 건너뜁니다.");
            // }
            
            // 관련 브랜드 조회 (계산값 - 같은 카테고리, 최대 5개) - null check 강화하여 활성화
            List<BrandListResponse> relatedBrandResponses = new ArrayList<>();
            if (brand.getCategory() != null && brand.getCategory().getCategoryId() != null) {
                try {
                    log.debug("관련 브랜드 조회 시작: categoryId={}", brand.getCategory().getCategoryId());
                    List<Brand> relatedBrands = brandRepository.findRelatedBrands(
                        brand.getCategory().getCategoryId(), 
                        brandId
                    );
                    
                    if (relatedBrands != null && !relatedBrands.isEmpty()) {
                        relatedBrandResponses = relatedBrands.stream()
                            .map(relatedBrand -> {
                                try {
                                    boolean isRelatedSaved = savedBrandRepository.existsByUserUserIdAndBrandBrandId(userId, relatedBrand.getBrandId());
                                    return BrandListResponse.from(relatedBrand, isRelatedSaved);
                                } catch (Exception e) {
                                    log.warn("관련 브랜드 찜 상태 확인 중 오류: {}", e.getMessage());
                                    return BrandListResponse.from(relatedBrand, false);
                                }
                            })
                            .collect(Collectors.toList());
                        log.debug("관련 브랜드 조회 완료: {}개", relatedBrandResponses.size());
                    } else {
                        log.debug("관련 브랜드가 없습니다.");
                    }
                } catch (Exception e) {
                    log.warn("관련 브랜드 조회 중 오류 발생: {}", e.getMessage());
                    relatedBrandResponses = new ArrayList<>();
                }
            } else {
                log.debug("카테고리 정보가 없어 관련 브랜드 조회를 건너뜁니다.");
            }
            
            log.debug("DTO 변환 시작");
            try {
                BrandDetailResponse response = BrandDetailResponse.from(brand, isSaved, categoryStats, relatedBrandResponses);
                log.debug("DTO 변환 완료");
                log.info("=== 유저용 브랜드 상세 조회 완료 ===");
                return response;
            } catch (Exception e) {
                log.error("DTO 변환 중 오류 발생: {}", e.getMessage(), e);
                // DTO 변환 실패 시 기본 정보만 포함하여 응답 생성
                BrandDetailResponse fallbackResponse = BrandDetailResponse.builder()
                    .brandId(brand.getBrandId())
                    .brandName(brand.getBrandName() != null ? brand.getBrandName() : "")
                    .categoryName("")
                    .manager(null)
                    .viewCount(0L)
                    .saveCount(0L)
                    .initialCost(null)
                    .totalInvestment(null)
                    .avgMonthlyRevenue(null)
                    .storeCount(null)
                    .brandDescription("")
                    .isSaved(isSaved)
                    .categoryStats(null)
                    .relatedBrands(new ArrayList<>())
                    .build();
                
                log.info("=== 유저용 브랜드 상세 조회 완료 (fallback) ===");
                return fallbackResponse;
            }
            
        } catch (Exception e) {
            log.error("유저용 브랜드 상세 조회 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * 브랜드 찜하기/찜해제 토글
     */
    @Transactional
    public boolean toggleSavedBrand(Long brandId, Long userId) {
        log.info("=== 브랜드 찜하기/찜해제 토글: brandId={}, userId={} ===", brandId, userId);
        
        try {
            // 입력값 검증
            if (brandId == null) {
                throw new IllegalArgumentException("브랜드 ID가 null입니다.");
            }
            if (userId == null) {
                throw new IllegalArgumentException("사용자 ID가 null입니다.");
            }
            
            log.info("입력값 검증 완료: brandId={}, userId={}", brandId, userId);
            
            // 이미 찜한 상태인지 확인
            boolean isAlreadySaved = savedBrandRepository.existsByUserUserIdAndBrandBrandId(userId, brandId);
            log.info("현재 찜 상태: {}", isAlreadySaved);
            
            if (isAlreadySaved) {
                // 이미 찜한 상태면 찜해제
                log.info("이미 찜한 브랜드입니다. 찜해제를 진행합니다.");
                savedBrandRepository.deleteByUserUserIdAndBrandBrandId(userId, brandId);
                
                // 브랜드 찜 수 감소
                brandDetailRepository.decrementSaveCount(brandId);
                log.info("브랜드 찜 수 감소 완료");
                
                log.info("=== 브랜드 찜해제 완료 ===");
                return false; // 찜해제됨
            } else {
                // 찜하지 않은 상태면 찜하기
                log.info("찜하지 않은 브랜드입니다. 찜하기를 진행합니다.");
                
                // 브랜드 존재 여부 확인
                log.info("브랜드 존재 여부 확인 중: brandId={}", brandId);
                Brand brand = brandRepository.findById(brandId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 브랜드입니다."));
                log.info("브랜드 조회 완료: {}", brand.getBrandName());
                
                // 사용자 존재 여부 확인
                log.info("사용자 존재 여부 확인 중: userId={}", userId);
                User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
                log.info("사용자 조회 완료: {}", user.getName());
                
                // SavedBrand 생성 및 저장
                log.info("SavedBrand 생성 중...");
                SavedBrand savedBrand = SavedBrand.builder()
                    .user(user)
                    .brand(brand)
                    .savedAt(LocalDateTime.now())
                    .build();
                
                log.info("SavedBrand 저장 중...");
                savedBrandRepository.save(savedBrand);
                
                // 브랜드 찜 수 증가
                brandDetailRepository.incrementSaveCount(brandId);
                log.info("브랜드 찜 수 증가 완료");
                
                log.info("=== 브랜드 찜하기 완료 ===");
                return true; // 찜하기됨
            }
            
        } catch (Exception e) {
            log.error("브랜드 찜하기/찜해제 토글 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 브랜드 찜하기
     */
    @Transactional
    public void saveBrand(Long brandId, Long userId) {
        log.info("=== 브랜드 찜하기: brandId={}, userId={} ===", brandId, userId);
        
        try {
            // 이미 찜한 상태인지 확인
            if (savedBrandRepository.existsByUserUserIdAndBrandBrandId(userId, brandId)) {
                throw new IllegalArgumentException("이미 찜한 브랜드입니다.");
            }
            
            // 브랜드 존재 여부 확인
            Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 브랜드입니다."));
            
            // 사용자 존재 여부 확인
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
            
            // SavedBrand 생성 및 저장
            SavedBrand savedBrand = SavedBrand.builder()
                .user(user)
                .brand(brand)
                .savedAt(LocalDateTime.now())
                .build();
            
            savedBrandRepository.save(savedBrand);
            
            log.info("=== 브랜드 찜하기 완료 ===");
            
        } catch (Exception e) {
            log.error("브랜드 찜하기 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * 브랜드 찜 해제
     */
    @Transactional
    public void unsaveBrand(Long brandId, Long userId) {
        log.info("=== 브랜드 찜 해제: brandId={}, userId={} ===", brandId, userId);
        
        try {
            // 찜한 상태인지 확인
            if (!savedBrandRepository.existsByUserUserIdAndBrandBrandId(userId, brandId)) {
                throw new IllegalArgumentException("찜하지 않은 브랜드입니다.");
            }
            
            // SavedBrand 엔티티 삭제
            savedBrandRepository.deleteByUserUserIdAndBrandBrandId(userId, brandId);
            
            log.info("=== 브랜드 찜 해제 완료 ===");
            
        } catch (Exception e) {
            log.error("브랜드 찜 해제 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * 사용자의 찜한 브랜드 목록 조회
     */
    public List<BrandListResponse> getSavedBrands(Long userId) {
        log.info("=== 찜한 브랜드 목록 조회 시작: userId={} ===", userId);
        
        try {
            // 사용자의 찜한 브랜드 목록 조회 (브랜드 정보 포함)
            List<SavedBrand> savedBrands = savedBrandRepository.findByUserUserIdWithBrand(userId);
            log.info("Repository에서 조회된 찜한 브랜드 수: {}", savedBrands != null ? savedBrands.size() : "null");
            
            if (savedBrands == null || savedBrands.isEmpty()) {
                log.info("찜한 브랜드가 없습니다.");
                return new ArrayList<>();
            }
            
            // SavedBrand → BrandListResponse 변환
            List<BrandListResponse> responses = savedBrands.stream()
                .map(savedBrand -> {
                    Brand brand = savedBrand.getBrand();
                    if (brand == null) {
                        log.warn("찜한 브랜드의 브랜드 정보가 null입니다: savedBrandId={}", savedBrand.getSaveId());
                        return null;
                    }
                    return BrandListResponse.from(brand, true); // 찜한 상태는 true
                })
                .filter(response -> response != null) // null 제거
                .collect(Collectors.toList());
            
            log.info("변환 완료된 응답 수: {}", responses.size());
            log.info("=== 찜한 브랜드 목록 조회 완료 ===");
            return responses;
            
        } catch (Exception e) {
            log.error("찜한 브랜드 목록 조회 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * 브랜드 찜 상태 조회
     */
    public Map<Long, Boolean> getBrandSaveStatus(List<Long> brandIds, Long userId) {
        log.info("=== 브랜드 찜 상태 조회 시작: brandIds={}, userId={} ===", brandIds, userId);
        
        try {
            Map<Long, Boolean> saveStatus = new HashMap<>();
            
            for (Long brandId : brandIds) {
                boolean isSaved = savedBrandRepository.existsByUserUserIdAndBrandBrandId(userId, brandId);
                saveStatus.put(brandId, isSaved);
            }
            
            log.info("=== 브랜드 찜 상태 조회 완료 ===");
            return saveStatus;
            
        } catch (Exception e) {
            log.error("브랜드 찜 상태 조회 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    // === 유틸리티 메소드들 ===
    
    /**
     * 카테고리 통계 계산 (비활성화)
     * TODO: 향후 필요시 활성화
     */
    // private BrandDetailResponse.CategoryStats calculateCategoryStats(Long categoryId) {
    //     // 카테고리 통계 데이터 조회
    //     Object[] stats = brandDetailRepository.calculateCategoryStats(categoryId);
    //     
    //     if (stats[0] == null) {
    //         // 해당 카테고리에 브랜드가 없는 경우 기본값 반환
    //         return BrandDetailResponse.CategoryStats.builder()
    //             .avgInitialCost(BigDecimal.ZERO)
    //             .avgTotalInvestment(BigDecimal.ZERO)
    //             .avgMonthlyRevenue(BigDecimal.ZERO)
    //             .avgStoreCount(0.0)
    //             .competitiveness(BrandDetailResponse.CompetitivenessScore.builder()
    //                 .initialCostScore(50.0)
    //                 .revenueScore(50.0)
    //                 .investmentScore(50.0)
    //                 .storeCountScore(50.0)
    //                 .overallScore(50.0)
    //                 .build())
    //             .build();
    //     }
    //     
    //     // 통계 데이터 추출
    //     BigDecimal avgInitialCost = (BigDecimal) stats[0];
    //     BigDecimal avgTotalInvestment = (BigDecimal) stats[1];
    //     BigDecimal avgMonthlyRevenue = (BigDecimal) stats[2];
    //     Double avgStoreCount = (Double) stats[3];
    //     BigDecimal maxInitialCost = (BigDecimal) stats[4];
    //     BigDecimal minInitialCost = (BigDecimal) stats[5];
    //     BigDecimal maxMonthlyRevenue = (BigDecimal) stats[6];
    //     BigDecimal minMonthlyRevenue = (BigDecimal) stats[7];
    //     Long totalBrands = (Long) stats[8];
    //     
    //     // 경쟁력 점수 계산 (현재 브랜드 vs 카테고리 평균)
    //     BrandDetailResponse.CategoryStats.CompetitivenessScore competitiveness = 
    //         calculateCompetitivenessScore(avgInitialCost, avgTotalInvestment, avgMonthlyRevenue, avgStoreCount);
    //     
    //     return BrandDetailResponse.CategoryStats.builder()
    //         .avgInitialCost(avgInitialCost != null ? avgInitialCost : BigDecimal.ZERO)
    //         .avgTotalInvestment(avgTotalInvestment != null ? avgTotalInvestment : BigDecimal.ZERO)
    //         .avgMonthlyRevenue(avgMonthlyRevenue != null ? avgMonthlyRevenue : BigDecimal.ZERO)
    //         .avgStoreCount(avgStoreCount != null ? avgStoreCount : 0.0)
    //         .competitiveness(competitiveness)
    //         .build();
    // }
    
    /**
     * 경쟁력 점수 계산 (비활성화)
     * TODO: 향후 필요시 활성화
     */
    // private BrandDetailResponse.CompetitivenessScore calculateCompetitivenessScore(
    //         BigDecimal avgInitialCost, BigDecimal avgTotalInvestment, 
    //         BigDecimal avgMonthlyRevenue, Double avgStoreCount) {
    //     
    //     // 실제 경쟁력 점수 계산 로직
    //     // 현재는 샘플 값으로 반환 (추후 실제 브랜드 데이터와 비교하여 계산)
    //     return BrandDetailResponse.CompetitivenessScore.builder()
    //         .initialCostScore(75.0)    // 가맹비 경쟁력 (낮을수록 좋음, 평균 대비 25% 좋음)
    //         .revenueScore(80.0)        // 매출 경쟁력 (높을수록 좋음, 평균 대비 20% 좋음)
    //         .investmentScore(70.0)     // 투자비 경쟁력 (낮을수록 좋음, 평균 대비 15% 좋음)
    //         .storeCountScore(85.0)     // 매장수 경쟁력 (많을수록 좋음, 평균 대비 35% 좋음)
    //         .overallScore(77.5)        // 종합 점수
    //         .build();
    // }
}
