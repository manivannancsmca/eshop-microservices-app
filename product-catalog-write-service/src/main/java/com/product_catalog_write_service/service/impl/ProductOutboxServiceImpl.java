package com.product_catalog_write_service.service.impl;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;

import org.apache.avro.Conversions;
import org.apache.avro.LogicalTypes;
import org.springframework.stereotype.Service;

import com.common.avro.schemas.ProductEvent;
import com.product_catalog_write_service.entity.Outbox;
import com.product_catalog_write_service.entity.Product;
import com.product_catalog_write_service.repository.OutboxRepository;
import com.product_catalog_write_service.service.ProductOutboxService;
import com.product_catalog_write_service.util.AvroSerializerUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductOutboxServiceImpl implements ProductOutboxService {

    private final OutboxRepository outboxRepository;

    @Override
    public void createProduct(Product savedProduct) {
        saveOutboxEvent(savedProduct, "ACTIVE", "PRODUCT_CREATED");
    }

    @Override
    public void updateProduct(Product savedProduct) {
        saveOutboxEvent(savedProduct, "UPDATED", "PRODUCT_UPDATED");
    }

    @Override
    public void deleteProduct(Product savedProduct) {
        saveOutboxEvent(savedProduct, "DELETED", "PRODUCT_DELETED");
    }

    private void saveOutboxEvent(Product product,
            String productStatus,
            String eventType) {
            log.info("saveOutboxEvent ::::::::: ", productStatus);
        ProductEvent payload = buildProductEvent(product, productStatus);

        byte[] avroBytes = AvroSerializerUtil.serializeProductPayload(payload);

        Outbox outbox = new Outbox();
        outbox.setAggregateId(product.getId().toString());
        outbox.setEventType(eventType);
        outbox.setPayload(avroBytes);

        outboxRepository.save(outbox);
    }

    private ProductEvent buildProductEvent(Product product, String status) {

        return ProductEvent.newBuilder()
                .setEventId(UUID.randomUUID().toString())
                .setId(product.getId())
                .setSku(product.getSku())
                .setName(product.getName())
                .setDescription(product.getDescription())
                .setBrandId(product.getBrand().getId())
                .setBrandName(product.getBrand().getBrandName())
                .setCategoryId(product.getCategory().getId())
                .setCategoryName(product.getCategory().getCategoryName())
                //.setTags(new ArrayList<>(product.getTags()))
                .setPrice(toDecimal(product.getPrice()))
                .setStockCount(product.getStockCount())
                .setStatus(status)
                .setIsDeleted("DELETED".equals(status))
                .setUpdatedAt(Instant.now())
                .build();
    }

    private ByteBuffer toDecimal(BigDecimal value) {
        return new Conversions.DecimalConversion()
                .toBytes(
                        value,
                        null,
                        LogicalTypes.decimal(12, 2));
    }

}