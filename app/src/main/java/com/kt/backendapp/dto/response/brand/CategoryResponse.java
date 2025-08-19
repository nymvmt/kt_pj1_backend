package com.kt.backendapp.dto.response.brand;

import com.kt.backendapp.entity.BrandCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponse {
    private Long categoryId;
    private String categoryName;
    private Long brandCount;        // 해당 카테고리의 브랜드 수
    
    // Entity → DTO 변환 메소드
    public static CategoryResponse from(BrandCategory category) {
        return CategoryResponse.builder()
            .categoryId(category.getCategoryId())
            .categoryName(category.getCategoryName())
            .brandCount(0L) // 기본값, Service에서 설정
            .build();
    }
    
    // 브랜드 수 포함 변환 메소드
    public static CategoryResponse from(BrandCategory category, Long brandCount) {
        CategoryResponse response = from(category);
        response.setBrandCount(brandCount);
        return response;
    }
}
