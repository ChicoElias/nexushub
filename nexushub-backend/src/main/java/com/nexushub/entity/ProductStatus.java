package com.nexushub.entity;

/**
 * Lifecycle states for a Product listing.
 *
 * ACTIVE       — visible to all buyers; available for purchase.
 * INACTIVE     — hidden by the seller (e.g. temporarily unavailable).
 * OUT_OF_STOCK — automatically set by the system when stock reaches 0;
 *                still visible but not purchasable.
 */
public enum ProductStatus {
    ACTIVE,
    INACTIVE,
    OUT_OF_STOCK
}
