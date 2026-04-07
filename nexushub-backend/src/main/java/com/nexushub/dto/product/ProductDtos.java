package com.nexushub.dto.product;

import com.nexushub.entity.ProductStatus;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class ProductDtos {

    // ── Product Request ───────────────────────────────────────────────

    public record ProductRequest(
            @NotBlank(message = "Product name is required")
            @Size(min = 2, max = 120, message = "Name must be between 2 and 120 characters")
            String name,

            @Size(max = 2000, message = "Description cannot exceed 2000 characters")
            String description,

            @NotNull(message = "Price is required")
            @DecimalMin(value = "0.01", message = "Price must be greater than 0")
            @DecimalMax(value = "99999.99", message = "Price cannot exceed $99,999.99")
            @Digits(integer = 5, fraction = 2, message = "Invalid price format (max: 99999.99)")
            BigDecimal price,

            @NotNull(message = "Stock is required")
            @Min(value = 0, message = "Stock cannot be negative")
            @Max(value = 99999, message = "Stock cannot exceed 99,999 units")
            Integer stock,

            @Size(max = 80, message = "Category cannot exceed 80 characters")
            String category,

            String imageUrl
    ) {}

    // ── Status Update Request ─────────────────────────────────────────

    public record StatusUpdateRequest(
            @NotNull(message = "Status is required")
            ProductStatus status
    ) {}

    // ── Product Response ──────────────────────────────────────────────

    public record ProductResponse(
            Long id,
            String name,
            String description,
            BigDecimal price,
            Integer stock,
            String category,
            String imageUrl,
            Long sellerId,
            String sellerName,
            ProductStatus status,
            boolean available,
            boolean lowStock,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}

    // ── Paginated Response ────────────────────────────────────────────

    public record PagedProductResponse(
            List<ProductResponse> content,
            int page,
            int size,
            long totalElements,
            int totalPages,
            boolean last
    ) {}

    // ── Catalog Stats Response ────────────────────────────────────────

    /**
     * Aggregated statistics for the product catalog.
     * Useful for seller dashboards and portfolio demos.
     */
    public record CatalogStatsResponse(
            long totalActiveProducts,
            long totalOutOfStock,
            long totalInactive,
            BigDecimal averagePrice,
            BigDecimal highestPrice,
            BigDecimal lowestPrice,
            Map<String, Long> productsByCategory
    ) {}
}
