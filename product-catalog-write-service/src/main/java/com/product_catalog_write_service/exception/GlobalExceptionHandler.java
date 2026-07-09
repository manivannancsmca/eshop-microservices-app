package com.product_catalog_write_service.exception;

import jakarta.servlet.http.HttpServletRequest;

import org.apache.kafka.common.errors.DuplicateResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.product_catalog_write_service.dto.response.ApiResponse;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BrandNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleBrandNotFound(
            BrandNotFoundException ex, HttpServletRequest request) {
        log.warn("Resource not found: {} — {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorResponse(ex.getMessage()));
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleCategoryNotFound(
            CategoryNotFoundException ex, HttpServletRequest request) {
        log.warn("Resource not found: {} — {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorResponse(ex.getMessage()));
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleProductNotFound(
            ProductNotFoundException ex, HttpServletRequest request) {
        log.warn("Resource not found: {} — {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorResponse(ex.getMessage()));
    }


    @ExceptionHandler(DuplicateSkuException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateSku(
            DuplicateSkuException ex, HttpServletRequest request) {
        log.warn("Resource not found: {} — {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorResponse(ex.getMessage()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(
            ResourceNotFoundException ex, HttpServletRequest request) {
        log.warn("Resource not found: {} — {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorResponse(ex.getMessage()));
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateResource(
            DuplicateResourceException ex, HttpServletRequest request) {
        log.warn("Duplicate resource: {} — {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(errorResponse(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });

        log.warn("Validation failed: {}", errors);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(false, "Validation failed", errors, java.time.LocalDateTime.now()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolation(
            DataIntegrityViolationException ex) {
        log.error("Data integrity violation", ex);
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(errorResponse("A database constraint was violated. The resource may already exist."));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingParam(
            MissingServletRequestParameterException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse("Missing required parameter: " + ex.getParameterName()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse("Invalid value for parameter: " + ex.getName()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotReadable(
            HttpMessageNotReadableException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse("Malformed JSON request body"));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex) {
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(errorResponse("HTTP method not supported: " + ex.getMethod()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(
            Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception on {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse("An unexpected internal error occurred"));
    }

    public static <T> ApiResponse<T> errorResponse(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
}