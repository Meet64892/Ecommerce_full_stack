package com.smartshop.product.mapper;

import com.smartshop.product.dto.ProductCreateRequest;
import com.smartshop.product.dto.ProductDto;
import com.smartshop.product.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * ProductMapper - MapStruct mapper for product conversions.
 *
 * <h2>Purpose</h2>
 * Generates compile-time conversions between the entity and its DTOs, avoiding
 * hand-written, error-prone mapping code.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>{@code @Mapping(target="id", ignore=true)}: the database assigns the id,
 *       so we must not copy one from the create request.</li>
 *   <li>{@code @Mapping(target="rating", ...)}: new products start unrated.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Injected into {@code ProductServiceImpl}.
 *
 * @author SmartShop Team
 */
@Mapper(componentModel = "spring")
public interface ProductMapper {

    /**
     * Maps an entity to its API DTO.
     *
     * @param product the persistent product
     * @return the DTO view
     */
    ProductDto toDto(Product product);

    /**
     * Maps a create request to a new entity (id/rating set by the system).
     *
     * @param request the validated creation payload
     * @return a transient {@link Product} ready to persist
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rating", ignore = true)
    Product toEntity(ProductCreateRequest request);
}
