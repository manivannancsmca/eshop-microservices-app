package com.product_catalog_read_service.dto;

import java.math.BigDecimal;
import java.util.List;

public class ProductSearchRequest {
    private String keyword;
    private String brandName;
    private String categoryName;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Boolean includeOutOfStock;
    private List<String> tags;
    
    // Pagination and Sorting controls
    private int page = 0;
    private int size = 10;
    private String sortBy = "updatedAt"; // default sorting field
    private String sortDirection = "DESC"; // ASC or DESC
}
