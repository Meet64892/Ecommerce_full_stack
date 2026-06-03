package com.smartshop.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

/**
 * User - JPA entity that stores account profile and credential metadata.
 *
 * <h2>Purpose</h2>
 * The entity represents the durable user record owned by user-service. It deliberately stores only a BCrypt hash,
 * never a raw password, because raw passwords cannot be safely recovered after a database breach.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>JPA entity: A Java object mapped to the `users` table by Hibernate.</li>
 *   <li>Auditing: @CreatedDate, @LastModifiedDate, and @CreatedBy are filled by Spring Data listeners.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Repositories load and persist User objects, services enforce business rules, and mappers convert entities to DTOs.
 *
 * @see com.smartshop.user.repository.UserRepository
 * @author SmartShop Team
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true, length = 320)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(nullable = false, length = 100)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Role role = Role.CUSTOMER;

    @Column(nullable = false)
    private boolean enabled = true;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private Instant updatedAt;

    @CreatedBy
    @Column(updatable = false, length = 100)
    private String createdBy;

    /**
     * Builds a new user while forcing callers to provide an already-hashed password.
     *
     * @param email unique email address used as username
     * @param passwordHash BCrypt password hash, never the raw password
     * @param firstName display first name
     * @param lastName display last name
     * @param role authorization role assigned to the user
     */
    public User(String email, String passwordHash, String firstName, String lastName, Role role) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }
}
