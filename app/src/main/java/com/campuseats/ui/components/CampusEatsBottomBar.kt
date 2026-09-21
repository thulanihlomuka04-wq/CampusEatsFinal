package com.campuseats.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.campuseats.navigation.NavRoutes
import com.campuseats.security.UserRole

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

@Composable
fun CampusEatsBottomBar(
    role: UserRole,
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    val items = when (role) {
        UserRole.STUDENT -> listOf(
            BottomNavItem("Home", Icons.Default.Home, NavRoutes.StudentHome.route),
            BottomNavItem("Vendors", Icons.Default.Store, NavRoutes.Vendors.route),
            BottomNavItem("Cart", Icons.Default.ShoppingCart, NavRoutes.Cart.route),
            BottomNavItem("History", Icons.Default.History, NavRoutes.OrderHistory.route)
        )
        UserRole.VENDOR -> listOf(
            BottomNavItem("Dashboard", Icons.Default.Home, NavRoutes.VendorDashboard.route),
            BottomNavItem("Food Items", Icons.Default.Fastfood, NavRoutes.FoodManagement.route),
            BottomNavItem("Orders", Icons.Default.Receipt, NavRoutes.VendorOrders.route)
        )
        UserRole.ADMIN -> listOf(
            BottomNavItem("Dashboard", Icons.Default.Home, NavRoutes.AdminDashboard.route),
            BottomNavItem("Users", Icons.Default.People, NavRoutes.UserManagement.route),
            BottomNavItem("Vendors", Icons.Default.Store, NavRoutes.VendorManagement.route),
            BottomNavItem("Reports", Icons.Default.Assessment, NavRoutes.Reports.route)
        )
    }

    NavigationBar {
        items.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = selected,
                onClick = {
                    if (!selected) {
                        onNavigate(item.route)
                    }
                }
            )
        }
    }
}
