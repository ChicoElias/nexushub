package com.nexushub.repository;

import com.nexushub.entity.Product;
import com.nexushub.entity.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // ── Listing queries ───────────────────────────────────────────────

    Page<Product> findByStatus(ProductStatus status, Pageable pageable);

    List<Product> findByCategoryAndStatus(String category, ProductStatus status);

    List<Product> findBySeller_IdAndStatusNot(Long sellerId, ProductStatus status);

    @Query("SELECT p FROM Product p WHERE p.status = 'ACTIVE' AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Product> searchByNameOrDescription(@Param("query") String query);

    // ── Duplicate detection ───────────────────────────────────────────

    /**
     * Checks whether a seller already has an active or inactive product
     * with the same name (case-insensitive). Used to prevent duplicate listings.
     */
    boolean existsBySeller_IdAndNameIgnoreCaseAndStatusNot(
            Long sellerId, String name, ProductStatus excludedStatus);

    /**
     * Same check as above but excludes a specific product ID.
     * Used during updates to allow renaming to the same name.
     */
    @Query("SELECT COUNT(p) > 0 FROM Product p WHERE p.seller.id = :sellerId " +
           "AND LOWER(p.name) = LOWER(:name) " +
           "AND p.status != 'OUT_OF_STOCK' " +
           "AND p.id != :excludeId")
    boolean existsDuplicateNameForUpdate(
            @Param("sellerId") Long sellerId,
            @Param("name") String name,
            @Param("excludeId") Long excludeId);

    // ── Stats queries ─────────────────────────────────────────────────

    long countByStatus(ProductStatus status);

    @Query("SELECT AVG(p.price) FROM Product p WHERE p.status = 'ACTIVE'")
    Optional<BigDecimal> findAveragePriceOfActive();

    @Query("SELECT MAX(p.price) FROM Product p WHERE p.status = 'ACTIVE'")
    Optional<BigDecimal> findHighestPriceOfActive();

    @Query("SELECT MIN(p.price) FROM Product p WHERE p.status = 'ACTIVE'")
    Optional<BigDecimal> findLowestPriceOfActive();

    @Query("SELECT p.category, COUNT(p) FROM Product p " +
           "WHERE p.status = 'ACTIVE' AND p.category IS NOT NULL " +
           "GROUP BY p.category ORDER BY COUNT(p) DESC")
    List<Object[]> countActiveProductsByCategory();
}
