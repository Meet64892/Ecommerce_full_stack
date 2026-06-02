package com.smartshop.product.controller;

import com.smartshop.common.dto.ApiResponse;
import com.smartshop.product.entity.Category;
import com.smartshop.product.repository.jpa.CategoryRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * CategoryController - Minimal CRUD for product categories.
 *
 * <h2>Purpose</h2>
 * Lets admins create and list categories that products are grouped under.
 * Intentionally thin (no dedicated service) since categories are simple
 * reference data.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>{@code @Validated} on the class enables validation of method parameters
 *       like {@code @RequestParam @NotBlank}.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Reached via {@code /api/products/categories} through the gateway.
 *
 * @author SmartShop Team
 */
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Validated
@Tag(name = "Categories", description = "Product category management")
public class CategoryController {

    private final CategoryRepository categoryRepository;

    /**
     * Lists all categories.
     *
     * @return 200 OK with the categories
     */
    @GetMapping
    @Operation(summary = "List categories")
    public ResponseEntity<ApiResponse<List<Category>>> list() {
        return ResponseEntity.ok(ApiResponse.ok(categoryRepository.findAll()));
    }

    /**
     * Creates a category.
     *
     * @param name        required category name
     * @param description optional description
     * @return 201 Created with the new category
     */
    @PostMapping
    @Operation(summary = "Create a category")
    public ResponseEntity<ApiResponse<Category>> create(
            @RequestParam @NotBlank String name,
            @RequestParam(required = false) String description) {
        Category category = categoryRepository.save(
                Category.builder().name(name).description(description).build());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(category, "Category created"));
    }
}
