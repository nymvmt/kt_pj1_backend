package com.kt.backendapp.service;

import com.kt.backendapp.dto.response.brand.BrandDetailResponse;
import com.kt.backendapp.dto.response.brand.BrandListResponse;
import com.kt.backendapp.dto.response.brand.CategoryResponse;
import com.kt.backendapp.entity.Brand;
import com.kt.backendapp.repository.BrandRepository;
import com.kt.backendapp.repository.BrandDetailRepository;
import com.kt.backendapp.repository.BrandCategoryRepository;
import com.kt.backendapp.repository.SavedBrandRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import com.kt.backendapp.entity.BrandCategory;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PublicBrandService {
    
    private final BrandRepository brandRepository;
    private final BrandCategoryRepository brandCategoryRepository;
    private final SavedBrandRepository savedBrandRepository;
    private final ViewCountService viewCountService;
    
    /**
     * 공개 브랜드 목록 조회 (기본 정보만)
     */
    public List<BrandListResponse> getPublicBrands(Long managerId) {
        log.info("=== 공개 브랜드 목록 조회 시작: managerId={} ===", managerId);
        
        try {
            // 전체 브랜드 목록 조회 (연관 엔티티 포함)
            List<Brand> brands = brandRepository.findAllWithDetails();
            log.info("Repository에서 조회된 브랜드 수: {}", brands != null ? brands.size() : "null");
            
            if (brands != null && !brands.isEmpty()) {
                log.debug("첫 번째 브랜드 정보: {}", brands.get(0));
            }
            
            // Brand → BrandListResponse 변환 (찜 상태는 false로 설정, 매니저인 경우 isManaged 필드 설정)
            log.info("매니저 ID로 isManaged 필드 설정 시작: managerId={}", managerId);
            List<BrandListResponse> responses = brands.stream()
                .map(brand -> {
                    boolean isManaged = false;
                    if (managerId != null) {
                        Long brandManagerId = brand.getManager().getManagerId();
                        isManaged = brandManagerId.equals(managerId);
                        log.info("브랜드 {} 매니저 ID 비교: brandManagerId={}, requestManagerId={}, isManaged={}", 
                            brand.getBrandName(), brandManagerId, managerId, isManaged);
                    } else {
                        log.info("매니저 ID가 null이므로 isManaged=false로 설정");
                    }
                    return BrandListResponse.from(brand, false, isManaged);
                })
                .collect(Collectors.toList());
            
            log.info("변환 완료된 응답 수: {}", responses.size());
            log.info("=== 공개 브랜드 목록 조회 완료 ===");
            return responses;
            
        } catch (Exception e) {
            log.error("공개 브랜드 목록 조회 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * 공개 브랜드 상세 조회 (기본 정보만)
     */
    @Transactional
    public BrandDetailResponse getPublicBrandDetail(Long brandId) {
        log.info("=== 공개 브랜드 상세 조회 시작: brandId={} ===", brandId);
        
        try {
            // 브랜드 기본 정보 조회
            Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 브랜드입니다."));
            
            // 조회수 증가 (시스템 관리값) - 공통 서비스 사용
            viewCountService.incrementViewCount(brandId);
            
            // 공개용 응답 생성 (찜 상태, 관련 브랜드, 카테고리 통계 제외)
            return BrandDetailResponse.from(brand, false, null, null);
            
        } catch (Exception e) {
            log.error("공개 브랜드 상세 조회 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * 카테고리별 브랜드 목록 조회
     */
    public List<BrandListResponse> getBrandsByCategory(Long categoryId, Long managerId) {
        log.info("=== 카테고리별 브랜드 목록 조회 시작: categoryId={}, managerId={} ===", categoryId, managerId);
        
        try {
            // 카테고리 존재 여부 확인
            if (!brandCategoryRepository.existsById(categoryId)) {
                throw new IllegalArgumentException("존재하지 않는 카테고리입니다.");
            }
            
            // 카테고리별 브랜드 목록 조회
            List<Brand> brands = brandRepository.findBrandsByCategoryWithDetails(categoryId);
            log.info("카테고리 {}에서 조회된 브랜드 수: {}", categoryId, brands.size());
            
            // Brand → BrandListResponse 변환 (찜 상태는 false로 설정, 매니저인 경우 isManaged 필드 설정)
            List<BrandListResponse> responses = brands.stream()
                .map(brand -> {
                    boolean isManaged = false;
                    if (managerId != null) {
                        isManaged = brand.getManager().getManagerId().equals(managerId);
                    }
                    return BrandListResponse.from(brand, false, isManaged);
                })
                .collect(Collectors.toList());
            
            log.info("=== 카테고리별 브랜드 목록 조회 완료 ===");
            return responses;
            
        } catch (Exception e) {
            log.error("카테고리별 브랜드 목록 조회 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * 브랜드 검색
     */
    public List<BrandListResponse> searchBrands(String keyword, Long managerId) {
        log.info("=== 브랜드 검색 시작: keyword={}, managerId={} ===", keyword, managerId);
        
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                throw new IllegalArgumentException("검색어를 입력해주세요.");
            }
            
            // 키워드로 브랜드 검색
            List<Brand> brands = brandRepository.findByBrandNameContainingIgnoreCase(keyword.trim());
            log.info("키워드 '{}'로 검색된 브랜드 수: {}", keyword, brands.size());
            
            // Brand → BrandListResponse 변환 (찜 상태는 false로 설정, 매니저인 경우 isManaged 필드 설정)
            List<BrandListResponse> responses = brands.stream()
                .map(brand -> {
                    boolean isManaged = false;
                    if (managerId != null) {
                        isManaged = brand.getManager().getManagerId().equals(managerId);
                    }
                    return BrandListResponse.from(brand, false, isManaged);
                })
                .collect(Collectors.toList());
            
            log.info("=== 브랜드 검색 완료 ===");
            return responses;
            
        } catch (Exception e) {
            log.error("브랜드 검색 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * 카테고리 목록 조회
     */
    public List<CategoryResponse> getCategories() {
        log.info("=== 카테고리 목록 조회 시작 ===");
        
        try {
            // 모든 카테고리 조회
            List<BrandCategory> categories = brandCategoryRepository.findAll();
            log.info("Repository에서 조회된 카테고리 수: {}", categories.size());
            
            // 각 카테고리의 브랜드 수 계산
            List<CategoryResponse> responses = categories.stream()
                .map(category -> {
                    Long brandCount = brandRepository.countByCategoryCategoryId(category.getCategoryId());
                    return CategoryResponse.from(category, brandCount);
                })
                .collect(Collectors.toList());
            
            log.info("=== 카테고리 목록 조회 완료 ===");
            return responses;
            
        } catch (Exception e) {
            log.error("카테고리 목록 조회 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
    }
}
