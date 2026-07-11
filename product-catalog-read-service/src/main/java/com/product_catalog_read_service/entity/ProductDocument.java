package com.product_catalog_read_service.entity;


import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import org.springframework.data.elasticsearch.annotations.DateFormat;

import java.math.BigDecimal;
import java.util.List;

@Document(indexName = "products")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ProductDocument {

    @Id
    private String id; // maps to your 'id' field, stored as String for Elasticsearch compatibility

    @Field(type = FieldType.Keyword)
    private String eventId;

    @Field(type = FieldType.Keyword)
    private String sku;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String name;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String description;

    @Field(type = FieldType.Long)
    private Long brandId;

    @Field(type = FieldType.Keyword) // Keyword type allows filtering and grouping by exact brand name
    private String brandName;

    @Field(type = FieldType.Long)
    private Long categoryId;

    @Field(type = FieldType.Keyword)
    private String categoryName;

    @Field(type = FieldType.Keyword) // Storing tags as array of keywords for category matching
    private List<String> tags;

    @Field(type = FieldType.Double) // Best performance mapping for numeric sorting/filtering in ES
    private BigDecimal price;

    @Field(type = FieldType.Integer)
    private Integer stockCount;

    @Field(type = FieldType.Keyword)
    private String status;

    @Field(type = FieldType.Boolean)
    private Boolean isDeleted;

    @Field(type = FieldType.Date, format = DateFormat.epoch_millis)
    private Long updatedAt;

    
}
