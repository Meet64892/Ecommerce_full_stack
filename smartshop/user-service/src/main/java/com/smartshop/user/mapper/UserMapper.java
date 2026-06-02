package com.smartshop.user.mapper;

import com.smartshop.user.dto.RegisterRequest;
import com.smartshop.user.dto.UserDto;
import com.smartshop.user.entity.User;
import org.mapstruct.*;

/**
 * UserMapper - MapStruct Mapper for User Entity ↔ DTO Conversion
 *
 * <h2>Purpose</h2>
 * Handles conversion between User entities and DTOs without manual field-by-field
 * mapping code. MapStruct generates the implementation at compile time — not reflection.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>WHY MapStruct over manual mapping?
 *       Manual: userDto.setUsername(user.getUsername()); userDto.setEmail(user.getEmail()); ...
 *       Problem: if you add a field to User, you must remember to add it to the mapper too.
 *       MapStruct: automatically maps same-named fields. Compile error if mapping is incomplete
 *       (with unmappedTargetPolicy = ERROR). Refactoring-safe.</li>
 *   <li>WHY MapStruct over ModelMapper?
 *       ModelMapper uses reflection at runtime: finds getters/setters via reflection, maps by name.
 *       MapStruct generates plain Java code at compile time: no reflection overhead, visible in IDE.</li>
 *   <li>componentModel = "spring": MapStruct generates @Component on the implementation,
 *       making it injectable as a Spring bean. Without this, you'd use Mappers.getMapper(UserMapper.class).</li>
 *   <li>@Mapping(target="passwordHash", ignore=true): When creating a User from RegisterRequest,
 *       we don't map the password directly — the service layer BCrypt-hashes it first.
 *       Ignoring it here prevents MapStruct from trying (and failing) to map password → passwordHash.</li>
 * </ul>
 *
 * @author SmartShop Team
 */
@Mapper(
    componentModel = "spring",         // Generates @Component — injectable as Spring bean
    unmappedTargetPolicy = ReportingPolicy.IGNORE  // Ignore unmapped target properties (not error)
)
public interface UserMapper {

    /**
     * Converts a User entity to UserDto for API responses.
     * MapStruct maps same-named fields automatically.
     * Fields in UserDto that don't exist in User are mapped via explicit @Mapping annotations.
     *
     * @param user the User entity from the database
     * @return a safe DTO representation without sensitive fields (no passwordHash)
     */
    UserDto toDto(User user);

    /**
     * Converts a RegisterRequest to a User entity for persistence.
     * passwordHash and role are intentionally NOT mapped here — the service sets them.
     * Ignored fields:
     *   - passwordHash: service BCrypt-hashes the password before setting
     *   - id, createdAt, updatedAt, createdBy, updatedBy: set by DB/Spring Auditing
     *
     * @param request the validated registration request
     * @return a partially-built User entity (passwordHash must be set by caller)
     */
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    User toEntity(RegisterRequest request);
}
