package com.smartshop.user.service;

import com.smartshop.common.exception.ValidationException;
import com.smartshop.user.dto.BrandApplyRequest;
import com.smartshop.user.dto.BrandDto;
import com.smartshop.user.dto.CreateVendorAdminRequest;
import com.smartshop.user.dto.PlatformStatsDto;
import com.smartshop.user.entity.Brand;
import com.smartshop.user.entity.BrandStatus;
import com.smartshop.user.entity.Role;
import com.smartshop.user.entity.User;
import com.smartshop.user.exception.EmailAlreadyExistsException;
import com.smartshop.user.exception.UserNotFoundException;
import com.smartshop.user.mapper.BrandMapper;
import com.smartshop.user.repository.BrandRepository;
import com.smartshop.user.repository.UserRepository;
import com.smartshop.user.security.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class BrandServiceImpl implements BrandService {
    private final BrandRepository brandRepository;
    private final UserRepository userRepository;
    private final BrandMapper brandMapper;
    private final PasswordEncoder passwordEncoder;

    public BrandServiceImpl(
            BrandRepository brandRepository,
            UserRepository userRepository,
            BrandMapper brandMapper,
            PasswordEncoder passwordEncoder) {
        this.brandRepository = brandRepository;
        this.userRepository = userRepository;
        this.brandMapper = brandMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('USER')")
    public BrandDto applyForBrand(BrandApplyRequest request) {
        User owner = currentUser();
        if (owner.getRole() != Role.USER) {
            throw new ValidationException("Only customers can apply to sell on the marketplace");
        }
        if (brandRepository.existsByOwnerUserId(owner.getId())) {
            throw new ValidationException("You already have a brand application");
        }
        String slug = request.slug().trim().toLowerCase();
        if (brandRepository.existsBySlug(slug)) {
            throw new ValidationException("Brand slug is already taken");
        }
        Brand brand = new Brand(request.name().trim(), slug, request.description().trim(), owner.getId());
        Brand saved = brandRepository.save(brand);
        return brandMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public BrandDto getMyBrand() {
        User user = currentUser();
        if (user.getRole() == Role.SUPER_ADMIN) {
            throw new ValidationException("Super admins do not own a single brand");
        }
        Brand brand = brandRepository.findByOwnerUserId(user.getId())
                .orElseThrow(() -> new ValidationException("No brand linked to your account"));
        return brandMapper.toDto(brand);
    }

    @Override
    @Transactional(readOnly = true)
    public BrandDto getBrand(UUID id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Brand not found"));
        if (SecurityUtils.hasRole("SUPER_ADMIN")) {
            return brandMapper.toDto(brand);
        }
        User user = currentUser();
        if (user.getRole() == Role.ADMIN && brand.getOwnerUserId().equals(user.getId())) {
            return brandMapper.toDto(brand);
        }
        if (brand.getStatus() != BrandStatus.APPROVED) {
            throw new ValidationException("Brand is not publicly available");
        }
        return brandMapper.toDto(brand);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Page<BrandDto> listBrands(Pageable pageable) {
        return brandRepository.findAll(pageable).map(brandMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Page<BrandDto> listPendingBrands(Pageable pageable) {
        return brandRepository.findByStatus(BrandStatus.PENDING, pageable).map(brandMapper::toDto);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public BrandDto approveBrand(UUID id, BigDecimal commissionPercent) {
        Brand brand = loadBrand(id);
        if (brand.getStatus() != BrandStatus.PENDING) {
            throw new ValidationException("Only pending brands can be approved");
        }
        brand.setStatus(BrandStatus.APPROVED);
        if (commissionPercent != null) {
            brand.setCommissionPercent(commissionPercent);
        }
        User owner = userRepository.findById(brand.getOwnerUserId())
                .orElseThrow(() -> new UserNotFoundException(brand.getOwnerUserId().toString()));
        owner.setRole(Role.ADMIN);
        owner.setBrandId(brand.getId());
        userRepository.save(owner);
        return brandMapper.toDto(brandRepository.save(brand));
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public BrandDto rejectBrand(UUID id) {
        Brand brand = loadBrand(id);
        brand.setStatus(BrandStatus.REJECTED);
        return brandMapper.toDto(brandRepository.save(brand));
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public BrandDto suspendBrand(UUID id) {
        Brand brand = loadBrand(id);
        brand.setStatus(BrandStatus.SUSPENDED);
        User owner = userRepository.findById(brand.getOwnerUserId()).orElseThrow();
        owner.setEnabled(false);
        userRepository.save(owner);
        return brandMapper.toDto(brandRepository.save(brand));
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public BrandDto activateBrand(UUID id) {
        Brand brand = loadBrand(id);
        brand.setStatus(BrandStatus.APPROVED);
        User owner = userRepository.findById(brand.getOwnerUserId()).orElseThrow();
        owner.setEnabled(true);
        userRepository.save(owner);
        return brandMapper.toDto(brandRepository.save(brand));
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public BrandDto createVendorAdmin(CreateVendorAdminRequest request) {
        String email = request.email().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email);
        }
        String slug = request.brand().slug().trim().toLowerCase();
        if (brandRepository.existsBySlug(slug)) {
            throw new ValidationException("Brand slug is already taken");
        }
        User user = new User(
                email,
                passwordEncoder.encode(request.password()),
                request.firstName().trim(),
                request.lastName().trim(),
                Role.ADMIN);
        User savedUser = userRepository.save(user);
        Brand brand = new Brand(
                request.brand().name().trim(),
                slug,
                request.brand().description().trim(),
                savedUser.getId());
        brand.setStatus(BrandStatus.APPROVED);
        if (request.commissionPercent() != null) {
            brand.setCommissionPercent(request.commissionPercent());
        }
        Brand savedBrand = brandRepository.save(brand);
        savedUser.setBrandId(savedBrand.getId());
        userRepository.save(savedUser);
        return brandMapper.toDto(savedBrand);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public PlatformStatsDto platformStats() {
        long customers = userRepository.count();
        long vendors = brandRepository.count();
        long pending = brandRepository.countByStatus(BrandStatus.PENDING);
        long approved = brandRepository.countByStatus(BrandStatus.APPROVED);
        long suspended = brandRepository.countByStatus(BrandStatus.SUSPENDED);
        return new PlatformStatsDto(customers, vendors, pending, approved, suspended);
    }

    private Brand loadBrand(UUID id) {
        return brandRepository.findById(id).orElseThrow(() -> new ValidationException("Brand not found"));
    }

    private User currentUser() {
        String email = SecurityUtils.currentUsername()
                .orElseThrow(() -> new ValidationException("Not authenticated"));
        return userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
    }
}
