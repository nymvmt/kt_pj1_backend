package com.kt.backendapp.dto.response.brand;

import com.kt.backendapp.entity.Brand;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrandListResponse {
    private Long brandId;
    private String brandName;
    private String categoryName;
    private Long viewCount;
    private Long saveCount;
    private BigDecimal initialCost;
    private BigDecimal avgMonthlyRevenue;
    private String managerName;
    
    // 추가 정보 (필요시)
    private boolean isSaved;        // 현재 사용자가 찜했는지 여부
    
    // Entity → DTO 변환 메소드
    public static BrandListResponse from(Brand brand) {
        return BrandListResponse.builder()
            .brandId(brand.getBrandId())
            .brandName(brand.getBrandName())
            .categoryName(brand.getCategory() != null ? brand.getCategory().getCategoryName() : null)
            .viewCount(brand.getDetails() != null ? brand.getDetails().getViewCount() : 0L)
            .saveCount(brand.getDetails() != null ? brand.getDetails().getSaveCount() : 0L)
            .initialCost(brand.getDetails() != null ? brand.getDetails().getInitialCost() : null)
            .avgMonthlyRevenue(brand.getDetails() != null ? brand.getDetails().getAvgMonthlyRevenue() : null)
            .managerName(brand.getManager() != null ? brand.getManager().getName() : null)
            .isSaved(false) // 기본값, Service에서 설정
            .build();
    }
    
    // 찜 상태 포함 변환 메소드
    public static BrandListResponse from(Brand brand, boolean isSaved) {
        BrandListResponse response = from(brand);
        response.setSaved(isSaved);
        return response;
    }
}
