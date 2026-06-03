package com.smartshop.product.repository.jpa;

import com.smartshop.product.entity.Product;
import com.smartshop.product.entity.ProductApprovalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {

    Optional<Product> findByStockKeepingUnit(String stockKeepingUnit);

    boolean existsByStockKeepingUnit(String stockKeepingUnit);

    Page<Product> findByApprovalStatus(ProductApprovalStatus status, Pageable pageable);

    Page<Product> findByBrandId(UUID brandId, Pageable pageable);
}
