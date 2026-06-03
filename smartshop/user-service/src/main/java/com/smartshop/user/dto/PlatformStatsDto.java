package com.smartshop.user.dto;

public record PlatformStatsDto(
        long totalCustomers,
        long totalVendors,
        long pendingBrandApplications,
        long approvedBrands,
        long suspendedBrands) {
}
