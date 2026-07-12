package com.product_catalog_read_service.consumer;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.time.Instant;

import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
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

    // Thread-safe specific reader mapped to your target shared-JAR Avro class
    private final SpecificDatumReader<ProductEvent> internalPayloadReader = new SpecificDatumReader<>(
            ProductEvent.class);

    @KafkaListener(topics = "cdc.product_catalog.outbox", groupId = "product-read-service-group")
    public void syncWithElasticsearch(ConsumerRecord<String, GenericRecord> record, Acknowledgment ack) {
        String productId = record.key();
        GenericRecord outerRecord = record.value();

        log.info("-------------------------------");
        log.info("Consumer received an automatically unwrapped Avro record wrapper from Kafka.");

        if (outerRecord == null) {
            log.info("CDC Event [HARD DELETE] -> Dropping Product: {}", productId);
            if (productId != null) {
                productRepository.deleteById(productId);
            }
            ack.acknowledge();
            return;
        }

        try {
            // 1. Safe field extraction by string name, completely independent of column
            // order or table layout
            Object eventTypeObj = outerRecord.get("event_type");
            Object aggregateIdObj = outerRecord.get("aggregate_id");
            String eventType = eventTypeObj != null ? eventTypeObj.toString() : "";
            String aggregateId = aggregateIdObj != null ? aggregateIdObj.toString() : "";

            if ("PRODUCT_DELETED".equals(eventType)) {
                log.info("CDC Event [METADATA DELETE] -> Dropping Product ID: {}", aggregateId);
                productRepository.deleteById(aggregateId);
                ack.acknowledge();
                return;
            }

            // 2. Fetch the inner database byte array payload column contents
            Object payloadObj = outerRecord.get("payload");
            byte[] rawAvroBytes = null;

            if (payloadObj instanceof ByteBuffer) {
                ByteBuffer buffer = (ByteBuffer) payloadObj;
                rawAvroBytes = new byte[buffer.remaining()];
                buffer.get(rawAvroBytes);
            } else if (payloadObj instanceof byte[]) {
                rawAvroBytes = (byte[]) payloadObj;
            }

            if (rawAvroBytes == null || rawAvroBytes.length == 0) {
                log.warn("Extracted internal payload field is empty for aggregate ID: {}", aggregateId);
                ack.acknowledge();
                return;
            }

            // 3. Decode the raw nested bytes cleanly back into the typed target Java bean
            ProductEvent productAvroEvent = internalPayloadReader.read(
                    null,
                    DecoderFactory.get().binaryDecoder(rawAvroBytes, null));

            if (Boolean.TRUE.equals(productAvroEvent.getIsDeleted())) {
                log.info("CDC Event [SOFT DELETE] -> Dropping Product: {}", productAvroEvent.getId());
                productRepository.deleteById(String.valueOf(productAvroEvent.getId()));
                ack.acknowledge();
                return;
            }

            log.info("CDC Event [SYNC] -> Processing Index Update for Product: {}", productAvroEvent.getId());
            saveDocument(productAvroEvent, ack);

        } catch (Exception pipelineError) {
            log.error("Fatal pipeline processing failure inside outbox decoder: {}", pipelineError.getMessage(),
                    pipelineError);
            throw new RuntimeException("Consumer processing pipeline failure", pipelineError);
        }
    }

    private void saveDocument(ProductEvent productAvroEvent, Acknowledgment ack) {
        ProductDocument doc = new ProductDocument();
        doc.setId(String.valueOf(productAvroEvent.getId()));
        doc.setEventId(productAvroEvent.getEventId().toString());
        doc.setSku(productAvroEvent.getSku().toString());
        doc.setName(productAvroEvent.getName().toString());

        doc.setDescription(
                productAvroEvent.getDescription() != null ? productAvroEvent.getDescription().toString() : "");

        doc.setBrandId(productAvroEvent.getBrandId());
        doc.setBrandName(productAvroEvent.getBrandName().toString());
        doc.setCategoryId(productAvroEvent.getCategoryId());
        doc.setCategoryName(productAvroEvent.getCategoryName().toString());

        java.nio.ByteBuffer buffer = productAvroEvent.getPrice();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);

        BigDecimal extractedPrice = new BigDecimal(new java.math.BigInteger(bytes), 2);
        doc.setPrice(extractedPrice.doubleValue());

        doc.setStockCount(productAvroEvent.getStockCount());
        doc.setStatus(productAvroEvent.getStatus().toString());
        doc.setIsDeleted(productAvroEvent.getIsDeleted());

        // Instant timestampInstant = productAvroEvent.getUpdatedAt();
        // doc.setUpdatedAt(timestampInstant.toEpochMilli());

        // Convert the Instant to a formatted string matching MySQL's structure (e.g.,
        // "2026-07-12 17:11:02")
        java.time.Instant timestampInstant = productAvroEvent.getUpdatedAt();
        java.time.format.DateTimeFormatter mysqlFormatter = java.time.format.DateTimeFormatter
                .ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(java.time.ZoneId.systemDefault()); // Or use ZoneId.of("UTC") depending on your DB
                                                             // configuration

        doc.setUpdatedAt(mysqlFormatter.format(timestampInstant));

        productRepository.save(doc);
        ack.acknowledge();
    }
}