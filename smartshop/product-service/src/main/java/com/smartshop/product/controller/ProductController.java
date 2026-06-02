package com.smartshop.product.controller;

import com.smartshop.product.dto.ProductCreateRequest;
import com.smartshop.product.dto.ProductDto;
import com.smartshop.product.dto.ProductSearchRequest;
import com.smartshop.product.service.ProductSearchService;
import com.smartshop.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

/**
 * ProductController - Product CRUD and search endpoints.
 *
 * <h2>Purpose</h2>
 * Provides REST APIs for managing and querying catalog products.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Separation of concerns: CRUD and search delegated to dedicated services.</li>
 *   <li>OpenAPI annotations: generated docs aid consumer integration.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * API gateway routes /products calls to this controller.
 *
 * @see ProductService
 * @author SmartShop Team
 */
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductSearchService productSearchService;

    /**
     * Creates a product.
     *
     * @param request product payload
     * @return created product
     */
    @Operation(summary = "Create product")
    @ApiResponse(responseCode = "200", description = "Product created")
    @PostMapping
    public ProductDto create(@Valid @RequestBody final ProductCreateRequest request) {
        return productService.create(request);
    }

    /**
     * Updates product data.
     *
     * @param id product id
     * @param request update payload
     * @return updated product
     */
    @Operation(summary = "Update product")
    @ApiResponse(responseCode = "200", description = "Product updated")
    @PutMapping("/{id}")
    public ProductDto update(@PathVariable final Long id, @Valid @RequestBody final ProductCreateRequest request) {
        return productService.update(id, request);
    }

    /**
     * Gets one product.
     *
     * @param id product id
     * @return product dto
     */
    @Operation(summary = "Get product by id")
    @ApiResponse(responseCode = "200", description = "Product returned")
    @GetMapping("/{id}")
    public ProductDto getById(@Parameter(description = "Product ID") @PathVariable final Long id) {
        return productService.getById(id);
    }

    /**
     * Lists all products.
     *
     * @return product list
     */
    @Operation(summary = "List products")
    @ApiResponse(responseCode = "200", description = "Products returned")
    @GetMapping
    public List<ProductDto> getAll() {
        return productService.getAll();
    }

    /**
     * Deletes product by id.
     *
     * @param id product id
     */
    @Operation(summary = "Delete product")
    @ApiResponse(responseCode = "204", description = "Product deleted")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable final Long id) {
        productService.delete(id);
    }

    /**
     * Searches products using Elasticsearch.
     *
     * @param request search filter request
     * @return paged results
     */
    @Operation(summary = "Search products")
    @ApiResponse(responseCode = "200", description = "Search results returned")
    @PostMapping("/search")
    public Page<ProductDto> search(@RequestBody final ProductSearchRequest request) {
        return productSearchService.search(request);
    }
}
