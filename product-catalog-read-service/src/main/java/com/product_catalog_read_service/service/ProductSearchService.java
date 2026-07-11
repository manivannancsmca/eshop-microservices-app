package com.product_catalog_read_service.service;

import java.util.Optional;

import org.springframework.data.domain.Page;

import com.product_catalog_read_service.dto.ProductSearchRequest;
import com.product_catalog_read_service.entity.ProductDocument;

public interface ProductSearchService {

    Optional<ProductDocument> findById(String id);
    Optional<ProductDocument> findBySku(String sku);
    Page<ProductDocument> searchProducts(ProductSearchRequest request);
}
