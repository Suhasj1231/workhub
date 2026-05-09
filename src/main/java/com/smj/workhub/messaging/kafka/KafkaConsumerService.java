package com.smj.workhub.messaging.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smj.workhub.comment.event.CommentCreatedEvent;
import com.smj.workhub.notification.entity.NotificationType;
import com.smj.workhub.notification.service.NotificationService;

@Service
public class KafkaConsumerService {

    private final ObjectMapper objectMapper;
    private final NotificationService notificationService;

    public KafkaConsumerService(ObjectMapper objectMapper,
                                NotificationService notificationService) {
        this.objectMapper = objectMapper;
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = "comment-created-topic", groupId = "workhub-group")
    public void consumeCommentCreatedEvent(String message) {
        try {
            CommentCreatedEvent event = objectMapper.readValue(message, CommentCreatedEvent.class);

            System.out.println("CommentCreatedEvent received from Kafka: " + event.getCommentId());

            notificationService.createNotification(
                    event.getActorUserId(),
                    NotificationType.COMMENT_CREATED,
                    "New comment added on task",
                    event.getTaskId(),
                    event.getProjectId(),
                    event.getWorkspaceId(),
                    null
            );

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize CommentCreatedEvent", e);
        }
    }
}
