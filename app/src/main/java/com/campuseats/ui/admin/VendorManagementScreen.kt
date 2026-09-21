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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
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
import com.campuseats.data.local.entity.VendorEntity
import com.campuseats.navigation.NavRoutes
import com.campuseats.security.UserRole
import com.campuseats.ui.components.CampusEatsBottomBar
import com.campuseats.ui.components.CampusEatsTopBar
import com.campuseats.ui.components.EmptyStateView

@Composable
fun VendorManagementScreen(
    viewModel: AdminViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    onNavigateToUsers: () -> Unit,
    onNavigateToReports: () -> Unit
) {
    val vendors by viewModel.allVendors.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var showAddDialog by remember { mutableStateOf(false) }

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
                title = "Campus Vendor Management",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        },
        bottomBar = {
            CampusEatsBottomBar(
                role = UserRole.ADMIN,
                currentRoute = NavRoutes.VendorManagement.route,
                onNavigate = { route ->
                    when (route) {
                        NavRoutes.AdminDashboard.route -> onNavigateToDashboard()
                        NavRoutes.UserManagement.route -> onNavigateToUsers()
                        NavRoutes.Reports.route -> onNavigateToReports()
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Vendor")
            }
        }
    ) { padding ->
        if (vendors.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.Store,
                title = "No Campus Vendors",
                message = "Register your first campus cafeteria or food stall.",
                actionButtonText = "Register Vendor",
                onActionClick = { showAddDialog = true },
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
                item {
                    Text(
                        text = "Registered Food Stalls (${vendors.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(vendors) { vendor ->
                    AdminVendorItemRow(
                        vendor = vendor,
                        onToggleStatus = { viewModel.toggleVendorStatus(vendor) },
                        onDelete = { viewModel.deleteVendor(vendor) }
                    )
                }
            }
        }

        if (showAddDialog) {
            AddVendorDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { name, desc, loc, hours, email, pass ->
                    viewModel.addVendor(name, desc, loc, hours, email, pass)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun AdminVendorItemRow(
    vendor: VendorEntity,
    onToggleStatus: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = vendor.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = vendor.campusLocation,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Hours: ${vendor.openingHours} • Rating: ${vendor.rating}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (vendor.isOpen) "Open" else "Closed",
                        style = MaterialTheme.typography.labelSmall
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Switch(
                        checked = vendor.isOpen,
                        onCheckedChange = { onToggleStatus() }
                    )
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Default.DeleteOutline,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AddVendorDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, desc: String, loc: String, hours: String, email: String?, pass: String?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var campusLocation by remember { mutableStateOf("") }
    var hours by remember { mutableStateOf("08:00 - 17:00") }
    var vendorEmail by remember { mutableStateOf("") }
    var vendorPassword by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Register Campus Vendor", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Vendor Name *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = campusLocation,
                    onValueChange = { campusLocation = it },
                    label = { Text("Campus Location *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = hours,
                    onValueChange = { hours = it },
                    label = { Text("Opening Hours") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = vendorEmail,
                    onValueChange = { vendorEmail = it },
                    label = { Text("Vendor Login Email (Optional)") },
                    placeholder = { Text("e.g. grill@campuseats.com") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = vendorPassword,
                    onValueChange = { vendorPassword = it },
                    label = { Text("Vendor Password (Optional)") },
                    placeholder = { Text("Initial password") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && campusLocation.isNotBlank()) {
                        val email = vendorEmail.trim().takeIf { it.isNotEmpty() }
                        val pass = vendorPassword.trim().takeIf { it.isNotEmpty() }
                        onConfirm(name, description, campusLocation, hours, email, pass)
                    }
                }
            ) {
                Text("Register")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
