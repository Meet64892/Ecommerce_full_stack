package com.smartshop.product.service;

import com.smartshop.common.exception.ValidationException;
import com.smartshop.product.dto.CategoryDto;
import com.smartshop.product.dto.ProductCreateRequest;
import com.smartshop.product.dto.ProductDto;
import com.smartshop.product.entity.Category;
import com.smartshop.product.entity.Product;
import com.smartshop.product.entity.ProductApprovalStatus;
import com.smartshop.product.exception.ProductNotFoundException;
import com.smartshop.product.mapper.ProductMapper;
import com.smartshop.product.repository.jpa.CategoryRepository;
import com.smartshop.product.repository.jpa.ProductRepository;
import com.smartshop.product.repository.search.ProductSearchRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductSearchRepository productSearchRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    public ProductServiceImpl(
            ProductRepository productRepository,
            ProductSearchRepository productSearchRepository,
            CategoryRepository categoryRepository,
            ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productSearchRepository = productSearchRepository;
        this.categoryRepository = categoryRepository;
        this.productMapper = productMapper;
    }

    @Override
    @Transactional
    public ProductDto create(ProductCreateRequest request) {
        if (productRepository.existsByStockKeepingUnit(request.stockKeepingUnit())) {
            throw new ValidationException("SKU already exists: " + request.stockKeepingUnit());
        }
        Category category = categoryRepository
                .findById(request.categoryId())
                .orElseThrow(() -> new ProductNotFoundException(request.categoryId()));
        Product product = new Product(
                request.name(), request.description(), request.price(), request.stockKeepingUnit(), category);
        if (request.brandId() != null) {
            product.setBrandId(request.brandId());
            product.setApprovalStatus(ProductApprovalStatus.PENDING);
        } else {
            product.setApprovalStatus(ProductApprovalStatus.APPROVED);
        }
        Product saved = productRepository.save(product);
        if (saved.getApprovalStatus() == ProductApprovalStatus.APPROVED) {
            ProductIndexingSupport.prepareForIndexing(saved);
            productSearchRepository.save(saved);
        }
        return productMapper.toDto(saved);
    }

    @Override
    @Transactional
    public ProductDto update(UUID id, ProductCreateRequest request) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        Category category = categoryRepository
                .findById(request.categoryId())
                .orElseThrow(() -> new ProductNotFoundException(request.categoryId()));
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStockKeepingUnit(request.stockKeepingUnit());
        product.setCategory(category);
        if (product.getApprovalStatus() == ProductApprovalStatus.APPROVED) {
            ProductIndexingSupport.prepareForIndexing(product);
            productSearchRepository.save(product);
        }
        return productMapper.toDto(productRepository.save(product));
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDto get(UUID id) {
        return productRepository.findById(id).map(productMapper::toDto).orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDto> list(Pageable pageable) {
        return productRepository.findByApprovalStatus(ProductApprovalStatus.APPROVED, pageable).map(productMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDto> listPending(Pageable pageable) {
        return productRepository.findByApprovalStatus(ProductApprovalStatus.PENDING, pageable).map(productMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDto> listByBrand(UUID brandId, Pageable pageable) {
        return productRepository.findByBrandId(brandId, pageable).map(productMapper::toDto);
    }

    @Override
    @Transactional
    public ProductDto approve(UUID id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        product.setApprovalStatus(ProductApprovalStatus.APPROVED);
        Product saved = productRepository.save(product);
        ProductIndexingSupport.prepareForIndexing(saved);
        productSearchRepository.save(saved);
        return productMapper.toDto(saved);
    }

    @Override
    @Transactional
    public ProductDto reject(UUID id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        product.setApprovalStatus(ProductApprovalStatus.REJECTED);
        productRepository.save(product);
        productSearchRepository.deleteById(id);
        return productMapper.toDto(product);
    }

    @Override
    @Transactional(readOnly = true)
    public long countPending() {
        return productRepository.countByApprovalStatus(ProductApprovalStatus.PENDING);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        productRepository.delete(product);
        productSearchRepository.deleteById(id);
    }

    @Override
    @Transactional
    public CategoryDto createCategory(CategoryDto categoryDto) {
        Category category = new Category(categoryDto.name(), categoryDto.description());
        return productMapper.toDto(categoryRepository.save(category));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> categories() {
        return categoryRepository.findAll().stream().map(productMapper::toDto).toList();
    }
}
