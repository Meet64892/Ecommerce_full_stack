package com.smartshop.product.controller;

import com.smartshop.product.entity.Category;
import com.smartshop.product.repository.CategoryRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * CategoryController - Category CRUD endpoints.
 *
 * <h2>Purpose</h2>
 * Manages product taxonomy, which powers catalog navigation and search filters.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Reference data: controlled vocabulary for domain consistency.</li>
 *   <li>Thin controller: delegates persistence to repository directly for simple CRUD.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Provides APIs consumed by admin or back-office tooling.
 *
 * @see CategoryRepository
 * @author SmartShop Team
 */
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryRepository categoryRepository;

    /**
     * Creates category.
     *
     * @param category category payload
     * @return persisted category
     */
    @Operation(summary = "Create category")
    @ApiResponse(responseCode = "200", description = "Category created")
    @PostMapping
    public Category create(@RequestBody final Category category) {
        return categoryRepository.save(category);
    }

    /**
     * Lists categories.
     *
     * @return category list
     */
    @Operation(summary = "List categories")
    @ApiResponse(responseCode = "200", description = "Categories returned")
    @GetMapping
    public List<Category> getAll() {
        return categoryRepository.findAll();
    }
}
