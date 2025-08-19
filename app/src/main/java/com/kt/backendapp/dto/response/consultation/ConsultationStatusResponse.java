package com.kt.backendapp.dto.response.consultation;

import com.kt.backendapp.entity.ConsultationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsultationStatusResponse {
    private Long statusCode;
    private String statusName;
    
    // Entity → DTO 변환 메소드
    public static ConsultationStatusResponse from(ConsultationStatus status) {
        return ConsultationStatusResponse.builder()
            .statusCode(status.getStatusCode())
            .statusName(status.getStatusName())
            .build();
    }
}