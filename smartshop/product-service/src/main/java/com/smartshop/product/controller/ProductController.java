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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * ProductController - CRUD + search endpoints for the catalog.
 *
 * <h2>Purpose</h2>
 * Exposes product management (create/read/update/delete), paginated listing, and
 * an Elasticsearch-backed search endpoint.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>{@code Pageable} is resolved from {@code ?page=&size=&sort=} query params
 *       by Spring; {@code @PageableDefault} sets sensible defaults.</li>
 *   <li>Responses use the shared {@link ApiResponse} envelope.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Reached via the gateway's programmatic route at {@code /api/products/**}.
 *
 * @author SmartShop Team
 */
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Catalog CRUD and search")
public class ProductController {

    private final ProductService productService;
    private final ProductSearchService productSearchService;

    /**
     * Creates a product.
     *
     * @param request validated creation payload
     * @return 201 Created with the new product
     */
    @PostMapping
    @Operation(summary = "Create a product")
    public ResponseEntity<ApiResponse<ProductDto>> create(@Valid @RequestBody ProductCreateRequest request) {
        ProductDto created = productService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(created, "Product created"));
    }

    /**
     * @param id product id
     * @return 200 OK with the product
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get a product by id")
    public ResponseEntity<ApiResponse<ProductDto>> getById(
            @Parameter(description = "Product id") @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(productService.getById(id)));
    }

    /**
     * Lists products with pagination.
     *
     * @param pageable resolved from query params (defaults to 20 per page)
     * @return 200 OK with a page of products
     */
    @GetMapping
    @Operation(summary = "List products (paged)")
    public ResponseEntity<ApiResponse<Page<ProductDto>>> list(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(productService.list(pageable)));
    }

    /**
     * Full-text / filtered search via Elasticsearch.
     *
     * @param query      optional text term
     * @param minPrice   optional min price
     * @param maxPrice   optional max price
     * @param categoryId optional category filter
     * @param minRating  optional minimum rating
     * @param pageable   paging/sorting
     * @return 200 OK with a page of matching products
     */
    @GetMapping("/search")
    @Operation(summary = "Search products", description = "Filters by text, price range, category, rating.")
    public ResponseEntity<ApiResponse<Page<ProductDto>>> search(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Double minRating,
            @PageableDefault(size = 20) Pageable pageable) {
        ProductSearchRequest req = new ProductSearchRequest(query, minPrice, maxPrice, categoryId, minRating);
        return ResponseEntity.ok(ApiResponse.ok(productSearchService.search(req, pageable)));
    }

    /**
     * Updates a product.
     *
     * @param id      product id
     * @param request new values
     * @return 200 OK with the updated product
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update a product")
    public ResponseEntity<ApiResponse<ProductDto>> update(
            @PathVariable Long id, @Valid @RequestBody ProductCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.of(productService.update(id, request), "Product updated"));
    }

    /**
     * Deletes a product.
     *
     * @param id product id
     * @return 200 OK acknowledging deletion
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.ok(ApiResponse.of(null, "Product deleted"));
    }
}
