package com.nexushub.controller;

import com.nexushub.dto.product.ProductDtos.*;
import com.nexushub.entity.ProductStatus;
import com.nexushub.entity.User;
import com.nexushub.service.AuthService;
import com.nexushub.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Product catalog management")
public class ProductController {

    private final ProductService productService;
    private final AuthService authService;

    // ── Public endpoints ──────────────────────────────────────────────

    @GetMapping
    @Operation(summary = "List all active products (paginated)")
    public ResponseEntity<PagedProductResponse> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(productService.getAllProducts(page, size));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID")
    public ResponseEntity<ProductResponse> getProductById(
            @PathVariable @Parameter(description = "Product ID") Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/search")
    @Operation(summary = "Search products by name or description")
    public ResponseEntity<List<ProductResponse>> searchProducts(
            @RequestParam String query) {
        return ResponseEntity.ok(productService.searchProducts(query));
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Filter products by category")
    public ResponseEntity<List<ProductResponse>> getByCategory(
            @PathVariable String category) {
        return ResponseEntity.ok(productService.getProductsByCategory(category));
    }

    @GetMapping("/stats")
    @Operation(summary = "Get aggregated catalog statistics",
               description = "Returns counts by status, price range, and breakdown by category.")
    public ResponseEntity<CatalogStatsResponse> getCatalogStats() {
        return ResponseEntity.ok(productService.getCatalogStats());
    }

    // ── Authenticated endpoints ───────────────────────────────────────

    @GetMapping("/my")
    @Operation(summary = "Get all products owned by the authenticated user")
    public ResponseEntity<List<ProductResponse>> getMyProducts(
            @RequestHeader("X-Auth-Token") String token) {
        User user = authService.validateToken(token);
        return ResponseEntity.ok(productService.getMyProducts(user));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new product listing")
    public ResponseEntity<ProductResponse> createProduct(
            @RequestHeader("X-Auth-Token") String token,
            @Valid @RequestBody ProductRequest request) {
        User user = authService.validateToken(token);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.createProduct(request, user));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a product (full update)")
    public ResponseEntity<ProductResponse> updateProduct(
            @RequestHeader("X-Auth-Token") String token,
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        User user = authService.validateToken(token);
        return ResponseEntity.ok(productService.updateProduct(id, request, user));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Change product status (ACTIVE / INACTIVE)",
               description = "OUT_OF_STOCK is managed automatically; " +
                             "use PUT /products/{id} to add stock.")
    public ResponseEntity<ProductResponse> updateStatus(
            @RequestHeader("X-Auth-Token") String token,
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateRequest request) {
        User user = authService.validateToken(token);
        return ResponseEntity.ok(productService.updateStatus(id, request.status(), user));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Soft-delete a product (sets status to INACTIVE)")
    public ResponseEntity<Void> deleteProduct(
            @RequestHeader("X-Auth-Token") String token,
            @PathVariable Long id) {
        User user = authService.validateToken(token);
        productService.deleteProduct(id, user);
        return ResponseEntity.noContent().build();
    }
}
