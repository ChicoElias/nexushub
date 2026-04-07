package com.nexushub.android.data.model

// ── Auth models ───────────────────────────────────────────────────────────────

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class AuthResponse(
    val userId: Long,
    val name: String,
    val email: String,
    val token: String
)

// ── Product models ────────────────────────────────────────────────────────────

data class ProductRequest(
    val name: String,
    val description: String,
    val price: Double,
    val stock: Int,
    val category: String?,
    val imageUrl: String?
)

data class ProductResponse(
    val id: Long,
    val name: String,
    val description: String?,
    val price: Double,
    val stock: Int,
    val category: String?,
    val imageUrl: String?,
    val sellerId: Long,
    val sellerName: String,
    /** Mirrors com.nexushub.entity.ProductStatus: ACTIVE | INACTIVE | OUT_OF_STOCK */
    val status: String,
    val available: Boolean,
    val lowStock: Boolean,
    val createdAt: String?,
    val updatedAt: String?
)

data class PagedProductResponse(
    val content: List<ProductResponse>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
    val last: Boolean
)

// ── Error model ───────────────────────────────────────────────────────────────

data class ApiError(
    val timestamp: String?,
    val status: Int,
    val error: String?,
    val message: String
)
