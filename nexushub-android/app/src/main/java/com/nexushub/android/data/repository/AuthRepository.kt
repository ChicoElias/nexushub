package com.nexushub.android.data.repository

import com.nexushub.android.data.api.NexusHubApiService
import com.nexushub.android.data.model.AuthResponse
import com.nexushub.android.data.model.LoginRequest
import com.nexushub.android.data.model.RegisterRequest
import com.nexushub.android.util.NetworkResult
import com.nexushub.android.util.safeApiCall

class AuthRepository(private val apiService: NexusHubApiService) {

    suspend fun register(name: String, email: String, password: String): NetworkResult<AuthResponse> {
        return safeApiCall { apiService.register(RegisterRequest(name, email, password)) }
    }

    suspend fun login(email: String, password: String): NetworkResult<AuthResponse> {
        return safeApiCall { apiService.login(LoginRequest(email, password)) }
    }
}
