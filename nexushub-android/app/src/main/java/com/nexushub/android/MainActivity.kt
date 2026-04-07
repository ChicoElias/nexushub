package com.nexushub.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.nexushub.android.data.api.RetrofitClient
import com.nexushub.android.data.repository.AuthRepository
import com.nexushub.android.data.repository.ProductRepository
import com.nexushub.android.ui.auth.AuthViewModel
import com.nexushub.android.ui.navigation.NexusHubNavGraph
import com.nexushub.android.ui.navigation.Routes
import com.nexushub.android.ui.product.ProductViewModel
import com.nexushub.android.ui.theme.NexusHubTheme
import com.nexushub.android.util.SessionManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NexusHubTheme {
                NexusHubApp()
            }
        }
    }
}

@Composable
fun NexusHubApp() {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val navController = rememberNavController()

    // Determine start destination based on persisted session
    val startDestination = remember {
        val isLoggedIn = runBlocking { sessionManager.isLoggedIn.first() }
        if (isLoggedIn) Routes.PRODUCT_LIST else Routes.LOGIN
    }

    // Build ViewModels with manual factory (no Hilt to keep it simple)
    val apiService = RetrofitClient.apiService

    val authViewModel: AuthViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                AuthViewModel(AuthRepository(apiService)) as T
        }
    )

    val productViewModel: ProductViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                ProductViewModel(ProductRepository(apiService)) as T
        }
    )

    // Persist session whenever auth succeeds
    val authState by authViewModel.uiState.collectAsState()
    LaunchedEffect(authState.authResponse) {
        authState.authResponse?.let { auth ->
            sessionManager.saveSession(auth.token, auth.userId, auth.name, auth.email)
        }
    }

    NexusHubNavGraph(
        navController = navController,
        authViewModel = authViewModel,
        productViewModel = productViewModel,
        startDestination = startDestination
    )
}
