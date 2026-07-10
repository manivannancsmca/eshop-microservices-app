package com.product_catalog_write_service.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.product_catalog_write_service.entity.Outbox;

public interface OutboxRepository extends JpaRepository<Outbox, UUID> {
}
