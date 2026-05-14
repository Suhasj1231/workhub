package com.smj.workhub.messaging.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smj.workhub.comment.event.CommentCreatedEvent;
import com.smj.workhub.notification.entity.NotificationType;
import com.smj.workhub.notification.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smj.workhub.common.event.entity.ProcessedEvent;
import com.smj.workhub.common.event.repository.ProcessedEventRepository;

import java.time.Instant;

@Service
public class KafkaConsumerService {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerService.class);

    private final ObjectMapper objectMapper;
    private final NotificationService notificationService;
    private final ProcessedEventRepository processedEventRepository;

    public KafkaConsumerService(ObjectMapper objectMapper,
                                NotificationService notificationService,
                                ProcessedEventRepository processedEventRepository) {
        this.objectMapper = objectMapper;
        this.notificationService = notificationService;
        this.processedEventRepository = processedEventRepository;
    }

    @KafkaListener(topics = "comment-created-topic", groupId = "workhub-group")
    public void consumeCommentCreatedEvent(String message) {
        try {
            CommentCreatedEvent event = objectMapper.readValue(message, CommentCreatedEvent.class);

            if (processedEventRepository.existsByEventId(event.getEventId())) {

                log.warn(
                        "Skipping duplicate Kafka event | eventId={}",
                        event.getEventId()
                );

                return;
            }

            log.info(
                    "CommentCreatedEvent received from Kafka | eventId={} | commentId={}",
                    event.getEventId(),
                    event.getCommentId()
            );

            notificationService.createNotification(
                    event.getActorUserId(),
                    NotificationType.COMMENT_CREATED,
                    "New comment added on task",
                    event.getTaskId(),
                    event.getProjectId(),
                    event.getWorkspaceId(),
                    null
            );

            processedEventRepository.save(
                    new ProcessedEvent(
                            event.getEventId(),
                            Instant.now()
                    )
            );

            log.info(
                    "Marked Kafka event as processed | eventId={}",
                    event.getEventId()
            );

        } catch (JsonProcessingException e) {

            log.error(
                    "Failed to deserialize Kafka message: {}",
                    message,
                    e
            );

        } catch (Exception e) {

            log.error(
                    "Retryable failure while processing CommentCreatedEvent | message={}",
                    message,
                    e
            );

            throw e;
        }
    }
}
