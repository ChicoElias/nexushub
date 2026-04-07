package com.nexushub.service;

import com.nexushub.dto.product.ProductDtos.*;
import com.nexushub.entity.Product;
import com.nexushub.entity.ProductStatus;
import com.nexushub.entity.User;
import com.nexushub.exception.BadRequestException;
import com.nexushub.exception.ResourceNotFoundException;
import com.nexushub.exception.UnauthorizedException;
import com.nexushub.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    // ── Read operations ───────────────────────────────────────────────

    @Transactional(readOnly = true)
    public PagedProductResponse getAllProducts(int page, int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Product> productPage = productRepository.findByStatus(ProductStatus.ACTIVE, pageable);

        List<ProductResponse> content = productPage.getContent().stream()
                .map(this::toResponse)
                .toList();

        return new PagedProductResponse(
                content,
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.isLast()
        );
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        return toResponse(findVisibleById(id));
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> searchProducts(String query) {
        if (query == null || query.isBlank()) {
            throw new BadRequestException("Search query cannot be empty");
        }
        return productRepository.searchByNameOrDescription(query.trim())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByCategory(String category) {
        return productRepository.findByCategoryAndStatus(category, ProductStatus.ACTIVE)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getMyProducts(User seller) {
        // Returns all non-deleted products for the seller (including INACTIVE / OUT_OF_STOCK)
        return productRepository.findBySeller_IdAndStatusNot(seller.getId(), null)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ── Write operations ──────────────────────────────────────────────

    /**
     * Creates a new product listing.
     *
     * Business rules enforced:
     * 1. A seller cannot have two active/inactive products with the same name (case-insensitive).
     * 2. If initial stock is 0, the product is created as OUT_OF_STOCK.
     */
    @Transactional
    public ProductResponse createProduct(ProductRequest request, User seller) {
        assertNoDuplicateName(seller.getId(), request.name().trim(), null);

        ProductStatus initialStatus = resolveStatus(request.stock(), ProductStatus.ACTIVE);

        Product product = Product.builder()
                .name(request.name().trim())
                .description(request.description())
                .price(request.price())
                .stock(request.stock())
                .category(request.category())
                .imageUrl(request.imageUrl())
                .seller(seller)
                .status(initialStatus)
                .build();

        Product saved = productRepository.save(product);
        log.info("Product created: '{}' (id={}) status={} by userId={}",
                saved.getName(), saved.getId(), saved.getStatus(), seller.getId());
        return toResponse(saved);
    }

    /**
     * Updates an existing product.
     *
     * Business rules enforced:
     * 1. Only the owner can update their product.
     * 2. Duplicate name check excludes the product being updated.
     * 3. Stock change may trigger automatic status transition.
     */
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request, User requester) {
        Product product = findOwnedById(id, requester);

        assertNoDuplicateName(requester.getId(), request.name().trim(), id);

        product.setName(request.name().trim());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setCategory(request.category());
        product.setImageUrl(request.imageUrl());

        // Re-evaluate status only when stock is being changed
        if (!product.getStock().equals(request.stock())) {
            product.setStock(request.stock());
            // Preserve INACTIVE if seller chose it; only auto-manage ACTIVE <-> OUT_OF_STOCK
            if (product.getStatus() != ProductStatus.INACTIVE) {
                product.setStatus(resolveStatus(request.stock(), ProductStatus.ACTIVE));
            }
        }

        Product saved = productRepository.save(product);
        log.info("Product updated: id={} status={}", saved.getId(), saved.getStatus());
        return toResponse(saved);
    }

    /**
     * Changes a product's status (ACTIVE / INACTIVE / OUT_OF_STOCK).
     *
     * Business rules:
     * - Cannot set ACTIVE if stock is 0 (would be immediately OUT_OF_STOCK anyway).
     * - OUT_OF_STOCK can only be cleared by adding stock via updateProduct.
     */
    @Transactional
    public ProductResponse updateStatus(Long id, ProductStatus newStatus, User requester) {
        Product product = findOwnedById(id, requester);

        if (newStatus == ProductStatus.ACTIVE && product.getStock() == 0) {
            throw new BadRequestException(
                    "Cannot set product to ACTIVE when stock is 0. Add stock first.");
        }
        if (newStatus == ProductStatus.OUT_OF_STOCK) {
            throw new BadRequestException(
                    "OUT_OF_STOCK is managed automatically by the system based on stock level.");
        }

        product.setStatus(newStatus);
        Product saved = productRepository.save(product);
        log.info("Product status changed: id={} -> {}", id, newStatus);
        return toResponse(saved);
    }

    /**
     * Soft-deletes a product by setting its status to INACTIVE.
     * The record is preserved in the database for historical integrity.
     */
    @Transactional
    public void deleteProduct(Long id, User requester) {
        Product product = findOwnedById(id, requester);
        product.setStatus(ProductStatus.INACTIVE);
        productRepository.save(product);
        log.info("Product soft-deleted (INACTIVE): id={}", id);
    }

    /**
     * Returns aggregated statistics across the active product catalog.
     * Demonstrates business-level reporting beyond basic CRUD.
     */
    @Transactional(readOnly = true)
    public CatalogStatsResponse getCatalogStats() {
        long active    = productRepository.countByStatus(ProductStatus.ACTIVE);
        long oos       = productRepository.countByStatus(ProductStatus.OUT_OF_STOCK);
        long inactive  = productRepository.countByStatus(ProductStatus.INACTIVE);

        BigDecimal avg  = productRepository.findAveragePriceOfActive()
                .map(v -> v.setScale(2, RoundingMode.HALF_UP))
                .orElse(BigDecimal.ZERO);
        BigDecimal high = productRepository.findHighestPriceOfActive().orElse(BigDecimal.ZERO);
        BigDecimal low  = productRepository.findLowestPriceOfActive().orElse(BigDecimal.ZERO);

        Map<String, Long> byCategory = new HashMap<>();
        productRepository.countActiveProductsByCategory()
                .forEach(row -> byCategory.put((String) row[0], (Long) row[1]));

        return new CatalogStatsResponse(active, oos, inactive, avg, high, low, byCategory);
    }

    // ── Private helpers ───────────────────────────────────────────────

    /** Finds a product visible to the public (ACTIVE or OUT_OF_STOCK — not INACTIVE). */
    private Product findVisibleById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
        if (product.getStatus() == ProductStatus.INACTIVE) {
            throw new ResourceNotFoundException("Product", id);
        }
        return product;
    }

    /** Finds any product owned by the requester, regardless of status. */
    private Product findOwnedById(Long id, User requester) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
        assertOwnership(product, requester);
        return product;
    }

    private void assertOwnership(Product product, User requester) {
        if (!product.getSeller().getId().equals(requester.getId())) {
            throw new UnauthorizedException("You are not the owner of this product");
        }
    }

    /**
     * Prevents duplicate product names per seller (case-insensitive).
     * @param excludeId if non-null, the product being updated is excluded from the check.
     */
    private void assertNoDuplicateName(Long sellerId, String name, Long excludeId) {
        boolean duplicate;
        if (excludeId == null) {
            duplicate = productRepository
                    .existsBySeller_IdAndNameIgnoreCaseAndStatusNot(
                            sellerId, name, ProductStatus.INACTIVE);
        } else {
            duplicate = productRepository.existsDuplicateNameForUpdate(sellerId, name, excludeId);
        }
        if (duplicate) {
            throw new BadRequestException(
                    "You already have a product named '" + name + "'. Choose a different name.");
        }
    }

    /**
     * Determines the correct status based on stock quantity.
     * Stock = 0 → OUT_OF_STOCK. Stock > 0 → use the provided default.
     */
    private ProductStatus resolveStatus(int stock, ProductStatus defaultStatus) {
        return stock == 0 ? ProductStatus.OUT_OF_STOCK : defaultStatus;
    }

    private ProductResponse toResponse(Product p) {
        return new ProductResponse(
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getPrice(),
                p.getStock(),
                p.getCategory(),
                p.getImageUrl(),
                p.getSeller().getId(),
                p.getSeller().getName(),
                p.getStatus(),
                p.isAvailable(),
                p.isLowStock(),
                p.getCreatedAt(),
                p.getUpdatedAt()
        );
    }
}
