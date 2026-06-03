package com.smartshop.user.controller;

import com.smartshop.common.dto.ApiResponse;
import com.smartshop.user.dto.BrandApplicationRequest;
import com.smartshop.user.dto.BrandDto;
import com.smartshop.user.service.BrandService;
import com.smartshop.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/brands")
@Tag(name = "Brands", description = "Vendor brand profiles and onboarding")
public class BrandController {
    private final BrandService brandService;
    private final UserService userService;

    public BrandController(BrandService brandService, UserService userService) {
        this.brandService = brandService;
        this.userService = userService;
    }

    @Operation(summary = "List approved brands", description = "Public catalog of active marketplace vendors")
    @GetMapping
    public ApiResponse<List<BrandDto>> listApproved() {
        return ApiResponse.success(brandService.listApproved(), "Brands loaded");
    }

    @Operation(summary = "Get brand profile")
    @GetMapping("/{id}")
    public ApiResponse<BrandDto> get(@PathVariable UUID id) {
        return ApiResponse.success(brandService.get(id), "Brand loaded");
    }

    @Operation(summary = "Apply to sell on the marketplace")
    @PostMapping("/apply")
    public ApiResponse<BrandDto> apply(@Valid @RequestBody BrandApplicationRequest request) {
        UUID userId = userService.currentUser().id();
        return ApiResponse.success(brandService.apply(userId, request), "Brand application submitted");
    }

    @Operation(summary = "My brand application")
    @GetMapping("/me")
    public ApiResponse<BrandDto> myBrand() {
        UUID userId = userService.currentUser().id();
        return ApiResponse.success(brandService.myBrand(userId), "Brand loaded");
    }
}
