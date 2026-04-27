package com.smj.workhub.notification.listener;

import com.smj.workhub.comment.event.CommentCreatedEvent;
import com.smj.workhub.notification.entity.NotificationType;
import com.smj.workhub.notification.service.NotificationService;
import com.smj.workhub.task.entity.Task;
import com.smj.workhub.task.repository.TaskRepository;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class NotificationEventListener {

    private static final Logger log = LoggerFactory.getLogger(NotificationEventListener.class);

    private final NotificationService notificationService;
    private final TaskRepository taskRepository;

    public NotificationEventListener(NotificationService notificationService,
                                     TaskRepository taskRepository) {
        this.notificationService = notificationService;
        this.taskRepository = taskRepository;
    }

    @Async
    @EventListener
    public void handleCommentCreated(CommentCreatedEvent event) {
        try {

            // Fetch task to determine notification target
            Task task = taskRepository.findById(event.getTaskId())
                    .orElse(null);

            if (task == null) {
                log.warn("Task not found for CommentCreatedEvent. eventId={}, taskId={}",
                        event.getEventId(), event.getTaskId());
                return;
            }

            Long targetUserId = task.getCreatedBy();

            // Avoid self-notification
            if (targetUserId == null || targetUserId.equals(event.getActorUserId())) {
                log.info("Skipping notification (self or null target). eventId={}, actorUserId={}, targetUserId={}",
                        event.getEventId(), event.getActorUserId(), targetUserId);
                return;
            }

            notificationService.createNotification(
                    targetUserId,
                    NotificationType.COMMENT_CREATED,
                    "New comment added on your task",
                    event.getWorkspaceId(),
                    event.getProjectId(),
                    event.getTaskId(),
                    null
            );

            log.info("Notification created successfully for CommentCreatedEvent. eventId={}, targetUserId={}",
                    event.getEventId(), targetUserId);

        } catch (Exception ex) {
            log.error("Failed to handle CommentCreatedEvent. eventId={}, error={}",
                    event.getEventId(), ex.getMessage(), ex);
        }
    }
}
