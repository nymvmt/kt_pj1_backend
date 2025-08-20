package com.kt.backendapp.service;

import com.kt.backendapp.repository.BrandDetailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 조회수 증가를 위한 공통 서비스
 * 모든 서비스에서 브랜드 상세 조회 시 조회수를 증가시키는 로직을 통일
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ViewCountService {
    
    private final BrandDetailRepository brandDetailRepository;
    
    /**
     * 브랜드 조회수 증가 (안전한 처리)
     * @param brandId 브랜드 ID
     */
    public void incrementViewCount(Long brandId) {
        try {
            log.debug("조회수 증가 시작: brandId={}", brandId);
            brandDetailRepository.incrementViewCount(brandId);
            log.debug("조회수 증가 완료");
        } catch (Exception e) {
            log.error("조회수 증가 중 오류 발생: {}", e.getMessage(), e);
            log.error("조회수 증가 실패 상세 정보:", e);
            // 조회수 증가 실패는 전체 요청을 실패시키지 않음
            // 상세한 오류 로그를 남기고 계속 진행
        }
    }
}
