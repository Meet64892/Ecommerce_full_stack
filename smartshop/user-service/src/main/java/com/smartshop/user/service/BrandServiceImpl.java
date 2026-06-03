package com.smartshop.user.service;

import com.smartshop.common.exception.ValidationException;
import com.smartshop.user.dto.BrandApplicationRequest;
import com.smartshop.user.dto.BrandDto;
import com.smartshop.user.dto.PlatformStatsDto;
import com.smartshop.user.entity.Brand;
import com.smartshop.user.entity.BrandStatus;
import com.smartshop.user.entity.Role;
import com.smartshop.user.entity.User;
import com.smartshop.user.exception.UserNotFoundException;
import com.smartshop.user.mapper.BrandMapper;
import com.smartshop.user.repository.BrandRepository;
import com.smartshop.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class BrandServiceImpl implements BrandService {
    private final BrandRepository brandRepository;
    private final UserRepository userRepository;
    private final BrandMapper brandMapper;

    public BrandServiceImpl(BrandRepository brandRepository, UserRepository userRepository, BrandMapper brandMapper) {
        this.brandRepository = brandRepository;
        this.userRepository = userRepository;
        this.brandMapper = brandMapper;
    }

    @Override
    @Transactional
    public BrandDto apply(UUID ownerUserId, BrandApplicationRequest request) {
        User owner = userRepository.findById(ownerUserId).orElseThrow(() -> new UserNotFoundException(ownerUserId));
        if (owner.getRole() == Role.SUPER_ADMIN) {
            throw new ValidationException("Platform admins cannot register as vendors");
        }
        if (brandRepository.findByOwnerUserId(ownerUserId).isPresent()) {
            throw new ValidationException("You already have a brand application or account");
        }
        if (brandRepository.existsByNameIgnoreCase(request.brandName())) {
            throw new ValidationException("Brand name is already taken");
        }
        Brand brand = brandRepository.save(new Brand(request.brandName().trim(), request.description(), ownerUserId));
        return brandMapper.toDto(brand);
    }

    @Override
    @Transactional
    public BrandDto approve(UUID brandId) {
        Brand brand = brandRepository.findById(brandId).orElseThrow(() -> new UserNotFoundException(brandId));
        if (brand.getStatus() != BrandStatus.PENDING) {
            throw new ValidationException("Only pending brands can be approved");
        }
        User owner = userRepository.findById(brand.getOwnerUserId()).orElseThrow(() -> new UserNotFoundException(brand.getOwnerUserId()));
        brand.setStatus(BrandStatus.APPROVED);
        owner.setRole(Role.SUPER_USER);
        owner.setBrandId(brand.getId());
        return brandMapper.toDto(brand);
    }

    @Override
    @Transactional
    public BrandDto reject(UUID brandId) {
        Brand brand = brandRepository.findById(brandId).orElseThrow(() -> new UserNotFoundException(brandId));
        brand.setStatus(BrandStatus.REJECTED);
        return brandMapper.toDto(brand);
    }

    @Override
    @Transactional
    public BrandDto suspend(UUID brandId) {
        Brand brand = brandRepository.findById(brandId).orElseThrow(() -> new UserNotFoundException(brandId));
        brand.setStatus(BrandStatus.SUSPENDED);
        User owner = userRepository.findById(brand.getOwnerUserId()).orElseThrow(() -> new UserNotFoundException(brand.getOwnerUserId()));
        owner.setEnabled(false);
        return brandMapper.toDto(brand);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BrandDto> listApproved() {
        return brandRepository.findByStatus(BrandStatus.APPROVED).stream().map(brandMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BrandDto> listByStatus(String status, Pageable pageable) {
        BrandStatus brandStatus = BrandStatus.valueOf(status.toUpperCase());
        return brandRepository.findByStatus(brandStatus, pageable).map(brandMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public BrandDto get(UUID id) {
        return brandRepository.findById(id).map(brandMapper::toDto).orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public BrandDto myBrand(UUID ownerUserId) {
        return brandRepository.findByOwnerUserId(ownerUserId).map(brandMapper::toDto)
                .orElseThrow(() -> new ValidationException("No brand registered for this account"));
    }

    @Override
    @Transactional(readOnly = true)
    public PlatformStatsDto platformStats() {
        long users = userRepository.count();
        long customers = userRepository.countByRole(Role.USER);
        long brandOwners = userRepository.countByRole(Role.SUPER_USER);
        long pending = brandRepository.findByStatus(BrandStatus.PENDING).size();
        long approved = brandRepository.findByStatus(BrandStatus.APPROVED).size();
        return new PlatformStatsDto(users, customers, brandOwners, pending, approved);
    }
}
