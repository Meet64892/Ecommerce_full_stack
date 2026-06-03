package com.smartshop.user.dto;

import com.smartshop.user.entity.BrandStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record BrandDto(
        UUID id,
        String name,
        String slug,
        String description,
        String logoUrl,
        UUID ownerUserId,
        BrandStatus status,
        BigDecimal commissionPercent,
        Instant createdAt) {
}
