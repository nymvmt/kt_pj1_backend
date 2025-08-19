package com.kt.backendapp.dto.response.brand;

import com.kt.backendapp.entity.BrandManager;
import com.kt.backendapp.entity.Brand;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrandManagerResponse {
    private Long managerId;
    private String name;
    private String email;
    private String phone;
    
    // 관리하는 브랜드 정보 (있을 경우)
    private ManagedBrand managedBrand;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ManagedBrand {
        private Long brandId;
        private String brandName;
        private String categoryName;
    }
    
    // Entity → DTO 변환 메소드
    public static BrandManagerResponse from(BrandManager manager) {
        return BrandManagerResponse.builder()
            .managerId(manager.getManagerId())
            .name(manager.getName())
            .email(manager.getEmail())
            .phone(manager.getPhone())
            .build();
    }
    
    // 관리 브랜드 포함 변환 메소드
    public static BrandManagerResponse from(BrandManager manager, Brand managedBrand) {
        ManagedBrand brandInfo = null;
        if (managedBrand != null) {
            brandInfo = ManagedBrand.builder()
                .brandId(managedBrand.getBrandId())
                .brandName(managedBrand.getBrandName())
                .categoryName(managedBrand.getCategory() != null ? 
                    managedBrand.getCategory().getCategoryName() : null)
                .build();
        }
        
        BrandManagerResponse response = from(manager);
        response.setManagedBrand(brandInfo);
        return response;
    }
}
