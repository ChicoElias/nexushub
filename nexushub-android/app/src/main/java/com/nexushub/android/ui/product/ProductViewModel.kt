package com.nexushub.android.ui.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexushub.android.data.model.ProductRequest
import com.nexushub.android.data.model.ProductResponse
import com.nexushub.android.data.repository.ProductRepository
import com.nexushub.android.util.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ── UI State definitions ──────────────────────────────────────────────────────

data class ProductListUiState(
    val isLoading: Boolean = false,
    val products: List<ProductResponse> = emptyList(),
    val error: String? = null,
    val hasMore: Boolean = true,
    val currentPage: Int = 0
)

data class ProductDetailUiState(
    val isLoading: Boolean = false,
    val product: ProductResponse? = null,
    val error: String? = null
)

data class ProductFormUiState(
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null
)

// ── ViewModel ─────────────────────────────────────────────────────────────────

class ProductViewModel(private val repository: ProductRepository) : ViewModel() {

    private val _listState = MutableStateFlow(ProductListUiState())
    val listState: StateFlow<ProductListUiState> = _listState.asStateFlow()

    private val _detailState = MutableStateFlow(ProductDetailUiState())
    val detailState: StateFlow<ProductDetailUiState> = _detailState.asStateFlow()

    private val _formState = MutableStateFlow(ProductFormUiState())
    val formState: StateFlow<ProductFormUiState> = _formState.asStateFlow()

    // ── Product list ──────────────────────────────────────────────────

    fun loadProducts(refresh: Boolean = false) {
        val page = if (refresh) 0 else _listState.value.currentPage
        if (!refresh && !_listState.value.hasMore) return

        viewModelScope.launch {
            _listState.value = _listState.value.copy(isLoading = true, error = null)

            when (val result = repository.getProducts(page)) {
                is NetworkResult.Success -> {
                    val paged = result.data
                    val existing = if (refresh) emptyList() else _listState.value.products
                    _listState.value = ProductListUiState(
                        isLoading = false,
                        products = existing + paged.content,
                        hasMore = !paged.last,
                        currentPage = paged.page + 1
                    )
                }
                is NetworkResult.Error -> {
                    _listState.value = _listState.value.copy(isLoading = false, error = result.message)
                }
                is NetworkResult.Loading -> Unit
            }
        }
    }

    fun searchProducts(query: String) {
        viewModelScope.launch {
            _listState.value = ProductListUiState(isLoading = true)
            when (val result = repository.searchProducts(query)) {
                is NetworkResult.Success -> {
                    _listState.value = ProductListUiState(
                        products = result.data,
                        hasMore = false
                    )
                }
                is NetworkResult.Error -> {
                    _listState.value = ProductListUiState(error = result.message)
                }
                is NetworkResult.Loading -> Unit
            }
        }
    }

    // ── Product detail ────────────────────────────────────────────────

    fun loadProductDetail(id: Long) {
        viewModelScope.launch {
            _detailState.value = ProductDetailUiState(isLoading = true)
            when (val result = repository.getProductById(id)) {
                is NetworkResult.Success -> {
                    _detailState.value = ProductDetailUiState(product = result.data)
                }
                is NetworkResult.Error -> {
                    _detailState.value = ProductDetailUiState(error = result.message)
                }
                is NetworkResult.Loading -> Unit
            }
        }
    }

    // ── Create product ────────────────────────────────────────────────

    fun createProduct(token: String, request: ProductRequest) {
        viewModelScope.launch {
            _formState.value = ProductFormUiState(isLoading = true)
            when (val result = repository.createProduct(token, request)) {
                is NetworkResult.Success -> {
                    _formState.value = ProductFormUiState(success = true)
                }
                is NetworkResult.Error -> {
                    _formState.value = ProductFormUiState(error = result.message)
                }
                is NetworkResult.Loading -> Unit
            }
        }
    }

    fun resetFormState() {
        _formState.value = ProductFormUiState()
    }

    fun clearListError() {
        _listState.value = _listState.value.copy(error = null)
    }
}
