package com.kt.backendapp.repository;

import com.kt.backendapp.entity.Notification;
import com.kt.backendapp.entity.RecipientType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    // 수신자 ID와 타입으로 알림 목록 조회 (최신순)
    Page<Notification> findByRecipientIdAndRecipientTypeOrderByCreatedAtDesc(
        Long recipientId, RecipientType recipientType, Pageable pageable);
    
    // 읽지 않은 알림 개수 조회
    long countByRecipientIdAndRecipientTypeAndIsReadFalse(
        Long recipientId, RecipientType recipientType);
    
    // 모든 알림을 읽음 처리
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.recipientId = :recipientId AND n.recipientType = :recipientType")
    void markAllAsReadByRecipient(@Param("recipientId") Long recipientId, @Param("recipientType") RecipientType recipientType);
}
