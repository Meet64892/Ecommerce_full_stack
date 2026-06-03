package com.smartshop.product.mapper;

import com.smartshop.product.dto.CategoryDto;
import com.smartshop.product.dto.ProductDto;
import com.smartshop.product.entity.Category;
import com.smartshop.product.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "categoryId", expression = "java(resolveCategoryId(product))")
    @Mapping(target = "categoryName", expression = "java(resolveCategoryName(product))")
    @Mapping(target = "approvalStatus", expression = "java(product.getApprovalStatus() != null ? product.getApprovalStatus().name() : null)")
    ProductDto toDto(Product product);

    CategoryDto toDto(Category category);

    default UUID resolveCategoryId(Product product) {
        if (product.getCategoryId() != null) {
            return product.getCategoryId();
        }
        return product.getCategory() != null ? product.getCategory().getId() : null;
    }

    default String resolveCategoryName(Product product) {
        if (product.getCategoryName() != null) {
            return product.getCategoryName();
        }
        return product.getCategory() != null ? product.getCategory().getName() : null;
    }
}
