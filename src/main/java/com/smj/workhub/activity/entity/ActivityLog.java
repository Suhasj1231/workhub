package com.smj.workhub.activity.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(
        name = "activity_logs",
        indexes = {
                @Index(name = "idx_activity_user", columnList = "user_id"),
                @Index(name = "idx_activity_workspace", columnList = "workspace_id"),
                @Index(name = "idx_activity_project", columnList = "project_id"),
                @Index(name = "idx_activity_task", columnList = "task_id"),
                @Index(name = "idx_activity_created_at", columnList = "created_at")
        }
)
@Getter
@Setter
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // WHO
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // WHAT
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityAction action;

    // WHERE
    @Column(name = "workspace_id")
    private Long workspaceId;

    @Column(name = "project_id")
    private Long projectId;

    @Column(name = "task_id")
    private Long taskId;

    // MESSAGE
    @Column(nullable = false, length = 500)
    private String description;

    // METADATA (JSON as string)
    @Column(columnDefinition = "TEXT")
    private String metadata;

    // WHEN
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}