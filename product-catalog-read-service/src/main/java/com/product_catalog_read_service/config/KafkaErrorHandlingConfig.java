package com.product_catalog_read_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaErrorHandlingConfig {

    /**
     * Explicitly provision the DLT topic with healthy partition architecture
     */
    @Bean
    public NewTopic productDltTopic() {
        return TopicBuilder.name("cdc.product_catalog.outbox.DLT")
                .partitions(3)
                .replicas(1)
                .build();
    }

    /**
     * Define the Global Error Handler to manage retries and DLT routing
     */
    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<Object, Object> kafkaTemplate) {
        // 1. Configure the recovery action: Publish failed events to the '.DLT' topic suffix
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate);

        // 2. Configure retry logic: 3 execution attempts max, spaced 2000ms apart
        FixedBackOff backOff = new FixedBackOff(2000L, 2L);

        // 3. Assemble the Error Handler
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, backOff);
        
        // Don't waste time retrying non-recoverable errors (like a JSON/Avro parsing defect)
        errorHandler.addNotRetryableExceptions(NullPointerException.class); 
        
        return errorHandler;
    }
}
