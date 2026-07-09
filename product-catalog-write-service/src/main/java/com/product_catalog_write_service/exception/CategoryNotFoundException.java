package com.product_catalog_write_service.exception;

public class CategoryNotFoundException extends RuntimeException {

    public CategoryNotFoundException(String message) {
        super(message);
    }

}
