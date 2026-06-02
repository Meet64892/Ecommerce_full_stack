package com.smartshop.product.service;

import com.smartshop.common.exception.ResourceNotFoundException;
import com.smartshop.product.dto.ProductCreateRequest;
import com.smartshop.product.dto.ProductDto;
import com.smartshop.product.dto.ProductSearchRequest;
import com.smartshop.product.entity.Category;
import com.smartshop.product.entity.Product;
import com.smartshop.product.mapper.ProductMapper;
import com.smartshop.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ProductServiceImpl - Product Catalog Business Logic
 *
 * <h2>Purpose</h2>
 * Orchestrates product CRUD operations with dual-write to PostgreSQL and Elasticsearch.
 * Every create/update operation writes to the relational DB first (transactional),
 * then indexes in Elasticsearch (best-effort, non-transactional).
 *
 * @author SmartShop Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductServiceImpl {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ProductSearchService searchService;

    /**
     * Creates a new product and indexes it in Elasticsearch.
     * The JPA save is transactional; the ES index is best-effort (logged on failure).
     *
     * @param request the validated product creation data
     * @return the created product as a DTO
     */
    public ProductDto createProduct(ProductCreateRequest request) {
        log.info("Creating product with SKU: {}", request.sku());

        Product product = productMapper.toEntity(request);

        // Set a stub Category — in a full implementation, fetch it from CategoryRepository
        Category category = new Category();
        category.setId(request.categoryId());
        product.setCategory(category);
        product.setActive(true);
        product.setRating(0.0);
        product.setRatingCount(0);

        Product saved = productRepository.save(product);

        // Dual-write: index in Elasticsearch AFTER successful DB save
        // If this fails, the product is still in the DB — eventually consistent
        searchService.indexProduct(saved);

        log.info("Created product id={}", saved.getId());
        return productMapper.toDto(saved);
    }

    /**
     * Returns a product by ID.
     *
     * @param id the product ID
     * @return the product DTO
     */
    @Transactional(readOnly = true)
    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
        return productMapper.toDto(product);
    }

    /**
     * Returns a paginated list of all active products.
     *
     * @param pageable pagination parameters
     * @return page of product DTOs
     */
    @Transactional(readOnly = true)
    public Page<ProductDto> getAllProducts(Pageable pageable) {
        return productRepository.findByActiveTrue(pageable).map(productMapper::toDto);
    }

    /**
     * Full-text search via Elasticsearch.
     * Routes to ProductSearchService which builds and executes ES queries.
     *
     * @param searchRequest search filters and query text
     * @param pageable      pagination parameters
     * @return page of matching products from Elasticsearch
     */
    @Transactional(readOnly = true)
    public Page<ProductDto> searchProducts(ProductSearchRequest searchRequest, Pageable pageable) {
        return searchService.search(searchRequest, pageable).map(productMapper::toDto);
    }

    /**
     * Updates a product and re-indexes it in Elasticsearch.
     *
     * @param id      the product ID to update
     * @param request the updated product data
     * @return the updated product DTO
     */
    public ProductDto updateProduct(Long id, ProductCreateRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));

        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setOriginalPrice(request.originalPrice());
        product.setBrand(request.brand());

        Product saved = productRepository.save(product);

        // Re-index in Elasticsearch with updated data
        searchService.indexProduct(saved);

        return productMapper.toDto(saved);
    }

    /**
     * Soft-deletes a product by marking it as inactive.
     * Does NOT hard-delete from the database — historical orders reference products.
     * Removes from Elasticsearch index (won't appear in searches).
     *
     * @param id the product ID to deactivate
     */
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
        product.setActive(false);
        productRepository.save(product);
        searchService.removeProductFromIndex(id);
        log.info("Soft-deleted product id={}", id);
    }
}
