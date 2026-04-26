package com.smj.workhub.comment.event;

import java.time.Instant;

public class CommentCreatedEvent {

    private final Long commentId;
    private final Long taskId;
    private final Long projectId;
    private final Long workspaceId;
    private final Long actorUserId;
    private final Long parentCommentId;
    private final Instant createdAt;

    public CommentCreatedEvent(
            Long commentId,
            Long taskId,
            Long projectId,
            Long workspaceId,
            Long actorUserId,
            Long parentCommentId,
            Instant createdAt
    ) {
        this.commentId = commentId;
        this.taskId = taskId;
        this.projectId = projectId;
        this.workspaceId = workspaceId;
        this.actorUserId = actorUserId;
        this.parentCommentId = parentCommentId;
        this.createdAt = createdAt;
    }

    public Long getCommentId() {
        return commentId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public Long getWorkspaceId() {
        return workspaceId;
    }

    public Long getActorUserId() {
        return actorUserId;
    }

    public Long getParentCommentId() {
        return parentCommentId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}

// todo why not lombok construcotr and getters an setters

/*  need explation of the reason
❌ No Lombok here (intentional)
→ Events should be explicit and stable

❌ No Entity references
→ Future Kafka compatibility

✔ Immutable (final fields)
 */