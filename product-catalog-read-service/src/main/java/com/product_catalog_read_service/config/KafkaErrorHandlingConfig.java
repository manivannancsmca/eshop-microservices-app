package com.product_catalog_read_service.config;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaErrorHandlingConfig {

    /**
     * Dedicated ProducerFactory ensuring the DLT subsystem uses the Avro Serializer,
     * completely eliminating the ClassCastException.
     */
    @Bean
    public ProducerFactory<Object, Object> dltProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        // Explicitly set Confluent Avro Serializer for values
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        configProps.put("schema.registry.url", "http://localhost:8081");
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<Object, Object> dltKafkaTemplate() {
        return new KafkaTemplate<>(dltProducerFactory());
    }

    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<Object, Object> dltKafkaTemplate) {
        // Hardens processing by routing unrecoverable processing failures to an explicit dead-letter topic
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(dltKafkaTemplate,
                (record, exception) -> new TopicPartition("cdc.product_catalog.outbox.DLT", record.partition()));

        // Retry 3 times total (1 initial + 2 retries), spaced 2000ms apart
        FixedBackOff backOff = new FixedBackOff(2000L, 2L);

        return new DefaultErrorHandler(recoverer, backOff);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, GenericRecord> kafkaListenerContainerFactory(
            ConsumerFactory<String, GenericRecord> consumerFactory,
            DefaultErrorHandler errorHandler) {

        ConcurrentKafkaListenerContainerFactory<String, GenericRecord> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);

        // Bind the configured error handler directly to your container factory instance
        factory.setCommonErrorHandler(errorHandler);

        return factory;
    }
}