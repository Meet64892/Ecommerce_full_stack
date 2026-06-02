package com.smartshop.user.repository;

import com.smartshop.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * UserRepository - Spring Data JPA Repository for User Persistence
 *
 * <h2>Purpose</h2>
 * Provides all database operations for the User entity. By extending JpaRepository,
 * we get 15+ CRUD methods for free without writing any SQL:
 * findById, save, delete, findAll, count, existsById, etc.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>Spring Data JPA Query Derivation: Method names are parsed to generate SQL.
 *       "findByEmail" → SELECT * FROM users WHERE email = ?
 *       "findByUsernameOrEmail" → SELECT * FROM users WHERE username = ? OR email = ?
 *       No SQL to write — Spring generates it from the method name at startup.</li>
 *   <li>Optional return type: Repository methods return Optional<T> to force callers
 *       to handle the "not found" case explicitly. Returning null is the "billion dollar
 *       mistake" (Sir Tony Hoare's words) — Optional makes nullability explicit.
 *       Usage: repository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email))</li>
 *   <li>@Query (JPQL): For complex queries that can't be derived from method names.
 *       JPQL uses class names and field names (not table/column names) — if you rename
 *       a field, JPQL queries must be updated too. SQL is faster to write but ties you
 *       to the specific database schema.</li>
 * </ul>
 *
 * @see User
 * @author SmartShop Team
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their email address.
     * Used during login (email is the login identifier) and for duplicate check at registration.
     *
     * @param email the email address to search for
     * @return Optional containing the User if found, empty Optional if no user has this email
     */
    Optional<User> findByEmail(String email);

    /**
     * Finds a user by their username.
     * Used for username uniqueness checks and profile lookups.
     *
     * @param username the username to search for
     * @return Optional containing the User if found, or empty
     */
    Optional<User> findByUsername(String username);

    /**
     * Checks if a user with the given email already exists.
     * More efficient than findByEmail() when you only need a boolean —
     * generates "SELECT COUNT(*) > 0 WHERE email = ?" instead of fetching all fields.
     * Used for fast duplicate email check during registration.
     *
     * @param email the email to check for existence
     * @return true if a user with this email exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Checks if a user with the given username already exists.
     * Used for username availability check during registration.
     *
     * @param username the username to check
     * @return true if the username is taken, false if available
     */
    boolean existsByUsername(String username);

    /**
     * Finds a user by email or username using JPQL.
     * Used during login to allow users to log in with either their email or username.
     * The query uses OR so either field can match.
     *
     * @param email    the email to search for
     * @param username the username to search for
     * @return Optional containing the matching user, or empty if neither matches
     */
    @Query("SELECT u FROM User u WHERE u.email = :email OR u.username = :username")
    Optional<User> findByEmailOrUsername(@Param("email") String email,
                                          @Param("username") String username);
}
