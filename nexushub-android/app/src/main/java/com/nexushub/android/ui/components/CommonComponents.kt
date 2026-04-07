package com.nexushub.android.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.nexushub.android.data.model.ProductResponse
import java.text.NumberFormat
import java.util.Locale

// ── Product Card ──────────────────────────────────────────────────────────────

@Composable
fun ProductCard(
    product: ProductResponse,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val priceFormatter = NumberFormat.getCurrencyInstance(Locale.US)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box {
                SubcomposeAsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                    contentScale = ContentScale.Crop,
                    loading = {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        }
                    },
                    error = {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.BrokenImage,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outlineVariant,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                )

                // Status badge overlay
                when (product.status) {
                    "OUT_OF_STOCK" -> StatusBadge(
                        label = "Out of Stock",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp)
                    )
                    else -> if (product.lowStock) {
                        StatusBadge(
                            label = "Low Stock",
                            color = Color(0xFFF97316), // orange-500
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(6.dp)
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                product.category?.let { cat ->
                    Text(
                        text = cat.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                }

                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = priceFormatter.format(product.price),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )

                    val stockColor = when {
                        product.stock == 0 -> MaterialTheme.colorScheme.error
                        product.lowStock   -> Color(0xFFF97316)
                        else               -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                    Text(
                        text = if (product.stock == 0) "Out of stock" else "${product.stock} left",
                        style = MaterialTheme.typography.bodySmall,
                        color = stockColor
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "by ${product.sellerName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ── Status Badge ──────────────────────────────────────────────────────────────

@Composable
private fun StatusBadge(label: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(4.dp),
        color = color.copy(alpha = 0.9f)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

// ── Loading / Error / Empty states ───────────────────────────────────────────

@Composable
fun LoadingState(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.Warning,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Icon(Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Retry")
        }
    }
}

@Composable
fun EmptyState(message: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
