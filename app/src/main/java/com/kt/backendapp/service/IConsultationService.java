package com.kt.backendapp.service;

import com.kt.backendapp.dto.request.consultation.ConsultationCreateRequest;
import com.kt.backendapp.dto.request.consultation.ConsultationRescheduleRequest;
import com.kt.backendapp.dto.request.consultation.ConsultationUserResponseRequest;
import com.kt.backendapp.dto.response.consultation.ConsultationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 상담 관련 서비스 인터페이스
 */
public interface IConsultationService {
    
    /**
     * 상담 신청 (중복 신청 방지 로직 포함)
     */
    ConsultationResponse createConsultation(ConsultationCreateRequest request);
    
    /**
     * 사용자의 모든 상담 목록 조회 (취소된 것도 포함)
     */
    Page<ConsultationResponse> getUserConsultations(Long userId, Pageable pageable);
    
    /**
     * 사용자의 일정 조정 요청 목록 조회
     */
    List<ConsultationResponse> getUserRescheduleRequests(Long userId);
    
    /**
     * 매니저의 모든 상담 목록 조회 (취소된 것도 포함)
     */
    Page<ConsultationResponse> getManagerConsultations(Long managerId, Pageable pageable);
    
    /**
     * 매니저의 상담 일정 조정 요청 (권한 검증 포함)
     */
    ConsultationResponse rescheduleConsultation(Long consultationId, ConsultationRescheduleRequest request, Long managerId);
    
    /**
     * 사용자의 일정 조정 응답 처리 (수락/거절) (권한 검증 포함)
     */
    ConsultationResponse respondToReschedule(Long consultationId, ConsultationUserResponseRequest request, Long userId);
    
    /**
     * 상담 취소 (권한 검증 포함)
     */
    void cancelConsultation(Long consultationId, Long userId);
    
    /**
     * 매니저의 상담 바로 확정 (PENDING → CONFIRMED) (권한 검증 포함)
     */
    ConsultationResponse confirmConsultation(Long consultationId, Long managerId);
    
    /**
     * 매니저의 상담 취소 (권한 검증 포함)
     */
    void cancelConsultationByManager(Long consultationId, Long managerId);
}
