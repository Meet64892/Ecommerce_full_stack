package com.smartshop.product.mapper;

import com.smartshop.product.dto.ProductDto;
import com.smartshop.product.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * ProductMapper - MapStruct mapping between Product entity and DTO.
 *
 * <h2>Purpose</h2>
 * Generates deterministic mapping code at compile-time, avoiding manual boilerplate and runtime
 * reflection overhead.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Compile-time safety: mapping mismatches fail build.</li>
 *   <li>Nested mapping: category details mapped into flat DTO fields.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * ProductService uses mapper for all API response transformations.
 *
 * @see ProductDto
 * @author SmartShop Team
 */
@Mapper(componentModel = "spring")
public interface ProductMapper {

    /**
     * Maps Product to ProductDto.
     *
     * @param product product entity
     * @return product dto projection
     */
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    ProductDto toDto(Product product);
}
