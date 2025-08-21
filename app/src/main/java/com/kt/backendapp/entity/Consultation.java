package com.kt.backendapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "consultation")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Consultation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "consultation_id")
    private Long consultationId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_code")  // referencedColumnName 제거
    private ConsultationStatus status;
    
    @Column(name = "preferred_date")
    private LocalDate preferredDate;
    
    @Column(name = "preferred_time")
    private LocalTime preferredTime;
    
    @Column(name = "manager_note", columnDefinition = "TEXT")
    private String managerNote;
    
    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;
    

    
    // 상담 일정 조정 관련 필드들
    @Column(name = "adjusted_date")
    private LocalDate adjustedDate;
    
    @Column(name = "adjusted_time")
    private LocalTime adjustedTime;
    
    @Column(name = "adjustment_reason", columnDefinition = "TEXT")
    private String adjustmentReason;
    
    @Column(name = "adjustment_requested_at")
    private LocalDateTime adjustmentRequestedAt;
    
    // 사용자 응답 관련 필드들
    @Enumerated(EnumType.STRING)
    @Column(name = "user_response")
    private UserResponseType userResponse;
    
    @Column(name = "user_response_at")
    private LocalDateTime userResponseAt;
    
    // 중복 신청 제한을 위한 활성 상태
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
}
