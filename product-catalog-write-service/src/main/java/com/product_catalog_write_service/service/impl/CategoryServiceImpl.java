package com.product_catalog_write_service.service.impl;

import org.springframework.stereotype.Service;

import com.product_catalog_write_service.dto.request.CreateCategoryRequest;
import com.product_catalog_write_service.dto.request.UpdateCategoryRequest;
import com.product_catalog_write_service.dto.response.CategoryResponse;
import com.product_catalog_write_service.entity.Category;
import com.product_catalog_write_service.exception.CategoryNotFoundException;
import com.product_catalog_write_service.mapper.CategoryMapper;
import com.product_catalog_write_service.repository.CategoryRepository;
import com.product_catalog_write_service.service.CategoryService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repository;
    private final CategoryMapper mapper;

    @Override
    public CategoryResponse create(CreateCategoryRequest request) {

        Category category = mapper.toEntity(request);

        category = repository.save(category);

        return mapper.toResponse(category);
    }

    @Override
    public CategoryResponse update(Long id, UpdateCategoryRequest request) {

        Category category = repository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));

        mapper.update(request, category);

        return mapper.toResponse(category);
    }

    @Override
    public void delete(Long id) {

        Category category = repository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));

        category.setActive(false);
        repository.save(category);
    }
}
