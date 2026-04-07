package com.nexushub.android.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexushub.android.data.model.AuthResponse
import com.nexushub.android.data.repository.AuthRepository
import com.nexushub.android.util.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val authResponse: AuthResponse? = null,
    val error: String? = null
)

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Email and password are required")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            when (val result = repository.login(email.trim(), password)) {
                is NetworkResult.Success -> {
                    _uiState.value = AuthUiState(authResponse = result.data)
                }
                is NetworkResult.Error -> {
                    _uiState.value = AuthUiState(error = result.message)
                }
                is NetworkResult.Loading -> Unit
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "All fields are required")
            return
        }
        if (password.length < 6) {
            _uiState.value = _uiState.value.copy(error = "Password must be at least 6 characters")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            when (val result = repository.register(name.trim(), email.trim(), password)) {
                is NetworkResult.Success -> {
                    _uiState.value = AuthUiState(authResponse = result.data)
                }
                is NetworkResult.Error -> {
                    _uiState.value = AuthUiState(error = result.message)
                }
                is NetworkResult.Loading -> Unit
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
