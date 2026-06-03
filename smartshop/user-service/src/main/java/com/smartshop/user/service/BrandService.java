package com.smartshop.user.service;

import com.smartshop.user.dto.BrandApplyRequest;
import com.smartshop.user.dto.BrandDto;
import com.smartshop.user.dto.CreateVendorAdminRequest;
import com.smartshop.user.dto.PlatformStatsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.UUID;

public interface BrandService {
    BrandDto applyForBrand(BrandApplyRequest request);

    BrandDto getMyBrand();

    BrandDto getBrand(UUID id);

    Page<BrandDto> listBrands(Pageable pageable);

    Page<BrandDto> listPendingBrands(Pageable pageable);

    BrandDto approveBrand(UUID id, BigDecimal commissionPercent);

    BrandDto rejectBrand(UUID id);

    BrandDto suspendBrand(UUID id);

    BrandDto activateBrand(UUID id);

    BrandDto createVendorAdmin(CreateVendorAdminRequest request);

    PlatformStatsDto platformStats();
}
