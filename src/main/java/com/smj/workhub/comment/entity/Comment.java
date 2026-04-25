package com.smj.workhub.comment.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(
        name = "comments",
        indexes = {
                @Index(
                        name = "idx_comment_task_deleted_created",
                        columnList = "task_id, deleted, created_at"
                ),
                @Index(
                        name = "idx_comment_user",
                        columnList = "user_id"
                ),
                @Index(
                        name = "idx_comment_parent",
                        columnList = "parent_comment_id"
                )
        }
)
@Getter
@Setter
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔗 TASK (Many comments belong to one task)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "task_id", nullable = false)
    private com.smj.workhub.task.entity.Task task;

    // 👤 USER (who wrote the comment)
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 💬 CONTENT
    @Column(nullable = false, length = 2000)
    private String content;

    // 🔁 REPLY SUPPORT (self reference, optional)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment;

    // 🗑 SOFT DELETE
    @Column(nullable = false)
    private boolean deleted = false;

    // 🕒 AUDIT
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}