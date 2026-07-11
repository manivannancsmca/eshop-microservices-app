package com.product_catalog_read_service.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "failed_product_events")
@EntityListeners(AuditingEntityListener.class)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class FailedProductEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_key")
    private String eventKey;

    // Storing the full Avro event state as a JSON string for easy readability during manual review
    @Column(name = "payload_json", columnDefinition = "TEXT")
    private String payloadJson;

    @Column(name = "original_topic")
    private String originalTopic;

    @Column(name = "partition_id")
    private Integer partitionId;

    @Column(name = "offset_value")
    private Long offsetValue;

    @Column(name = "exception_message", columnDefinition = "TEXT")
    private String exceptionMessage;

    @Column(name = "status")
    private String status; // e.g., PENDING_REVIEW, REPLAYED, IGNORED

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    
}
