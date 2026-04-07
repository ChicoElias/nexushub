package com.nexushub.android.ui.product

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexushub.android.data.model.ProductRequest
import com.nexushub.android.util.SessionManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProductScreen(
    viewModel: ProductViewModel,
    onProductCreated: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.formState.collectAsState()
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    // Collect token reactively — no runBlocking on the main thread
    val token by sessionManager.token.collectAsState(initial = null)

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf<String?>(null) }
    var priceError by remember { mutableStateOf<String?>(null) }
    var stockError by remember { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            viewModel.resetFormState()
            onProductCreated()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { snackbarHostState.showSnackbar(it) }
    }

    fun validate(): Boolean {
        nameError = if (name.isBlank()) "Name is required" else null
        priceError = when {
            price.isBlank() -> "Price is required"
            price.toDoubleOrNull() == null -> "Enter a valid number"
            price.toDouble() <= 0 -> "Price must be greater than 0"
            price.toDouble() > 99999.99 -> "Price cannot exceed \$99,999.99"
            else -> null
        }
        stockError = when {
            stock.isBlank() -> "Stock is required"
            stock.toIntOrNull() == null -> "Enter a whole number"
            stock.toInt() < 0 -> "Stock cannot be negative"
            else -> null
        }
        return nameError == null && priceError == null && stockError == null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Product") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Product Details",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it; nameError = null },
                label = { Text("Product Name *") },
                modifier = Modifier.fillMaxWidth(),
                isError = nameError != null,
                supportingText = nameError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                singleLine = true,
                enabled = !uiState.isLoading
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 6,
                enabled = !uiState.isLoading
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it; priceError = null },
                    label = { Text("Price (USD) *") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = priceError != null,
                    supportingText = priceError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                    singleLine = true,
                    prefix = { Text("$") },
                    enabled = !uiState.isLoading
                )

                OutlinedTextField(
                    value = stock,
                    onValueChange = { stock = it; stockError = null },
                    label = { Text("Stock *") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = stockError != null,
                    supportingText = stockError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                    singleLine = true,
                    enabled = !uiState.isLoading
                )
            }

            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Category") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g. Electronics, Clothing...") },
                singleLine = true,
                enabled = !uiState.isLoading
            )

            OutlinedTextField(
                value = imageUrl,
                onValueChange = { imageUrl = it },
                label = { Text("Image URL (optional)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                singleLine = true,
                enabled = !uiState.isLoading
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (!validate()) return@Button
                    val authToken = token ?: run {
                        // Token not available — session expired
                        return@Button
                    }
                    val request = ProductRequest(
                        name = name.trim(),
                        description = description.trim(),
                        price = price.toDouble(),
                        stock = stock.toInt(),
                        // Use null — not an empty string — when fields are blank
                        category = category.trim().ifBlank { null },
                        imageUrl = imageUrl.trim().ifBlank { null }
                    )
                    viewModel.createProduct(authToken, request)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = !uiState.isLoading && token != null
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Publish Product", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
