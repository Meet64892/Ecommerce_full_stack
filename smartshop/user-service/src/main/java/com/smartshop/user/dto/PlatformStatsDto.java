package com.smartshop.user.dto;

public record PlatformStatsDto(
        long totalUsers,
        long totalCustomers,
        long totalBrandOwners,
        long pendingBrandApplications,
        long approvedBrands
) {
}
