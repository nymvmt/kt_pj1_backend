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
    /**
     * 브랜드 ID로 브랜드 상세 정보 조회
     */
    Optional<BrandDetail> findByBrandBrandId(Long brandId);
    
    /**
     * 브랜드 조회수 증가
     * - 브랜드 상세 페이지 조회 시 호출
     * - 동시성 문제 방지를 위해 UPDATE 쿼리 사용
     * - null 안전성을 위해 COALESCE 사용
     */
    @Modifying
    @Query("UPDATE BrandDetail bd SET bd.viewCount = COALESCE(bd.viewCount, 0) + 1 WHERE bd.brand.brandId = :brandId")
    void incrementViewCount(@Param("brandId") Long brandId);
    
    /**
     * 브랜드 찜 수 증가
     * - 사용자가 브랜드를 찜할 때 호출
     * - 실제 SavedBrand 저장과 함께 수행
     */
    @Modifying
    @Query("UPDATE BrandDetail bd SET bd.saveCount = bd.saveCount + 1 WHERE bd.brand.brandId = :brandId")
    void incrementSaveCount(@Param("brandId") Long brandId);
    
    /**
     * 브랜드 찜 수 감소
     * - 사용자가 찜을 취소할 때 호출
     * - saveCount가 0보다 클 때만 감소 (음수 방지)
     */
    @Modifying
    @Query("UPDATE BrandDetail bd SET bd.saveCount = bd.saveCount - 1 WHERE bd.brand.brandId = :brandId AND bd.saveCount > 0")
    void decrementSaveCount(@Param("brandId") Long brandId);
    
    /**
     * 카테고리별 브랜드 통계 한 번에 조회
     * - 해당 카테고리의 모든 브랜드 통계 정보 계산
     * - 반환값: [평균가맹비, 평균총창업비용, 평균월매출, 평균매장수, 최대가맹비, 최소가맹비, 최대월매출, 최소월매출, 브랜드수]
     * - 브랜드 상세 페이지에서 경쟁력 분석용 데이터 제공
     * - CAST를 사용하여 storeCount를 double로 변환 (AVG 함수 적용 위해)
     */
    @Query("SELECT AVG(bd.initialCost), AVG(bd.totalInvestment), AVG(bd.avgMonthlyRevenue), AVG(CAST(bd.storeCount AS double)), " +
           "MAX(bd.initialCost), MIN(bd.initialCost), MAX(bd.avgMonthlyRevenue), MIN(bd.avgMonthlyRevenue), COUNT(bd) " +
           "FROM BrandDetail bd WHERE bd.brand.category.categoryId = :categoryId")
    Object[] calculateCategoryStats(@Param("categoryId") Long categoryId);
}
