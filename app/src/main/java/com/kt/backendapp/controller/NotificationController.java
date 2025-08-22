package com.kt.backendapp.controller;

import com.kt.backendapp.dto.common.ApiResponse;
import com.kt.backendapp.dto.common.PageResponse;
import com.kt.backendapp.dto.response.notification.NotificationResponse;
import com.kt.backendapp.entity.Notification;
import com.kt.backendapp.entity.RecipientType;
import com.kt.backendapp.service.INotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class NotificationController {
    
    private final INotificationService notificationService;
    
    /**
     * 사용자 알림 목록 조회
     */
    @GetMapping("/api/user/notifications")
    public ResponseEntity<ApiResponse<PageResponse<NotificationResponse>>> getUserNotifications(
            @RequestHeader("User-Id") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Notification> notifications = notificationService.getUserNotifications(userId, pageable);
        
        Page<NotificationResponse> responsePage = notifications.map(NotificationResponse::from);
        PageResponse<NotificationResponse> pageResponse = PageResponse.of(responsePage);
        
        return ResponseEntity.ok(ApiResponse.success(pageResponse, "알림 목록을 조회했습니다."));
    }
    
    /**
     * 매니저 알림 목록 조회
     */
    @GetMapping("/api/manager/notifications")
    public ResponseEntity<ApiResponse<PageResponse<NotificationResponse>>> getManagerNotifications(
            @RequestHeader("Manager-Id") Long managerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Notification> notifications = notificationService.getManagerNotifications(managerId, pageable);
        
        Page<NotificationResponse> responsePage = notifications.map(NotificationResponse::from);
        PageResponse<NotificationResponse> pageResponse = PageResponse.of(responsePage);
        
        return ResponseEntity.ok(ApiResponse.success(pageResponse, "알림 목록을 조회했습니다."));
    }
    
    /**
     * 알림 읽음 처리
     */
    @PutMapping("/api/notifications/{notificationId}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok(ApiResponse.success(null, "알림을 읽음 처리했습니다."));
    }
    
    /**
     * 사용자 읽지 않은 알림 개수 조회
     */
    @GetMapping("/api/user/notifications/unread-count")
    public ResponseEntity<ApiResponse<Long>> getUserUnreadCount(@RequestHeader("User-Id") Long userId) {
        long count = notificationService.getUnreadCount(userId, RecipientType.USER);
        return ResponseEntity.ok(ApiResponse.success(count, "읽지 않은 알림 개수를 조회했습니다."));
    }
    
    /**
     * 매니저 읽지 않은 알림 개수 조회
     */
    @GetMapping("/api/manager/notifications/unread-count")
    public ResponseEntity<ApiResponse<Long>> getManagerUnreadCount(@RequestHeader("Manager-Id") Long managerId) {
        long count = notificationService.getUnreadCount(managerId, RecipientType.MANAGER);
        return ResponseEntity.ok(ApiResponse.success(count, "읽지 않은 알림 개수를 조회했습니다."));
    }
}
