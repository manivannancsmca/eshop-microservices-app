package com.product_catalog_write_service.exception;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(String message) {
        super(message);
    }

}
