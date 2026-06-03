package com.smartshop.user.controller;

import com.smartshop.common.dto.ApiResponse;
import com.smartshop.user.dto.BrandDto;
import com.smartshop.user.dto.PlatformStatsDto;
import com.smartshop.user.dto.UpdateUserRoleRequest;
import com.smartshop.user.dto.UserDto;
import jakarta.validation.Valid;
import com.smartshop.user.service.BrandService;
import com.smartshop.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('SUPER_ADMIN')")
@Tag(name = "Platform Admin", description = "Super Admin marketplace operations")
public class AdminController {
    private final BrandService brandService;
    private final UserService userService;

    public AdminController(BrandService brandService, UserService userService) {
        this.brandService = brandService;
        this.userService = userService;
    }

    @Operation(summary = "Platform dashboard metrics")
    @GetMapping("/stats")
    public ApiResponse<PlatformStatsDto> stats() {
        return ApiResponse.success(brandService.platformStats(), "Platform stats loaded");
    }

    @Operation(summary = "List brand applications")
    @GetMapping("/brands")
    public ApiResponse<Page<BrandDto>> listBrands(@RequestParam(defaultValue = "PENDING") String status, Pageable pageable) {
        return ApiResponse.success(brandService.listByStatus(status, pageable), "Brands loaded");
    }

    @Operation(summary = "Approve vendor brand")
    @PatchMapping("/brands/{id}/approve")
    public ApiResponse<BrandDto> approveBrand(@PathVariable UUID id) {
        return ApiResponse.success(brandService.approve(id), "Brand approved");
    }

    @Operation(summary = "Reject vendor brand")
    @PatchMapping("/brands/{id}/reject")
    public ApiResponse<BrandDto> rejectBrand(@PathVariable UUID id) {
        return ApiResponse.success(brandService.reject(id), "Brand rejected");
    }

    @Operation(summary = "Suspend vendor brand")
    @PatchMapping("/brands/{id}/suspend")
    public ApiResponse<BrandDto> suspendBrand(@PathVariable UUID id) {
        return ApiResponse.success(brandService.suspend(id), "Brand suspended");
    }

    @Operation(summary = "List all users")
    @GetMapping("/users")
    public ApiResponse<Page<UserDto>> listUsers(Pageable pageable) {
        return ApiResponse.success(userService.listUsers(pageable), "Users loaded");
    }

    @Operation(summary = "Change user role", description = "Promote customers to vendor admin (SUPER_USER) or demote as needed.")
    @PatchMapping("/users/{id}/role")
    public ApiResponse<UserDto> updateUserRole(@PathVariable UUID id, @Valid @RequestBody UpdateUserRoleRequest request) {
        return ApiResponse.success(userService.updateUserRole(id, request.role()), "User role updated");
    }
}
