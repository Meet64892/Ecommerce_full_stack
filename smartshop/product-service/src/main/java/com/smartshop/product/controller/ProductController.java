package com.smartshop.product.controller;

import com.smartshop.common.dto.ApiResponse;
import com.smartshop.product.dto.ProductCreateRequest;
import com.smartshop.product.dto.ProductDto;
import com.smartshop.product.dto.ProductSearchRequest;
import com.smartshop.product.service.ProductSearchService;
import com.smartshop.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * ProductController - REST API for catalog CRUD and search.
 *
 * <h2>Purpose</h2>
 * The controller exposes shopper-facing read operations and admin-facing write operations. It keeps HTTP details such
 * as status codes and query binding separate from transactional catalog logic.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Search endpoint: Delegates to Elasticsearch projection for text queries.</li>
 *   <li>Pagination: Pageable protects the service from returning unbounded product lists.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Gateway routes `/products/**` here, and the controller delegates to ProductService and ProductSearchService.
 *
 * @see ProductService
 * @author SmartShop Team
 */
@RestController
@RequestMapping("/products")
@Validated
@Tag(name = "Products", description = "Catalog CRUD and search")
public class ProductController {
    private final ProductService productService;
    private final ProductSearchService productSearchService;

    /**
     * Creates the controller with explicit service dependencies.
     *
     * @param productService catalog service
     * @param productSearchService search service
     */
    public ProductController(ProductService productService, ProductSearchService productSearchService) {
        this.productService = productService;
        this.productSearchService = productSearchService;
    }

    /**
     * Creates a product.
     *
     * @param request validated product payload
     * @return HTTP 201 with created product
     */
    @Operation(summary = "Create product", description = "Creates a product in PostgreSQL and indexes it in Elasticsearch.")
    @PostMapping
    public ResponseEntity<ApiResponse<ProductDto>> create(@Valid @RequestBody ProductCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(productService.create(request), "Product created"));
    }

    /**
     * Lists products.
     *
     * @param pageable page request
     * @return page of products
     */
    @Operation(summary = "List products", description = "Returns products with pagination metadata.")
    @GetMapping
    public ApiResponse<Page<ProductDto>> list(Pageable pageable) {
        return ApiResponse.success(productService.list(pageable), "Products loaded");
    }

    /**
     * Searches products through Elasticsearch.
     *
     * @param request query and filters bound from query parameters
     * @param pageable page request
     * @return page of matching products
     */
    @Operation(summary = "Search products", description = "Searches text and filters by category, price, and rating.")
    @GetMapping("/search")
    public ApiResponse<Page<ProductDto>> search(@Valid @ModelAttribute ProductSearchRequest request, Pageable pageable) {
        return ApiResponse.success(productSearchService.search(request, pageable), "Search complete");
    }

    /**
     * Loads one product.
     *
     * @param id product id
     * @return product DTO
     */
    @Operation(summary = "Get product", description = "Loads a product by UUID.")
    @GetMapping("/{id}")
    public ApiResponse<ProductDto> get(@Parameter(description = "Product id") @PathVariable UUID id) {
        return ApiResponse.success(productService.get(id), "Product loaded");
    }

    /**
     * Updates a product.
     *
     * @param id product id
     * @param request validated update payload
     * @return updated product
     */
    @Operation(summary = "Update product", description = "Updates database row and search projection.")
    @PutMapping("/{id}")
    public ApiResponse<ProductDto> update(@PathVariable UUID id, @Valid @RequestBody ProductCreateRequest request) {
        return ApiResponse.success(productService.update(id, request), "Product updated");
    }

    /**
     * Deletes a product.
     *
     * @param id product id
     * @return response with no data
     */
    @Operation(summary = "Delete product", description = "Deletes a product from PostgreSQL and Elasticsearch.")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        productService.delete(id);
        return ApiResponse.success(null, "Product deleted");
    }
}
