package com.kt.backendapp.dto.request.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationReadRequest {
    private List<Long> notificationIds;  // 읽음 처리할 알림 ID 목록
}
