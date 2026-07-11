package com.product_catalog_read_service.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Service;

import com.product_catalog_read_service.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DebeziumProductSyncConsumer {

    private final ProductRepository productRepository;

    public void syncWithElasticsearch(ConsumerRecord<String, Product> record) {
    }

}
