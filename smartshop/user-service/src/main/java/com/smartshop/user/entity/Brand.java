package com.smartshop.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "brands")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
public class Brand {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, unique = true, length = 120)
    private String slug;

    @Column(length = 2000)
    private String description;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Column(name = "owner_user_id", nullable = false)
    private UUID ownerUserId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private BrandStatus status = BrandStatus.PENDING;

    @Column(name = "commission_percent", nullable = false, precision = 5, scale = 2)
    private BigDecimal commissionPercent = new BigDecimal("10.00");

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public Brand(String name, String slug, String description, UUID ownerUserId) {
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.ownerUserId = ownerUserId;
        this.status = BrandStatus.PENDING;
    }
}
