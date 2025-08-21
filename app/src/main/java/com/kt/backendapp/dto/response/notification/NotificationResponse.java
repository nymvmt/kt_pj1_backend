package com.kt.backendapp.dto.response.notification;

import com.kt.backendapp.entity.Notification;
import com.kt.backendapp.entity.RecipientType;
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
    private Long recipientId;
    private RecipientType recipientType;
    private Long consultationId;
    private String brandName;
    private String userName;
    private String statusName;
    private String message;
    private LocalDateTime createdAt;
    private Boolean isRead;
    
    public static NotificationResponse from(Notification notification) {
        String brandName = notification.getConsultation() != null && 
                          notification.getConsultation().getBrand() != null ?
                          notification.getConsultation().getBrand().getBrandName() : null;
        
        String userName = notification.getConsultation() != null && 
                         notification.getConsultation().getUser() != null ?
                         notification.getConsultation().getUser().getName() : null;
        
        String statusName = notification.getStatus() != null ?
                           notification.getStatus().getStatusName() : null;
        
        return NotificationResponse.builder()
            .notificationId(notification.getNotificationId())
            .recipientId(notification.getRecipientId())
            .recipientType(notification.getRecipientType())
            .consultationId(notification.getConsultation() != null ? 
                           notification.getConsultation().getConsultationId() : null)
            .brandName(brandName)
            .userName(userName)
            .statusName(statusName)
            .message(notification.getMessage())
            .createdAt(notification.getCreatedAt())
            .isRead(notification.getIsRead())
            .build();
    }
}