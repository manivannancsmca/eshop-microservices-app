package com.product_catalog_write_service.controller;

import com.product_catalog_write_service.dto.request.CreateBrandRequest;
import com.product_catalog_write_service.dto.request.UpdateBrandRequest;
import com.product_catalog_write_service.dto.response.ApiResponse;
import com.product_catalog_write_service.dto.response.BrandResponse;
import com.product_catalog_write_service.service.BrandService;

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
@RequestMapping("/api/v1/brands")
@RequiredArgsConstructor
@Tag(name = "Brand Catalog", description = "CRUD operations for the brand catalog")
public class BrandController {

    private final BrandService brandService;

    @PostMapping
    @Operation(summary = "Create a new brand")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Brand created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request payload")
    })
    public ResponseEntity<ApiResponse<BrandResponse>> create(
            @Valid @RequestBody CreateBrandRequest request) {

        BrandResponse response = brandService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(successResponse("Brand created successfully", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing brand")
    public ResponseEntity<ApiResponse<BrandResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateBrandRequest request) {

        BrandResponse response = brandService.update(id, request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(successResponse("Brand updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a brand (soft-delete)")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id) {

        brandService.delete(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(successResponse("Brand deleted successfully", null));
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
