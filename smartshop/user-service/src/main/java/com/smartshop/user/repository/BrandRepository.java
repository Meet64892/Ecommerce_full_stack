package com.smartshop.user.repository;

import com.smartshop.user.entity.Brand;
import com.smartshop.user.entity.BrandStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BrandRepository extends JpaRepository<Brand, UUID> {
    boolean existsBySlug(String slug);

    boolean existsByOwnerUserId(UUID ownerUserId);

    Optional<Brand> findByOwnerUserId(UUID ownerUserId);

    Page<Brand> findByStatus(BrandStatus status, Pageable pageable);

    long countByStatus(BrandStatus status);
}
