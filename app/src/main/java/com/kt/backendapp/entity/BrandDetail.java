package com.kt.backendapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "brand_detail")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrandDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detail_id")
    private Long detailId;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;
    
    @Column(name = "view_count", nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    @Builder.Default
    private Long viewCount = 0L;
    
    @Column(name = "save_count", nullable = false)
    @Builder.Default
    private Long saveCount = 0L;
    
    @Column(name = "initial_cost", precision = 15, scale = 2)
    private BigDecimal initialCost;
    
    @Column(name = "total_investment", precision = 15, scale = 2)
    private BigDecimal totalInvestment;
    
    @Column(name = "avg_monthly_revenue", precision = 15, scale = 2)
    private BigDecimal avgMonthlyRevenue;
    
    @Column(name = "store_count")
    private Integer storeCount;
    
    @Column(name = "brand_description", length = 200)
    private String brandDescription;
}
