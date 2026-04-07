package com.nexushub.android.util

/**
 * Sealed class wrapping API results.
 * Avoids scattering try/catch across ViewModels.
 */
sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error(val message: String, val code: Int? = null) : NetworkResult<Nothing>()
    data object Loading : NetworkResult<Nothing>()
}

/**
 * Executes [block] safely and wraps the result in [NetworkResult].
 */
suspend fun <T> safeApiCall(block: suspend () -> retrofit2.Response<T>): NetworkResult<T> {
    return try {
        val response = block()
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                NetworkResult.Success(body)
            } else {
                NetworkResult.Error("Empty response body", response.code())
            }
        } else {
            val errorMessage = response.errorBody()?.string()
                ?.let { parseErrorMessage(it) }
                ?: "Unexpected error (${response.code()})"
            NetworkResult.Error(errorMessage, response.code())
        }
    } catch (e: java.net.SocketTimeoutException) {
        NetworkResult.Error("Connection timed out. Check your network.")
    } catch (e: java.net.UnknownHostException) {
        NetworkResult.Error("Cannot reach server. Check the API base URL.")
    } catch (e: Exception) {
        NetworkResult.Error(e.localizedMessage ?: "Unknown error occurred")
    }
}

private fun parseErrorMessage(json: String): String {
    return try {
        val obj = org.json.JSONObject(json)
        obj.optString("message", "Request failed")
    } catch (e: Exception) {
        "Request failed"
    }
}
