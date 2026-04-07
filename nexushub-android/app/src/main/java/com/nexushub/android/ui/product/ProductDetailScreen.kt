package com.nexushub.android.ui.product

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.nexushub.android.ui.components.ErrorState
import com.nexushub.android.ui.components.LoadingState
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: Long,
    viewModel: ProductViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.detailState.collectAsState()
    val priceFormatter = NumberFormat.getCurrencyInstance(Locale.US)

    LaunchedEffect(productId) {
        viewModel.loadProductDetail(productId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.product?.name ?: "Product Detail") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        when {
            uiState.isLoading -> LoadingState(modifier = Modifier.padding(padding))

            uiState.error != null -> ErrorState(
                message = uiState.error!!,
                onRetry = { viewModel.loadProductDetail(productId) },
                modifier = Modifier.padding(padding)
            )

            uiState.product != null -> {
                val product = uiState.product!!

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Product image
                    AsyncImage(
                        model = product.imageUrl,
                        contentDescription = product.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp),
                        contentScale = ContentScale.Crop
                    )

                    Column(modifier = Modifier.padding(20.dp)) {
                        // Category badge
                        product.category?.let { cat ->
                            SuggestionChip(
                                onClick = {},
                                label = { Text(cat, style = MaterialTheme.typography.labelMedium) },
                                icon = { Icon(Icons.Default.Category, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // Product name
                        Text(
                            text = product.name,
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Price
                        Text(
                            text = priceFormatter.format(product.price),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 32.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Stock & Seller row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            InfoChip(
                                icon = { Icon(Icons.Default.Inventory, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                label = "${product.stock} in stock"
                            )
                            InfoChip(
                                icon = { Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                label = "by ${product.sellerName}"
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Description
                        if (!product.description.isNullOrBlank()) {
                            Text(
                                text = "Description",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = product.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 22.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Add to cart placeholder button (extensible)
                        Button(
                            onClick = { /* TODO: implement cart */ },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            enabled = product.stock > 0
                        ) {
                            Text(
                                text = if (product.stock > 0) "Add to Cart" else "Out of Stock",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoChip(icon: @Composable () -> Unit, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        icon()
        Text(label, style = MaterialTheme.typography.bodyMedium)
    }
}
