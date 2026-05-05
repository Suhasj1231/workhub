package com.smj.workhub.messaging.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    @KafkaListener(topics = "test-topic", groupId = "workhub-group")
    public void consume(String message) {
        System.out.println("Message received from Kafka: " + message);
    }
}
