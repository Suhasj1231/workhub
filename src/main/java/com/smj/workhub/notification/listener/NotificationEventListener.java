package com.smj.workhub.notification.listener;

import com.smj.workhub.comment.event.CommentCreatedEvent;
import com.smj.workhub.notification.entity.NotificationType;
import com.smj.workhub.notification.service.NotificationService;
import com.smj.workhub.task.entity.Task;
import com.smj.workhub.task.repository.TaskRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationEventListener {

    private final NotificationService notificationService;
    private final TaskRepository taskRepository;

    public NotificationEventListener(NotificationService notificationService,
                                     TaskRepository taskRepository) {
        this.notificationService = notificationService;
        this.taskRepository = taskRepository;
    }

    @EventListener
    public void handleCommentCreated(CommentCreatedEvent event) {

        // Fetch task to determine notification target
        Task task = taskRepository.findById(event.getTaskId())
                .orElse(null);

        if (task == null) {
            return;
        }

        Long targetUserId = task.getCreatedBy();

        // Avoid self-notification
        if (targetUserId == null || targetUserId.equals(event.getActorUserId())) {
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
    }
}
