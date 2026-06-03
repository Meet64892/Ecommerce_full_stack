package com.smartshop.user.repository;

import com.smartshop.user.entity.Brand;
import com.smartshop.user.entity.BrandStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BrandRepository extends JpaRepository<Brand, UUID> {
    List<Brand> findByStatus(BrandStatus status);

    Page<Brand> findByStatus(BrandStatus status, Pageable pageable);

    Optional<Brand> findByOwnerUserId(UUID ownerUserId);

    boolean existsByNameIgnoreCase(String name);
}
