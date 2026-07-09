package com.product_catalog_write_service.mapper;

import org.mapstruct.Mapper;

import com.product_catalog_write_service.dto.request.CreateCategoryRequest;
import com.product_catalog_write_service.dto.request.UpdateCategoryRequest;
import com.product_catalog_write_service.dto.response.CategoryResponse;
import com.product_catalog_write_service.entity.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category toEntity(CreateCategoryRequest request);

    CategoryResponse toResponse(Category category);

    void update(UpdateCategoryRequest request, Category category);

}