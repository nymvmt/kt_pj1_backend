package com.kt.backendapp.repository;

import com.kt.backendapp.entity.SavedBrand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface SavedBrandRepository extends JpaRepository<SavedBrand, Long> {
    Page<SavedBrand> findByUserUserId(Long userId, Pageable pageable);
    Optional<SavedBrand> findByUserUserIdAndBrandBrandId(Long userId, Long brandId);
    boolean existsByUserUserIdAndBrandBrandId(Long userId, Long brandId);
    @Transactional
    void deleteByUserUserIdAndBrandBrandId(Long userId, Long brandId);
}
