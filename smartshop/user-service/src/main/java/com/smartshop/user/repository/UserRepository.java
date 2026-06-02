package com.smartshop.user.repository;

import com.smartshop.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * UserRepository - Persistence abstraction for User aggregates.
 *
 * <h2>Purpose</h2>
 * Spring Data repository removes boilerplate SQL and returns Optional to force explicit handling
 * of missing rows, reducing NullPointerException risk.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Optional semantics: caller must decide success/fallback/error path explicitly.</li>
 *   <li>Repository pattern: decouples domain logic from persistence technology.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Service layer uses this repository for account lookups and updates.
 *
 * @see com.smartshop.user.service.UserServiceImpl
 * @author SmartShop Team
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by unique email address.
     *
     * @param email unique email
     * @return optional user if found
     */
    Optional<User> findByEmail(String email);
}
