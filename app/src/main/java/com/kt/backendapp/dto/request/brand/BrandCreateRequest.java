package com.kt.backendapp.dto.request.brand;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrandCreateRequest {
    
    @NotBlank(message = "브랜드명은 필수입니다.")
    @Size(max = 200, message = "브랜드명은 200자를 초과할 수 없습니다.")
    private String brandName;
    
    @NotNull(message = "카테고리는 필수입니다.")
    private Long categoryId;
    
    @NotNull(message = "매니저는 필수입니다.")
    private Long managerId;
    
    // BrandDetail 정보
    @Positive(message = "가맹비는 0보다 커야 합니다.")
    private BigDecimal initialCost;
    
    @Positive(message = "총 창업비용은 0보다 커야 합니다.")
    private BigDecimal totalInvestment;
    
    @Positive(message = "평균 월매출은 0보다 커야 합니다.")
    private BigDecimal avgMonthlyRevenue;
    
    @Positive(message = "매장수는 0보다 커야 합니다.")
    private Integer storeCount;
    
    @Size(max = 200, message = "브랜드 설명은 200자를 초과할 수 없습니다.")
    private String brandDescription;
}