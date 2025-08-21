package com.kt.backendapp.dto.request.consultation;

import com.kt.backendapp.entity.UserResponseType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 사용자의 상담 일정 조정 응답 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsultationUserResponseRequest {
    
    @NotNull(message = "사용자 응답은 필수입니다.")
    private UserResponseType userResponse;
}
