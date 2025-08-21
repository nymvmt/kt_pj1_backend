package com.kt.backendapp.repository;

import com.kt.backendapp.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import com.kt.backendapp.entity.BrandCategory;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {
    
    /**
     * 브랜드 상세 조회 (기본 정보만)
     * - 단순한 findById 사용으로 안정성 확보
     * - 연관 엔티티는 필요시 별도 조회
     */
    // findById는 JpaRepository에서 기본 제공하므로 별도 메서드 불필요
    
    /**
     * 전체 브랜드 목록 조회 (연관 엔티티 포함)
     * - 모든 브랜드를 상세 정보와 함께 조회
     * - LEFT JOIN FETCH로 N+1 문제 방지
     */
    @Query("SELECT b FROM Brand b " +
           "LEFT JOIN FETCH b.details d " +
           "LEFT JOIN FETCH b.category c " +
           "LEFT JOIN FETCH b.manager m " +
           "ORDER BY b.brandName ASC")
    List<Brand> findAllWithDetails();
    
    /**
     * 관련 브랜드 조회 (같은 카테고리, 현재 브랜드 제외)
     * - 같은 카테고리 내에서 현재 브랜드를 제외한 다른 브랜드들
     * - 조회수 높은 순으로 정렬
     */
    @Query("SELECT b FROM Brand b LEFT JOIN FETCH b.details d LEFT JOIN FETCH b.category c " +
           "WHERE c.categoryId = :categoryId AND b.brandId != :excludeBrandId " +
           "ORDER BY d.viewCount DESC")
    List<Brand> findRelatedBrands(@Param("categoryId") Long categoryId, 
                                 @Param("excludeBrandId") Long excludeBrandId);
    /**
     * 브랜드명 중복 체크
     * - 매니저 회원가입 시 브랜드명 중복 확인용
     */
    boolean existsByBrandName(String brandName);
    
    /**
     * 매니저별 브랜드 목록 조회
     * - N:1 관계로 변경: 매니저가 여러 브랜드를 관리할 수 있음
     * - 연관 엔티티 함께 로드 (LEFT JOIN FETCH)
     */
    @Query("SELECT b FROM Brand b " +
           "LEFT JOIN FETCH b.details d " +
           "LEFT JOIN FETCH b.category c " +
           "WHERE b.manager.managerId = :managerId " +
           "ORDER BY b.brandName ASC")
    List<Brand> findByManagerManagerId(@Param("managerId") Long managerId);
    
    /**
     * 매니저별 브랜드 목록 조회 (전체)
     * - 페이징 없이 매니저의 모든 브랜드 조회
     */
    @Query("SELECT b FROM Brand b " +
           "LEFT JOIN FETCH b.details d " +
           "LEFT JOIN FETCH b.category c " +
           "WHERE b.manager.managerId = :managerId " +
           "ORDER BY b.brandName ASC")
    List<Brand> findAllByManagerManagerId(@Param("managerId") Long managerId);
    
    /**
     * 카테고리별 브랜드 수 조회
     * - 카테고리 목록 조회 시 각 카테고리의 브랜드 개수 표시용
     */
    Long countByCategoryCategoryId(Long categoryId);
    
    /**
     * 카테고리별 브랜드 수 조회 (BrandCategory 엔티티로)
     * - 카테고리 목록 조회 시 각 카테고리의 브랜드 개수 표시용
     */
    Long countByCategory(BrandCategory category);
    
    /**
     * 카테고리 내 브랜드 목록 조회 (연관 엔티티 포함)
     * - 특정 카테고리의 모든 브랜드를 상세 정보와 함께 조회
     * - 조회수 높은 순으로 정렬
     * - LEFT JOIN FETCH로 N+1 문제 방지
     */
    @Query("SELECT b FROM Brand b " +
        "LEFT JOIN FETCH b.details d " +
        "LEFT JOIN FETCH b.category c " +
        "LEFT JOIN FETCH b.manager m " +
        "WHERE c.categoryId = :categoryId " +
        "ORDER BY d.viewCount DESC")
    List<Brand> findBrandsByCategoryWithDetails(@Param("categoryId") Long categoryId);
    
    /**
     * 브랜드명으로 검색 (대소문자 구분 없음)
     * - 키워드가 포함된 브랜드명을 가진 브랜드들을 검색
     * - 연관 엔티티와 함께 조회
     */
    @Query("SELECT b FROM Brand b " +
        "LEFT JOIN FETCH b.details d " +
        "LEFT JOIN FETCH b.category c " +
        "LEFT JOIN FETCH b.manager m " +
        "WHERE LOWER(b.brandName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
        "ORDER BY d.viewCount DESC")
    List<Brand> findByBrandNameContainingIgnoreCase(@Param("keyword") String keyword);
}
