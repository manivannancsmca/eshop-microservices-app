package com.product_catalog_write_service.mapper;

import org.mapstruct.Mapper;

import com.product_catalog_write_service.dto.request.CreateBrandRequest;
import com.product_catalog_write_service.dto.request.UpdateBrandRequest;
import com.product_catalog_write_service.dto.response.BrandResponse;
import com.product_catalog_write_service.entity.Brand;

@Mapper(componentModel = "spring")
public interface BrandMapper {

    Brand toEntity(CreateBrandRequest request);

    BrandResponse toResponse(Brand brand);

    void update(UpdateBrandRequest request, Brand brand);

}
