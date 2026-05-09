package com.smj.workhub.messaging.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smj.workhub.comment.event.CommentCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

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

            kafkaTemplate.send(topic, payload);

            System.out.println(
                    "CommentCreatedEvent sent to topic: "
                            + topic
                            + " | payload: "
                            + payload
            );

        } catch (JsonProcessingException e) {
            throw new RuntimeException(
                    "Failed to serialize CommentCreatedEvent",
                    e
            );
        }
    }
}
