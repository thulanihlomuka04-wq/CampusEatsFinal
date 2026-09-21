package com.campuseats.ui.vendor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.campuseats.data.local.entity.OrderEntity
import com.campuseats.data.local.entity.OrderStatus
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
    val orders by viewModel.orders.collectAsState()
    var selectedFilter by remember { mutableStateOf<OrderStatus?>(null) }

    val filteredOrders = remember(orders, selectedFilter) {
        if (selectedFilter == null) orders else orders.filter { it.status == selectedFilter }
    }

    Scaffold(
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
                        label = { Text("All (${orders.size})") }
                    )
                }
                items(OrderStatus.entries.toTypedArray()) { status ->
                    val count = orders.count { it.status == status }
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
                    title = "No Orders",
                    message = "No incoming orders in this filter category."
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredOrders) { order ->
                        VendorOrderCard(
                            order = order,
                            onUpdateStatus = { newStatus ->
                                viewModel.updateOrderStatus(order.id, newStatus)
                            }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun VendorOrderCard(
    order: OrderEntity,
    onUpdateStatus: (OrderStatus) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = order.orderNumber,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                StatusBadge(status = order.status)
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Student: ${order.studentName}",
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Placed: ${DateUtils.formatDateTime(order.createdAt)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (order.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Notes: \"${order.notes}\"",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total: ${CurrencyFormatter.format(order.totalAmount)}",
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Pickup PIN: ${order.pickupPin}",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action buttons to advance status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                when (order.status) {
                    OrderStatus.PLACED -> {
                        Button(
                            onClick = { onUpdateStatus(OrderStatus.ACCEPTED) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Accept Order", fontSize = 12.sp)
                        }
                        OutlinedButton(
                            onClick = { onUpdateStatus(OrderStatus.REJECTED) },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Reject", fontSize = 12.sp)
                        }
                    }
                    OrderStatus.ACCEPTED -> {
                        Button(
                            onClick = { onUpdateStatus(OrderStatus.PREPARING) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Start Kitchen Preparation")
                        }
                    }
                    OrderStatus.PREPARING -> {
                        Button(
                            onClick = { onUpdateStatus(OrderStatus.READY) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Mark as Ready for Collection")
                        }
                    }
                    OrderStatus.READY -> {
                        Button(
                            onClick = { onUpdateStatus(OrderStatus.COLLECTED) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CampusGreen)
                        ) {
                            Text("Verify PIN & Hand Over Order")
                        }
                    }
                    OrderStatus.COLLECTED -> {
                        Text(
                            text = "Order Successfully Collected",
                            style = MaterialTheme.typography.bodySmall,
                            color = CampusGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    OrderStatus.REJECTED -> {
                        Text(
                            text = "Order Rejected",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
