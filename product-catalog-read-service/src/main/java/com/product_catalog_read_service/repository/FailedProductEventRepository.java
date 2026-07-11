package com.product_catalog_read_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.product_catalog_read_service.entity.FailedProductEvent;

@Repository
public interface FailedProductEventRepository extends JpaRepository<FailedProductEvent, Long> {
}
