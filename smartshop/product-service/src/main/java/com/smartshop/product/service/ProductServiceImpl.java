package com.smartshop.product.service;

import com.smartshop.common.exception.ValidationException;
import com.smartshop.product.dto.CategoryDto;
import com.smartshop.product.dto.ProductCreateRequest;
import com.smartshop.product.dto.ProductDto;
import com.smartshop.product.entity.Category;
import com.smartshop.product.entity.Product;
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

/**
 * ProductServiceImpl - Implements catalog CRUD and dual-write indexing.
 *
 * <h2>Purpose</h2>
 * This service treats PostgreSQL as the system of record and Elasticsearch as a derived search projection. The simple
 * dual-write strategy shown here writes the database row first, then indexes the saved entity for search.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Dual-write tradeoff: Easy to understand, but production systems often add an outbox for stronger recovery.</li>
 *   <li>@Transactional: Database changes roll back if a runtime exception occurs before commit.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Controllers call this class, it persists data through repositories, and ProductSearchService later queries the index.
 *
 * @see ProductService
 * @author SmartShop Team
 */
@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductSearchRepository productSearchRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    /**
     * Creates the service with explicit repositories and mapper dependencies.
     *
     * @param productRepository PostgreSQL product repository
     * @param productSearchRepository Elasticsearch product repository
     * @param categoryRepository PostgreSQL category repository
     * @param productMapper MapStruct mapper
     */
    public ProductServiceImpl(ProductRepository productRepository, ProductSearchRepository productSearchRepository,
                              CategoryRepository categoryRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productSearchRepository = productSearchRepository;
        this.categoryRepository = categoryRepository;
        this.productMapper = productMapper;
    }

    /**
     * Creates a product and indexes it for search.
     *
     * @param request validated create payload
     * @return created product DTO
     * @throws ValidationException when the SKU is already used
     */
    @Override
    @Transactional
    public ProductDto create(ProductCreateRequest request) {
        if (productRepository.existsByStockKeepingUnit(request.stockKeepingUnit())) {
            throw new ValidationException("SKU already exists: " + request.stockKeepingUnit());
        }
        Category category = categoryRepository.findById(request.categoryId()).orElseThrow(() -> new ProductNotFoundException(request.categoryId()));
        Product product = new Product(request.name(), request.description(), request.price(), request.stockKeepingUnit(), category);
        Product saved = productRepository.save(product);
        // The search index is updated after the database save so the indexed document has the generated id.
        productSearchRepository.save(saved);
        return productMapper.toDto(saved);
    }

    /**
     * Updates a product and refreshes its search document.
     *
     * @param id product id
     * @param request validated update payload
     * @return updated product DTO
     * @throws ProductNotFoundException when the product or category does not exist
     */
    @Override
    @Transactional
    public ProductDto update(UUID id, ProductCreateRequest request) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        Category category = categoryRepository.findById(request.categoryId()).orElseThrow(() -> new ProductNotFoundException(request.categoryId()));
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStockKeepingUnit(request.stockKeepingUnit());
        product.setCategory(category);
        productSearchRepository.save(product);
        return productMapper.toDto(product);
    }

    /**
     * Loads a product from the transactional source of truth.
     *
     * @param id product id
     * @return product DTO
     * @throws ProductNotFoundException when no product exists
     */
    @Override
    @Transactional(readOnly = true)
    public ProductDto get(UUID id) {
        return productRepository.findById(id).map(productMapper::toDto).orElseThrow(() -> new ProductNotFoundException(id));
    }

    /**
     * Lists products through JPA pagination.
     *
     * @param pageable page request
     * @return page with content and total count metadata
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ProductDto> list(Pageable pageable) {
        return productRepository.findAll(pageable).map(productMapper::toDto);
    }

    /**
     * Deletes a product from both persistence stores.
     *
     * @param id product id
     * @throws ProductNotFoundException when no product exists
     */
    @Override
    @Transactional
    public void delete(UUID id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        productRepository.delete(product);
        productSearchRepository.deleteById(id);
    }

    /**
     * Creates a category record.
     *
     * @param categoryDto validated category payload
     * @return created category DTO
     */
    @Override
    @Transactional
    public CategoryDto createCategory(CategoryDto categoryDto) {
        Category category = new Category(categoryDto.name(), categoryDto.description());
        return productMapper.toDto(categoryRepository.save(category));
    }

    /**
     * Lists all categories for navigation facets.
     *
     * @return category DTO list
     */
    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> categories() {
        return categoryRepository.findAll().stream().map(productMapper::toDto).toList();
    }
}
