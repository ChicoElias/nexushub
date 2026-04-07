package com.nexushub.android.data.api

import com.nexushub.android.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface NexusHubApiService {

    // ── Auth ──────────────────────────────────────────────────────────

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    // ── Products ──────────────────────────────────────────────────────

    @GET("products")
    suspend fun getProducts(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<PagedProductResponse>

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id: Long): Response<ProductResponse>

    @GET("products/search")
    suspend fun searchProducts(@Query("query") query: String): Response<List<ProductResponse>>

    @GET("products/category/{category}")
    suspend fun getProductsByCategory(
        @Path("category") category: String
    ): Response<List<ProductResponse>>

    @GET("products/my")
    suspend fun getMyProducts(
        @Header("X-Auth-Token") token: String
    ): Response<List<ProductResponse>>

    @POST("products")
    suspend fun createProduct(
        @Header("X-Auth-Token") token: String,
        @Body request: ProductRequest
    ): Response<ProductResponse>

    @PUT("products/{id}")
    suspend fun updateProduct(
        @Header("X-Auth-Token") token: String,
        @Path("id") id: Long,
        @Body request: ProductRequest
    ): Response<ProductResponse>

    @DELETE("products/{id}")
    suspend fun deleteProduct(
        @Header("X-Auth-Token") token: String,
        @Path("id") id: Long
    ): Response<Unit>
}
