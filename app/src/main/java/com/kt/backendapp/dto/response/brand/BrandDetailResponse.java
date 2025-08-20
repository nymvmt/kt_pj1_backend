package com.kt.backendapp.dto.response.brand;

import com.kt.backendapp.entity.Brand;
import com.kt.backendapp.entity.BrandDetail;
import com.kt.backendapp.entity.BrandCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrandDetailResponse {
    // 기본 정보
    private Long brandId;
    private String brandName;
    private String categoryName;
    
    // 매니저 정보
    private ManagerInfo manager;
    
    // 통계 정보
    private Long viewCount;
    private Long saveCount;
    
    // 재무 정보
    private BigDecimal initialCost;
    private BigDecimal totalInvestment;
    private BigDecimal avgMonthlyRevenue;
    private Integer storeCount;
    private String brandDescription;
    
    // 사용자 상태
    private boolean isSaved;
    
    // 카테고리 통계 비교
    private CategoryStats categoryStats;
    
    // 관련 브랜드들
    private List<BrandListResponse> relatedBrands;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ManagerInfo {
        private Long managerId;
        private String name;
        private String email;
        private String phone;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CategoryStats {
        private BigDecimal avgInitialCost;
        private BigDecimal avgTotalInvestment;
        private BigDecimal avgMonthlyRevenue;
        private Double avgStoreCount;
        
        // 경쟁력 지표 (현재 브랜드가 평균 대비 얼마나 좋은지)
        private CompetitivenessScore competitiveness;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CompetitivenessScore {
        private double initialCostScore;      // 가맹비 경쟁력 (낮을수록 좋음, 0-100)
        private double revenueScore;          // 매출 경쟁력 (높을수록 좋음, 0-100)
        private double investmentScore;       // 투자비 경쟁력 (낮을수록 좋음, 0-100)
        private double storeCountScore;       // 매장수 경쟁력 (높을수록 좋음, 0-100)
        private double overallScore;          // 종합 점수 (0-100)
    }
    
    // Entity → DTO 변환 메소드
    public static BrandDetailResponse from(Brand brand) {
        if (brand == null) {
            return null;
        }
        
        // 기본 정보 null check 강화
        Long brandId = brand.getBrandId();
        String brandName = brand.getBrandName() != null ? brand.getBrandName() : "";
        
        if (brandId == null) {
            throw new IllegalArgumentException("브랜드 ID가 null입니다.");
        }
        
        // 매니저 정보 null check 강화
        ManagerInfo managerInfo = null;
        if (brand.getManager() != null) {
            try {
                managerInfo = ManagerInfo.builder()
                    .managerId(brand.getManager().getManagerId())
                    .name(brand.getManager().getName() != null ? brand.getManager().getName() : "")
                    .email(brand.getManager().getEmail() != null ? brand.getManager().getEmail() : "")
                    .phone(brand.getManager().getPhone() != null ? brand.getManager().getPhone() : "")
                    .build();
            } catch (Exception e) {
                // 매니저 정보 생성 실패 시 null로 설정
                managerInfo = null;
            }
        }
        
        // 연관 엔티티 안전하게 접근 - null check 강화
        BrandDetail details = brand.getDetails();
        BrandCategory category = brand.getCategory();
        
        // 카테고리명 안전하게 추출
        String categoryName = "";
        if (category != null) {
            try {
                categoryName = category.getCategoryName() != null ? category.getCategoryName() : "";
            } catch (Exception e) {
                categoryName = "";
            }
        }
        
        // 상세 정보 안전하게 추출
        Long viewCount = 0L;
        Long saveCount = 0L;
        BigDecimal initialCost = null;
        BigDecimal totalInvestment = null;
        BigDecimal avgMonthlyRevenue = null;
        Integer storeCount = null;
        String brandDescription = "";
        
        if (details != null) {
            try {
                viewCount = details.getViewCount() != null ? details.getViewCount() : 0L;
                saveCount = details.getSaveCount() != null ? details.getSaveCount() : 0L;
                initialCost = details.getInitialCost();
                totalInvestment = details.getTotalInvestment();
                avgMonthlyRevenue = details.getAvgMonthlyRevenue();
                storeCount = details.getStoreCount();
                brandDescription = details.getBrandDescription() != null ? details.getBrandDescription() : "";
            } catch (Exception e) {
                // 상세 정보 추출 실패 시 기본값 사용
                viewCount = 0L;
                saveCount = 0L;
                initialCost = null;
                totalInvestment = null;
                avgMonthlyRevenue = null;
                storeCount = null;
                brandDescription = "";
            }
        }
        
        return BrandDetailResponse.builder()
            .brandId(brandId)
            .brandName(brandName)
            .categoryName(categoryName)
            .manager(managerInfo)
            .viewCount(viewCount)
            .saveCount(saveCount)
            .initialCost(initialCost)
            .totalInvestment(totalInvestment)
            .avgMonthlyRevenue(avgMonthlyRevenue)
            .storeCount(storeCount)
            .brandDescription(brandDescription)
            .isSaved(false) // 기본값, Service에서 설정
            .build();
    }
    
    // 찜 상태, 카테고리 통계, 관련 브랜드 포함 변환 메소드
    public static BrandDetailResponse from(Brand brand, boolean isSaved, CategoryStats categoryStats, List<BrandListResponse> relatedBrands) {
        BrandDetailResponse response = from(brand);
        if (response != null) {
            response.setSaved(isSaved);
            response.setCategoryStats(categoryStats);
            
            // 관련 브랜드 null check 강화
            List<BrandListResponse> safeRelatedBrands = new ArrayList<>();
            if (relatedBrands != null) {
                try {
                    safeRelatedBrands = relatedBrands.stream()
                        .filter(relatedBrand -> relatedBrand != null)
                        .collect(Collectors.toList());
                } catch (Exception e) {
                    safeRelatedBrands = new ArrayList<>();
                }
            }
            response.setRelatedBrands(safeRelatedBrands);
        }
        return response;
    }
}
