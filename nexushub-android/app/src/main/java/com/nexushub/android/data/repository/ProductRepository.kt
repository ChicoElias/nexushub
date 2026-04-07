package com.nexushub.android.data.repository

import com.nexushub.android.data.api.NexusHubApiService
import com.nexushub.android.data.model.PagedProductResponse
import com.nexushub.android.data.model.ProductRequest
import com.nexushub.android.data.model.ProductResponse
import com.nexushub.android.util.NetworkResult
import com.nexushub.android.util.safeApiCall

class ProductRepository(private val apiService: NexusHubApiService) {

    suspend fun getProducts(page: Int = 0, size: Int = 20): NetworkResult<PagedProductResponse> {
        return safeApiCall { apiService.getProducts(page, size) }
    }

    suspend fun getProductById(id: Long): NetworkResult<ProductResponse> {
        return safeApiCall { apiService.getProductById(id) }
    }

    suspend fun searchProducts(query: String): NetworkResult<List<ProductResponse>> {
        return safeApiCall { apiService.searchProducts(query) }
    }

    suspend fun getMyProducts(token: String): NetworkResult<List<ProductResponse>> {
        return safeApiCall { apiService.getMyProducts(token) }
    }

    suspend fun createProduct(
        token: String,
        request: ProductRequest
    ): NetworkResult<ProductResponse> {
        return safeApiCall { apiService.createProduct(token, request) }
    }

    suspend fun updateProduct(
        token: String,
        id: Long,
        request: ProductRequest
    ): NetworkResult<ProductResponse> {
        return safeApiCall { apiService.updateProduct(token, id, request) }
    }

    suspend fun deleteProduct(token: String, id: Long): NetworkResult<Unit> {
        return safeApiCall { apiService.deleteProduct(token, id) }
    }
}
