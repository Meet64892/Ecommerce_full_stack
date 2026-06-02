package com.smartshop.user.mapper;

import com.smartshop.user.dto.UserDto;
import com.smartshop.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * UserMapper - Compile-time mapper between User entities and DTOs.
 *
 * <h2>Purpose</h2>
 * MapStruct generates mapping code at compile time, which is faster and safer than reflection-based mappers such as
 * ModelMapper while avoiding repetitive manual mapping code. Generated mappers also fail the build when fields drift.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Compile-time generation: The implementation is produced during annotation processing.</li>
 *   <li>DTO mapping: Entity internals stay inside the service boundary.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * UserServiceImpl injects this mapper and converts entities before returning controller responses.
 *
 * @see UserDto
 * @author SmartShop Team
 */
@Mapper(componentModel = "spring")
public interface UserMapper {
    /**
     * Converts a User entity to a safe public DTO.
     *
     * @param user persistent entity loaded from the database
     * @return DTO with no password hash
     */
    @Mapping(target = "fullName", expression = "java(user.getFirstName() + \" \" + user.getLastName())")
    UserDto toDto(User user);
}
