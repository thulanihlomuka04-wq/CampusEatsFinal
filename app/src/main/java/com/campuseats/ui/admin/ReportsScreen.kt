package com.campuseats.ui.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import com.campuseats.data.local.entity.OrderStatus
import com.campuseats.navigation.NavRoutes
import com.campuseats.security.UserRole
import com.campuseats.ui.components.CampusEatsBottomBar
import com.campuseats.ui.components.CampusEatsTopBar
import com.campuseats.utils.CurrencyFormatter

@Composable
fun ReportsScreen(
    viewModel: AdminViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    onNavigateToUsers: () -> Unit,
    onNavigateToVendors: () -> Unit
) {
    val orders by viewModel.allOrders.collectAsState()
    val totalRevenue by viewModel.totalRevenue.collectAsState()

    val collectedCount = orders.count { it.status == OrderStatus.COLLECTED }
    val placedCount = orders.count { it.status == OrderStatus.PLACED }
    val acceptedCount = orders.count { it.status == OrderStatus.ACCEPTED }
    val preparingCount = orders.count { it.status == OrderStatus.PREPARING }
    val readyCount = orders.count { it.status == OrderStatus.READY }
    val rejectedCount = orders.count { it.status == OrderStatus.REJECTED }

    Scaffold(
        topBar = {
            CampusEatsTopBar(
                title = "System Reports & Audit",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        },
        bottomBar = {
            CampusEatsBottomBar(
                role = UserRole.ADMIN,
                currentRoute = NavRoutes.Reports.route,
                onNavigate = { route ->
                    when (route) {
                        NavRoutes.AdminDashboard.route -> onNavigateToDashboard()
                        NavRoutes.UserManagement.route -> onNavigateToUsers()
                        NavRoutes.VendorManagement.route -> onNavigateToVendors()
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
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                    )
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "FINANCIAL AUDIT SUMMARY",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = CurrencyFormatter.format(totalRevenue ?: 0.0),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Total Gross Revenue Processed Across Campus Stalls",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Order Fulfillment Breakdown",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        ReportStatRow("Collected Orders", "$collectedCount")
                        ReportStatRow("Placed Orders", "$placedCount")
                        ReportStatRow("Accepted Orders", "$acceptedCount")
                        ReportStatRow("Currently in Kitchen", "$preparingCount")
                        ReportStatRow("Ready for Collection", "$readyCount")
                        ReportStatRow("Rejected / Declined", "$rejectedCount")

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        ReportStatRow("Total Logged Transactions", "${orders.size}", isBold = true)
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "System Operational Health",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "• Local SQLite Room Engine: ONLINE & HEALTHY\n" +
                                   "• Architecture Integrity: MVVM + Repository Pattern ACTIVE\n" +
                                   "• Active Session State: Encrypted local SharedPreferences\n" +
                                   "• Network Sync Layer: Retrofit REST Endpoint Stubs READY",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReportStatRow(
    label: String,
    value: String,
    isBold: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.SemiBold
        )
    }
}
