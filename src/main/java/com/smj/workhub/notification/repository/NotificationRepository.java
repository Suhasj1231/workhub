package com.smj.workhub.notification.repository;

import com.smj.workhub.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Get all notifications for a user (latest first via pageable sort)
    Page<Notification> findByUserId(Long userId, Pageable pageable);

    // Get unread notifications for a user
    Page<Notification> findByUserIdAndReadFalse(Long userId, Pageable pageable);

    // Count unread notifications
    long countByUserIdAndReadFalse(Long userId);

    Optional<Notification> findByIdAndUserId(Long id, Long userId);

    @Modifying
    @Query("UPDATE Notification n SET n.read = true WHERE n.userId = :userId")
    void markAllAsRead(@Param("userId") Long userId);

}
