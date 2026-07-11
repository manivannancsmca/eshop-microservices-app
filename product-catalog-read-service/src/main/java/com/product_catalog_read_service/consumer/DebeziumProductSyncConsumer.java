package com.product_catalog_read_service.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.common.avro.schemas.ProductEvent;
import com.product_catalog_read_service.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DebeziumProductSyncConsumer {

    private final ProductRepository productRepository;

    @KafkaListener(topics = "cdc.product_catalog.products", groupId = "product-read-service-group")
    public void syncWithElasticsearch(ConsumerRecord<String, ProductEvent> record) {

        String productId = record.key();
        ProductEvent productAvroEvent = record.value();

        // 1. If Debezium sends a tombstone/null payload, remove it instantly from search visibility
        if (productAvroEvent == null) {
            productRepository.deleteById(productId);
            return;
        }

    }

}
