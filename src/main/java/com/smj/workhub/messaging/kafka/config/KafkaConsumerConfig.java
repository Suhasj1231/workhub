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

        // Retry every 1 second with maximum 2 retries
        FixedBackOff fixedBackOff = new FixedBackOff(
                1000L,
                2L
        );

        DeadLetterPublishingRecoverer recoverer =
                new DeadLetterPublishingRecoverer(
                        kafkaTemplate,
                        (record, ex) -> {

                            String deadLetterTopic = record.topic() + "-dlt";

                            log.error(
                                    "EVENT_MOVED_TO_DLQ | originalTopic={} | deadLetterTopic={} | partition={} | payload={} | error={}",
                                    record.topic(),
                                    deadLetterTopic,
                                    record.partition(),
                                    record.value(),
                                    ex.getMessage(),
                                    ex
                            );

                            return new TopicPartition(
                                    deadLetterTopic,
                                    record.partition()
                            );
                        }
                );

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(
                recoverer,
                fixedBackOff
        );

        errorHandler.setRetryListeners((record, ex, deliveryAttempt) ->
                log.warn(
                        "EVENT_RETRY_TRIGGERED | topic={} | attempt={} | payload={} | error={}",
                        record.topic(),
                        deliveryAttempt,
                        record.value(),
                        ex.getMessage(),
                        ex
                )
        );

        errorHandler.addNotRetryableExceptions(
                JsonProcessingException.class
        );

        log.info(
                "KAFKA_ERROR_HANDLING_CONFIGURED | retries={} | retryBackoffMs={} | dlqEnabled=true",
                2,
                1000
        );

        return errorHandler;
    }
}
