package com.smj.workhub.comment.event;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public class CommentCreatedEvent implements Serializable {

    private UUID eventId;
    private Long commentId;
    private Long taskId;
    private Long projectId;
    private Long workspaceId;
    private Long actorUserId;
    private Long parentCommentId;
    private Instant occurredAt;

    // Default constructor (required for deserialization)
    public CommentCreatedEvent() {
    }

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

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(Long workspaceId) {
        this.workspaceId = workspaceId;
    }

    public Long getActorUserId() {
        return actorUserId;
    }

    public void setActorUserId(Long actorUserId) {
        this.actorUserId = actorUserId;
    }

    public Long getParentCommentId() {
        return parentCommentId;
    }

    public void setParentCommentId(Long parentCommentId) {
        this.parentCommentId = parentCommentId;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(Instant occurredAt) {
        this.occurredAt = occurredAt;
    }
}
