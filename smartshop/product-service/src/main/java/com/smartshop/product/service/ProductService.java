package com.smartshop.product.service;

import com.smartshop.product.dto.ProductCreateRequest;
import com.smartshop.product.dto.ProductDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * ProductService - Business operations for the product catalog.
 *
 * <h2>Purpose</h2>
 * Contract for creating, reading, updating, deleting, and listing products. The
 * implementation handles the dual write to PostgreSQL and Elasticsearch.
 *
 * <h2>How it fits in the system</h2>
 * Implemented by {@code ProductServiceImpl}; used by {@code ProductController}.
 *
 * @author SmartShop Team
 */
public interface ProductService {

    /**
     * Creates a product (and indexes it for search).
     *
     * @param request validated creation payload
     * @return the created product
     */
    ProductDto create(ProductCreateRequest request);

    /**
     * @param id product id
     * @return the product
     * @throws com.smartshop.product.exception.ProductNotFoundException if absent
     */
    ProductDto getById(Long id);

    /**
     * Lists products with pagination.
     *
     * @param pageable page/size/sort
     * @return a page of products
     */
    Page<ProductDto> list(Pageable pageable);

    /**
     * Updates a product (and re-indexes it).
     *
     * @param id      product id
     * @param request new values
     * @return the updated product
     */
    ProductDto update(Long id, ProductCreateRequest request);

    /**
     * Deletes a product from both stores.
     *
     * @param id product id
     */
    void delete(Long id);
}
