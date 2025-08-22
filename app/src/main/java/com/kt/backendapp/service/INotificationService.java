package com.kt.backendapp.service;

import com.kt.backendapp.entity.Consultation;
import com.kt.backendapp.entity.Notification;
import com.kt.backendapp.entity.RecipientType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 알림 관련 서비스 인터페이스
 */
public interface INotificationService {
    
    /**
     * 상담 상태 변경 시 사용자와 매니저에게 알림 생성 (안전한 버전)
     */
    void createConsultationNotifications(Consultation consultation);
    
    /**
     * 알림 생성 (안전한 버전)
     */
    void createNotification(Long recipientId, RecipientType recipientType, 
                          Consultation consultation, String message);
    
    /**
     * 사용자의 알림 목록 조회
     */
    Page<Notification> getUserNotifications(Long userId, Pageable pageable);
    
    /**
     * 매니저의 알림 목록 조회
     */
    Page<Notification> getManagerNotifications(Long managerId, Pageable pageable);
    
    /**
     * 알림 읽음 처리
     */
    void markAsRead(Long notificationId);
    
    /**
     * 사용자의 읽지 않은 알림 개수 조회
     */
    long getUnreadCount(Long recipientId, RecipientType recipientType);
}
