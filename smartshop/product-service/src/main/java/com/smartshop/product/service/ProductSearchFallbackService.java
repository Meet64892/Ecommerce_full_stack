package com.smartshop.product.service;

import com.smartshop.product.dto.ProductDto;
import com.smartshop.product.dto.ProductSearchRequest;
import com.smartshop.product.entity.Product;
import com.smartshop.product.mapper.ProductMapper;
import com.smartshop.product.repository.jpa.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * PostgreSQL fallback when Elasticsearch is unavailable.
 */
@Service
public class ProductSearchFallbackService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductSearchFallbackService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Transactional(readOnly = true)
    public Page<ProductDto> search(ProductSearchRequest request, Pageable pageable) {
        Specification<Product> spec = (root, query, cb) -> {
            query.distinct(true);
            List<Predicate> predicates = new ArrayList<>();
            if (request.query() != null && !request.query().isBlank()) {
                String pattern = "%" + request.query().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), pattern),
                        cb.like(cb.lower(root.get("description")), pattern)
                ));
            }
            if (request.categoryId() != null) {
                predicates.add(cb.equal(root.get("category").get("id"), request.categoryId()));
            }
            if (request.minPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), request.minPrice()));
            }
            if (request.maxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), request.maxPrice()));
            }
            if (request.minRating() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("rating"), request.minRating()));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };

        Page<Product> page = productRepository.findAll(spec, pageable);
        List<ProductDto> content = page.getContent().stream().map(productMapper::toDto).toList();
        return new PageImpl<>(content, pageable, page.getTotalElements());
    }
}
