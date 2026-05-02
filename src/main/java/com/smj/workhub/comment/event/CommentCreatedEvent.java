package com.smj.workhub.comment.event;

import java.time.Instant;
import java.util.UUID;

public class CommentCreatedEvent {

    private final UUID eventId;
    private final Long commentId;
    private final Long taskId;
    private final Long projectId;
    private final Long workspaceId;
    private final Long actorUserId;
    private final Long parentCommentId;
    private final Instant occurredAt;

    public CommentCreatedEvent(
            UUID eventId,
            Long commentId,
            Long taskId,
            Long projectId,
            Long workspaceId,
            Long actorUserId,
            Long parentCommentId,
            Instant occurredAt
    ) {
        this.eventId = eventId;
        this.commentId = commentId;
        this.taskId = taskId;
        this.projectId = projectId;
        this.workspaceId = workspaceId;
        this.actorUserId = actorUserId;
        this.parentCommentId = parentCommentId;
        this.occurredAt = occurredAt;
    }

    public UUID getEventId() {
        return eventId;
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

    public Instant getOccurredAt() {
        return occurredAt;
    }
}


/*  need explation of the reason
❌ No Lombok here (intentional)
→ Events should be explicit and stable

❌ No Entity references
→ Future Kafka compatibility

✔ Immutable (final fields)
 */