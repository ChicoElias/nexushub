package com.nexushub.android.ui.product

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nexushub.android.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    viewModel: ProductViewModel,
    onProductClick: (Long) -> Unit,
    onCreateClick: () -> Unit
) {
    val uiState by viewModel.listState.collectAsState()
    val gridState = rememberLazyGridState()

    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }

    // Initial load
    LaunchedEffect(Unit) {
        viewModel.loadProducts()
    }

    // Infinite scroll: load next page when near the end
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisible = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItems = gridState.layoutInfo.totalItemsCount
            !uiState.isLoading && uiState.hasMore && lastVisible >= totalItems - 4
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) viewModel.loadProducts()
    }

    Scaffold(
        topBar = {
            if (isSearching) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onSearch = {
                        if (it.isNotBlank()) viewModel.searchProducts(it)
                    },
                    active = false,
                    onActiveChange = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Search products...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = {
                            isSearching = false
                            searchQuery = ""
                            viewModel.loadProducts(refresh = true)
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Cancel search")
                        }
                    }
                ) {}
            } else {
                TopAppBar(
                    title = { Text("NexusHub") },
                    actions = {
                        IconButton(onClick = { isSearching = true }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateClick) {
                Icon(Icons.Default.Add, contentDescription = "Add product")
            }
        }
    ) { padding ->
        when {
            uiState.isLoading && uiState.products.isEmpty() -> {
                LoadingState(modifier = Modifier.padding(padding))
            }

            uiState.error != null && uiState.products.isEmpty() -> {
                ErrorState(
                    message = uiState.error!!,
                    onRetry = { viewModel.loadProducts(refresh = true) },
                    modifier = Modifier.padding(padding)
                )
            }

            uiState.products.isEmpty() -> {
                EmptyState(
                    message = "No products found",
                    modifier = Modifier.padding(padding)
                )
            }

            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 160.dp),
                    state = gridState,
                    contentPadding = PaddingValues(
                        start = 16.dp, end = 16.dp,
                        top = padding.calculateTopPadding() + 8.dp,
                        bottom = padding.calculateBottomPadding() + 80.dp
                    ),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = uiState.products,
                        key = { it.id }
                    ) { product ->
                        ProductCard(
                            product = product,
                            onClick = { onProductClick(product.id) }
                        )
                    }

                    if (uiState.isLoading) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = androidx.compose.ui.Alignment.Center
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(28.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
