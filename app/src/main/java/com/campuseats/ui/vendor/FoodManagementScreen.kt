package com.campuseats.ui.vendor

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.campuseats.data.local.entity.FoodItemEntity
import com.campuseats.navigation.NavRoutes
import com.campuseats.security.UserRole
import com.campuseats.ui.components.CampusEatsBottomBar
import com.campuseats.ui.components.CampusEatsTopBar
import com.campuseats.ui.components.EmptyStateView
import com.campuseats.utils.CurrencyFormatter

@Composable
fun FoodManagementScreen(
    viewModel: VendorViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    onNavigateToOrders: () -> Unit
) {
    val foodItems by viewModel.foodItems.collectAsState()
    val actionMessage by viewModel.actionMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var showAddDialog by remember { mutableStateOf(false) }

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
                title = "Food Items Management",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        },
        bottomBar = {
            CampusEatsBottomBar(
                role = UserRole.VENDOR,
                currentRoute = NavRoutes.FoodManagement.route,
                onNavigate = { route ->
                    when (route) {
                        NavRoutes.VendorDashboard.route -> onNavigateToDashboard()
                        NavRoutes.VendorOrders.route -> onNavigateToOrders()
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
                Icon(Icons.Default.Add, contentDescription = "Add Food Item")
            }
        }
    ) { padding ->
        if (foodItems.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.Fastfood,
                title = "No Food Items",
                message = "You haven't added any dishes to your campus menu yet.",
                actionButtonText = "Add First Food Item",
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
                        text = "Current Menu Items (${foodItems.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(foodItems) { item ->
                    VendorFoodItemRow(
                        item = item,
                        onToggleAvailability = { viewModel.toggleAvailability(item) },
                        onDelete = { viewModel.deleteFoodItem(item) }
                    )
                }
            }
        }

        if (showAddDialog) {
            AddFoodItemDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { name, desc, price, cat, veg ->
                    viewModel.addFoodItem(name, desc, price, cat, veg)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun VendorFoodItemRow(
    item: FoodItemEntity,
    onToggleAvailability: () -> Unit,
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
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = item.category,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = CurrencyFormatter.format(item.price),
                    fontWeight = FontWeight.SemiBold
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (item.isAvailable) "Active" else "Sold Out",
                        style = MaterialTheme.typography.labelSmall
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Switch(
                        checked = item.isAvailable,
                        onCheckedChange = { onToggleAvailability() }
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
fun AddFoodItemDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, desc: String, price: Double, category: String, isVeg: Boolean) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Mains") }
    var isVegetarian by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Menu Item", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Item Name") },
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
                    value = priceText,
                    onValueChange = { priceText = it },
                    label = { Text("Price (e.g. 45.00)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category (e.g. Burgers, Breakfast)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isVegetarian, onCheckedChange = { isVegetarian = it })
                    Text("Vegetarian Friendly")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val price = priceText.toDoubleOrNull() ?: 0.0
                    if (name.isNotBlank() && price > 0.0) {
                        onConfirm(name, description, price, category, isVegetarian)
                    }
                }
            ) {
                Text("Add Item")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
