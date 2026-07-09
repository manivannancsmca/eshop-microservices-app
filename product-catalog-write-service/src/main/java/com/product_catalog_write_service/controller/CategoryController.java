package com.product_catalog_write_service.controller;

import com.product_catalog_write_service.dto.request.CreateCategoryRequest;
import com.product_catalog_write_service.dto.request.UpdateCategoryRequest;
import com.product_catalog_write_service.dto.response.ApiResponse;
import com.product_catalog_write_service.dto.response.CategoryResponse;
import com.product_catalog_write_service.service.CategoryService;

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
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Category Catalog", description = "CRUD operations for the category catalog")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @Operation(summary = "Create a new Category")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Category created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request payload")
    })
    public ResponseEntity<ApiResponse<CategoryResponse>> create(
            @Valid @RequestBody CreateCategoryRequest request) {

        CategoryResponse response = categoryService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(successResponse("Category created successfully", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing category")
    public ResponseEntity<ApiResponse<CategoryResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCategoryRequest request) {

        CategoryResponse response = categoryService.update(id, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(successResponse("Category updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a category (soft-delete)")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id) {

        categoryService.delete(id);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(successResponse("Category deleted successfully", null));
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
