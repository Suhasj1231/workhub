package com.smj.workhub.notification.dto;

import com.smj.workhub.notification.entity.NotificationType;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class NotificationResponse {

    private Long id;

    private NotificationType type;

    private String message;

    private boolean read;

    private Long workspaceId;
    private Long projectId;
    private Long taskId;

    private Instant createdAt;
}
