package com.product_catalog_read_service.consumer;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import com.common.avro.schemas.ProductEvent;
import com.product_catalog_read_service.entity.ProductDocument;
import com.product_catalog_read_service.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DebeziumProductSyncConsumer {

    private final ProductRepository productRepository;

    @KafkaListener(topics = "cdc.product_catalog.outbox", groupId = "product-read-service-group")
    public void syncWithElasticsearch(ConsumerRecord<String, ProductEvent> record, Acknowledgment ack) {

        String productId = record.key();
        ProductEvent productAvroEvent = record.value();

        // 1. If Debezium sends a tombstone/null payload, remove it instantly from
        // search visibility
        if (productAvroEvent == null) {

            log.info("CDC Event [HARD DELETE] -> Dropping Product ", productId);
            if (productId != null) {
                productRepository.deleteById(productId);
            }
            ack.acknowledge(); // Safely commit offset after action
            return;
        }

        if (Boolean.TRUE.equals(productAvroEvent.getIsDeleted())) {
            System.out.println("CDC Event [SOFT DELETE] -> Dropping Product " + productAvroEvent.getId());
            productRepository.deleteById(String.valueOf(productAvroEvent.getId()));
            ack.acknowledge();
            return;
        }

        log.info("CDC Event [SYNC] -> Processing Index Update for Product: ", productAvroEvent.getId());

        saveDocument(productAvroEvent, ack);

    }

    private void saveDocument(ProductEvent productAvroEvent, Acknowledgment ack) {
        try {
            // 3. Map Avro fields to your Elasticsearch Document
            ProductDocument doc = new ProductDocument();
            doc.setId(String.valueOf(productAvroEvent.getId()));
            doc.setEventId(productAvroEvent.getEventId().toString());
            doc.setSku(productAvroEvent.getSku().toString());
            doc.setName(productAvroEvent.getName().toString());

            // Handle nullable description
            doc.setDescription(
                    productAvroEvent.getDescription() != null ? productAvroEvent.getDescription().toString() : "");

            doc.setBrandId(productAvroEvent.getBrandId());
            doc.setBrandName(productAvroEvent.getBrandName().toString());
            doc.setCategoryId(productAvroEvent.getCategoryId());
            doc.setCategoryName(productAvroEvent.getCategoryName().toString());

            // Safe conversion of Avro Decimal logic layer to Java BigDecimal
            // BigDecimal extractedPrice = productAvroEvent.getPrice();

            java.nio.ByteBuffer buffer = productAvroEvent.getPrice();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);

            BigDecimal extractedPrice = new BigDecimal(new BigInteger(bytes), 2);
            doc.setPrice(extractedPrice);

            doc.setStockCount(productAvroEvent.getStockCount());
            doc.setStatus(productAvroEvent.getStatus().toString());
            doc.setIsDeleted(productAvroEvent.getIsDeleted());

            Instant timestampInstant = productAvroEvent.getUpdatedAt();
            doc.setUpdatedAt(timestampInstant.toEpochMilli());

            // 4. Commit directly into Elasticsearch index
            productRepository.save(doc);

            ack.acknowledge();

        } catch (Exception e) {
            // Log the error carefully without breaking the loop or losing track of the
            // offset partition
            System.err.println("Failed to cleanly sync tracking instance to search layer: " + e.getMessage());
            // Pro Tip: In production, send failed events to a Dead Letter Topic (DLT) here
            // instead of stalling the pipeline
        }
    }

}
