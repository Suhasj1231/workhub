package com.smj.workhub.messaging.kafka;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTestRunner {

    @Bean
    CommandLineRunner testKafka(KafkaProducerService kafkaProducerService) {
        return args -> {
            kafkaProducerService.sendMessage("test-topic", "First Kafka Message");
        };
    }
}