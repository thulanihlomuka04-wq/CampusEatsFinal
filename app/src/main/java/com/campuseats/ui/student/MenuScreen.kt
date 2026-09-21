package com.campuseats.ui.student

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.campuseats.data.local.entity.FoodItemEntity
import com.campuseats.data.local.entity.VendorEntity
import com.campuseats.ui.components.CampusEatsTopBar
import com.campuseats.ui.components.EmptyStateView
import com.campuseats.ui.theme.CampusGreen
import com.campuseats.ui.theme.CampusOrange
import com.campuseats.utils.CurrencyFormatter

@Composable
fun MenuScreen(
    vendorId: String,
    viewModel: StudentViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToCart: () -> Unit
) {
    var vendor by remember { mutableStateOf<VendorEntity?>(null) }
    val menuItems by viewModel.getMenuForVendor(vendorId).collectAsState(initial = emptyList())
    val cartItems by viewModel.cartItems.collectAsState()

    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var detailItem by remember { mutableStateOf<FoodItemEntity?>(null) }

    LaunchedEffect(vendorId) {
        vendor = viewModel.getVendor(vendorId)
    }

    val categories = remember(menuItems) {
        listOf("All") + menuItems.map { it.category }.distinct()
    }

    val filteredItems = remember(menuItems, selectedCategory) {
        if (selectedCategory == null || selectedCategory == "All") {
            menuItems
        } else {
            menuItems.filter { it.category == selectedCategory }
        }
    }

    val totalCartCount = cartItems.sumOf { it.quantity }
    val totalCartPrice = cartItems.sumOf { it.foodItem.price * it.quantity }

    Scaffold(
        topBar = {
            CampusEatsTopBar(
                title = vendor?.name ?: "Vendor Menu",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack,
                actions = {
                    IconButton(onClick = onNavigateToCart) {
                        Icon(
                            Icons.Default.ShoppingCart,
                            contentDescription = "Cart",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )
        },
        bottomBar = {
            if (totalCartCount > 0) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "$totalCartCount items in Cart",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = CurrencyFormatter.format(totalCartPrice),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Button(
                            onClick = onNavigateToCart,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("View Cart ($totalCartCount)", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Category selector
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    FilterChip(
                        selected = (selectedCategory == null && cat == "All") || selectedCategory == cat,
                        onClick = { selectedCategory = if (cat == "All") null else cat },
                        label = { Text(cat) }
                    )
                }
            }

            if (filteredItems.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.Fastfood,
                    title = "Menu Items Coming Soon",
                    message = "This campus vendor has not listed items in this category yet."
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredItems) { item ->
                        FoodItemCard(
                            item = item,
                            onClick = { detailItem = item },
                            onAddToCart = { viewModel.addToCart(item) }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }

    // Food Details Dialog (Flow: Vendor Menu -> Food Details -> Add to Cart)
    detailItem?.let { item ->
        FoodDetailsDialog(
            item = item,
            onDismiss = { detailItem = null },
            onAddToCart = { quantity ->
                repeat(quantity) {
                    viewModel.addToCart(item)
                }
                detailItem = null
            }
        )
    }
}

@Composable
fun FoodItemCard(
    item: FoodItemEntity,
    onClick: () -> Unit,
    onAddToCart: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Food Image Thumbnail representation
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (item.isAvailable) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                        else MaterialTheme.colorScheme.surfaceVariant
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.RestaurantMenu,
                    contentDescription = item.name,
                    tint = if (item.isAvailable) MaterialTheme.colorScheme.primary else Color.Gray,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (item.isAvailable) MaterialTheme.colorScheme.onSurface else Color.Gray
                    )
                    if (item.isVegetarian) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Eco,
                            contentDescription = "Vegetarian",
                            tint = CampusGreen,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = CurrencyFormatter.format(item.price),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (item.isAvailable) MaterialTheme.colorScheme.primary else Color.Gray
                    )

                    // Availability badge
                    Surface(
                        color = if (item.isAvailable) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = if (item.isAvailable) "Available" else "Sold Out",
                            color = if (item.isAvailable) CampusGreen else Color(0xFFD32F2F),
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    if (item.calories != null) {
                        Text(
                            text = "${item.calories} kcal",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Add button (disabled if unavailable)
            Button(
                onClick = onAddToCart,
                enabled = item.isAvailable,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                if (item.isAvailable) {
                    Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add", fontSize = 12.sp)
                } else {
                    Text("Sold Out", fontSize = 11.sp, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun FoodDetailsDialog(
    item: FoodItemEntity,
    onDismiss: () -> Unit,
    onAddToCart: (Int) -> Unit
) {
    var quantity by remember { mutableIntStateOf(1) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = { onAddToCart(quantity) },
                enabled = item.isAvailable,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                if (item.isAvailable) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add ${quantity} to Cart • ${CurrencyFormatter.format(item.price * quantity)}")
                } else {
                    Text("Item Unavailable")
                }
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Close")
            }
        },
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Food Details",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Food Image Hero Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            if (item.isAvailable) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                            else MaterialTheme.colorScheme.surfaceVariant
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.RestaurantMenu,
                            contentDescription = item.name,
                            tint = if (item.isAvailable) MaterialTheme.colorScheme.primary else Color.Gray,
                            modifier = Modifier.size(52.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item.category.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Name & Vegetarian Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    if (item.isVegetarian) {
                        Surface(
                            color = Color(0xFFE8F5E9),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Eco,
                                    contentDescription = null,
                                    tint = CampusGreen,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "Vegetarian",
                                    color = CampusGreen,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                // Price and Availability
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = CurrencyFormatter.format(item.price),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = CampusOrange
                    )

                    Surface(
                        color = if (item.isAvailable) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (item.isAvailable) "In Stock & Available" else "Currently Unavailable",
                            color = if (item.isAvailable) CampusGreen else Color(0xFFD32F2F),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }

                // Description
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (item.calories != null) {
                    Text(
                        text = "Nutritional info: ${item.calories} calories per serving",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Quantity selector if available
                if (item.isAvailable) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Quantity:",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { if (quantity > 1) quantity-- },
                                enabled = quantity > 1
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Decrease")
                            }
                            Text(
                                text = quantity.toString(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                            IconButton(onClick = { quantity++ }) {
                                Icon(Icons.Default.Add, contentDescription = "Increase")
                            }
                        }
                    }
                }
            }
        }
    )
}
