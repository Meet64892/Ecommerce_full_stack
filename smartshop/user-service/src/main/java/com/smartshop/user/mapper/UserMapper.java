package com.smartshop.user.mapper;

import com.smartshop.user.dto.UserDto;
import com.smartshop.user.entity.User;
import org.mapstruct.Mapper;

/**
 * UserMapper - MapStruct mapper between {@link User} and {@link UserDto}.
 *
 * <h2>Purpose</h2>
 * Converting entities to DTOs by hand is tedious and error-prone (forget a field
 * and you ship a bug). MapStruct generates the implementation at COMPILE time.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li><b>MapStruct vs manual</b>: zero hand-written getter/setter plumbing,
 *       and a forgotten field becomes a compile-time warning.</li>
 *   <li><b>MapStruct vs ModelMapper</b>: MapStruct emits plain Java at compile
 *       time (fast, debuggable, no reflection). ModelMapper maps reflectively at
 *       runtime — slower and failures surface only at runtime.</li>
 *   <li>{@code componentModel = "spring"} makes the generated mapper a Spring
 *       bean so it can be constructor-injected.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Injected into {@code UserServiceImpl} to produce {@code UserDto} responses.
 *
 * @author SmartShop Team
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Maps a persistent user to its safe API representation. Fields with
     * matching names (id, email, fullName, role, enabled, createdAt) are mapped
     * automatically; the password hash has no DTO target so it is never copied.
     *
     * @param user the entity to convert
     * @return the corresponding DTO
     */
    UserDto toDto(User user);
}
