package com.smartshop.product.mapper;

import com.smartshop.product.dto.CategoryDto;
import com.smartshop.product.dto.ProductDto;
import com.smartshop.product.entity.Category;
import com.smartshop.product.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * ProductMapper - Compile-time mapper for catalog DTOs.
 *
 * <h2>Purpose</h2>
 * MapStruct avoids reflection and catches field mismatches at build time. It keeps controller response construction
 * concise while making entity-to-DTO transformations explicit.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Flattening: Nested category fields are mapped to categoryId/categoryName.</li>
 *   <li>Generated implementation: The mapper class is produced during compilation.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * ProductServiceImpl and ProductSearchService use this mapper before returning API responses.
 *
 * @see ProductDto
 * @author SmartShop Team
 */
@Mapper(componentModel = "spring")
public interface ProductMapper {
    /**
     * Converts Product to ProductDto while flattening category fields.
     *
     * @param product persistent product entity
     * @return public product DTO
     */
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    ProductDto toDto(Product product);

    /**
     * Converts Category to CategoryDto.
     *
     * @param category persistent category entity
     * @return public category DTO
     */
    CategoryDto toDto(Category category);
}
