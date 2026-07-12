package com.product_catalog_read_service.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.stereotype.Service;

import com.product_catalog_read_service.dto.ProductSearchRequest;
import com.product_catalog_read_service.entity.ProductDocument;
import com.product_catalog_read_service.service.ProductSearchService;

import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.json.JsonData;
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
        NativeQueryBuilder queryBuilder = new NativeQueryBuilder();
        List<Query> mustClauses = new ArrayList<>();
        List<Query> filterClauses = new ArrayList<>();

        // 1. Text-Search Clause (Fuzzy text lookup across name and description)
        if (request.getKeyword() != null && !request.getKeyword().isBlank()) {
            mustClauses.add(Query.of(q -> q.multiMatch(m -> m
                    .fields("name^3", "description") // Give name 3x higher matching score relevancy
                    .query(request.getKeyword())
                    .fuzziness("AUTO") // Handles small spelling typos smoothly
            )));
        }

        // 2. Exact Brand Name Filtering
        if (request.getBrandName() != null && !request.getBrandName().isBlank()) {
            filterClauses.add(Query.of(q -> q.term(t -> t.field("brandName").value(request.getBrandName()))));
        }

        // 3. Exact Category Name Filtering
        if (request.getCategoryName() != null && !request.getCategoryName().isBlank()) {
            filterClauses.add(Query.of(q -> q.term(t -> t.field("categoryName").value(request.getCategoryName()))));
        }

        // 4. Price Boundaries Filtering
        if (request.getMinPrice() != null || request.getMaxPrice() != null) {
            filterClauses.add(Query.of(q -> q.range(r -> r
                    .number(nb -> {
                        nb.field("price");
                        if (request.getMinPrice() != null) {
                            nb.gte(request.getMinPrice().doubleValue()); // Or use your specific numeric type directly
                        }
                        if (request.getMaxPrice() != null) {
                            nb.lte(request.getMaxPrice().doubleValue());
                        }
                        return nb;
                    }))));
        }

        // 5. Inventory Tracking Control
        if (Boolean.FALSE.equals(request.getIncludeOutOfStock())) {
            filterClauses.add(Query.of(q -> q.range(r -> r
                    .number(nb -> nb
                            .field("stockCount")
                            .gt(0.0) // Elasticsearch treats all number range variants as floating points here
                    ))));
        }

        // Enforce active catalog state rule (Ignore items marked soft-deleted)
        filterClauses.add(Query.of(q -> q.term(t -> t.field("isDeleted").value(false))));

        // Assemble boolean logical conditions
        Query finalDslQuery = Query.of(q -> q.bool(b -> b.must(mustClauses).filter(filterClauses)));
        queryBuilder.withQuery(finalDslQuery);

        // 7. Pagination layout configuration
        PageRequest pageable = PageRequest.of(request.getPage(), request.getSize());
        queryBuilder.withPageable(pageable);

        // 8. Sorting Configuration
        SortOrder direction = "ASC".equalsIgnoreCase(request.getSortDirection()) ? SortOrder.Asc : SortOrder.Desc;
        queryBuilder.withSort(s -> s.field(f -> f.field(request.getSortBy()).order(direction)));

        // 9. Execute query inside cluster indices
        SearchHits<ProductDocument> searchHits = elasticsearchOperations.search(queryBuilder.build(),
                ProductDocument.class);

        // Extract payloads cleanly from hit wrappers
        List<ProductDocument> products = searchHits.stream()
                .map(SearchHit::getContent)
                .toList();

        return new PageImpl<>(products, pageable, searchHits.getTotalHits());
    }

    @Override
    public Page<ProductDocument> findAll(Pageable pageable) {
        // 1. Define a literal match_all body syntax passing standard pagination rules directly to the constructor
        StringQuery matchAllQuery = new StringQuery("{ \"match_all\": {} }", pageable);

        // 2. Execute search using the explicit implementation instance
        SearchHits<ProductDocument> searchHits = elasticsearchOperations.search(
                matchAllQuery, 
                ProductDocument.class
        );

        // 3. Extract your document collection cleanly from the returned metadata layer
        List<ProductDocument> products = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());

        // 4. Return as standard Spring Data page impl object
        return new PageImpl<>(
                products, 
                pageable, 
                searchHits.getTotalHits()
        );
    }

}
