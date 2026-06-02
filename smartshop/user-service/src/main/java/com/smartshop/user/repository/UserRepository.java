package com.smartshop.user.repository;

import com.smartshop.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * UserRepository - Spring Data JPA repository for {@link User}.
 *
 * <h2>Purpose</h2>
 * Provides CRUD + query-derivation for users without writing SQL/JPQL by hand.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li><b>JpaRepository</b>: inherits save/findById/findAll/delete/paging.</li>
 *   <li><b>Derived query</b>: {@code findByEmail} — Spring parses the method name
 *       and generates the query automatically.</li>
 *   <li><b>Optional return</b>: forces callers to handle the "not found" case
 *       explicitly instead of risking a {@code NullPointerException}.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Used by {@code UserServiceImpl} and {@code UserDetailsServiceImpl} (login).
 *
 * @author SmartShop Team
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Looks up a user by their unique email (the login identifier).
     *
     * @param email the email to search for
     * @return the matching user, or empty if none exists
     */
    Optional<User> findByEmail(String email);

    /**
     * Efficient existence check used during registration to reject duplicates
     * without loading the whole entity.
     *
     * @param email the email to test
     * @return true if a user with this email already exists
     */
    boolean existsByEmail(String email);
}
