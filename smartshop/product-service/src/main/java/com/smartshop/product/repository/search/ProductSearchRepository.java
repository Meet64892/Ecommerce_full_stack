package com.smartshop.product.repository.search;

import com.smartshop.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.UUID;

public interface ProductSearchRepository extends ElasticsearchRepository<Product, UUID> {
    Page<Product> findByNameContainingOrDescriptionContaining(String name, String description, Pageable pageable);
}
