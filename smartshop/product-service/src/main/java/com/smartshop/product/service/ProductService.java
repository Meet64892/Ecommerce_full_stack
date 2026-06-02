package com.smartshop.product.service;

import com.smartshop.product.dto.ProductCreateRequest;
import com.smartshop.product.dto.ProductDto;
import java.util.List;

/**
 * ProductService - Product catalog use-case contract.
 *
 * <h2>Purpose</h2>
 * Defines product CRUD operations independent of controller or persistence details.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Service boundary: keeps HTTP and data-layer concerns separated.</li>
 *   <li>Transactional writes: create/update/delete as atomic operations.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * ProductController delegates domain operations to this contract.
 *
 * @see ProductServiceImpl
 * @author SmartShop Team
 */
public interface ProductService {

    /**
     * Creates product and indexes it in Elasticsearch.
     *
     * @param request product payload
     * @return created product dto
     */
    ProductDto create(ProductCreateRequest request);

    /**
     * Updates existing product.
     *
     * @param id product id
     * @param request new product values
     * @return updated dto
     */
    ProductDto update(Long id, ProductCreateRequest request);

    /**
     * Retrieves product by id.
     *
     * @param id product id
     * @return product dto
     */
    ProductDto getById(Long id);

    /**
     * Lists all products.
     *
     * @return all products
     */
    List<ProductDto> getAll();

    /**
     * Deletes product and search index record.
     *
     * @param id product id
     */
    void delete(Long id);
}
