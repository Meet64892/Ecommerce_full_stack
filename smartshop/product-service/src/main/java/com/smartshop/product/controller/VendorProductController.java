package com.smartshop.product.controller;

import com.smartshop.common.dto.ApiResponse;
import com.smartshop.product.dto.ProductDto;
import com.smartshop.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/vendor/products")
@Tag(name = "Vendor Products", description = "Brand owner catalog management")
public class VendorProductController {
    private final ProductService productService;

    public VendorProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "List my brand products")
    @GetMapping
    public ApiResponse<Page<ProductDto>> myProducts(Pageable pageable) {
        return ApiResponse.success(productService.listForCurrentVendor(pageable), "Vendor products loaded");
    }
}
