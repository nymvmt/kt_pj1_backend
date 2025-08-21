package com.kt.backendapp.dto.request.consultation;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 매니저의 상담 일정 조정 요청 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsultationRescheduleRequest {
    
    @NotNull(message = "조정된 날짜는 필수입니다.")
    @Future(message = "조정된 날짜는 현재 날짜 이후여야 합니다.")
    private LocalDate adjustedDate;
    
    @NotNull(message = "조정된 시간은 필수입니다.")
    private LocalTime adjustedTime;
    
    private String adjustmentReason;
    
    private String managerNote;
}
