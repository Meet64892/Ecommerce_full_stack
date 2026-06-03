package com.smartshop.product.repository.jpa;

import com.smartshop.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {

    Optional<Product> findByStockKeepingUnit(String stockKeepingUnit);

    boolean existsByStockKeepingUnit(String stockKeepingUnit);
}
