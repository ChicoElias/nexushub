package com.nexushub.android.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.nexushub.android.ui.auth.AuthViewModel
import com.nexushub.android.ui.auth.LoginScreen
import com.nexushub.android.ui.auth.RegisterScreen
import com.nexushub.android.ui.product.CreateProductScreen
import com.nexushub.android.ui.product.ProductDetailScreen
import com.nexushub.android.ui.product.ProductListScreen
import com.nexushub.android.ui.product.ProductViewModel

// ── Route constants ───────────────────────────────────────────────────────────

object Routes {
    const val LOGIN          = "login"
    const val REGISTER       = "register"
    const val PRODUCT_LIST   = "products"
    const val PRODUCT_DETAIL = "products/{productId}"
    const val CREATE_PRODUCT = "products/create"

    fun productDetail(id: Long) = "products/$id"
}

// ── Nav graph ─────────────────────────────────────────────────────────────────

@Composable
fun NexusHubNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    productViewModel: ProductViewModel,
    startDestination: String,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(Routes.PRODUCT_LIST) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Routes.REGISTER)
                }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                viewModel = authViewModel,
                onRegisterSuccess = {
                    navController.navigate(Routes.PRODUCT_LIST) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.PRODUCT_LIST) {
            ProductListScreen(
                viewModel = productViewModel,
                onProductClick = { id ->
                    navController.navigate(Routes.productDetail(id))
                },
                onCreateClick = {
                    navController.navigate(Routes.CREATE_PRODUCT)
                }
            )
        }

        composable(
            route = Routes.PRODUCT_DETAIL,
            arguments = listOf(navArgument("productId") { type = NavType.LongType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getLong("productId") ?: return@composable
            ProductDetailScreen(
                productId = productId,
                viewModel = productViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.CREATE_PRODUCT) {
            CreateProductScreen(
                viewModel = productViewModel,
                onProductCreated = {
                    navController.popBackStack()
                    productViewModel.loadProducts(refresh = true)
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
