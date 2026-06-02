package com.smartshop.user.entity;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * User - Persistent user identity aggregate.
 *
 * <h2>Purpose</h2>
 * Stores canonical user account data and audit metadata. Password hashes (not plaintext passwords)
 * are stored because BCrypt with per-hash salt mitigates rainbow-table attacks.
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li>BCrypt hashing: one-way adaptive hashing with configurable work factor.</li>
 *   <li>JPA auditing: captures who created/updated rows and when changes occurred.</li>
 * </ul>
 *
 * <h2>How it fits in the system</h2>
 * Controllers accept DTOs, service layer validates and maps to this entity, repository persists it.
 *
 * @see com.smartshop.user.config.AuditConfig
 * @author SmartShop Team
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @CreatedBy
    @Column(name = "created_by", nullable = false)
    private String createdBy;
}
