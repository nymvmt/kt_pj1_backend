package com.kt.backendapp.dto.response.brand;

import com.kt.backendapp.entity.BrandManager;
import com.kt.backendapp.entity.Brand;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrandManagerResponse {
    private Long managerId;
    private String name;
    private String email;
    private String phone;
    
    // 관리하는 브랜드 정보들 (여러 개)
    private List<ManagedBrand> managedBrands;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ManagedBrand {
        private Long brandId;
        private String brandName;
        private String categoryName;
    }
    
    // Entity → DTO 변환 메소드 (브랜드 없음)
    public static BrandManagerResponse from(BrandManager manager) {
        return BrandManagerResponse.builder()
            .managerId(manager.getManagerId())
            .name(manager.getName())
            .email(manager.getEmail())
            .phone(manager.getPhone())
            .managedBrands(List.of()) // 빈 리스트
            .build();
    }
    
    // 관리 브랜드 리스트 포함 변환 메소드
    public static BrandManagerResponse from(BrandManager manager, List<Brand> managedBrands) {
        List<ManagedBrand> brandInfoList = managedBrands.stream()
            .map(brand -> ManagedBrand.builder()
                .brandId(brand.getBrandId())
                .brandName(brand.getBrandName())
                .categoryName(brand.getCategory() != null ? 
                    brand.getCategory().getCategoryName() : null)
                .build())
            .collect(Collectors.toList());
        
        return BrandManagerResponse.builder()
            .managerId(manager.getManagerId())
            .name(manager.getName())
            .email(manager.getEmail())
            .phone(manager.getPhone())
            .managedBrands(brandInfoList)
            .build();
    }
}