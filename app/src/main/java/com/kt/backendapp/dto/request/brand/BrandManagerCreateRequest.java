package com.kt.backendapp.dto.request.brand;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrandManagerCreateRequest {
    
    // 매니저 정보
    @NotBlank(message = "매니저 이름은 필수입니다.")
    @Size(max = 100, message = "매니저 이름은 100자를 초과할 수 없습니다.")
    private String name;
    
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @Size(max = 255, message = "이메일은 255자를 초과할 수 없습니다.")
    private String email;
    
    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 6, max = 255, message = "비밀번호는 6자 이상 255자 이하여야 합니다.")
    private String password;
    
    @Size(max = 20, message = "전화번호는 20자를 초과할 수 없습니다.")
    private String phone;
    
    // 브랜드 정보 (필수 - 동시 등록)
    @NotBlank(message = "브랜드명은 필수입니다.")
    @Size(max = 200, message = "브랜드명은 200자를 초과할 수 없습니다.")
    private String brandName;
    
    @NotNull(message = "카테고리는 필수입니다.")
    private Long categoryId;
    
    // 브랜드 상세 정보 (모두 필수)
    @NotNull(message = "가맹비는 필수입니다.")
    @Positive(message = "가맹비는 0보다 커야 합니다.")
    private BigDecimal initialCost;
    
    @NotNull(message = "총 창업비용은 필수입니다.")
    @Positive(message = "총 창업비용은 0보다 커야 합니다.")
    private BigDecimal totalInvestment;
    
    @NotNull(message = "평균 월매출은 필수입니다.")
    @Positive(message = "평균 월매출은 0보다 커야 합니다.")
    private BigDecimal avgMonthlyRevenue;
    
    @NotNull(message = "매장수는 필수입니다.")
    @Positive(message = "매장수는 0보다 커야 합니다.")
    private Integer storeCount;
    
    @NotBlank(message = "브랜드 설명은 필수입니다.")
    @Size(max = 500, message = "브랜드 설명은 500자를 초과할 수 없습니다.")
    private String brandDescription;
}