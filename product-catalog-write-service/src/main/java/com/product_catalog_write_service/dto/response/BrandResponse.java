package com.product_catalog_write_service.dto.response;

import java.time.LocalDateTime;

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
public class BrandResponse {

    private Long id;

    private String brandCode;

    private String brandName;

    private String description;

    private Boolean active;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;

    // getters/setters
}
