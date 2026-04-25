package com.smj.workhub.notification.service.impl;

import com.smj.workhub.notification.dto.NotificationResponse;
import com.smj.workhub.notification.entity.Notification;
import com.smj.workhub.notification.entity.NotificationType;
import com.smj.workhub.notification.repository.NotificationRepository;
import com.smj.workhub.notification.service.NotificationService;
import com.smj.workhub.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public void createNotification(Long userId,
                                   NotificationType type,
                                   String message,
                                   Long workspaceId,
                                   Long projectId,
                                   Long taskId,
                                   String metadata) {

        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setMessage(message);
        notification.setWorkspaceId(workspaceId);
        notification.setProjectId(projectId);
        notification.setTaskId(taskId);
        notification.setMetadata(metadata);
        notification.setRead(false);

        notificationRepository.save(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getUserNotifications(Long userId, Pageable pageable) {
        return notificationRepository.findByUserId(userId, pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getUnreadNotifications(Long userId, Pageable pageable) {
        return notificationRepository.findByUserIdAndReadFalse(userId, pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    @Override
    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository
                .findByIdAndUserId(notificationId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Notification not found with id: " + notificationId
                ));

        notification.setRead(true);
    }

    @Override
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsRead(userId);
    }

    private NotificationResponse toResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .type(notification.getType())
                .message(notification.getMessage())
                .read(notification.isRead())
                .workspaceId(notification.getWorkspaceId())
                .projectId(notification.getProjectId())
                .taskId(notification.getTaskId())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
