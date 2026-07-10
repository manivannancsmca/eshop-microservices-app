package com.product_catalog_write_service.service;

import com.product_catalog_write_service.entity.Product;

public interface ProductOutboxService {

    void createProduct(Product product);

    void updateProduct(Product savedProduct);

    void deleteProduct(Product savedProduct);

}
