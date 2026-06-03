package com.smartshop.user.mapper;

import com.smartshop.user.dto.BrandDto;
import com.smartshop.user.entity.Brand;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BrandMapper {
    BrandDto toDto(Brand brand);
}
