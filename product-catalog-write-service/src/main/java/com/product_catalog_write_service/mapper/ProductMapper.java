package com.product_catalog_write_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.product_catalog_write_service.dto.request.CreateProductRequest;
import com.product_catalog_write_service.dto.request.UpdateProductRequest;
import com.product_catalog_write_service.dto.response.ProductResponse;
import com.product_catalog_write_service.entity.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    Product toEntity(CreateProductRequest request);

    ProductResponse toResponse(Product product);

    void update(UpdateProductRequest request, @MappingTarget Product product);

}