package com.nexushub.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    /** Stock level below which the product is considered low-stock. */
    public static final int LOW_STOCK_THRESHOLD = 5;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stock;

    @Column(length = 80)
    private String category;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    /**
     * Product lifecycle status.
     * Managed by the seller (ACTIVE / INACTIVE) and automatically
     * adjusted by the system when stock reaches zero (OUT_OF_STOCK).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ProductStatus status = ProductStatus.ACTIVE;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ── Derived helpers ───────────────────────────────────────────────

    public boolean isAvailable() {
        return status == ProductStatus.ACTIVE && stock > 0;
    }

    public boolean isLowStock() {
        return stock > 0 && stock <= LOW_STOCK_THRESHOLD;
    }
}
