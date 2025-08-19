package com.kt.backendapp.dto.request.consultation;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsultationStatusUpdateRequest {
    
    @NotNull(message = "상담 상태는 필수입니다.")
    private Long statusCode;
    
    @Size(max = 500, message = "매니저 메모는 500자를 초과할 수 없습니다.")
    private String managerNote;
}