package com.kt.backendapp.dto.response.brand;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrandStatsResponse {
    private Long categoryId;
    private String categoryName;
    
    // 카테고리 평균 통계
    private BigDecimal avgInitialCost;
    private BigDecimal avgTotalInvestment;
    private BigDecimal avgMonthlyRevenue;
    private Double avgStoreCount;
    
    // 카테고리 내 브랜드 수
    private Long brandCount;
    
    // 최고/최저 값들
    private BigDecimal maxInitialCost;
    private BigDecimal minInitialCost;
    private BigDecimal maxMonthlyRevenue;
    private BigDecimal minMonthlyRevenue;
    
    // 통계 데이터로부터 생성하는 정적 메소드
    public static BrandStatsResponse of(Long categoryId, String categoryName, 
                                       BigDecimal avgInitialCost, BigDecimal avgTotalInvestment,
                                       BigDecimal avgMonthlyRevenue, Double avgStoreCount,
                                       Long brandCount, BigDecimal maxInitialCost, 
                                       BigDecimal minInitialCost, BigDecimal maxMonthlyRevenue,
                                       BigDecimal minMonthlyRevenue) {
        return BrandStatsResponse.builder()
            .categoryId(categoryId)
            .categoryName(categoryName)
            .avgInitialCost(avgInitialCost)
            .avgTotalInvestment(avgTotalInvestment)
            .avgMonthlyRevenue(avgMonthlyRevenue)
            .avgStoreCount(avgStoreCount)
            .brandCount(brandCount)
            .maxInitialCost(maxInitialCost)
            .minInitialCost(minInitialCost)
            .maxMonthlyRevenue(maxMonthlyRevenue)
            .minMonthlyRevenue(minMonthlyRevenue)
            .build();
    }
}
