package com.product_catalog_write_service.service;

import com.product_catalog_write_service.dto.request.CreateCategoryRequest;
import com.product_catalog_write_service.dto.request.UpdateCategoryRequest;
import com.product_catalog_write_service.dto.response.CategoryResponse;

public interface CategoryService {

    CategoryResponse create(CreateCategoryRequest request);

    CategoryResponse update(Long id, UpdateCategoryRequest request);

    void delete(Long id);
}
