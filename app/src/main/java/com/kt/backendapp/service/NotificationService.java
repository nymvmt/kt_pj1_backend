package com.kt.backendapp.service;

import com.kt.backendapp.entity.*;
import com.kt.backendapp.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    
    /**
     * 상담 상태 변경 시 사용자와 매니저에게 알림 생성 (안전한 버전)
     */
    @Transactional
    public void createConsultationNotifications(Consultation consultation) {
        try {
            log.info("알림 생성 시작 - 상담ID: {}", consultation.getConsultationId());
            
            // Null 체크 및 필수 데이터 검증
            if (consultation == null) {
                log.warn("상담 객체가 null입니다.");
                return;
            }
            
            if (consultation.getUser() == null) {
                log.warn("상담의 사용자 정보가 null입니다. - 상담ID: {}", consultation.getConsultationId());
                return;
            }
            
            if (consultation.getBrand() == null) {
                log.warn("상담의 브랜드 정보가 null입니다. - 상담ID: {}", consultation.getConsultationId());
                return;
            }
            
            if (consultation.getBrand().getManager() == null) {
                log.warn("브랜드의 매니저 정보가 null입니다. - 상담ID: {}", consultation.getConsultationId());
                return;
            }
            
            if (consultation.getStatus() == null) {
                log.warn("상담의 상태 정보가 null입니다. - 상담ID: {}", consultation.getConsultationId());
                return;
            }
            
            Long userId = consultation.getUser().getUserId();
            Long managerId = consultation.getBrand().getManager().getManagerId();
            String statusName = consultation.getStatus().getStatusName();
            String brandName = consultation.getBrand().getBrandName();
            String userName = consultation.getUser().getName();
            
            log.info("알림 생성 데이터 - 사용자ID: {}, 매니저ID: {}, 상태: {}, 브랜드: {}, 사용자명: {}", 
                    userId, managerId, statusName, brandName, userName);
            
            // 사용자용 알림 생성
            String userMessage = getUserMessage(statusName, brandName);
            createNotification(userId, RecipientType.USER, consultation, userMessage);
            log.info("사용자 알림 생성 완료 - 사용자ID: {}, 메시지: {}", userId, userMessage);
            
            // 매니저용 알림 생성
            String managerMessage = getManagerMessage(statusName, userName);
            createNotification(managerId, RecipientType.MANAGER, consultation, managerMessage);
            log.info("매니저 알림 생성 완료 - 매니저ID: {}, 메시지: {}", managerId, managerMessage);
            
            log.info("상담 상태 변경 알림 생성 완료 - 상담ID: {}, 상태: {}", 
                    consultation.getConsultationId(), statusName);
        } catch (Exception e) {
            log.error("알림 생성 실패 - 상담ID: {}, 오류 타입: {}, 오류 메시지: {}", 
                    consultation != null ? consultation.getConsultationId() : "null", 
                    e.getClass().getSimpleName(), e.getMessage(), e);
            // 알림 생성 실패가 전체 상담 처리를 막지 않도록 예외를 잡음
        }
    }
    
    /**
     * 알림 생성 (안전한 버전)
     */
    @Transactional
    public void createNotification(Long recipientId, RecipientType recipientType, 
                                 Consultation consultation, String message) {
        try {
            log.info("개별 알림 생성 - 수신자ID: {}, 타입: {}, 메시지: {}", recipientId, recipientType, message);
            
            Notification notification = Notification.builder()
                .recipientId(recipientId)
                .recipientType(recipientType)
                .consultation(consultation)
                .status(consultation.getStatus())
                .message(message)
                .build();
            
            Notification savedNotification = notificationRepository.save(notification);
            log.info("알림 저장 완료 - 알림ID: {}", savedNotification.getNotificationId());
        } catch (Exception e) {
            log.error("개별 알림 생성 실패 - 수신자ID: {}, 타입: {}, 오류: {}", 
                    recipientId, recipientType, e.getMessage(), e);
            throw e; // 개별 알림 생성 실패는 상위로 전파
        }
    }
    
    /**
     * 사용자의 알림 목록 조회
     */
    @Transactional(readOnly = true)
    public Page<Notification> getUserNotifications(Long userId, Pageable pageable) {
        return notificationRepository.findByRecipientIdAndRecipientTypeOrderByCreatedAtDesc(
            userId, RecipientType.USER, pageable);
    }
    
    /**
     * 매니저의 알림 목록 조회
     */
    @Transactional(readOnly = true)
    public Page<Notification> getManagerNotifications(Long managerId, Pageable pageable) {
        return notificationRepository.findByRecipientIdAndRecipientTypeOrderByCreatedAtDesc(
            managerId, RecipientType.MANAGER, pageable);
    }
    
    /**
     * 알림 읽음 처리
     */
    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 알림입니다."));
        
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }
    
    /**
     * 사용자의 읽지 않은 알림 개수 조회
     */
    @Transactional(readOnly = true)
    public long getUnreadCount(Long recipientId, RecipientType recipientType) {
        return notificationRepository.countByRecipientIdAndRecipientTypeAndIsReadFalse(
            recipientId, recipientType);
    }
    
    /**
     * 사용자용 메시지 생성 (안전한 버전)
     */
    private String getUserMessage(String statusName, String brandName) {
        if (statusName == null) statusName = "UNKNOWN";
        if (brandName == null) brandName = "알 수 없는 브랜드";
        
        return switch (statusName) {
            case "PENDING" -> String.format("[%s] 예약이 신청되었습니다. 브랜드 담당자 확인 후 예약이 완료됩니다.", brandName);
            case "RESCHEDULE_REQUEST" -> String.format("[%s] 조정 요청 중입니다.", brandName);
            case "CONFIRMED" -> String.format("[%s] 예약이 확정되었습니다.", brandName);
            case "CANCELLED" -> String.format("[%s] 예약 신청이 취소되었습니다.", brandName);
            default -> String.format("[%s] 상담 상태가 변경되었습니다.", brandName);
        };
    }
    
    /**
     * 매니저용 메시지 생성 (안전한 버전)
     */
    private String getManagerMessage(String statusName, String userName) {
        if (statusName == null) statusName = "UNKNOWN";
        if (userName == null) userName = "알 수 없는 사용자";
        
        return switch (statusName) {
            case "PENDING" -> String.format("[%s님] 예약이 확정 대기 중입니다.", userName);
            case "RESCHEDULE_REQUEST" -> String.format("[%s님] 일정 조정을 요청했습니다.", userName);
            case "CONFIRMED" -> String.format("[%s님] 예약이 확정되었습니다.", userName);
            case "CANCELLED" -> String.format("[%s님] 예약 신청이 취소되었습니다.", userName);
            default -> String.format("[%s님] 상담 상태가 변경되었습니다.", userName);
        };
    }
}