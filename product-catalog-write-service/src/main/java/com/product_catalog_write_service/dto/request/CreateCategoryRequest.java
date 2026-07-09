package com.product_catalog_write_service.dto.request;

import jakarta.validation.constraints.NotBlank;
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
public class CreateCategoryRequest {

    @NotBlank
    private String categoryCode;

    @NotBlank
    private String categoryName;

    private String description;
}
