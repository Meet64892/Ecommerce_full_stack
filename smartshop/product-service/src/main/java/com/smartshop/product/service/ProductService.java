package com.smartshop.product.service;

import com.smartshop.product.dto.CategoryDto;
import com.smartshop.product.dto.ProductCreateRequest;
import com.smartshop.product.dto.ProductDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    ProductDto create(ProductCreateRequest request);

    ProductDto update(UUID id, ProductCreateRequest request);

    ProductDto get(UUID id);

    Page<ProductDto> list(Pageable pageable);

    Page<ProductDto> listPending(Pageable pageable);

    Page<ProductDto> listByBrand(UUID brandId, Pageable pageable);

    ProductDto approve(UUID id);

    ProductDto reject(UUID id);

    long countPending();

    void delete(UUID id);

    CategoryDto createCategory(CategoryDto categoryDto);

    List<CategoryDto> categories();
}
