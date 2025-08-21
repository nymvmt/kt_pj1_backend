package com.kt.backendapp.dto.response.consultation;

import com.kt.backendapp.entity.Consultation;
import com.kt.backendapp.entity.UserResponseType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsultationResponse {
    private Long consultationId;
    
    // 사용자 정보
    private UserInfo user;
    
    // 브랜드 정보
    private BrandInfo brand;
    
    // 상담 상태
    private StatusInfo status;
    
    // 일정 정보
    private LocalDate preferredDate;
    private LocalTime preferredTime;
    private String managerNote;
    
    // 시간 정보
    private LocalDateTime createdAt;
    private LocalDateTime confirmedAt;

    
    // 일정 조정 관련 정보
    private LocalDate adjustedDate;
    private LocalTime adjustedTime;
    private String adjustmentReason;
    private LocalDateTime adjustmentRequestedAt;
    
    // 사용자 응답 관련 정보
    private UserResponseType userResponse;
    private LocalDateTime userResponseAt;
    
    // 활성 상태
    private Boolean isActive;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserInfo {
        private Long userId;
        private String name;
        private String email;
        private String phone;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BrandInfo {
        private Long brandId;
        private String brandName;
        private String categoryName;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StatusInfo {
        private Long statusCode;
        private String statusName;
    }
    
    // Entity → DTO 변환 메소드
    public static ConsultationResponse from(Consultation consultation) {
        UserInfo userInfo = null;
        if (consultation.getUser() != null) {
            userInfo = UserInfo.builder()
                .userId(consultation.getUser().getUserId())
                .name(consultation.getUser().getName())
                .email(consultation.getUser().getEmail())
                .phone(consultation.getUser().getPhone())
                .build();
        }
        
        BrandInfo brandInfo = null;
        if (consultation.getBrand() != null) {
            brandInfo = BrandInfo.builder()
                .brandId(consultation.getBrand().getBrandId())
                .brandName(consultation.getBrand().getBrandName())
                .categoryName(consultation.getBrand().getCategory() != null ? 
                    consultation.getBrand().getCategory().getCategoryName() : null)
                .build();
        }
        
        StatusInfo statusInfo = null;
        if (consultation.getStatus() != null) {
            statusInfo = StatusInfo.builder()
                .statusCode(consultation.getStatus().getStatusCode())
                .statusName(consultation.getStatus().getStatusName())
                .build();
        }
        
        return ConsultationResponse.builder()
            .consultationId(consultation.getConsultationId())
            .user(userInfo)
            .brand(brandInfo)
            .status(statusInfo)
            .preferredDate(consultation.getPreferredDate())
            .preferredTime(consultation.getPreferredTime())
            .managerNote(consultation.getManagerNote())
            .createdAt(consultation.getCreatedAt())
            .confirmedAt(consultation.getConfirmedAt())

            .adjustedDate(consultation.getAdjustedDate())
            .adjustedTime(consultation.getAdjustedTime())
            .adjustmentReason(consultation.getAdjustmentReason())
            .adjustmentRequestedAt(consultation.getAdjustmentRequestedAt())
            .userResponse(consultation.getUserResponse())
            .userResponseAt(consultation.getUserResponseAt())
            .isActive(consultation.getIsActive())
            .build();
    }
}
