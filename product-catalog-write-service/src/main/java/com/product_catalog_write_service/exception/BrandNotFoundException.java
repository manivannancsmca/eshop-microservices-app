package com.product_catalog_write_service.exception;

public class BrandNotFoundException extends RuntimeException {

    public BrandNotFoundException(String message) {
        super(message);
    }
}
