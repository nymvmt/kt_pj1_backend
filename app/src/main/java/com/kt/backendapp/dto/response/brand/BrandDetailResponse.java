package com.kt.backendapp.dto.response.brand;

import com.kt.backendapp.entity.Brand;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
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
        ManagerInfo managerInfo = null;
        if (brand.getManager() != null) {
            managerInfo = ManagerInfo.builder()
                .managerId(brand.getManager().getManagerId())
                .name(brand.getManager().getName())
                .email(brand.getManager().getEmail())
                .phone(brand.getManager().getPhone())
                .build();
        }
        
        return BrandDetailResponse.builder()
            .brandId(brand.getBrandId())
            .brandName(brand.getBrandName())
            .categoryName(brand.getCategory() != null ? brand.getCategory().getCategoryName() : null)
            .manager(managerInfo)
            .viewCount(brand.getDetails() != null ? brand.getDetails().getViewCount() : 0L)
            .saveCount(brand.getDetails() != null ? brand.getDetails().getSaveCount() : 0L)
            .initialCost(brand.getDetails() != null ? brand.getDetails().getInitialCost() : null)
            .totalInvestment(brand.getDetails() != null ? brand.getDetails().getTotalInvestment() : null)
            .avgMonthlyRevenue(brand.getDetails() != null ? brand.getDetails().getAvgMonthlyRevenue() : null)
            .storeCount(brand.getDetails() != null ? brand.getDetails().getStoreCount() : null)
            .brandDescription(brand.getDetails() != null ? brand.getDetails().getBrandDescription() : null)
            .isSaved(false) // 기본값, Service에서 설정
            .build();
    }
    
    // 찜 상태, 카테고리 통계, 관련 브랜드 포함 변환 메소드
    public static BrandDetailResponse from(Brand brand, boolean isSaved, CategoryStats categoryStats, List<BrandListResponse> relatedBrands) {
        BrandDetailResponse response = from(brand);
        response.setSaved(isSaved);
        response.setCategoryStats(categoryStats);
        response.setRelatedBrands(relatedBrands);
        return response;
    }
}
