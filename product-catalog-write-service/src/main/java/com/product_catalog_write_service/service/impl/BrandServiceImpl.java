package com.product_catalog_write_service.service.impl;

import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import com.product_catalog_write_service.dto.request.CreateBrandRequest;
import com.product_catalog_write_service.dto.request.UpdateBrandRequest;
import com.product_catalog_write_service.dto.response.BrandResponse;
import com.product_catalog_write_service.entity.Brand;
import com.product_catalog_write_service.exception.BrandNotFoundException;
import com.product_catalog_write_service.mapper.BrandMapper;
import com.product_catalog_write_service.repository.BrandRepository;
import com.product_catalog_write_service.service.BrandService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;

    @Override
    public BrandResponse create(CreateBrandRequest request) {

        Brand brand = brandMapper.toEntity(request);

        brand = brandRepository.save(brand);

        return brandMapper.toResponse(brand);
    }

    @Override
    public BrandResponse update(Long id, UpdateBrandRequest request) {

        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new BrandNotFoundException("Brand not found"));

        brandMapper.update(request, brand);

        return brandMapper.toResponse(brand);
    }

    @Override
    public void delete(Long id) {

        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new BrandNotFoundException("Brand not found"));

        brand.setActive(false);
    }
}
