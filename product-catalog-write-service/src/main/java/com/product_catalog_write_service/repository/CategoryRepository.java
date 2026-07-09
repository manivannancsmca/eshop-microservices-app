package com.product_catalog_write_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.product_catalog_write_service.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
