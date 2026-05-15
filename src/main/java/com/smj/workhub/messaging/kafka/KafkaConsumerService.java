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
import java.time.Duration;

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
            Instant processingStartedAt = Instant.now();

            CommentCreatedEvent event = objectMapper.readValue(message, CommentCreatedEvent.class);

            log.info(
                    "EVENT_CONSUMED | eventId={} | rawMessageReceived=true",
                    event.getEventId()
            );

            if (processedEventRepository.existsByEventId(event.getEventId())) {

                log.warn(
                        "EVENT_DUPLICATE_SKIPPED | eventId={}",
                        event.getEventId()
                );

                return;
            }

            log.info(
                    "EVENT_PROCESSING_STARTED | eventId={} | commentId={} | taskId={}",
                    event.getEventId(),
                    event.getCommentId(),
                    event.getTaskId()
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

            long processingTimeMs = Duration.between(
                    processingStartedAt,
                    Instant.now()
            ).toMillis();

            log.info(
                    "EVENT_PROCESSED_SUCCESSFULLY | eventId={} | processingTimeMs={}",
                    event.getEventId(),
                    processingTimeMs
            );

        } catch (JsonProcessingException e) {

            log.error(
                    "EVENT_DESERIALIZATION_FAILED | rawMessage={} | error={}",
                    message,
                    e.getMessage(),
                    e
            );

        } catch (Exception e) {

            log.error(
                    "EVENT_PROCESSING_FAILED | rawMessage={} | error={}",
                    message,
                    e.getMessage(),
                    e
            );

            throw e;
        }
    }
}
