package com.kt.backendapp.dto.request.brand;

import com.kt.backendapp.dto.common.SortOption;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrandSearchRequest {
    
    private Long categoryId;        // 카테고리 필터
    private String search;          // 브랜드명 검색
    
    @Builder.Default
    private int page = 0;           // 페이지 번호 (0부터 시작)
    
    @Builder.Default
    private int size = 20;          // 페이지 크기
    
    @Builder.Default
    private SortOption sort = SortOption.NAME_ASC;  // 정렬 옵션
}
