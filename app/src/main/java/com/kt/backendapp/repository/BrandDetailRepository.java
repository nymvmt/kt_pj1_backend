package com.kt.backendapp.repository;

import com.kt.backendapp.entity.BrandDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface BrandDetailRepository extends JpaRepository<BrandDetail, Long> {
    Optional<BrandDetail> findByBrandBrandId(Long brandId);
    
    @Modifying
    @Query("UPDATE BrandDetail bd SET bd.viewCount = bd.viewCount + 1 WHERE bd.brand.brandId = :brandId")
    void incrementViewCount(@Param("brandId") Long brandId);
    
    @Modifying
    @Query("UPDATE BrandDetail bd SET bd.saveCount = bd.saveCount + 1 WHERE bd.brand.brandId = :brandId")
    void incrementSaveCount(@Param("brandId") Long brandId);
    
    @Modifying
    @Query("UPDATE BrandDetail bd SET bd.saveCount = bd.saveCount - 1 WHERE bd.brand.brandId = :brandId AND bd.saveCount > 0")
    void decrementSaveCount(@Param("brandId") Long brandId);
    
    // 카테고리별 평균 통계 조회
    @Query("SELECT AVG(bd.initialCost) FROM BrandDetail bd JOIN bd.brand b WHERE b.category.categoryId = :categoryId")
    BigDecimal getAvgInitialCostByCategory(@Param("categoryId") Long categoryId);
    
    @Query("SELECT AVG(bd.totalInvestment) FROM BrandDetail bd JOIN bd.brand b WHERE b.category.categoryId = :categoryId")
    BigDecimal getAvgTotalInvestmentByCategory(@Param("categoryId") Long categoryId);
    
    @Query("SELECT AVG(bd.avgMonthlyRevenue) FROM BrandDetail bd JOIN bd.brand b WHERE b.category.categoryId = :categoryId")
    BigDecimal getAvgMonthlyRevenueByCategory(@Param("categoryId") Long categoryId);
    
    @Query("SELECT AVG(bd.storeCount) FROM BrandDetail bd JOIN bd.brand b WHERE b.category.categoryId = :categoryId")
    Double getAvgStoreCountByCategory(@Param("categoryId") Long categoryId);
}
