package com.smartshop.product.mapper;

import com.smartshop.product.dto.ProductCreateRequest;
import com.smartshop.product.dto.ProductDto;
import com.smartshop.product.entity.Product;
import org.mapstruct.*;

/**
 * ProductMapper - MapStruct Mapper for Product Entity ↔ DTO Conversion
 *
 * @author SmartShop Team
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {

    /**
     * Converts a Product entity to ProductDto.
     * Category name is extracted from the nested Category object.
     *
     * @param product the product entity
     * @return the product DTO
     */
    @Mapping(target = "categoryName", expression = "java(product.getCategory() != null ? product.getCategory().getName() : null)")
    ProductDto toDto(Product product);

    /**
     * Converts a ProductCreateRequest to a Product entity.
     * Category, auditing fields, and computed fields are set by the service layer.
     *
     * @param request the creation request
     * @return a partially-built Product entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "categoryName", ignore = true)
    @Mapping(target = "rating", ignore = true)
    @Mapping(target = "ratingCount", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Product toEntity(ProductCreateRequest request);
}
