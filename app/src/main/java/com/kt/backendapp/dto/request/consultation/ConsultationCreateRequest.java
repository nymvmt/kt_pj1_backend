package com.kt.backendapp.dto.request.consultation;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsultationCreateRequest {
    
    @NotNull(message = "사용자 ID는 필수입니다.")
    private Long userId;
    
    @NotNull(message = "브랜드 ID는 필수입니다.")
    private Long brandId;
    
    @NotNull(message = "희망 날짜는 필수입니다.")
    private LocalDate preferredDate;
    
    @NotNull(message = "희망 시간은 필수입니다.")
    private String preferredTime;
}