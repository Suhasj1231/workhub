package com.smj.workhub.notification.service;

import com.smj.workhub.notification.dto.NotificationResponse;
import com.smj.workhub.notification.entity.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {

    // Create notification (used internally by other services)
    void createNotification(Long userId,
                            NotificationType type,
                            String message,
                            Long workspaceId,
                            Long projectId,
                            Long taskId,
                            String metadata);

    // Get all notifications for current user
    Page<NotificationResponse> getUserNotifications(Long userId, Pageable pageable);

    // Get unread notifications
    Page<NotificationResponse> getUnreadNotifications(Long userId, Pageable pageable);

    // Get unread count (for badge)
    long getUnreadCount(Long userId);

    // Mark one notification as read
    void markAsRead(Long notificationId, Long userId);

    // Mark all notifications as read
    void markAllAsRead(Long userId);
}
