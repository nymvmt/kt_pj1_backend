package com.kt.backendapp.repository;

import com.kt.backendapp.entity.BrandManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BrandManagerRepository extends JpaRepository<BrandManager, Long> {
    Optional<BrandManager> findByEmail(String email);
    boolean existsByEmail(String email);
    
    // 브랜드매니저가 관리하는 브랜드 조회 (역방향)
    @Query("SELECT bm FROM BrandManager bm WHERE bm.managerId = " +
           "(SELECT b.manager.managerId FROM Brand b WHERE b.brandId = :brandId)")
    Optional<BrandManager> findByBrandId(@Param("brandId") Long brandId);
    
    // 브랜드매니저가 이미 다른 브랜드를 관리하는지 확인
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END " +
           "FROM Brand b WHERE b.manager.managerId = :managerId")
    boolean hasAssignedBrand(@Param("managerId") Long managerId);
}
