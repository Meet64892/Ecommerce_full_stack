package com.smartshop.user.repository;

import com.smartshop.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * UserRepository - Spring Data access layer for User entities.
 *
 * <h2>Purpose</h2>
 * Repository interfaces express persistence operations without hand-writing boilerplate SQL. Returning Optional
 * forces service code to handle missing users explicitly instead of risking NullPointerException.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>JpaRepository: Provides CRUD, pagination, and sorting operations.</li>
 *   <li>Query derivation: Spring creates SQL from method names such as findByEmail.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * UserServiceImpl calls this repository inside transactions to load and save user data.
 *
 * @see User
 * @author SmartShop Team
 */
public interface UserRepository extends JpaRepository<User, UUID> {
    /**
     * Finds a user by email for login and duplicate checks.
     *
     * @param email normalized email address
     * @return Optional containing a user when present, otherwise Optional.empty
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks uniqueness before registration.
     *
     * @param email email address to test
     * @return true when a row already uses the email
     */
    boolean existsByEmail(String email);
}
