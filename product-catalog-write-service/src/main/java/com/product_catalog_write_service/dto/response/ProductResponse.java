package com.product_catalog_write_service.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode

public class ProductResponse {

    private Long id;

    private String sku;

    private String name;

    private String description;

    private Long brandId;

    private String brandName;

    private Long categoryId;

    private String categoryName;

    //private Set<String> tags;

    private BigDecimal price;

    private Integer stockCount;

    private Boolean active;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;
}
