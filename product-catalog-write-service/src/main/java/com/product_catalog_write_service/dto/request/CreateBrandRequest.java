package com.product_catalog_write_service.dto.request;

import jakarta.validation.constraints.NotBlank;
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
public class CreateBrandRequest {

    @NotBlank
    @Size(max = 50)
    private String brandCode;

    @NotBlank
    @Size(max = 100)
    private String brandName;

    private String description;
}
