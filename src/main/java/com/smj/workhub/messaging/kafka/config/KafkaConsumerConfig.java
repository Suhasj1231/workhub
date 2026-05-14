package com.smj.workhub.messaging.kafka.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConsumerConfig {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerConfig.class);

    @Bean
    public DefaultErrorHandler kafkaErrorHandler(
            KafkaTemplate<String, String> kafkaTemplate
    ) {

        FixedBackOff fixedBackOff = new FixedBackOff(
                1000L,
                2L
        );

        DeadLetterPublishingRecoverer recoverer =
                new DeadLetterPublishingRecoverer(
                        kafkaTemplate,
                        (record, ex) -> new TopicPartition(
                                record.topic() + "-dlt",
                                record.partition()
                        )
                );

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(
                recoverer,
                fixedBackOff
        );

        errorHandler.setRetryListeners((record, ex, deliveryAttempt) ->
                log.warn(
                        "Retrying Kafka message | topic={} | attempt={} | payload={}",
                        record.topic(),
                        deliveryAttempt,
                        record.value(),
                        ex
                )
        );

        errorHandler.addNotRetryableExceptions(
                JsonProcessingException.class
        );

        log.info("Kafka DLQ support enabled");

        return errorHandler;
    }
}
