package com.product_catalog_write_service.service;

import com.product_catalog_write_service.dto.request.CreateProductRequest;
import com.product_catalog_write_service.dto.request.UpdateProductRequest;
import com.product_catalog_write_service.dto.response.ProductResponse;

public interface ProductCommandService {

    ProductResponse create(CreateProductRequest request);

    ProductResponse update(Long id, UpdateProductRequest request);

    void delete(Long id);
}
