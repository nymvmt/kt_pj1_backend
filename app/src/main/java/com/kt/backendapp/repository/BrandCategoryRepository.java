package com.kt.backendapp.repository;

import com.kt.backendapp.entity.BrandCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BrandCategoryRepository extends JpaRepository<BrandCategory, Long> {
    Optional<BrandCategory> findByCategoryName(String categoryName);
    boolean existsByCategoryName(String categoryName);
}
