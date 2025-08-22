package com.kt.backendapp.service;

/**
 * 조회수 관련 서비스 인터페이스
 */
public interface IViewCountService {
    
    /**
     * 브랜드 조회수 증가 (안전한 처리)
     * @param brandId 브랜드 ID
     */
    void incrementViewCount(Long brandId);
}
