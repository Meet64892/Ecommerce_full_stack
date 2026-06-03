package com.smartshop.user.service;

import com.smartshop.user.dto.BrandApplicationRequest;
import com.smartshop.user.dto.BrandDto;
import com.smartshop.user.dto.PlatformStatsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface BrandService {
    BrandDto apply(UUID ownerUserId, BrandApplicationRequest request);

    BrandDto approve(UUID brandId);

    BrandDto reject(UUID brandId);

    BrandDto suspend(UUID brandId);

    List<BrandDto> listApproved();

    Page<BrandDto> listByStatus(String status, Pageable pageable);

    BrandDto get(UUID id);

    BrandDto myBrand(UUID ownerUserId);

    PlatformStatsDto platformStats();
}
