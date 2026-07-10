package com.product_catalog_read_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.common.avro.schemas.ProductEvent;

public interface ProductRepository extends ElasticsearchRepository<ProductEvent, String> {

    Page<ProductEvent> findByNameOrDescription(String name, String description, Pageable pageable);
}
