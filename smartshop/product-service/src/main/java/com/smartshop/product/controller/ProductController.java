package com.smartshop.product.controller;

import com.smartshop.common.dto.ApiResponse;
import com.smartshop.product.dto.ProductCreateRequest;
import com.smartshop.product.dto.ProductDto;
import com.smartshop.product.dto.ProductSearchRequest;
import com.smartshop.product.service.ProductServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * ProductController - REST Endpoints for Product Catalog Management
 *
 * @author SmartShop Team
 */
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Products", description = "Product catalog CRUD and search endpoints")
public class ProductController {

    private final ProductServiceImpl productService;

    @GetMapping
    @Operation(summary = "List all active products with pagination")
    public ResponseEntity<ApiResponse<Page<ProductDto>>> getAllProducts(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(productService.getAllProducts(pageable)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID")
    public ResponseEntity<ApiResponse<ProductDto>> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(productService.getProductById(id)));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new product (Admin/Seller only)")
    public ResponseEntity<ApiResponse<ProductDto>> createProduct(
            @Valid @RequestBody ProductCreateRequest request) {
        log.info("POST /products - creating: {}", request.sku());
        ProductDto created = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(created, "Product created successfully"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a product")
    public ResponseEntity<ApiResponse<ProductDto>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(productService.updateProduct(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft-delete a product (marks as inactive)")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Product deactivated successfully"));
    }

    /**
     * Full-text product search with filters.
     * Routes to Elasticsearch for full-text matching; PostgreSQL for structured queries.
     *
     * @param query      optional text search query
     * @param categoryId optional category filter
     * @param minPrice   optional minimum price
     * @param maxPrice   optional maximum price
     * @param minRating  optional minimum rating
     * @param brand      optional brand filter
     * @param pageable   pagination parameters
     * @return paginated search results
     */
    @GetMapping("/search")
    @Operation(
        summary = "Search products with full-text and filters",
        description = "Searches products using Elasticsearch. Supports full-text query, " +
                      "price range, category, rating, and brand filters."
    )
    public ResponseEntity<ApiResponse<Page<ProductDto>>> searchProducts(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) java.math.BigDecimal minPrice,
            @RequestParam(required = false) java.math.BigDecimal maxPrice,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) String brand,
            @PageableDefault(size = 20) Pageable pageable) {

        ProductSearchRequest searchRequest = new ProductSearchRequest(
                query, categoryId, minPrice, maxPrice, minRating, brand);
        Page<ProductDto> results = productService.searchProducts(searchRequest, pageable);
        return ResponseEntity.ok(ApiResponse.success(results));
    }
}
