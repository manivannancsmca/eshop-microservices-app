package com.product_catalog_write_service.dto.request;

import java.math.BigDecimal;
import java.util.Set;

import com.product_catalog_write_service.entity.ProductStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
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
public class CreateProductRequest {

    @NotBlank
    @Size(max = 50)
    private String sku;

    @NotBlank
    @Size(max = 200)
    private String name;

    private String description;

    @NotNull
    private Long brandId;

    @NotNull
    private Long categoryId;

    //private Set<String> tags;

    @NotNull
    @PositiveOrZero
    private BigDecimal price;

    @NotNull
    @PositiveOrZero
    private Integer stockCount;

    @NotNull
    private ProductStatus status;

}
