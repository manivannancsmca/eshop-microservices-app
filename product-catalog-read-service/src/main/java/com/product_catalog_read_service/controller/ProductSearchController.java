package com.product_catalog_read_service.controller;

import com.product_catalog_read_service.dto.ApiResponse;
import com.product_catalog_read_service.dto.ProductSearchRequest;
import com.product_catalog_read_service.entity.ProductDocument;
import com.product_catalog_read_service.service.ProductSearchService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
public class ProductSearchController {

    private final ProductSearchService productSearchService;

    public ProductSearchController(ProductSearchService productSearchService) {
        this.productSearchService = productSearchService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDocument>> getProductById(@PathVariable String id) {
        return productSearchService.findById(id)
                .map(product -> ResponseEntity.ok(
                        ApiResponse.success(HttpStatus.OK.value(), "Product retrieved successfully by ID.", product)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        ApiResponse.error(HttpStatus.NOT_FOUND.value(), "Product not found with ID: " + id)));
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<ApiResponse<ProductDocument>> getProductBySku(@PathVariable String sku) {
        return productSearchService.findBySku(sku)
                .map(product -> ResponseEntity.ok(
                        ApiResponse.success(HttpStatus.OK.value(), "Product retrieved successfully by SKU.", product)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        ApiResponse.error(HttpStatus.NOT_FOUND.value(), "Product not found with SKU: " + sku)));
    }

    @PostMapping("/search")
    public ResponseEntity<ApiResponse<Page<ProductDocument>>> searchProducts(
            @RequestBody ProductSearchRequest request) {
        Page<ProductDocument> searchResultPage = productSearchService.searchProducts(request);

        ApiResponse<Page<ProductDocument>> response = ApiResponse.success(
                HttpStatus.OK.value(),
                "Product search results compiled successfully.",
                searchResultPage);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductDocument>>> getAllProducts(
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        
        Page<ProductDocument> productsPage = productSearchService.findAll(pageable);
        
        ApiResponse<Page<ProductDocument>> response = ApiResponse.success(
                HttpStatus.OK.value(),
                "All products retrieved successfully.",
                productsPage
        );
        
        return ResponseEntity.ok(response);
    }
}
