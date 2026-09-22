package com.campuseats.ui.vendor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.campuseats.data.local.entity.OrderStatus
import com.campuseats.navigation.NavRoutes
import com.campuseats.security.UserRole
import com.campuseats.ui.components.CampusEatsBottomBar
import com.campuseats.ui.components.CampusEatsTopBar
import com.campuseats.ui.components.RoleIndicatorCard
import com.campuseats.ui.components.StatusBadge
import com.campuseats.utils.CurrencyFormatter

@Composable
fun VendorDashboardScreen(
    viewModel: VendorViewModel,
    onNavigateToFoodManagement: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onLogout: () -> Unit
) {
    val currentVendorId by viewModel.currentVendorId.collectAsState()
    val allVendors by viewModel.allVendors.collectAsState()
    val vendor by viewModel.vendorDetails.collectAsState(initial = null)
    val foodItems by viewModel.foodItems.collectAsState()
    val orders by viewModel.orders.collectAsState()

    // Key metrics required for Vendor Dashboard
    val activeFoodItemsCount = foodItems.count { it.isAvailable }
    val totalFoodItemsCount = foodItems.size
    val pendingOrdersCount = orders.count { it.status == OrderStatus.PLACED }
    val preparingOrdersCount = orders.count { it.status == OrderStatus.PREPARING }
    val completedOrdersCount = orders.count { it.status == OrderStatus.COLLECTED }
    val completedOrdersRevenue = orders
        .filter { it.status == OrderStatus.COLLECTED }
        .sumOf { it.totalAmount }

    val recentActionableOrders = orders.filter {
        it.status == OrderStatus.PLACED || it.status == OrderStatus.ACCEPTED || it.status == OrderStatus.PREPARING
    }.take(3)

    Scaffold(
        topBar = {
            CampusEatsTopBar(
                title = "Vendor Portal",
                onLogoutClick = onLogout
            )
        },
        bottomBar = {
            CampusEatsBottomBar(
                role = UserRole.VENDOR,
                currentRoute = NavRoutes.VendorDashboard.route,
                onNavigate = { route ->
                    when (route) {
                        NavRoutes.FoodManagement.route -> onNavigateToFoodManagement()
                        NavRoutes.VendorOrders.route -> onNavigateToOrders()
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Vendor Info Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "STALL DASHBOARD",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            RoleIndicatorCard(role = UserRole.VENDOR)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = vendor?.name ?: "Campus Stall",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = vendor?.campusLocation ?: "Student Center",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        vendor?.openingHours?.let { hours ->
                            Text(
                                text = "Hours: $hours",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Optional Stall Switcher if multiple stalls exist
            if (allVendors.size > 1) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Switch Stall",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold
                        )
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(allVendors) { v ->
                                FilterChip(
                                    selected = v.id == currentVendorId,
                                    onClick = { viewModel.selectVendor(v.id) },
                                    label = { Text(v.name) },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Store,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Stat Cards Grid: Exact metrics specified
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = "Active Food Items",
                        value = "$activeFoodItemsCount / $totalFoodItemsCount",
                        icon = Icons.Default.Fastfood,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Pending Orders",
                        value = "$pendingOrdersCount",
                        icon = Icons.Default.HourglassTop,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = "Orders Preparing",
                        value = "$preparingOrdersCount",
                        icon = Icons.Default.Restaurant,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Completed Orders",
                        value = "$completedOrdersCount",
                        icon = Icons.Default.CheckCircle,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                MetricCard(
                    title = "Total Sales from Completed Orders",
                    value = CurrencyFormatter.format(completedOrdersRevenue),
                    icon = Icons.Default.Payments,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Quick Nav Links
            item {
                Text(
                    text = "Vendor Management Operations",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                ActionNavCard(
                    title = "Menu & Food Items",
                    subtitle = "Add, edit, or toggle availability of dishes ($activeFoodItemsCount active)",
                    icon = Icons.Default.Fastfood,
                    onClick = onNavigateToFoodManagement
                )
            }

            item {
                ActionNavCard(
                    title = "Live Incoming Orders",
                    subtitle = "Accept, prepare, and complete customer orders ($pendingOrdersCount pending)",
                    icon = Icons.Default.Receipt,
                    onClick = onNavigateToOrders
                )
            }

            // Actionable Incoming Orders Shortcut
            if (recentActionableOrders.isNotEmpty()) {
                item {
                    Text(
                        text = "Orders Requiring Kitchen Action",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(recentActionableOrders) { order ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = order.orderNumber,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                StatusBadge(status = order.status)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Student: ${order.studentName}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Total: ${CurrencyFormatter.format(order.totalAmount)}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                when (order.status) {
                                    OrderStatus.PLACED -> {
                                        Button(
                                            onClick = { viewModel.updateOrderStatus(order.id, OrderStatus.ACCEPTED) },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("Accept", fontSize = 12.sp)
                                        }
                                        OutlinedButton(
                                            onClick = { viewModel.updateOrderStatus(order.id, OrderStatus.REJECTED) },
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("Reject", fontSize = 12.sp)
                                        }
                                    }
                                    OrderStatus.ACCEPTED -> {
                                        Button(
                                            onClick = { viewModel.updateOrderStatus(order.id, OrderStatus.PREPARING) },
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("Start Preparation", fontSize = 12.sp)
                                        }
                                    }
                                    OrderStatus.PREPARING -> {
                                        Button(
                                            onClick = { viewModel.updateOrderStatus(order.id, OrderStatus.READY) },
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("Mark as Ready", fontSize = 12.sp)
                                        }
                                    }
                                    else -> Unit
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ActionNavCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.size(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
