package com.smartshop.user.controller;

import com.smartshop.common.dto.ApiResponse;
import com.smartshop.user.dto.BrandApplyRequest;
import com.smartshop.user.dto.BrandDto;
import com.smartshop.user.dto.CreateVendorAdminRequest;
import com.smartshop.user.dto.PlatformStatsDto;
import com.smartshop.user.service.BrandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/brands")
@Tag(name = "Brands", description = "Multi-vendor brand onboarding and management")
public class BrandController {

    private final BrandService brandService;

    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @Operation(summary = "Apply to sell", description = "Customer applies to become a brand/vendor (pending review).")
    @PostMapping("/apply")
    public ApiResponse<BrandDto> apply(@Valid @RequestBody BrandApplyRequest request) {
        return ApiResponse.success(brandService.applyForBrand(request), "Brand application submitted");
    }

    @Operation(summary = "My brand", description = "Vendor admin views their brand profile.")
    @GetMapping("/me")
    public ApiResponse<BrandDto> myBrand() {
        return ApiResponse.success(brandService.getMyBrand(), "Brand loaded");
    }

    @Operation(summary = "Pending brands", description = "Super admin lists pending vendor applications.")
    @GetMapping("/pending")
    public ApiResponse<Page<BrandDto>> pending(Pageable pageable) {
        return ApiResponse.success(brandService.listPendingBrands(pageable), "Pending brands loaded");
    }

    @Operation(summary = "Platform stats", description = "Super admin dashboard metrics from user/brand data.")
    @GetMapping("/stats/overview")
    public ApiResponse<PlatformStatsDto> stats() {
        return ApiResponse.success(brandService.platformStats(), "Platform stats loaded");
    }

    @Operation(summary = "List brands", description = "Super admin lists all brands.")
    @GetMapping
    public ApiResponse<Page<BrandDto>> list(Pageable pageable) {
        return ApiResponse.success(brandService.listBrands(pageable), "Brands loaded");
    }

    @Operation(summary = "Get brand", description = "Load brand by id (public if approved).")
    @GetMapping("/{id}")
    public ApiResponse<BrandDto> get(@PathVariable UUID id) {
        return ApiResponse.success(brandService.getBrand(id), "Brand loaded");
    }

    @Operation(summary = "Approve brand", description = "Super admin approves vendor and promotes owner to ADMIN.")
    @PostMapping("/{id}/approve")
    public ApiResponse<BrandDto> approve(
            @PathVariable UUID id,
            @RequestParam(required = false) BigDecimal commissionPercent) {
        return ApiResponse.success(brandService.approveBrand(id, commissionPercent), "Brand approved");
    }

    @Operation(summary = "Reject brand", description = "Super admin rejects vendor application.")
    @PostMapping("/{id}/reject")
    public ApiResponse<BrandDto> reject(@PathVariable UUID id) {
        return ApiResponse.success(brandService.rejectBrand(id), "Brand rejected");
    }

    @Operation(summary = "Suspend brand", description = "Super admin suspends an active vendor.")
    @PostMapping("/{id}/suspend")
    public ApiResponse<BrandDto> suspend(@PathVariable UUID id) {
        return ApiResponse.success(brandService.suspendBrand(id), "Brand suspended");
    }

    @Operation(summary = "Activate brand", description = "Super admin reactivates a suspended vendor.")
    @PostMapping("/{id}/activate")
    public ApiResponse<BrandDto> activate(@PathVariable UUID id) {
        return ApiResponse.success(brandService.activateBrand(id), "Brand activated");
    }

    @Operation(summary = "Create vendor admin", description = "Super admin creates a brand owner account directly.")
    @PostMapping("/vendor-admin")
    public ApiResponse<BrandDto> createVendorAdmin(@Valid @RequestBody CreateVendorAdminRequest request) {
        return ApiResponse.success(brandService.createVendorAdmin(request), "Vendor admin created");
    }
}
