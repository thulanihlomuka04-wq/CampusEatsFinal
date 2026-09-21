package com.campuseats.ui.admin

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import com.campuseats.data.local.entity.UserEntity
import com.campuseats.navigation.NavRoutes
import com.campuseats.security.UserRole
import com.campuseats.ui.components.CampusEatsBottomBar
import com.campuseats.ui.components.CampusEatsTopBar
import com.campuseats.ui.components.EmptyStateView
import com.campuseats.ui.components.RoleIndicatorCard

@Composable
fun UserManagementScreen(
    viewModel: AdminViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    onNavigateToVendors: () -> Unit,
    onNavigateToReports: () -> Unit
) {
    val users by viewModel.allUsers.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var selectedRoleFilter by remember { mutableStateOf<UserRole?>(null) }

    val filteredUsers = remember(users, selectedRoleFilter) {
        if (selectedRoleFilter == null) users else users.filter { it.role == selectedRoleFilter }
    }

    LaunchedEffect(statusMessage) {
        statusMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearStatusMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CampusEatsTopBar(
                title = "User Management",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        },
        bottomBar = {
            CampusEatsBottomBar(
                role = UserRole.ADMIN,
                currentRoute = NavRoutes.UserManagement.route,
                onNavigate = { route ->
                    when (route) {
                        NavRoutes.AdminDashboard.route -> onNavigateToDashboard()
                        NavRoutes.VendorManagement.route -> onNavigateToVendors()
                        NavRoutes.Reports.route -> onNavigateToReports()
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
                        selected = selectedRoleFilter == null,
                        onClick = { selectedRoleFilter = null },
                        label = { Text("All (${users.size})") }
                    )
                }
                items(UserRole.entries.toTypedArray()) { role ->
                    val count = users.count { it.role == role }
                    FilterChip(
                        selected = selectedRoleFilter == role,
                        onClick = { selectedRoleFilter = role },
                        label = { Text("${role.name} ($count)") }
                    )
                }
            }

            if (filteredUsers.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.People,
                    title = "No Users",
                    message = "No registered users match this role filter."
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredUsers) { user ->
                        AdminUserCard(
                            user = user,
                            onDelete = { viewModel.deleteUser(user.id) }
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
fun AdminUserCard(
    user: UserEntity,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = user.fullName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    RoleIndicatorCard(role = user.role)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "ID: ${user.studentOrStaffId} • Phone: ${if (user.phoneNumber.isNotBlank()) user.phoneNumber else "N/A"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                Icon(
                    Icons.Default.DeleteOutline,
                    contentDescription = "Delete User",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
