package com.kt.backendapp.repository;

import com.kt.backendapp.entity.SavedBrand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;


@Repository
public interface SavedBrandRepository extends JpaRepository<SavedBrand, Long> {
    /**
     * 사용자의 찜 목록 조회 (기본)
     * - 전체 목록 반환
     */
    List<SavedBrand> findByUserUserId(Long userId);
    
    /**
     * 특정 사용자의 특정 브랜드 찜 정보 조회
     * - 찜 상태 확인용
     */
    Optional<SavedBrand> findByUserUserIdAndBrandBrandId(Long userId, Long brandId);
    
    /**
     * 특정 사용자가 특정 브랜드를 찜했는지 확인
     * - 찜하기/취소 전 중복 체크용
     * - 브랜드 목록/상세에서 찜 상태 표시용
     */
    boolean existsByUserUserIdAndBrandBrandId(Long userId, Long brandId);
    
    /**
     * 특정 사용자의 특정 브랜드 찜 삭제
     * - 찜 취소 시 사용
     * - @Transactional 필수 (삭제 쿼리)
     */
    @Transactional
    void deleteByUserUserIdAndBrandBrandId(Long userId, Long brandId);
    
    /**
     * 사용자의 찜 목록 조회 (브랜드 정보 함께 로드)
     * - FETCH JOIN으로 브랜드 상세 정보까지 한 번에 로드
     * - N+1 문제 방지
     * - 최근 찜한 순서로 정렬 (savedAt DESC)
     */
    @Query("SELECT sb FROM SavedBrand sb " +
           "JOIN FETCH sb.brand b " +
           "JOIN FETCH b.details " +
           "JOIN FETCH b.category " +
           "WHERE sb.user.userId = :userId " +
           "ORDER BY sb.savedAt DESC")
    List<SavedBrand> findByUserUserIdWithBrand(@Param("userId") Long userId);
}
