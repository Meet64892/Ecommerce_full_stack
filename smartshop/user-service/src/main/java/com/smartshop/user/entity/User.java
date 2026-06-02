package com.smartshop.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.EntityListeners;
import java.time.Instant;

/**
 * User - JPA entity mapping the {@code users} table.
 *
 * <h2>Purpose</h2>
 * The persistent representation of a registered account. Stores credentials
 * (only a BCrypt <i>hash</i>, never the plaintext password) and profile data.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li><b>@Entity / @Table</b>: marks this class as a managed persistent type
 *       mapped to a specific table.</li>
 *   <li><b>Auditing</b>: {@code @CreatedDate} / {@code @LastModifiedDate} /
 *       {@code @CreatedBy} are auto-filled by the {@link AuditingEntityListener}
 *       (enabled via {@code @EnableJpaAuditing}). We never set timestamps by
 *       hand, eliminating "forgot to update updatedAt" bugs.</li>
 *   <li><b>Password storage</b>: the column holds a BCrypt hash. BCrypt embeds a
 *       per-user random salt and a configurable work factor ("rounds"), so two
 *       users with the same password get different hashes and brute-forcing is
 *       deliberately slow.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Managed by {@code UserRepository}; mapped to {@code UserDto} for API output so
 * the hash and internal fields never leave the service.
 *
 * @author SmartShop Team
 */
@Entity
@Table(name = "users")
// Hooks the auditing listener so auditing annotations are honored on persist/update.
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor   // JPA requires a no-args constructor to instantiate via reflection.
@AllArgsConstructor
@Builder
public class User {

    /**
     * Surrogate primary key. IDENTITY strategy delegates id generation to the
     * database's auto-increment/sequence, which is simple and works well with
     * PostgreSQL's BIGSERIAL.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Unique login identifier; enforced unique at the DB level (see migration). */
    @Column(nullable = false, unique = true)
    private String email;

    /** BCrypt hash of the password. Never store or log the plaintext. */
    @Column(nullable = false)
    private String passwordHash;

    /** Display name. */
    @Column(nullable = false)
    private String fullName;

    /**
     * The user's role. {@code @Enumerated(STRING)} persists the NAME ("ADMIN")
     * rather than the ordinal index — ordinals break catastrophically if the
     * enum order ever changes.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    /** Soft enable/disable flag honored by Spring Security's UserDetails. */
    @Column(nullable = false)
    private boolean enabled;

    // --- Auditing fields (auto-populated) -----------------------------------

    /** Set once when the row is first inserted. Not updatable thereafter. */
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    /** Updated on every save. */
    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    /** Principal that created the row, supplied by the AuditorAware bean. */
    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private String createdBy;
}
