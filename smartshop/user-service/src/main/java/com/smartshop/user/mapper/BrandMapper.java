package com.smartshop.user.mapper;

import com.smartshop.user.dto.BrandDto;
import com.smartshop.user.entity.Brand;
import org.springframework.stereotype.Component;

@Component
public class BrandMapper {
    public BrandDto toDto(Brand brand) {
        return new BrandDto(
                brand.getId(),
                brand.getName(),
                brand.getSlug(),
                brand.getDescription(),
                brand.getLogoUrl(),
                brand.getOwnerUserId(),
                brand.getStatus(),
                brand.getCommissionPercent(),
                brand.getCreatedAt());
    }
}
