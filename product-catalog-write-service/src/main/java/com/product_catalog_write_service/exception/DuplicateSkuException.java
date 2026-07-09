package com.product_catalog_write_service.exception;

public class DuplicateSkuException extends RuntimeException {

    DuplicateSkuException(String message) {
        super(message);
    }
}