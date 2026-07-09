package com.product_catalog_write_service.controller;

import com.product_catalog_write_service.dto.request.CreateProductRequest;
import com.product_catalog_write_service.dto.request.UpdateProductRequest;
import com.product_catalog_write_service.dto.response.ApiResponse;
import com.product_catalog_write_service.dto.response.ProductResponse;
import com.product_catalog_write_service.service.ProductCommandService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Product Catalog", description = "CRUD operations for the product catalog")
public class ProductCommandController {

    private final ProductCommandService productCommandService;

    @PostMapping
    @Operation(summary = "Create a new product")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201", description = "Product created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", description = "Invalid request payload"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409", description = "Product with this SKU already exists")
    })
    public ResponseEntity<ApiResponse<ProductResponse>> create(
            @Valid @RequestBody CreateProductRequest request) {

        ProductResponse response = productCommandService.create(request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(successResponse("Product created successfully", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing product")
    public ResponseEntity<ApiResponse<ProductResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request) {

        ProductResponse response = productCommandService.update(id, request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(successResponse("Product updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product (soft-delete)")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id) {

        productCommandService.delete(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(successResponse("Product deleted successfully", null));
    }

    public static <T> ApiResponse<T> successResponse(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
