package com.smj.workhub.notification.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(
        name = "notifications",
        indexes = {
                @Index(name = "idx_notification_user", columnList = "user_id"),
                @Index(name = "idx_notification_workspace", columnList = "workspace_id"),
                @Index(name = "idx_notification_read", columnList = "is_read"),
                @Index(name = "idx_notification_created_at", columnList = "created_at")
        }
)
@Getter
@Setter
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // WHO receives notification
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // CONTEXT (multi-tenant + navigation)
    @Column(name = "workspace_id")
    private Long workspaceId;

    @Column(name = "project_id")
    private Long projectId;

    @Column(name = "task_id")
    private Long taskId;

    // TYPE (what happened)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    // MESSAGE (UI display)
    @Column(nullable = false, length = 500)
    private String message;

    // OPTIONAL metadata (future extensibility)
    @Column(columnDefinition = "TEXT")
    private String metadata;

    // READ STATUS
    @Column(name = "is_read", nullable = false)
    private boolean read = false;

    // TIMESTAMP
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}