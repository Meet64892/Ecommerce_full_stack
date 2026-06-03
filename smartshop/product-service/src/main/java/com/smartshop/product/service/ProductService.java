package com.smartshop.product.service;

import com.smartshop.product.dto.CategoryDto;
import com.smartshop.product.dto.ProductCreateRequest;
import com.smartshop.product.dto.ProductDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * ProductService - Application service contract for catalog management.
 *
 * <h2>Purpose</h2>
 * The service interface defines catalog use cases without tying controllers to persistence details. It also makes the
 * dual-write behavior an implementation detail instead of a controller concern.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Transactional write model: PostgreSQL writes and search-index updates are coordinated by the service.</li>
 *   <li>Pagination: Large catalogs are returned page by page to protect memory and latency.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * ProductController and CategoryController call this interface; ProductServiceImpl performs database work.
 *
 * @see ProductServiceImpl
 * @author SmartShop Team
 */
public interface ProductService {
    /**
     * Creates a product.
     *
     * @param request validated product payload
     * @return created product DTO
     */
    ProductDto create(ProductCreateRequest request);

    /**
     * Updates a product.
     *
     * @param id product id
     * @param request validated product payload
     * @return updated product DTO
     */
    ProductDto update(UUID id, ProductCreateRequest request);

    /**
     * Loads one product by id.
     *
     * @param id product id
     * @return product DTO
     */
    ProductDto get(UUID id);

    /**
     * Lists products by page.
     *
     * @param pageable page request
     * @return page of products
     */
    Page<ProductDto> list(Pageable pageable);

    /**
     * Deletes a product from PostgreSQL and Elasticsearch.
     *
     * @param id product id
     */
    void delete(UUID id);

    /**
     * Creates a category.
     *
     * @param categoryDto category payload
     * @return created category DTO
     */
    CategoryDto createCategory(CategoryDto categoryDto);

    /**
     * Lists all categories.
     *
     * @return category DTOs
     */
    java.util.List<CategoryDto> categories();

    Page<ProductDto> listPending(Pageable pageable);

    ProductDto approve(UUID id);

    ProductDto reject(UUID id);

    Page<ProductDto> listForCurrentVendor(Pageable pageable);
}
