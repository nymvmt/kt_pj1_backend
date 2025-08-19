package com.kt.backendapp.dto.response.auth;

import com.kt.backendapp.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private Long userId;
    private String email;
    private String name;
    private String phone;
    
    // 추가 통계 정보 (선택적)
    private UserStats stats;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserStats {
        private Long savedBrandCount;      // 찜한 브랜드 수
        private Long consultationCount;    // 신청한 상담 수
        private Long unreadNotificationCount; // 읽지 않은 알림 수
    }
    
    // Entity → DTO 변환 메소드
    public static UserResponse from(User user) {
        return UserResponse.builder()
            .userId(user.getUserId())
            .email(user.getEmail())
            .name(user.getName())
            .phone(user.getPhone())
            .build();
    }
    
    // 통계 정보 포함 변환 메소드
    public static UserResponse from(User user, UserStats stats) {
        UserResponse response = from(user);
        response.setStats(stats);
        return response;
    }
}
