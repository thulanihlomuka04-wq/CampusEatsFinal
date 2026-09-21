package com.campuseats.ui.student

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.campuseats.navigation.NavRoutes
import com.campuseats.security.UserRole
import com.campuseats.ui.components.CampusEatsBottomBar
import com.campuseats.ui.components.CampusEatsTopBar
import com.campuseats.ui.components.EmptyStateView

@Composable
fun VendorsScreen(
    viewModel: StudentViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToMenu: (String) -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    val vendors by viewModel.vendors.collectAsState()
    val availableFoodCounts by viewModel.availableFoodCounts.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    val filteredVendors = vendors.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
        it.description.contains(searchQuery, ignoreCase = true) ||
        it.campusLocation.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            CampusEatsTopBar(
                title = "Campus Vendors",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        },
        bottomBar = {
            CampusEatsBottomBar(
                role = UserRole.STUDENT,
                currentRoute = NavRoutes.Vendors.route,
                onNavigate = { route ->
                    when (route) {
                        NavRoutes.StudentHome.route -> onNavigateToHome()
                        NavRoutes.Cart.route -> onNavigateToCart()
                        NavRoutes.OrderHistory.route -> onNavigateToHistory()
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search vendors or food...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                shape = RoundedCornerShape(12.dp)
            )

            if (filteredVendors.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.Store,
                    title = "No Vendors Found",
                    message = "No campus vendor matched your search terms."
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredVendors) { vendor ->
                        val availableCount = availableFoodCounts[vendor.id] ?: 0
                        VendorCardItem(
                            vendor = vendor,
                            availableFoodCount = availableCount,
                            onClick = { onNavigateToMenu(vendor.id) }
                        )
                    }
                }
            }
        }
    }
}
