package com.kt.backendapp.repository;

import com.kt.backendapp.entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {
    
    // 브랜드 상세 조회 (모든 연관 엔티티 함께 로드)
    @Query("SELECT b FROM Brand b " +
           "JOIN FETCH b.details d " +
           "JOIN FETCH b.category c " +
           "LEFT JOIN FETCH b.manager m " +
           "WHERE b.brandId = :brandId")
    Optional<Brand> findByIdWithDetails(@Param("brandId") Long brandId);
    
    // 필터링된 브랜드 목록 조회
    @Query("SELECT b FROM Brand b JOIN FETCH b.details d JOIN FETCH b.category c " +
           "WHERE (:categoryId IS NULL OR c.categoryId = :categoryId) " +
           "AND (:search IS NULL OR REPLACE(b.brandName, ' ', '') LIKE CONCAT('%', REPLACE(:search, ' ', ''), '%'))")
    Page<Brand> findBrandsWithFilters(@Param("categoryId") Long categoryId, 
                                     @Param("search") String search, 
                                     Pageable pageable);
    
    // 관련 브랜드 조회 (같은 카테고리)
    @Query("SELECT b FROM Brand b JOIN FETCH b.details d JOIN FETCH b.category c " +
           "WHERE c.categoryId = :categoryId AND b.brandId != :excludeBrandId " +
           "ORDER BY d.viewCount DESC")
    List<Brand> findRelatedBrands(@Param("categoryId") Long categoryId, 
                                 @Param("excludeBrandId") Long excludeBrandId, 
                                 Pageable pageable);
    
    // 브랜드명 중복 체크
    boolean existsByBrandName(String brandName);
    
    // 매니저별 브랜드 조회
    Optional<Brand> findByManagerManagerId(Long managerId);
}
