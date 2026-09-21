package com.campuseats.ui.student

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.campuseats.data.local.entity.OrderEntity
import com.campuseats.navigation.NavRoutes
import com.campuseats.security.UserRole
import com.campuseats.ui.components.CampusEatsBottomBar
import com.campuseats.ui.components.CampusEatsTopBar
import com.campuseats.ui.components.EmptyStateView
import com.campuseats.ui.components.StatusBadge
import com.campuseats.utils.CurrencyFormatter
import com.campuseats.utils.DateUtils

@Composable
fun OrderHistoryScreen(
    viewModel: StudentViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToOrderStatus: (String) -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToVendors: () -> Unit,
    onNavigateToCart: () -> Unit
) {
    val orders by viewModel.getStudentOrders().collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            CampusEatsTopBar(
                title = "Order History",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        },
        bottomBar = {
            CampusEatsBottomBar(
                role = UserRole.STUDENT,
                currentRoute = NavRoutes.OrderHistory.route,
                onNavigate = { route ->
                    when (route) {
                        NavRoutes.StudentHome.route -> onNavigateToHome()
                        NavRoutes.Vendors.route -> onNavigateToVendors()
                        NavRoutes.Cart.route -> onNavigateToCart()
                    }
                }
            )
        }
    ) { padding ->
        if (orders.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.History,
                title = "No Orders Placed Yet",
                message = "Your active and past campus food orders will appear here.",
                actionButtonText = "Find Food Now",
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
                items(orders) { order ->
                    OrderHistoryCard(
                        order = order,
                        onClick = { onNavigateToOrderStatus(order.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun OrderHistoryCard(
    order: OrderEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
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
                text = order.vendorName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = DateUtils.formatDateTime(order.createdAt),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total: ${CurrencyFormatter.format(order.totalAmount)}",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "PIN: ${order.pickupPin}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
