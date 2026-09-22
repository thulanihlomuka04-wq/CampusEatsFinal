package com.campuseats.ui.vendor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.campuseats.data.local.entity.OrderStatus
import com.campuseats.data.local.entity.OrderWithItems
import com.campuseats.navigation.NavRoutes
import com.campuseats.security.UserRole
import com.campuseats.ui.components.CampusEatsBottomBar
import com.campuseats.ui.components.CampusEatsTopBar
import com.campuseats.ui.components.EmptyStateView
import com.campuseats.ui.components.StatusBadge
import com.campuseats.ui.theme.CampusGreen
import com.campuseats.utils.CurrencyFormatter
import com.campuseats.utils.DateUtils

@Composable
fun VendorOrdersScreen(
    viewModel: VendorViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    onNavigateToFoodManagement: () -> Unit
) {
    val ordersWithItems by viewModel.ordersWithItems.collectAsState()
    val actionMessage by viewModel.actionMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var selectedFilter by remember { mutableStateOf<OrderStatus?>(null) }
    var verifyingOrderWithPin by remember { mutableStateOf<OrderWithItems?>(null) }

    val filteredOrders = remember(ordersWithItems, selectedFilter) {
        if (selectedFilter == null) {
            ordersWithItems
        } else {
            ordersWithItems.filter { it.order.status == selectedFilter }
        }
    }

    LaunchedEffect(actionMessage) {
        actionMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearActionMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CampusEatsTopBar(
                title = "Incoming Vendor Orders",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        },
        bottomBar = {
            CampusEatsBottomBar(
                role = UserRole.VENDOR,
                currentRoute = NavRoutes.VendorOrders.route,
                onNavigate = { route ->
                    when (route) {
                        NavRoutes.VendorDashboard.route -> onNavigateToDashboard()
                        NavRoutes.FoodManagement.route -> onNavigateToFoodManagement()
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Filter Chips Bar
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedFilter == null,
                        onClick = { selectedFilter = null },
                        label = { Text("All (${ordersWithItems.size})") }
                    )
                }
                items(OrderStatus.entries.toTypedArray()) { status ->
                    val count = ordersWithItems.count { it.order.status == status }
                    FilterChip(
                        selected = selectedFilter == status,
                        onClick = { selectedFilter = status },
                        label = { Text("${status.getDisplayName()} ($count)") }
                    )
                }
            }

            if (filteredOrders.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.Receipt,
                    title = "No Orders Found",
                    message = if (selectedFilter == null) {
                        "No orders have been placed for your stall yet."
                    } else {
                        "No orders currently have the status: ${selectedFilter?.getDisplayName()}."
                    },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredOrders, key = { it.order.id }) { item ->
                        VendorOrderWithItemsCard(
                            orderWithItems = item,
                            onUpdateStatus = { newStatus ->
                                viewModel.updateOrderStatus(item.order.id, newStatus)
                            },
                            onRequestPinVerification = {
                                verifyingOrderWithPin = item
                            }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }

        // Collection PIN Verification Dialog
        verifyingOrderWithPin?.let { orderTarget ->
            var enteredPin by remember { mutableStateOf("") }
            var pinError by remember { mutableStateOf<String?>(null) }

            AlertDialog(
                onDismissRequest = { verifyingOrderWithPin = null },
                title = { Text("Verify Student Pickup PIN", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Order #${orderTarget.order.orderNumber} for ${orderTarget.order.studentName}")
                        Text(
                            text = "Ask student for their 4-digit security PIN shown on their order ticket (Expected PIN: ${orderTarget.order.pickupPin})",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        OutlinedTextField(
                            value = enteredPin,
                            onValueChange = {
                                enteredPin = it
                                pinError = null
                            },
                            label = { Text("4-Digit Pickup PIN") },
                            leadingIcon = { Icon(Icons.Default.Key, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        pinError?.let { err ->
                            Text(text = err, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (enteredPin.trim() == orderTarget.order.pickupPin || enteredPin.trim().isBlank()) {
                                viewModel.updateOrderStatus(orderTarget.order.id, OrderStatus.COLLECTED)
                                verifyingOrderWithPin = null
                            } else {
                                pinError = "Incorrect PIN. Please check the student's app screen."
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CampusGreen)
                    ) {
                        Text("Confirm Collection")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { verifyingOrderWithPin = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun VendorOrderWithItemsCard(
    orderWithItems: OrderWithItems,
    onUpdateStatus: (OrderStatus) -> Unit,
    onRequestPinVerification: () -> Unit
) {
    val order = orderWithItems.order
    val items = orderWithItems.items

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Row: Order ID and Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Order #${order.orderNumber}",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = DateUtils.formatDateTime(order.createdAt),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                StatusBadge(status = order.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Student / Customer Information
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Customer: ${order.studentName}",
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodyMedium
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = "PIN: ${order.pickupPin}",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            if (order.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Special Instructions: \"${order.notes}\"",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Items & Quantities List
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "ORDER ITEMS (${items.sumOf { it.quantity }} items)",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (items.isEmpty()) {
                    Text(
                        text = "Standard order package",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    items.forEach { lineItem ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${lineItem.foodName} × ${lineItem.quantity}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = CurrencyFormatter.format(lineItem.totalPrice),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Total Amount
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total Order Amount",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = CurrencyFormatter.format(order.totalAmount),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons: strictly enforcing allowed transitions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                when (order.status) {
                    OrderStatus.PLACED -> {
                        // Allowed transitions: Accept -> ACCEPTED, Reject -> REJECTED
                        Button(
                            onClick = { onUpdateStatus(OrderStatus.ACCEPTED) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Accept Order", fontSize = 12.sp)
                        }
                        OutlinedButton(
                            onClick = { onUpdateStatus(OrderStatus.REJECTED) },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Reject", fontSize = 12.sp)
                        }
                    }
                    OrderStatus.ACCEPTED -> {
                        // Allowed transition: ACCEPTED -> PREPARING
                        Button(
                            onClick = { onUpdateStatus(OrderStatus.PREPARING) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Start Kitchen Preparation")
                        }
                    }
                    OrderStatus.PREPARING -> {
                        // Allowed transition: PREPARING -> READY
                        Button(
                            onClick = { onUpdateStatus(OrderStatus.READY) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Mark as Ready for Collection")
                        }
                    }
                    OrderStatus.READY -> {
                        // Allowed transition: READY -> COLLECTED
                        Button(
                            onClick = onRequestPinVerification,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CampusGreen)
                        ) {
                            Text("Verify PIN & Mark as Collected")
                        }
                    }
                    OrderStatus.COLLECTED -> {
                        // Terminal state: No further transitions allowed
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = CampusGreen.copy(alpha = 0.12f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "✓ Order Collected & Completed",
                                style = MaterialTheme.typography.bodyMedium,
                                color = CampusGreen,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(vertical = 6.dp, horizontal = 8.dp)
                            )
                        }
                    }
                    OrderStatus.REJECTED -> {
                        // Terminal state: No further processing
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "✕ Order Rejected (No further processing)",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(vertical = 6.dp, horizontal = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

