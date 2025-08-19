package com.kt.backendapp.dto.response.notification;

import com.kt.backendapp.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {
    private Long notificationId;
    private Long consultationId;
    private String notificationType;
    private String message;
    private LocalDateTime createdAt;
    private Boolean isRead;
    
    // 관련 상담 정보 (요약)
    private ConsultationInfo consultation;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ConsultationInfo {
        private Long consultationId;
        private String brandName;
        private String statusName;
    }
    
    // Entity → DTO 변환 메소드
    public static NotificationResponse from(Notification notification) {
        ConsultationInfo consultationInfo = null;
        if (notification.getConsultation() != null) {
            consultationInfo = ConsultationInfo.builder()
                .consultationId(notification.getConsultation().getConsultationId())
                .brandName(notification.getConsultation().getBrand() != null ? 
                    notification.getConsultation().getBrand().getBrandName() : null)
                .statusName(notification.getConsultation().getStatus() != null ? 
                    notification.getConsultation().getStatus().getStatusName() : null)
                .build();
        }
        
        return NotificationResponse.builder()
            .notificationId(notification.getNotificationId())
            .consultationId(notification.getConsultation() != null ? 
                notification.getConsultation().getConsultationId() : null)
            .notificationType(notification.getStatus() != null ? 
                notification.getStatus().getStatusName() : null)  // status를 통해 타입 정보 가져옴
            .message(notification.getMessage())
            .createdAt(notification.getCreatedAt())
            .isRead(notification.getIsRead())
            .consultation(consultationInfo)
            .build();
    }
}
