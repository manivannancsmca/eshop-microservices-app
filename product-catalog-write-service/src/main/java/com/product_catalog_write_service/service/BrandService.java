package com.product_catalog_write_service.service;

import com.product_catalog_write_service.dto.request.CreateBrandRequest;
import com.product_catalog_write_service.dto.request.UpdateBrandRequest;
import com.product_catalog_write_service.dto.response.BrandResponse;

public interface BrandService {

    BrandResponse create(CreateBrandRequest request);

    BrandResponse update(Long id, UpdateBrandRequest request);

    void delete(Long id);
}