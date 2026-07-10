package com.product_catalog_write_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "outbox")
@EntityListeners(AuditingEntityListener.class)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Outbox {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // "product" allows Debezium's Outbox Router to route events to a "product" topic
    @Column(nullable = false)
    private String aggregateType = "product"; 

    @Column(nullable = false)
    private String aggregateId;

    @Column(nullable = false)
    private String eventType; // e.g., PRODUCT_CREATED, PRODUCT_UPDATED

    // Store the binary Avro representation directly
    @Lob
    @Column(nullable = false, columnDefinition = "LONGBLOB")
    private byte[] payload;
}