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
import com.smartshop.product.security.MarketplaceContext;
import com.smartshop.product.security.MarketplacePrincipal;
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

    public ProductServiceImpl(ProductRepository productRepository, ProductSearchRepository productSearchRepository,
                              CategoryRepository categoryRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productSearchRepository = productSearchRepository;
        this.categoryRepository = categoryRepository;
        this.productMapper = productMapper;
    }

    @Override
    @Transactional
    public ProductDto create(ProductCreateRequest request) {
        MarketplacePrincipal principal = requirePrincipal();
        if (productRepository.existsByStockKeepingUnit(request.stockKeepingUnit())) {
            throw new ValidationException("SKU already exists: " + request.stockKeepingUnit());
        }
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ProductNotFoundException(request.categoryId()));
        Product product = new Product(request.name(), request.description(), request.price(), request.stockKeepingUnit(), category);
        if (principal.isSuperAdmin()) {
            product.setApprovalStatus(ProductApprovalStatus.APPROVED);
        } else if (principal.isVendor()) {
            if (principal.brandId() == null) {
                throw new ValidationException("Approved brand required before listing products");
            }
            product.setBrandId(principal.brandId());
            product.setApprovalStatus(ProductApprovalStatus.PENDING);
        } else {
            throw new ValidationException("Only vendors or platform admins can create products");
        }
        Product saved = productRepository.save(product);
        indexIfApproved(saved);
        return productMapper.toDto(saved);
    }

    @Override
    @Transactional
    public ProductDto update(UUID id, ProductCreateRequest request) {
        Product product = loadForMutation(id);
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ProductNotFoundException(request.categoryId()));
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStockKeepingUnit(request.stockKeepingUnit());
        product.setCategory(category);
        if (!product.getApprovalStatus().equals(ProductApprovalStatus.APPROVED)) {
            product.setApprovalStatus(ProductApprovalStatus.PENDING);
        }
        indexIfApproved(product);
        return productMapper.toDto(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDto get(UUID id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        MarketplacePrincipal principal = MarketplaceContext.get();
        if (product.getApprovalStatus() != ProductApprovalStatus.APPROVED) {
            if (principal == null) {
                throw new ProductNotFoundException(id);
            }
            if (!principal.isSuperAdmin() && !(principal.isVendor() && principal.brandId() != null
                    && principal.brandId().equals(product.getBrandId()))) {
                throw new ProductNotFoundException(id);
            }
        }
        return productMapper.toDto(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDto> list(Pageable pageable) {
        return productRepository.findByApprovalStatus(ProductApprovalStatus.APPROVED, pageable).map(productMapper::toDto);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Product product = loadForMutation(id);
        productRepository.delete(product);
        productSearchRepository.deleteById(id);
    }

    @Override
    @Transactional
    public CategoryDto createCategory(CategoryDto categoryDto) {
        requireSuperAdminOrVendor();
        Category category = new Category(categoryDto.name(), categoryDto.description());
        return productMapper.toDto(categoryRepository.save(category));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> categories() {
        return categoryRepository.findAll().stream().map(productMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDto> listPending(Pageable pageable) {
        requireSuperAdmin();
        return productRepository.findByApprovalStatus(ProductApprovalStatus.PENDING, pageable).map(productMapper::toDto);
    }

    @Override
    @Transactional
    public ProductDto approve(UUID id) {
        requireSuperAdmin();
        Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        product.setApprovalStatus(ProductApprovalStatus.APPROVED);
        indexIfApproved(product);
        return productMapper.toDto(product);
    }

    @Override
    @Transactional
    public ProductDto reject(UUID id) {
        requireSuperAdmin();
        Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        product.setApprovalStatus(ProductApprovalStatus.REJECTED);
        productSearchRepository.deleteById(id);
        return productMapper.toDto(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDto> listForCurrentVendor(Pageable pageable) {
        MarketplacePrincipal principal = requirePrincipal();
        if (!principal.isVendor() || principal.brandId() == null) {
            throw new ValidationException("Vendor brand required");
        }
        return productRepository.findByBrandId(principal.brandId(), pageable).map(productMapper::toDto);
    }

    private Product loadForMutation(UUID id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        MarketplacePrincipal principal = requirePrincipal();
        if (principal.isSuperAdmin()) {
            return product;
        }
        if (principal.isVendor() && principal.brandId() != null && principal.brandId().equals(product.getBrandId())) {
            return product;
        }
        throw new ValidationException("You cannot modify this product");
    }

    private void indexIfApproved(Product product) {
        if (product.getApprovalStatus() == ProductApprovalStatus.APPROVED) {
            ProductIndexingSupport.prepareForIndexing(product);
            productSearchRepository.save(product);
        } else {
            productSearchRepository.deleteById(product.getId());
        }
    }

    private MarketplacePrincipal requirePrincipal() {
        MarketplacePrincipal principal = MarketplaceContext.get();
        if (principal == null) {
            throw new ValidationException("Authenticated marketplace context required");
        }
        return principal;
    }

    private void requireSuperAdmin() {
        if (!requirePrincipal().isSuperAdmin()) {
            throw new ValidationException("Super Admin access required");
        }
    }

    private void requireSuperAdminOrVendor() {
        MarketplacePrincipal principal = requirePrincipal();
        if (!principal.isSuperAdmin() && !principal.isVendor()) {
            throw new ValidationException("Vendor or Super Admin access required");
        }
    }
}
