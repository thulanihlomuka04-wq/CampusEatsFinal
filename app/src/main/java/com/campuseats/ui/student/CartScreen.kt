package com.campuseats.ui.student

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RemoveShoppingCart
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.campuseats.data.local.entity.OrderEntity
import com.campuseats.data.repository.CartItem
import com.campuseats.ui.components.CampusEatsTopBar
import com.campuseats.ui.components.EmptyStateView
import com.campuseats.ui.theme.CampusGreen
import com.campuseats.ui.theme.CampusOrange
import com.campuseats.utils.CurrencyFormatter

@Composable
fun CartScreen(
    viewModel: StudentViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToVendors: () -> Unit,
    onOrderPlacedSuccess: (String) -> Unit
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val orderState by viewModel.orderPlacementState.collectAsState()
    var notes by remember { mutableStateOf("") }

    val subtotal = cartItems.sumOf { it.foodItem.price * it.quantity }

    // Order Confirmation Dialog (Requirement: ORDER CONFIRMATION: Show order ID, vendor, items, total, estimated time, pickup PIN)
    orderState.placedOrder?.let { placedOrder ->
        OrderConfirmationDialog(
            order = placedOrder,
            viewModel = viewModel,
            onTrackOrder = { orderId ->
                viewModel.resetOrderPlacementState()
                onOrderPlacedSuccess(orderId)
            },
            onDismiss = {
                viewModel.resetOrderPlacementState()
                onNavigateToVendors()
            }
        )
    }

    Scaffold(
        topBar = {
            CampusEatsTopBar(
                title = "My Food Cart",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack,
                actions = {
                    if (cartItems.isNotEmpty()) {
                        TextButton(onClick = { viewModel.clearCart() }) {
                            Text("Clear", color = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    shadowElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Amount:", style = MaterialTheme.typography.titleMedium)
                            Text(
                                text = CurrencyFormatter.format(subtotal),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        if (orderState.errorMessage != null) {
                            Text(
                                text = orderState.errorMessage!!,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

                        Button(
                            onClick = { viewModel.placeOrder(notes) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !orderState.isLoading
                        ) {
                            if (orderState.isLoading) {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = "Confirm & Place Order",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        if (cartItems.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.RemoveShoppingCart,
                title = "Your Cart is Empty",
                message = "Explore campus food vendors and add delicious meals to your cart!",
                actionButtonText = "Browse Vendors",
                onActionClick = onNavigateToVendors,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(cartItems) { item ->
                    CartItemRow(
                        item = item,
                        onIncrease = { viewModel.updateCartQuantity(item.foodItem.id, item.quantity + 1) },
                        onDecrease = { viewModel.updateCartQuantity(item.foodItem.id, item.quantity - 1) },
                        onRemove = { viewModel.removeFromCart(item.foodItem.id) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Special Preparation Notes (e.g. No onions, extra napkins)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 3
                    )
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Order Summary",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Food Subtotal", style = MaterialTheme.typography.bodyMedium)
                                Text(CurrencyFormatter.format(subtotal), fontWeight = FontWeight.Medium)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Campus Pickup Fee", style = MaterialTheme.typography.bodyMedium)
                                Text("FREE", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemRow(
    item: CartItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.foodItem.name,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = CurrencyFormatter.format(item.foodItem.price),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDecrease, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(16.dp))
                }
                Text(
                    text = "${item.quantity}",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                IconButton(onClick = onIncrease, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(16.dp))
                }
                IconButton(onClick = onRemove, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Default.DeleteOutline,
                        contentDescription = "Remove",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun OrderConfirmationDialog(
    order: OrderEntity,
    viewModel: StudentViewModel,
    onTrackOrder: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val items by viewModel.getOrderItems(order.id).collectAsState(initial = emptyList())

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = { onTrackOrder(order.id) },
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Track Live Order", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Done")
            }
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = CampusGreen,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Order Confirmed!",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Order ID & Vendor
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Order ID",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = order.orderNumber,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Vendor",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = order.vendorName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                HorizontalDivider()

                // Pickup PIN box
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "YOUR PICKUP PIN",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = order.pickupPin,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 6.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Show this PIN at pickup counter",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Estimated time
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        tint = CampusOrange,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Estimated Preparation Time: ~${order.estimatedPickupMinutes} minutes",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }

                HorizontalDivider()

                // Items list
                Text(
                    text = "Items Ordered (${items.size}):",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items.forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${item.quantity}x ${item.foodName}",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = CurrencyFormatter.format(item.subtotal),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                HorizontalDivider()

                // Total
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total Amount:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = CurrencyFormatter.format(order.totalAmount),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    )
}
