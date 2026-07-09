package com.product_catalog_write_service.service.impl;

import org.springframework.stereotype.Service;

import com.product_catalog_write_service.dto.request.CreateProductRequest;
import com.product_catalog_write_service.dto.request.UpdateProductRequest;
import com.product_catalog_write_service.dto.response.ProductResponse;
import com.product_catalog_write_service.entity.Brand;
import com.product_catalog_write_service.entity.Category;
import com.product_catalog_write_service.entity.Product;
import com.product_catalog_write_service.entity.ProductStatus;
import com.product_catalog_write_service.exception.ProductNotFoundException;
import com.product_catalog_write_service.exception.ResourceNotFoundException;
import com.product_catalog_write_service.mapper.ProductMapper;
import com.product_catalog_write_service.repository.BrandRepository;
import com.product_catalog_write_service.repository.CategoryRepository;
import com.product_catalog_write_service.repository.ProductRepository;
import com.product_catalog_write_service.service.ProductCommandService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductCommandServiceImpl
        implements ProductCommandService {

    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;

    private final ProductMapper productMapper;

    @Override
    public ProductResponse create(CreateProductRequest request) {

        Product product = productMapper.toEntity(request);

        @SuppressWarnings("null")
        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new ProductNotFoundException("Brand not found"));

        @SuppressWarnings("null")
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ProductNotFoundException("Category not found"));

        product.setBrand(brand);
        product.setCategory(category);

        product = productRepository.save(product);

        // publish ProductCreatedEvent

        return productMapper.toResponse(product);
    }

    @Override
    public ProductResponse update(Long id,
            UpdateProductRequest request) {

        @SuppressWarnings("null")
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        productMapper.update(request, product);

        @SuppressWarnings("null")
        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new ProductNotFoundException("Brand not found"));

        @SuppressWarnings("null")
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ProductNotFoundException("Category not found"));

        product.setBrand(brand);
        product.setCategory(category);

        // publish ProductUpdatedEvent

        return productMapper.toResponse(product);
    }

    @Override
    public void delete(Long id) {

        @SuppressWarnings("null")
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        product.setStatus(ProductStatus.OUT_OF_STOCK);

        productRepository.save(product);
    }
}
