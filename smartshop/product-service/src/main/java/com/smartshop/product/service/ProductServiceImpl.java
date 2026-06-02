package com.smartshop.product.service;

import com.smartshop.product.dto.ProductCreateRequest;
import com.smartshop.product.dto.ProductDto;
import com.smartshop.product.entity.Product;
import com.smartshop.product.exception.ProductNotFoundException;
import com.smartshop.product.mapper.ProductMapper;
import com.smartshop.product.repository.jpa.ProductRepository;
import com.smartshop.product.repository.search.ProductSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ProductServiceImpl - Implements catalog operations with a dual-write strategy.
 *
 * <h2>Purpose</h2>
 * Keeps PostgreSQL (authoritative) and Elasticsearch (search) in sync: every
 * create/update writes to BOTH stores; every delete removes from BOTH.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li><b>Dual-write strategy</b>: after committing the relational row we index
 *       the same data into ES. This is simple and great for learning. (In a
 *       hardened system you would make indexing resilient — e.g. publish a change
 *       event / outbox so a transient ES outage doesn't lose the update.)</li>
 *   <li><b>@Transactional scope</b>: the transaction protects the PostgreSQL
 *       write. The ES call is best-effort within the same method; we log and
 *       continue if indexing fails so the source of truth still succeeds.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Called by {@code ProductController}; reads/writes via both repositories.
 *
 * @author SmartShop Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductSearchRepository productSearchRepository;
    private final ProductMapper productMapper;

    /** {@inheritDoc} */
    @Override
    @Transactional
    public ProductDto create(ProductCreateRequest request) {
        Product product = productMapper.toEntity(request);
        // 1) Authoritative write to PostgreSQL.
        Product saved = productRepository.save(product);
        // 2) Index into Elasticsearch for search (best-effort, logged on failure).
        indexQuietly(saved);
        log.info("Created product id={} name={}", saved.getId(), saved.getName());
        return productMapper.toDto(saved);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public ProductDto getById(Long id) {
        return productMapper.toDto(findOrThrow(id));
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public Page<ProductDto> list(Pageable pageable) {
        // Page.map preserves paging metadata while converting each element.
        return productRepository.findAll(pageable).map(productMapper::toDto);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public ProductDto update(Long id, ProductCreateRequest request) {
        Product product = findOrThrow(id);
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setCategoryId(request.categoryId());
        Product saved = productRepository.save(product);
        // Re-index so search reflects the new values.
        indexQuietly(saved);
        return productMapper.toDto(saved);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public void delete(Long id) {
        Product product = findOrThrow(id);
        productRepository.delete(product);
        // Remove from the index too so deleted items stop appearing in search.
        try {
            productSearchRepository.deleteById(id);
        } catch (Exception e) {
            log.warn("Failed to delete product {} from search index: {}", id, e.getMessage());
        }
    }

    /**
     * Loads a product or throws a 404-mapped exception.
     *
     * @param id product id
     * @return the product entity
     */
    private Product findOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    /**
     * Indexes a product into Elasticsearch without failing the main flow.
     *
     * @param product the product to index
     */
    private void indexQuietly(Product product) {
        try {
            productSearchRepository.save(product);
        } catch (Exception e) {
            // Search availability must not block the authoritative write.
            log.warn("Failed to index product {} into Elasticsearch: {}", product.getId(), e.getMessage());
        }
    }
}
