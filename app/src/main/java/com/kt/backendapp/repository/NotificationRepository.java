package com.kt.backendapp.repository;

import com.kt.backendapp.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByRecipientUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    Long countByRecipientUserIdAndIsReadFalse(Long userId);
    
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.recipient.userId = :userId")
    void markAllAsReadByUserId(@Param("userId") Long userId);
}
