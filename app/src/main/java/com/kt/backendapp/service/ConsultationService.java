package com.kt.backendapp.service;

import com.kt.backendapp.dto.request.consultation.ConsultationCreateRequest;
import com.kt.backendapp.dto.request.consultation.ConsultationRescheduleRequest;
import com.kt.backendapp.dto.request.consultation.ConsultationUserResponseRequest;
import com.kt.backendapp.dto.response.consultation.ConsultationResponse;
import com.kt.backendapp.entity.*;
import com.kt.backendapp.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConsultationService {
    
    private final ConsultationRepository consultationRepository;
    private final UserRepository userRepository;
    private final BrandRepository brandRepository;
    private final ConsultationStatusRepository consultationStatusRepository;
    private final NotificationService notificationService;
    
    /**
     * 상담 신청 (중복 신청 방지 로직 포함)
     */
    @Transactional
    public ConsultationResponse createConsultation(ConsultationCreateRequest request) {
        log.info("=== 상담 신청 시작 ===");
        log.info("사용자ID: {}, 브랜드ID: {}", request.getUserId(), request.getBrandId());
        log.info("희망날짜: {}, 희망시간: {}", request.getPreferredDate(), request.getPreferredTime());
        
        try {
            // 중복 신청 확인
            log.info("1. 중복 신청 확인 중...");
            consultationRepository.findActiveConsultation(request.getUserId(), request.getBrandId())
                .ifPresent(consultation -> {
                    String statusMessage = getStatusMessage(consultation.getStatus().getStatusCode());
                    throw new IllegalStateException(
                        String.format("이미 해당 브랜드에 %s 상담이 있습니다. 중복 신청은 불가능합니다.", statusMessage)
                    );
                });
            log.info("중복 신청 확인 완료");
            
            // 사용자 조회
            log.info("2. 사용자 조회 중...");
            User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
            log.info("사용자 조회 완료: {}", user.getName());
            
            // 브랜드 조회
            log.info("3. 브랜드 조회 중...");
            Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 브랜드입니다."));
            log.info("브랜드 조회 완료: {}", brand.getBrandName());
            
            // PENDING 상태 조회 (status_code = 1)
            log.info("4. 상담 상태 조회 중...");
            ConsultationStatus pendingStatus = consultationStatusRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("상담 상태를 찾을 수 없습니다."));
            log.info("상담 상태 조회 완료: {}", pendingStatus.getStatusName());
            
            // 시간 문자열을 LocalTime으로 변환
            LocalTime preferredTime;
            try {
                preferredTime = LocalTime.parse(request.getPreferredTime());
            } catch (Exception e) {
                throw new IllegalArgumentException("희망 시간 형식이 올바르지 않습니다: " + request.getPreferredTime());
            }

            // 상담 엔티티 생성
            log.info("5. 상담 엔티티 생성 중...");
            Consultation consultation = Consultation.builder()
                .user(user)
                .brand(brand)
                .status(pendingStatus)
                .preferredDate(request.getPreferredDate())
                .preferredTime(preferredTime)
                .isActive(true)
                .build();
            log.info("상담 엔티티 생성 완료");
            
            log.info("6. 데이터베이스 저장 중...");
            Consultation savedConsultation = consultationRepository.save(consultation);
            log.info("데이터베이스 저장 완료 - 상담ID: {}", savedConsultation.getConsultationId());
            
            // 알림 생성 (별도 트랜잭션에서 처리, 실패해도 상담 생성은 성공)
            log.info("7. 알림 생성 중...");
            try {
                // 간단한 알림 생성 (연관 엔티티 조회 없이)
                if (savedConsultation.getUser() != null && savedConsultation.getBrand() != null) {
                    log.info("알림 생성을 위한 기본 정보 확인 완료");
                    // 실제 알림 생성은 스킵하고 나중에 구현
                    log.info("알림 생성 스킵 (임시)");
                } else {
                    log.warn("알림 생성을 위한 필수 정보가 부족합니다.");
                }
            } catch (Exception e) {
                log.error("알림 생성 실패 - 상담ID: {}", savedConsultation.getConsultationId(), e);
                // 알림 생성 실패는 무시하고 계속 진행
            }
            
            log.info("8. 응답 생성 중...");
            ConsultationResponse response = ConsultationResponse.from(savedConsultation);
            log.info("=== 상담 신청 완료 ===");
            
            return response;
            
        } catch (Exception e) {
            log.error("=== 상담 신청 실패 ===");
            log.error("오류 타입: {}", e.getClass().getSimpleName());
            log.error("오류 메시지: {}", e.getMessage());
            log.error("스택트레이스: ", e);
            throw e;
        }
    }
    
    /**
     * 사용자의 모든 상담 목록 조회 (취소된 것도 포함)
     */
    @Transactional(readOnly = true)
    public Page<ConsultationResponse> getUserConsultations(Long userId, Pageable pageable) {
        log.info("사용자 상담 목록 조회 - 사용자ID: {}", userId);
        Page<Consultation> consultations = consultationRepository.findAllConsultationsByUserId(userId, pageable);
        log.info("조회된 상담 수: {}", consultations.getTotalElements());
        return consultations.map(ConsultationResponse::from);
    }
    
    /**
     * 사용자의 일정 조정 요청 목록 조회
     */
    @Transactional(readOnly = true)
    public List<ConsultationResponse> getUserRescheduleRequests(Long userId) {
        List<Consultation> consultations = consultationRepository.findRescheduleRequestsByUserId(userId);
        return consultations.stream()
            .map(ConsultationResponse::from)
            .collect(Collectors.toList());
    }
    
    /**
     * 매니저의 모든 상담 목록 조회 (취소된 것도 포함)
     */
    @Transactional(readOnly = true)
    public Page<ConsultationResponse> getManagerConsultations(Long managerId, Pageable pageable) {
        log.info("매니저 상담 목록 조회 - 매니저ID: {}", managerId);
        Page<Consultation> consultations = consultationRepository.findAllConsultationsByManagerId(managerId, pageable);
        log.info("조회된 상담 수: {}", consultations.getTotalElements());
        return consultations.map(ConsultationResponse::from);
    }
    
    /**
     * 매니저의 상담 일정 조정 요청 (권한 검증 포함)
     */
    @Transactional
    public ConsultationResponse rescheduleConsultation(Long consultationId, ConsultationRescheduleRequest request, Long managerId) {
        log.info("매니저 상담 일정 조정 요청 - 상담ID: {}, 매니저ID: {}", consultationId, managerId);
        
        Consultation consultation = consultationRepository.findById(consultationId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상담입니다."));
        
        // 권한 검증: 매니저가 자신의 브랜드 상담만 조정할 수 있도록 검증
        if (!consultation.getBrand().getManager().getManagerId().equals(managerId)) {
            log.warn("상담 일정 조정 권한 없음 - 상담ID: {}, 요청매니저ID: {}, 브랜드매니저ID: {}", 
                consultationId, managerId, consultation.getBrand().getManager().getManagerId());
            throw new IllegalArgumentException("본인이 관리하는 브랜드의 상담만 일정 조정할 수 있습니다.");
        }
        
        // PENDING 상태인 상담만 일정 조정 가능
        if (!consultation.getStatus().getStatusCode().equals(1L)) {
            throw new IllegalStateException("신청 중인 상담만 일정 조정이 가능합니다.");
        }
        
        // RESCHEDULE_REQUEST 상태로 변경 (status_code = 2)
        ConsultationStatus rescheduleStatus = consultationStatusRepository.findById(2L)
            .orElseThrow(() -> new IllegalArgumentException("상담 상태를 찾을 수 없습니다."));
        
        consultation.setStatus(rescheduleStatus);
        consultation.setAdjustedDate(request.getAdjustedDate());
        consultation.setAdjustedTime(request.getAdjustedTime());
        consultation.setAdjustmentReason(request.getAdjustmentReason());
        consultation.setAdjustmentRequestedAt(LocalDateTime.now());
        
        if (request.getManagerNote() != null) {
            consultation.setManagerNote(request.getManagerNote());
        }
        
        Consultation savedConsultation = consultationRepository.save(consultation);
        log.info("상담 일정 조정 요청 완료 - 상담ID: {}, 매니저ID: {}", consultationId, managerId);
        
        // 알림 생성
        try {
            Consultation consultationWithRelations = consultationRepository.findByIdWithRelations(savedConsultation.getConsultationId())
                .orElse(savedConsultation);
            notificationService.createConsultationNotifications(consultationWithRelations);
        } catch (Exception e) {
            log.error("알림 생성 실패 - 상담ID: {}", savedConsultation.getConsultationId(), e);
        }
        
        return ConsultationResponse.from(savedConsultation);
    }
    
    /**
     * 사용자의 일정 조정 응답 처리 (수락/거절) (권한 검증 포함)
     */
    @Transactional
    public ConsultationResponse respondToReschedule(Long consultationId, ConsultationUserResponseRequest request, Long userId) {
        log.info("사용자 일정 조정 응답 - 상담ID: {}, 사용자ID: {}, 응답: {}", consultationId, userId, request.getUserResponse());
        
        Consultation consultation = consultationRepository.findById(consultationId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상담입니다."));
        
        // 권한 검증: 사용자가 자신의 상담만 응답할 수 있도록 검증
        if (!consultation.getUser().getUserId().equals(userId)) {
            log.warn("일정 조정 응답 권한 없음 - 상담ID: {}, 요청사용자ID: {}, 상담소유자ID: {}", 
                consultationId, userId, consultation.getUser().getUserId());
            throw new IllegalArgumentException("본인의 상담만 응답할 수 있습니다.");
        }
        
        // RESCHEDULE_REQUEST 상태인 상담만 응답 가능
        if (!consultation.getStatus().getStatusCode().equals(2L)) {
            throw new IllegalStateException("일정 조정 요청된 상담만 응답이 가능합니다.");
        }
        
        consultation.setUserResponse(request.getUserResponse());
        consultation.setUserResponseAt(LocalDateTime.now());
        
        if (request.getUserResponse() == UserResponseType.ACCEPT) {
            // 수락 시 CONFIRMED 상태로 변경 (status_code = 3)
            ConsultationStatus confirmedStatus = consultationStatusRepository.findById(3L)
                .orElseThrow(() -> new IllegalArgumentException("상담 상태를 찾을 수 없습니다."));
            consultation.setStatus(confirmedStatus);
            consultation.setConfirmedAt(LocalDateTime.now());
            
            // 조정된 날짜/시간을 실제 상담 날짜/시간으로 업데이트
            consultation.setPreferredDate(consultation.getAdjustedDate());
            consultation.setPreferredTime(consultation.getAdjustedTime());
            
        } else if (request.getUserResponse() == UserResponseType.REJECT) {
            // 거절 시 CANCELLED 상태로 변경 (status_code = 5)
            ConsultationStatus cancelledStatus = consultationStatusRepository.findById(5L)
                .orElseThrow(() -> new IllegalArgumentException("상담 상태를 찾을 수 없습니다."));
            consultation.setStatus(cancelledStatus);
            consultation.setIsActive(false);
        }
        
        Consultation savedConsultation = consultationRepository.save(consultation);
        log.info("사용자 일정 조정 응답 완료 - 상담ID: {}, 사용자ID: {}, 응답: {}", consultationId, userId, request.getUserResponse());
        
        // 알림 생성
        try {
            Consultation consultationWithRelations = consultationRepository.findByIdWithRelations(savedConsultation.getConsultationId())
                .orElse(savedConsultation);
            notificationService.createConsultationNotifications(consultationWithRelations);
        } catch (Exception e) {
            log.error("알림 생성 실패 - 상담ID: {}", savedConsultation.getConsultationId(), e);
        }
        
        return ConsultationResponse.from(savedConsultation);
    }
    
    /**
     * 상담 취소 (권한 검증 포함)
     */
    @Transactional
    public void cancelConsultation(Long consultationId, Long userId) {
        log.info("상담 취소 요청 - 상담ID: {}, 사용자ID: {}", consultationId, userId);
        
        Consultation consultation = consultationRepository.findById(consultationId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상담입니다."));
        
        // 권한 검증: 사용자가 자신의 상담만 취소할 수 있도록 검증
        if (!consultation.getUser().getUserId().equals(userId)) {
            log.warn("상담 취소 권한 없음 - 상담ID: {}, 요청사용자ID: {}, 상담소유자ID: {}", 
                consultationId, userId, consultation.getUser().getUserId());
            throw new IllegalArgumentException("본인의 상담만 취소할 수 있습니다.");
        }
        
        // 이미 취소된 상담인지 확인
        if (consultation.getStatus().getStatusCode().equals(5L)) {
            throw new IllegalStateException("이미 취소된 상담입니다.");
        }
        
        // 완료된 상담인지 확인 (완료된 상담은 취소 불가)
        if (consultation.getStatus().getStatusCode().equals(4L)) {
            throw new IllegalStateException("완료된 상담은 취소할 수 없습니다.");
        }
        
        // CANCELLED 상태로 변경 (status_code = 5)
        ConsultationStatus cancelledStatus = consultationStatusRepository.findById(5L)
            .orElseThrow(() -> new IllegalArgumentException("상담 상태를 찾을 수 없습니다."));
        
        consultation.setStatus(cancelledStatus);
        consultation.setIsActive(false);
        
        Consultation savedConsultation = consultationRepository.save(consultation);
        log.info("상담 취소 완료 - 상담ID: {}, 사용자ID: {}", consultationId, userId);
        
        // 알림 생성
        try {
            Consultation consultationWithRelations = consultationRepository.findByIdWithRelations(savedConsultation.getConsultationId())
                .orElse(savedConsultation);
            notificationService.createConsultationNotifications(consultationWithRelations);
        } catch (Exception e) {
            log.error("알림 생성 실패 - 상담ID: {}", savedConsultation.getConsultationId(), e);
        }
    }
    
    /**
     * 매니저의 상담 바로 확정 (PENDING → CONFIRMED) (권한 검증 포함)
     */
    @Transactional
    public ConsultationResponse confirmConsultation(Long consultationId, Long managerId) {
        log.info("매니저 상담 확정 요청 - 상담ID: {}, 매니저ID: {}", consultationId, managerId);
        
        Consultation consultation = consultationRepository.findById(consultationId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상담입니다."));
        
        // 권한 검증: 매니저가 자신의 브랜드 상담만 확정할 수 있도록 검증
        if (!consultation.getBrand().getManager().getManagerId().equals(managerId)) {
            log.warn("상담 확정 권한 없음 - 상담ID: {}, 요청매니저ID: {}, 브랜드매니저ID: {}", 
                consultationId, managerId, consultation.getBrand().getManager().getManagerId());
            throw new IllegalArgumentException("본인이 관리하는 브랜드의 상담만 확정할 수 있습니다.");
        }
        
        // PENDING 상태인 상담만 확정 가능
        if (!consultation.getStatus().getStatusCode().equals(1L)) {
            throw new IllegalStateException("신청 중인 상담만 확정이 가능합니다.");
        }
        
        // CONFIRMED 상태로 변경 (status_code = 3)
        ConsultationStatus confirmedStatus = consultationStatusRepository.findById(3L)
            .orElseThrow(() -> new IllegalArgumentException("상담 상태를 찾을 수 없습니다."));
        
        consultation.setStatus(confirmedStatus);
        consultation.setConfirmedAt(LocalDateTime.now());
        
        Consultation savedConsultation = consultationRepository.save(consultation);
        log.info("상담 확정 완료 - 상담ID: {}, 매니저ID: {}", consultationId, managerId);
        
        // 알림 생성
        try {
            Consultation consultationWithRelations = consultationRepository.findByIdWithRelations(savedConsultation.getConsultationId())
                .orElse(savedConsultation);
            notificationService.createConsultationNotifications(consultationWithRelations);
        } catch (Exception e) {
            log.error("알림 생성 실패 - 상담ID: {}", savedConsultation.getConsultationId(), e);
        }
        
        return ConsultationResponse.from(savedConsultation);
    }
    
    /**
     * 매니저의 상담 취소 (권한 검증 포함)
     */
    @Transactional
    public void cancelConsultationByManager(Long consultationId, Long managerId) {
        log.info("매니저 상담 취소 요청 - 상담ID: {}, 매니저ID: {}", consultationId, managerId);
        
        Consultation consultation = consultationRepository.findById(consultationId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상담입니다."));
        
        // 권한 검증: 매니저가 자신의 브랜드 상담만 취소할 수 있도록 검증
        if (!consultation.getBrand().getManager().getManagerId().equals(managerId)) {
            log.warn("상담 취소 권한 없음 - 상담ID: {}, 요청매니저ID: {}, 브랜드매니저ID: {}", 
                consultationId, managerId, consultation.getBrand().getManager().getManagerId());
            throw new IllegalArgumentException("본인이 관리하는 브랜드의 상담만 취소할 수 있습니다.");
        }
        
        // 이미 취소된 상담인지 확인
        if (consultation.getStatus().getStatusCode().equals(5L)) {
            throw new IllegalStateException("이미 취소된 상담입니다.");
        }
        
        // 완료된 상담인지 확인 (완료된 상담은 취소 불가)
        if (consultation.getStatus().getStatusCode().equals(4L)) {
            throw new IllegalStateException("완료된 상담은 취소할 수 없습니다.");
        }
        
        // CANCELLED 상태로 변경 (status_code = 5)
        ConsultationStatus cancelledStatus = consultationStatusRepository.findById(5L)
            .orElseThrow(() -> new IllegalArgumentException("상담 상태를 찾을 수 없습니다."));
        
        consultation.setStatus(cancelledStatus);
        consultation.setIsActive(false);
        
        Consultation savedConsultation = consultationRepository.save(consultation);
        log.info("매니저 상담 취소 완료 - 상담ID: {}, 매니저ID: {}", consultationId, managerId);
        
        // 알림 생성
        try {
            Consultation consultationWithRelations = consultationRepository.findByIdWithRelations(savedConsultation.getConsultationId())
                .orElse(savedConsultation);
            notificationService.createConsultationNotifications(consultationWithRelations);
        } catch (Exception e) {
            log.error("알림 생성 실패 - 상담ID: {}", savedConsultation.getConsultationId(), e);
        }
    }
    
    /**
     * 상담 상태 코드에 따른 메시지 반환
     */
    private String getStatusMessage(Long statusCode) {
        switch (statusCode.intValue()) {
            case 1: return "신청 중인";
            case 2: return "일정 조정 중인";
            case 3: return "확정된";
            case 4: return "완료된";
            case 5: return "취소된";
            default: return "진행 중인";
        }
    }

}