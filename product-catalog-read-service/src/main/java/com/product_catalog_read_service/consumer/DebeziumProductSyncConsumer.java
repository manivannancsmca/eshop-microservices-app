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
    
    // Reader for the internal typed payload from your shared JAR
    private final SpecificDatumReader<ProductEvent> internalPayloadReader = 
            new SpecificDatumReader<>(ProductEvent.class);

    @KafkaListener(topics = "cdc.product_catalog.outbox", groupId = "product-read-service-group")
    public void syncWithElasticsearch(ConsumerRecord<String, Object> record, Acknowledgment ack) {
        String productId = record.key();
        Object avroMessage = record.value();
        
        log.info("-------------------------------");
        log.info("Consumer read binary payload successfully from the topic.");

        if (avroMessage == null) {
            log.info("CDC Event [HARD DELETE] -> Dropping Product: {}", productId);
            if (productId != null) {
                productRepository.deleteById(productId);
            }
            ack.acknowledge();
            return;
        }

        try {
            // Ensure the dynamic value is parsed as a GenericRecord wrapper via standard KafkaAvroDeserializer
            if (!(avroMessage instanceof GenericRecord)) {
                throw new IllegalArgumentException("Expected Avro GenericRecord but received: " + avroMessage.getClass().getName());
            }

            GenericRecord outboxRecord = (GenericRecord) avroMessage;
            
            // 1. Read top-level outbox envelope metadata cleanly
            String eventType = outboxRecord.get("event_type") != null ? outboxRecord.get("event_type").toString() : "";
            String aggregateId = outboxRecord.get("aggregate_id") != null ? outboxRecord.get("aggregate_id").toString() : "";

            if ("PRODUCT_DELETED".equals(eventType)) {
                log.info("CDC Event [METADATA DELETE] -> Dropping Product ID: {}", aggregateId);
                productRepository.deleteById(aggregateId);
                ack.acknowledge();
                return;
            }

            // 2. Extract the nested database byte array payload
            Object payloadObj = outboxRecord.get("payload");
            byte[] rawAvroBytes;

            if (payloadObj instanceof ByteBuffer) {
                ByteBuffer buffer = (ByteBuffer) payloadObj;
                rawAvroBytes = new byte[buffer.remaining()];
                buffer.get(rawAvroBytes);
            } else if (payloadObj instanceof byte[]) {
                rawAvroBytes = (byte[]) payloadObj;
            } else {
                throw new IllegalStateException("Unexpected payload column data type: " + (payloadObj != null ? payloadObj.getClass().getName() : "null"));
            }

            // 3. Decode internal binary byte segment directly back into the typed ProductEvent class
            ProductEvent productAvroEvent = internalPayloadReader.read(
                    null, 
                    DecoderFactory.get().binaryDecoder(rawAvroBytes, null)
            );

            if (Boolean.TRUE.equals(productAvroEvent.getIsDeleted())) {
                log.info("CDC Event [SOFT DELETE] -> Dropping Product: {}", productAvroEvent.getId());
                productRepository.deleteById(String.valueOf(productAvroEvent.getId()));
                ack.acknowledge();
                return;
            }

            log.info("CDC Event [SYNC] -> Processing Index Update for Product: {}", productAvroEvent.getId());
            saveDocument(productAvroEvent, ack);

        } catch (Exception pipelineError) {
            log.error("Fatal pipeline processing failure inside Avro outbox decoder: {}", pipelineError.getMessage(), pipelineError);
            throw new RuntimeException("Avro processing error", pipelineError);
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

        Instant timestampInstant = productAvroEvent.getUpdatedAt();
        doc.setUpdatedAt(timestampInstant.toEpochMilli());

        productRepository.save(doc);
        ack.acknowledge();
    }
}