package com.smj.workhub.messaging.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smj.workhub.comment.event.CommentCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class KafkaProducerService {

    private static final Logger log = LoggerFactory.getLogger(KafkaProducerService.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate,
                                ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendCommentCreatedEvent(String topic,
                                        CommentCreatedEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            log.info(
                    "EVENT_PUBLISH_INITIATED | eventId={} | topic={} | commentId={}",
                    event.getEventId(),
                    topic,
                    event.getCommentId()
            );

            kafkaTemplate.send(topic, payload)
                    .whenComplete((result, ex) -> {

                        if (ex != null) {

                            log.error(
                                    "EVENT_PUBLISH_FAILED | eventId={} | topic={} | error={}",
                                    event.getEventId(),
                                    topic,
                                    ex.getMessage(),
                                    ex
                            );

                        } else {

                            log.info(
                                    "EVENT_PUBLISHED_SUCCESSFULLY | eventId={} | topic={} | partition={} | offset={}",
                                    event.getEventId(),
                                    topic,
                                    result.getRecordMetadata().partition(),
                                    result.getRecordMetadata().offset()
                            );
                        }
                    });

        } catch (JsonProcessingException e) {
            log.error(
                    "EVENT_SERIALIZATION_FAILED | eventId={} | error={}",
                    event.getEventId(),
                    e.getMessage(),
                    e
            );

            throw new RuntimeException(
                    "Failed to serialize CommentCreatedEvent",
                    e
            );
        }
    }
}
