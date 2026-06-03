package com.smartshop.product.service;

import com.smartshop.product.dto.ProductDto;
import com.smartshop.product.dto.ProductSearchRequest;
import com.smartshop.product.entity.Product;
import com.smartshop.product.entity.ProductApprovalStatus;
import com.smartshop.product.mapper.ProductMapper;
import com.smartshop.product.repository.search.ProductSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductSearchService {
    private static final Logger log = LoggerFactory.getLogger(ProductSearchService.class);

    private final ProductSearchRepository productSearchRepository;
    private final ProductMapper productMapper;
    private final ProductSearchFallbackService productSearchFallbackService;

    public ProductSearchService(
            ProductSearchRepository productSearchRepository,
            ProductMapper productMapper,
            ProductSearchFallbackService productSearchFallbackService) {
        this.productSearchRepository = productSearchRepository;
        this.productMapper = productMapper;
        this.productSearchFallbackService = productSearchFallbackService;
    }

    public Page<ProductDto> search(ProductSearchRequest request, Pageable pageable) {
        try {
            return searchElasticsearch(request, pageable);
        } catch (Exception ex) {
            log.warn("Elasticsearch search failed, using PostgreSQL fallback: {}", ex.getMessage());
            return productSearchFallbackService.search(request, pageable);
        }
    }

    private Page<ProductDto> searchElasticsearch(ProductSearchRequest request, Pageable pageable) {
        String query = request.query() == null || request.query().isBlank() ? "" : request.query();
        Page<Product> page = query.isBlank()
                ? productSearchRepository.findAll(pageable)
                : productSearchRepository.findByNameContainingOrDescriptionContaining(query, query, pageable);
        List<ProductDto> filtered = page.getContent().stream()
                .filter(ProductSearchService::isVisibleInShop)
                .filter(product -> request.categoryId() == null || request.categoryId().equals(resolveCategoryId(product)))
                .filter(product -> request.minPrice() == null || product.getPrice().compareTo(request.minPrice()) >= 0)
                .filter(product -> request.maxPrice() == null || product.getPrice().compareTo(request.maxPrice()) <= 0)
                .filter(product -> request.minRating() == null || product.getRating() >= request.minRating())
                .map(productMapper::toDto)
                .toList();
        return new PageImpl<>(filtered, pageable, page.getTotalElements());
    }

    private static boolean isVisibleInShop(Product product) {
        ProductApprovalStatus status = product.getApprovalStatus();
        return status == null || status == ProductApprovalStatus.APPROVED;
    }

    private static java.util.UUID resolveCategoryId(Product product) {
        if (product.getCategoryId() != null) {
            return product.getCategoryId();
        }
        return product.getCategory() != null ? product.getCategory().getId() : null;
    }
}
