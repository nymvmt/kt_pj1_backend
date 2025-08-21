package com.kt.backendapp.service;

import com.kt.backendapp.dto.request.brand.BrandCreateRequest;
import com.kt.backendapp.dto.request.brand.BrandUpdateRequest;
import com.kt.backendapp.dto.response.brand.BrandDetailResponse;
import com.kt.backendapp.dto.response.brand.BrandListResponse;
import com.kt.backendapp.dto.response.brand.CategoryResponse;
import com.kt.backendapp.entity.*;
import com.kt.backendapp.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ManagerBrandService {
    
    private final BrandRepository brandRepository;
    private final BrandCategoryRepository brandCategoryRepository;
    private final BrandManagerRepository brandManagerRepository;
    private final BrandDetailRepository brandDetailRepository;
    private final ViewCountService viewCountService;
    
    /**
     * 카테고리 목록 조회
     * GET /api/manager/categories
     */
    public List<CategoryResponse> getCategories() {
        List<BrandCategory> categories = brandCategoryRepository.findAll();
        
        return categories.stream()
            .map(category -> {
                // 각 카테고리별 브랜드 수 계산
                Long brandCount = brandRepository.countByCategory(category);
                return CategoryResponse.from(category, brandCount);
            })
            .collect(Collectors.toList());
    }
    
    /**
     * 전체 브랜드 목록 조회 (매니저용 - 브랜드 추가 가능)
     * GET /api/manager/brands/all
     */
    public List<BrandListResponse> getAllBrands(Long managerId) {
        // 전체 브랜드 조회 (페이징 없음)
        List<Brand> brands = brandRepository.findAllWithDetails();
        
        return brands.stream()
            .map(brand -> {
                // 매니저가 관리하는 브랜드인지 확인
                boolean isManaged = brand.getManager().getManagerId().equals(managerId);
                return BrandListResponse.from(brand, false, isManaged); // 매니저는 찜 기능 없음, 관리 여부 표시
            })
            .sorted((a, b) -> {
                // 매니저가 관리하는 브랜드를 상위로 정렬
                if (a.isManaged() && !b.isManaged()) return -1;
                if (!a.isManaged() && b.isManaged()) return 1;
                return 0;
            })
            .collect(Collectors.toList());
    }
    
    /**
     * 매니저의 브랜드 목록 조회 (전체 목록)
     * GET /api/v1/manager/brands
     */
    public List<BrandListResponse> getManagerBrands(Long managerId) {
        List<Brand> brands = brandRepository.findAllByManagerManagerId(managerId);
        
        return brands.stream()
            .map(brand -> BrandListResponse.from(brand, false)) // 매니저는 찜 기능 없음
            .collect(Collectors.toList());
    }
    
    /**
     * 매니저의 브랜드 상세 조회
     * GET /api/v1/manager/brands/{id}
     */
    @Transactional
    public BrandDetailResponse getManagerBrandDetail(Long brandId, Long managerId) {
        // 브랜드 조회 및 매니저 권한 확인
        Brand brand = brandRepository.findById(brandId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 브랜드입니다."));
        
        // 매니저 권한 검증: 본인이 관리하는 브랜드인지 확인
        if (!brand.getManager().getManagerId().equals(managerId)) {
            throw new IllegalArgumentException("해당 브랜드에 대한 권한이 없습니다.");
        }
        
        // BrandDetail 조회
        BrandDetail brandDetail = brandDetailRepository.findByBrandBrandId(brandId)
            .orElseThrow(() -> new IllegalArgumentException("브랜드 상세 정보를 찾을 수 없습니다."));
        
        // 조회수 증가 (시스템 관리값) - 공통 서비스 사용
        viewCountService.incrementViewCount(brandId);
        
        // 매니저용이므로 카테고리 통계, 관련 브랜드는 제외하고 기본 정보만 반환
        return BrandDetailResponse.builder()
            .brandId(brand.getBrandId())
            .brandName(brand.getBrandName())
            .categoryName(brand.getCategory().getCategoryName())
            .manager(BrandDetailResponse.ManagerInfo.builder()
                .managerId(brand.getManager().getManagerId())
                .name(brand.getManager().getName())
                .email(brand.getManager().getEmail())
                .phone(brand.getManager().getPhone())
                .build())
            .viewCount(brandDetail.getViewCount())
            .saveCount(brandDetail.getSaveCount())
            .initialCost(brandDetail.getInitialCost())
            .totalInvestment(brandDetail.getTotalInvestment())
            .avgMonthlyRevenue(brandDetail.getAvgMonthlyRevenue())
            .storeCount(brandDetail.getStoreCount())
            .brandDescription(brandDetail.getBrandDescription())
            .isSaved(false) // 매니저는 찜 기능 없음
            .build();
    }
    
    /**
     * 브랜드 등록
     * POST /api/v1/manager/brands
     */
    @Transactional
    public BrandDetailResponse createBrand(BrandCreateRequest request, Long managerId) {
        // 매니저 존재 확인
        BrandManager manager = brandManagerRepository.findById(managerId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 매니저입니다."));
        
        // 카테고리 존재 확인
        BrandCategory category = brandCategoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));
        
        // 브랜드명 중복 확인
        if (brandRepository.existsByBrandName(request.getBrandName())) {
            throw new IllegalArgumentException("이미 존재하는 브랜드명입니다.");
        }
        
        // Brand 엔티티 생성
        Brand brand = Brand.builder()
            .brandName(request.getBrandName())
            .category(category)
            .manager(manager)
            .build();
        
        // BrandDetail 엔티티 생성
        BrandDetail brandDetail = BrandDetail.builder()
            .brand(brand)
            .initialCost(request.getInitialCost())
            .totalInvestment(request.getTotalInvestment())
            .avgMonthlyRevenue(request.getAvgMonthlyRevenue())
            .storeCount(request.getStoreCount())
            .brandDescription(request.getBrandDescription())
            .viewCount(0L)
            .saveCount(0L)
            .build();
        
        // Brand에 BrandDetail 연결
        brand.setDetails(brandDetail);
        
        // 저장
        Brand savedBrand = brandRepository.save(brand);
        brandDetailRepository.save(brandDetail);
        
        // 응답 생성
        return BrandDetailResponse.builder()
            .brandId(savedBrand.getBrandId())
            .brandName(savedBrand.getBrandName())
            .categoryName(savedBrand.getCategory().getCategoryName())
            .manager(BrandDetailResponse.ManagerInfo.builder()
                .managerId(manager.getManagerId())
                .name(manager.getName())
                .email(manager.getEmail())
                .phone(manager.getPhone())
                .build())
            .viewCount(brandDetail.getViewCount())
            .saveCount(brandDetail.getSaveCount())
            .initialCost(brandDetail.getInitialCost())
            .totalInvestment(brandDetail.getTotalInvestment())
            .avgMonthlyRevenue(brandDetail.getAvgMonthlyRevenue())
            .storeCount(brandDetail.getStoreCount())
            .brandDescription(brandDetail.getBrandDescription())
            .isSaved(false)
            .build();
    }
    
    /**
     * 브랜드 수정
     * PUT /api/v1/manager/brands/{id}
     */
    @Transactional
    public BrandDetailResponse updateBrand(Long brandId, BrandUpdateRequest request, Long managerId) {
        // 브랜드 조회 및 매니저 권한 확인
        Brand brand = brandRepository.findById(brandId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 브랜드입니다."));
        
        // 매니저 권한 검증
        if (!brand.getManager().getManagerId().equals(managerId)) {
            throw new IllegalArgumentException("해당 브랜드에 대한 권한이 없습니다.");
        }
        
        // 카테고리 존재 확인 (카테고리 변경 시)
        if (!brand.getCategory().getCategoryId().equals(request.getCategoryId())) {
            BrandCategory newCategory = brandCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다."));
            brand.setCategory(newCategory);
        }
        
        // 브랜드명 중복 확인 (다른 브랜드와 중복되는지)
        if (!brand.getBrandName().equals(request.getBrandName()) && 
            brandRepository.existsByBrandName(request.getBrandName())) {
            throw new IllegalArgumentException("이미 존재하는 브랜드명입니다.");
        }
        
        // Brand 정보 업데이트
        brand.setBrandName(request.getBrandName());
        
        // BrandDetail 정보 업데이트
        BrandDetail details = brand.getDetails();
        details.setInitialCost(request.getInitialCost());
        details.setTotalInvestment(request.getTotalInvestment());
        details.setAvgMonthlyRevenue(request.getAvgMonthlyRevenue());
        details.setStoreCount(request.getStoreCount());
        details.setBrandDescription(request.getBrandDescription());
        
        // 저장 (더티 체킹으로 자동 업데이트)
        Brand updatedBrand = brandRepository.save(brand);
        
        // 응답 생성
        return BrandDetailResponse.builder()
            .brandId(updatedBrand.getBrandId())
            .brandName(updatedBrand.getBrandName())
            .categoryName(updatedBrand.getCategory().getCategoryName())
            .manager(BrandDetailResponse.ManagerInfo.builder()
                .managerId(updatedBrand.getManager().getManagerId())
                .name(updatedBrand.getManager().getName())
                .email(updatedBrand.getManager().getEmail())
                .phone(updatedBrand.getManager().getPhone())
                .build())
            .viewCount(details.getViewCount())
            .saveCount(details.getSaveCount())
            .initialCost(details.getInitialCost())
            .totalInvestment(details.getTotalInvestment())
            .avgMonthlyRevenue(details.getAvgMonthlyRevenue())
            .storeCount(details.getStoreCount())
            .brandDescription(details.getBrandDescription())
            .isSaved(false)
            .build();
    }
    
    /**
     * 브랜드 삭제
     * DELETE /api/v1/manager/brands/{id}
     */
    @Transactional
    public void deleteBrand(Long brandId, Long managerId) {
        // 브랜드 조회 및 매니저 권한 확인
        Brand brand = brandRepository.findById(brandId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 브랜드입니다."));
        
        // 매니저 권한 검증
        if (!brand.getManager().getManagerId().equals(managerId)) {
            throw new IllegalArgumentException("해당 브랜드에 대한 권한이 없습니다.");
        }
        
        // 브랜드 삭제 (BrandDetail은 cascade로 함께 삭제됨)
        brandRepository.delete(brand);
    }
}