package com.smartshop.user.mapper;

import com.smartshop.user.dto.UserDto;
import com.smartshop.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * UserMapper - MapStruct mapper between entity and DTO.
 *
 * <h2>Purpose</h2>
 * MapStruct generates compile-time mappers, giving type safety and speed versus reflection-based
 * mappers (e.g., ModelMapper) while avoiding repetitive manual mapping code.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Compile-time generation: mapping errors fail build early.</li>
 *   <li>Zero-reflection mapping: efficient for high-throughput APIs.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Service layer uses mapper when returning user projections through controllers.
 *
 * @see UserDto
 * @author SmartShop Team
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Maps persistent user entity to external DTO.
     *
     * @param user user entity from repository
     * @return safe API DTO without secret fields
     */
    @Mapping(target = "id", source = "id")
    UserDto toDto(User user);
}
