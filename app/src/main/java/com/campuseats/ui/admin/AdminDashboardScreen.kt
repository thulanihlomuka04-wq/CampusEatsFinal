package com.campuseats.ui.admin

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.campuseats.navigation.NavRoutes
import com.campuseats.security.UserRole
import com.campuseats.ui.components.CampusEatsBottomBar
import com.campuseats.ui.components.CampusEatsTopBar
import com.campuseats.ui.components.RoleIndicatorCard
import com.campuseats.utils.CurrencyFormatter

@Composable
fun AdminDashboardScreen(
    viewModel: AdminViewModel,
    onNavigateToUserManagement: () -> Unit,
    onNavigateToVendorManagement: () -> Unit,
    onNavigateToReports: () -> Unit,
    onLogout: () -> Unit
) {
    val adminName by viewModel.adminName.collectAsState()
    val userCount by viewModel.userCount.collectAsState()
    val vendorCount by viewModel.vendorCount.collectAsState()
    val orderCount by viewModel.orderCount.collectAsState()
    val totalRevenue by viewModel.totalRevenue.collectAsState()

    Scaffold(
        topBar = {
            CampusEatsTopBar(
                title = "Administrator Control Panel",
                onLogoutClick = onLogout
            )
        },
        bottomBar = {
            CampusEatsBottomBar(
                role = UserRole.ADMIN,
                currentRoute = NavRoutes.AdminDashboard.route,
                onNavigate = { route ->
                    when (route) {
                        NavRoutes.UserManagement.route -> onNavigateToUserManagement()
                        NavRoutes.VendorManagement.route -> onNavigateToVendorManagement()
                        NavRoutes.Reports.route -> onNavigateToReports()
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
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "SYSTEM ADMINISTRATOR",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            RoleIndicatorCard(role = UserRole.ADMIN)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = adminName ?: "Campus Admin",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Centralized University Management & Audit",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Stat Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AdminMetricTile(
                        title = "Registered Users",
                        value = "$userCount",
                        icon = Icons.Default.People,
                        modifier = Modifier.weight(1f)
                    )
                    AdminMetricTile(
                        title = "Campus Vendors",
                        value = "$vendorCount",
                        icon = Icons.Default.Store,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AdminMetricTile(
                        title = "Total Orders",
                        value = "$orderCount",
                        icon = Icons.Default.Receipt,
                        modifier = Modifier.weight(1f)
                    )
                    AdminMetricTile(
                        title = "Platform Sales",
                        value = CurrencyFormatter.format(totalRevenue ?: 0.0),
                        icon = Icons.Default.AttachMoney,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Text(
                    text = "System Administration Actions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                AdminActionTile(
                    title = "User Management",
                    subtitle = "Manage student, vendor, and admin accounts",
                    icon = Icons.Default.People,
                    onClick = onNavigateToUserManagement
                )
            }

            item {
                AdminActionTile(
                    title = "Vendor Management",
                    subtitle = "Register campus food stalls, update locations, audit vendors",
                    icon = Icons.Default.Store,
                    onClick = onNavigateToVendorManagement
                )
            }

            item {
                AdminActionTile(
                    title = "Reports & Analytics",
                    subtitle = "Audit food trends, sales performance, and order statuses",
                    icon = Icons.Default.Assessment,
                    onClick = onNavigateToReports
                )
            }
        }
    }
}

@Composable
fun AdminMetricTile(
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
            Spacer(modifier = Modifier.height(8.dp))
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
fun AdminActionTile(
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
            Spacer(modifier = Modifier.width(16.dp))
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
