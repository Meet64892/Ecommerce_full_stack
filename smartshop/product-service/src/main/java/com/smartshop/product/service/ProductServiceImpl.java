package com.smartshop.product.service;

import com.smartshop.product.dto.ProductCreateRequest;
import com.smartshop.product.dto.ProductDto;
import com.smartshop.product.entity.Category;
import com.smartshop.product.entity.Product;
import com.smartshop.product.exception.ProductNotFoundException;
import com.smartshop.product.mapper.ProductMapper;
import com.smartshop.product.repository.CategoryRepository;
import com.smartshop.product.repository.ProductRepository;
import com.smartshop.product.repository.ProductSearchRepository;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * ProductServiceImpl - Product CRUD + dual-write implementation.
 *
 * <h2>Purpose</h2>
 * Implements product operations and ensures dual-write consistency by updating both PostgreSQL and
 * Elasticsearch. PostgreSQL remains authoritative; Elasticsearch is a query-optimized projection.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Dual-write tradeoff: temporary divergence can occur if secondary write fails.</li>
 *   <li>Eventual consistency: read model converges after retries/reindex jobs.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Called by ProductController for CRUD endpoints.
 *
 * @see ProductSearchService
 * @author SmartShop Team
 */
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductSearchRepository productSearchRepository;
    private final ProductMapper productMapper;

    /**
     * Creates product and writes to both DB and Elasticsearch.
     *
     * @param request create payload
     * @return created dto
     */
    @Override
    @Transactional
    public ProductDto create(final ProductCreateRequest request) {
        final Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ProductNotFoundException("Category not found: " + request.categoryId()));

        final Product saved = productRepository.save(Product.builder()
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .rating(request.rating())
                .category(category)
                .updatedAt(Instant.now())
                .build());

        // Dual-write ensures search index remains queryable after source-of-truth update.
        productSearchRepository.save(saved);
        return productMapper.toDto(saved);
    }

    /**
     * Updates product state and reindexes search document.
     *
     * @param id product id
     * @param request update payload
     * @return updated dto
     */
    @Override
    @Transactional
    public ProductDto update(final Long id, final ProductCreateRequest request) {
        final Product existing = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found: " + id));
        final Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ProductNotFoundException("Category not found: " + request.categoryId()));

        existing.setName(request.name());
        existing.setDescription(request.description());
        existing.setPrice(request.price());
        existing.setRating(request.rating());
        existing.setCategory(category);
        existing.setUpdatedAt(Instant.now());

        final Product saved = productRepository.save(existing);
        productSearchRepository.save(saved);
        return productMapper.toDto(saved);
    }

    /**
     * Fetches product by id.
     *
     * @param id product id
     * @return dto
     */
    @Override
    public ProductDto getById(final Long id) {
        return productRepository.findById(id)
                .map(productMapper::toDto)
                .orElseThrow(() -> new ProductNotFoundException("Product not found: " + id));
    }

    /**
     * Lists all products from source of truth.
     *
     * @return product list
     */
    @Override
    public List<ProductDto> getAll() {
        return productRepository.findAll().stream().map(productMapper::toDto).toList();
    }

    /**
     * Deletes product from DB and search index.
     *
     * @param id product id
     */
    @Override
    @Transactional
    public void delete(final Long id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException("Product not found: " + id);
        }
        productRepository.deleteById(id);
        productSearchRepository.deleteById(id);
    }
}
