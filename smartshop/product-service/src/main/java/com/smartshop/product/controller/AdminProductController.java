package com.smartshop.product.controller;

import com.smartshop.common.dto.ApiResponse;
import com.smartshop.product.dto.ProductDto;
import com.smartshop.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/admin/products")
@Tag(name = "Admin Products", description = "Super Admin product moderation")
public class AdminProductController {
    private final ProductService productService;

    public AdminProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "List products pending approval")
    @GetMapping("/pending")
    public ApiResponse<Page<ProductDto>> pending(Pageable pageable) {
        return ApiResponse.success(productService.listPending(pageable), "Pending products loaded");
    }

    @Operation(summary = "Approve product for marketplace")
    @PatchMapping("/{id}/approve")
    public ApiResponse<ProductDto> approve(@PathVariable UUID id) {
        return ApiResponse.success(productService.approve(id), "Product approved");
    }

    @Operation(summary = "Reject product")
    @PatchMapping("/{id}/reject")
    public ApiResponse<ProductDto> reject(@PathVariable UUID id) {
        return ApiResponse.success(productService.reject(id), "Product rejected");
    }
}
