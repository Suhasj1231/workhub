package com.smj.workhub.comment.service.impl;
import java.time.Instant;

import com.smj.workhub.activity.entity.ActivityAction;
import com.smj.workhub.activity.service.ActivityService;
import com.smj.workhub.comment.dto.CommentResponse;
import com.smj.workhub.comment.dto.CreateCommentRequest;
import com.smj.workhub.comment.dto.UpdateCommentRequest;
import com.smj.workhub.comment.entity.Comment;
import com.smj.workhub.comment.repository.CommentRepository;
import com.smj.workhub.comment.service.CommentService;
import com.smj.workhub.common.exception.ResourceNotFoundException;
import com.smj.workhub.security.principal.UserPrincipal;
import com.smj.workhub.task.entity.Task;
import com.smj.workhub.task.repository.TaskRepository;
import com.smj.workhub.workspace.service.WorkspaceAccessService;
import com.smj.workhub.notification.entity.NotificationType;
import org.springframework.context.ApplicationEventPublisher;
import com.smj.workhub.comment.event.CommentCreatedEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final WorkspaceAccessService workspaceAccessService;
    private final ActivityService activityService;
    private final ApplicationEventPublisher eventPublisher;

    public CommentServiceImpl(
            CommentRepository commentRepository,
            TaskRepository taskRepository,
            WorkspaceAccessService workspaceAccessService,
            ActivityService activityService,
            ApplicationEventPublisher eventPublisher
    ) {
        this.commentRepository = commentRepository;
        this.taskRepository = taskRepository;
        this.workspaceAccessService = workspaceAccessService;
        this.activityService = activityService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public CommentResponse createComment(Long taskId, CreateCommentRequest request) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + taskId));

        Long workspaceId = task.getProject().getWorkspace().getId();
        Long userId = getCurrentUserId();

        // Access check
        workspaceAccessService.verifyWorkspaceAccess(workspaceId);

        Comment parent = null;
        if (request.getParentCommentId() != null) {
            parent = commentRepository.findById(request.getParentCommentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent comment not found"));

            // ensure same task
            if (!parent.getTask().getId().equals(taskId)) {
                throw new IllegalArgumentException("Parent comment belongs to different task");
            }
        }

        Comment comment = new Comment();
        comment.setTask(task);
        comment.setUserId(userId);
        comment.setContent(request.getContent());
        comment.setParentComment(parent);

        Comment saved = commentRepository.save(comment);

        // Activity
        activityService.logActivity(
                userId,
                ActivityAction.COMMENT_CREATED,
                workspaceId,
                task.getProject().getId(),
                taskId,
                "Comment added on task",
                null
        );
        // 📣 Publish domain event
        eventPublisher.publishEvent(
                new CommentCreatedEvent(
                        java.util.UUID.randomUUID(),
                        saved.getId(),
                        taskId,
                        task.getProject().getId(),
                        workspaceId,
                        userId,
                        parent != null ? parent.getId() : null,
                        Instant.now()
                )
        );

        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommentResponse> getCommentsByTask(Long taskId, Pageable pageable) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + taskId));

        Long workspaceId = task.getProject().getWorkspace().getId();
        Long userId = getCurrentUserId();

        workspaceAccessService.verifyWorkspaceAccess(workspaceId);

        return commentRepository.findByTaskIdAndDeletedFalse(taskId, pageable)
                .map(this::toResponse);
    }

    @Override
    public CommentResponse updateComment(Long commentId, UpdateCommentRequest request) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found: " + commentId));

        Long userId = getCurrentUserId();

        if (!comment.getUserId().equals(userId)) {
            throw new RuntimeException("You can only update your own comments");
        }

        comment.setContent(request.getContent());

        // Activity
        activityService.logActivity(
                userId,
                ActivityAction.COMMENT_UPDATED,
                comment.getTask().getProject().getWorkspace().getId(),
                comment.getTask().getProject().getId(),
                comment.getTask().getId(),
                "Comment updated",
                null
        );

        return toResponse(comment);
    }

    @Override
    public void deleteComment(Long commentId) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found: " + commentId));

        Long userId = getCurrentUserId();

        if (!comment.getUserId().equals(userId)) {
            throw new RuntimeException("You can only delete your own comments");
        }

        comment.setDeleted(true);

        // Activity
        activityService.logActivity(
                userId,
                ActivityAction.COMMENT_DELETED,
                comment.getTask().getProject().getWorkspace().getId(),
                comment.getTask().getProject().getId(),
                comment.getTask().getId(),
                "Comment deleted",
                null
        );
    }

    private CommentResponse toResponse(Comment c) {
        CommentResponse res = new CommentResponse();
        res.setId(c.getId());
        res.setTaskId(c.getTask().getId());
        res.setUserId(c.getUserId());
        res.setContent(c.getContent());
        res.setParentCommentId(c.getParentComment() != null ? c.getParentComment().getId() : null);
        res.setCreatedAt(c.getCreatedAt());
        res.setUpdatedAt(c.getUpdatedAt());
        return res;
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        return principal.getId();
    }
}
