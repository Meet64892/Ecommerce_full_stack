package com.smartshop.product.controller;

import com.smartshop.common.dto.ApiResponse;
import com.smartshop.product.dto.CategoryDto;
import com.smartshop.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * CategoryController - REST API for catalog categories.
 *
 * <h2>Purpose</h2>
 * Categories drive browsing and search filters, so they are managed independently from individual products. Keeping a
 * small controller reinforces that category use cases are simpler than product indexing use cases.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Facet data: Category lists become UI filter choices.</li>
 *   <li>Validation: Category names are checked before persistence.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * The gateway routes `/categories/**` here and ProductService persists category rows.
 *
 * @see ProductService
 * @author SmartShop Team
 */
@RestController
@RequestMapping("/categories")
@Tag(name = "Categories", description = "Catalog category management")
public class CategoryController {
    private final ProductService productService;

    /**
     * Creates the controller with its service dependency.
     *
     * @param productService catalog service
     */
    public CategoryController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Creates a category.
     *
     * @param request validated category payload
     * @return created category
     */
    @Operation(summary = "Create category", description = "Creates a new product category.")
    @PostMapping
    public ApiResponse<CategoryDto> create(@Valid @RequestBody CategoryDto request) {
        return ApiResponse.success(productService.createCategory(request), "Category created");
    }

    /**
     * Lists all categories.
     *
     * @return category list
     */
    @Operation(summary = "List categories", description = "Returns all categories for browsing facets.")
    @GetMapping
    public ApiResponse<List<CategoryDto>> list() {
        return ApiResponse.success(productService.categories(), "Categories loaded");
    }
}
