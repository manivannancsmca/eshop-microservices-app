package com.product_catalog_read_service.consumer;

import java.time.LocalDateTime;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import com.common.avro.schemas.ProductEvent;
import com.product_catalog_read_service.entity.FailedProductEvent;
import com.product_catalog_read_service.repository.FailedProductEventRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class DebeziumProductDltConsumer {

    private final FailedProductEventRepository failedEventRepository;

    @KafkaListener(topics = "cdc.product_catalog.outbox.DLT", groupId = "product-dlt-logging-group")
    public void consumeDeadLetterRecords(
            ConsumerRecord<String, ProductEvent> record,
            @Header(name = KafkaHeaders.DLT_EXCEPTION_MESSAGE, required = false) String exceptionMessage,
            @Header(name = KafkaHeaders.DLT_ORIGINAL_TOPIC, required = false) String originalTopic) {

        log.info("""
                ======================================================================
                ALERT: CRITICAL RECORD ROUTED TO DEAD LETTER TOPIC (DLT)
                Original Topic    : %s
                Failed Key        : %s
                Exception Root    : %s
                ======================================================================
                """,
                originalTopic,
                record.key(),
                exceptionMessage);

        try {
            FailedProductEvent quarantineRecord = new FailedProductEvent();
            quarantineRecord.setEventKey(record.key());

            // Convert the Avro model cleanly to a JSON string representation using the
            // built-in .toString() layout
            if (record.value() != null) {
                quarantineRecord.setPayloadJson(record.value().toString());
            } else {
                quarantineRecord.setPayloadJson("{ \"info\": \"Tombstone / Deletion Null Payload\" }");
            }

            quarantineRecord.setOriginalTopic(originalTopic != null ? originalTopic : "UNKNOWN");
            quarantineRecord.setPartitionId(record.partition());
            quarantineRecord.setOffsetValue(record.offset());

            // Truncate exception strings if necessary to fit clean column constraints
            quarantineRecord
                    .setExceptionMessage(exceptionMessage != null ? exceptionMessage : "No exception string passed");
            quarantineRecord.setStatus("PENDING_REVIEW");
            quarantineRecord.setCreatedAt(LocalDateTime.now());

            // Commit to remediation tracking database
            failedEventRepository.save(quarantineRecord);
        } catch (Exception fatalDbException) {
            // Ultimate fallback safety block to ensure failure storage itself never brings
            // down consumer loops
            log.info("CRITICAL FAULT: Unable to write data to remediation tracking system: "
                    + fatalDbException.getMessage());
        }

    }
}
