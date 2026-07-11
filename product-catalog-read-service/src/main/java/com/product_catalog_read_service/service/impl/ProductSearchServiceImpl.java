package com.product_catalog_read_service.service.impl;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import com.product_catalog_read_service.dto.ProductSearchRequest;
import com.product_catalog_read_service.entity.ProductDocument;
import com.product_catalog_read_service.service.ProductSearchService;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductSearchServiceImpl implements ProductSearchService {

    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public Optional<ProductDocument> findById(String id) {
        return Optional.ofNullable(elasticsearchOperations.get(id, ProductDocument.class));
    }

    @Override
    public Optional<ProductDocument> findBySku(String sku) {
       Query query = Query.of(q -> q.term(t -> t.field("sku").value(sku)));

       NativeQuery nativeQuery = new NativeQueryBuilder().withQuery(query).build();
       SearchHits<ProductDocument> hits = elasticsearchOperations.search(nativeQuery, ProductDocument.class);
       return hits.isEmpty() ? Optional.empty() : Optional.of(hits.getSearchHit(0).getContent());
    }

    @Override
    public Page<ProductDocument> searchProducts(ProductSearchRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'searchProducts'");
    }

}
