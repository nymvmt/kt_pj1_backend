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
    private Integer storeCount;
    private String brandDescription;
    private String managerName;
    
    // 추가 정보 (필요시)
    private boolean isSaved;        // 현재 사용자가 찜했는지 여부
    private boolean isManaged;      // 현재 매니저가 관리하는 브랜드인지 여부
    
    // Entity → DTO 변환 메소드
    public static BrandListResponse from(Brand brand) {
        if (brand == null) {
            return null;
        }
        
        return BrandListResponse.builder()
            .brandId(brand.getBrandId())
            .brandName(brand.getBrandName() != null ? brand.getBrandName() : "")
            .categoryName(brand.getCategory() != null ? brand.getCategory().getCategoryName() : "")
            .viewCount(brand.getDetails() != null ? brand.getDetails().getViewCount() : 0L)
            .saveCount(brand.getDetails() != null ? brand.getDetails().getSaveCount() : 0L)
            .initialCost(brand.getDetails() != null ? brand.getDetails().getInitialCost() : null)
            .avgMonthlyRevenue(brand.getDetails() != null ? brand.getDetails().getAvgMonthlyRevenue() : null)
            .storeCount(brand.getDetails() != null ? brand.getDetails().getStoreCount() : null)
            .brandDescription(brand.getDetails() != null ? brand.getDetails().getBrandDescription() : null)
            .managerName(brand.getManager() != null ? brand.getManager().getName() : "")
            .isSaved(false) // 기본값, Service에서 설정
            .isManaged(false) // 기본값, Service에서 설정
            .build();
    }
    
    // 찜 상태 포함 변환 메소드
    public static BrandListResponse from(Brand brand, boolean isSaved) {
        BrandListResponse response = from(brand);
        response.setSaved(isSaved);
        return response;
    }
    
    // 찜 상태와 관리 여부 포함 변환 메소드
    public static BrandListResponse from(Brand brand, boolean isSaved, boolean isManaged) {
        BrandListResponse response = from(brand, isSaved);
        response.setManaged(isManaged);
        return response;
    }
}
