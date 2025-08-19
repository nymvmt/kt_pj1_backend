package com.kt.backendapp.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResponse<T> {
    private List<T> content;
    private PageInfo pageInfo;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PageInfo {
        private int page;          // 현재 페이지 (0부터 시작)
        private int size;          // 페이지 크기
        private long totalElements; // 전체 요소 수
        private int totalPages;    // 전체 페이지 수
        private boolean first;     // 첫 번째 페이지 여부
        private boolean last;      // 마지막 페이지 여부
        private boolean hasNext;   // 다음 페이지 존재 여부
        private boolean hasPrevious; // 이전 페이지 존재 여부
    }
    
    // Spring Data Page를 PageResponse로 변환
    public static <T> PageResponse<T> of(Page<T> page) {
        return PageResponse.<T>builder()
            .content(page.getContent())
            .pageInfo(PageInfo.builder()
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build())
            .build();
    }
    
    // 변환된 데이터로 PageResponse 생성
    public static <T, U> PageResponse<U> of(Page<T> page, List<U> convertedContent) {
        return PageResponse.<U>builder()
            .content(convertedContent)
            .pageInfo(PageInfo.builder()
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build())
            .build();
    }
}