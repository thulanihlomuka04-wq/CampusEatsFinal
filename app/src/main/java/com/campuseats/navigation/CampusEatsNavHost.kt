package com.campuseats.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.campuseats.security.UserRole
import com.campuseats.ui.admin.AdminDashboardScreen
import com.campuseats.ui.admin.AdminViewModel
import com.campuseats.ui.admin.ReportsScreen
import com.campuseats.ui.admin.UserManagementScreen
import com.campuseats.ui.admin.VendorManagementScreen
import com.campuseats.ui.auth.AuthViewModel
import com.campuseats.ui.auth.LoginScreen
import com.campuseats.ui.auth.RegisterScreen
import com.campuseats.ui.student.CartScreen
import com.campuseats.ui.student.MenuScreen
import com.campuseats.ui.student.OrderHistoryScreen
import com.campuseats.ui.student.OrderStatusScreen
import com.campuseats.ui.student.StudentHomeScreen
import com.campuseats.ui.student.StudentViewModel
import com.campuseats.ui.student.VendorsScreen
import com.campuseats.ui.vendor.FoodManagementScreen
import com.campuseats.ui.vendor.VendorDashboardScreen
import com.campuseats.ui.vendor.VendorOrdersScreen
import com.campuseats.ui.vendor.VendorViewModel

/**
 * Top-Level Navigation Graph coordinating all destinations across Campus Eats.
 * Enforces role-based access control (RBAC) at the route level.
 */
@Composable
fun CampusEatsNavHost(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    studentViewModel: StudentViewModel,
    vendorViewModel: VendorViewModel,
    adminViewModel: AdminViewModel,
    modifier: Modifier = Modifier
) {
    // Startup routing based on active session
    val startDestination = if (authViewModel.isLoggedIn()) {
        when (authViewModel.getCurrentUserRole()) {
            UserRole.STUDENT -> NavRoutes.StudentHome.route
            UserRole.VENDOR -> NavRoutes.VendorDashboard.route
            UserRole.ADMIN -> NavRoutes.AdminDashboard.route
            null -> NavRoutes.Login.route
        }
    } else {
        NavRoutes.Login.route
    }

    val onLogoutAction = {
        authViewModel.logout()
        navController.navigate(NavRoutes.Login.route) {
            popUpTo(0) { inclusive = true }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // --- AUTHENTICATION FLOW (Unauthenticated Only) ---
        composable(NavRoutes.Login.route) {
            UnauthenticatedOnlyDestination(authViewModel = authViewModel, navController = navController) {
                LoginScreen(
                    viewModel = authViewModel,
                    onNavigateToRegister = {
                        navController.navigate(NavRoutes.Register.route)
                    },
                    onLoginSuccess = { role ->
                        val destination = when (role) {
                            UserRole.STUDENT -> NavRoutes.StudentHome.route
                            UserRole.VENDOR -> NavRoutes.VendorDashboard.route
                            UserRole.ADMIN -> NavRoutes.AdminDashboard.route
                        }
                        navController.navigate(destination) {
                            popUpTo(NavRoutes.Login.route) { inclusive = true }
                        }
                    }
                )
            }
        }

        composable(NavRoutes.Register.route) {
            UnauthenticatedOnlyDestination(authViewModel = authViewModel, navController = navController) {
                RegisterScreen(
                    viewModel = authViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onRegisterSuccess = { role ->
                        val destination = when (role) {
                            UserRole.STUDENT -> NavRoutes.StudentHome.route
                            UserRole.VENDOR -> NavRoutes.VendorDashboard.route
                            UserRole.ADMIN -> NavRoutes.AdminDashboard.route
                        }
                        navController.navigate(destination) {
                            popUpTo(NavRoutes.Login.route) { inclusive = true }
                        }
                    }
                )
            }
        }

        // --- STUDENT FLOW (Role: STUDENT) ---
        composable(NavRoutes.StudentHome.route) {
            RoleProtectedDestination(
                allowedRole = UserRole.STUDENT,
                authViewModel = authViewModel,
                navController = navController
            ) {
                StudentHomeScreen(
                    viewModel = studentViewModel,
                    onNavigateToMenu = { vendorId ->
                        navController.navigate(NavRoutes.Menu.createRoute(vendorId))
                    },
                    onNavigateToVendors = {
                        navController.navigate(NavRoutes.Vendors.route)
                    },
                    onNavigateToCart = {
                        navController.navigate(NavRoutes.Cart.route)
                    },
                    onNavigateToHistory = {
                        navController.navigate(NavRoutes.OrderHistory.route)
                    },
                    onNavigateToOrderStatus = { orderId ->
                        navController.navigate(NavRoutes.OrderStatus.createRoute(orderId))
                    },
                    onLogout = onLogoutAction
                )
            }
        }

        composable(NavRoutes.Vendors.route) {
            RoleProtectedDestination(
                allowedRole = UserRole.STUDENT,
                authViewModel = authViewModel,
                navController = navController
            ) {
                VendorsScreen(
                    viewModel = studentViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToMenu = { vendorId ->
                        navController.navigate(NavRoutes.Menu.createRoute(vendorId))
                    },
                    onNavigateToHome = {
                        navController.navigate(NavRoutes.StudentHome.route)
                    },
                    onNavigateToCart = {
                        navController.navigate(NavRoutes.Cart.route)
                    },
                    onNavigateToHistory = {
                        navController.navigate(NavRoutes.OrderHistory.route)
                    }
                )
            }
        }

        composable(
            route = NavRoutes.Menu.route,
            arguments = listOf(navArgument("vendorId") { type = NavType.StringType })
        ) { backStackEntry ->
            val vendorId = backStackEntry.arguments?.getString("vendorId") ?: ""
            RoleProtectedDestination(
                allowedRole = UserRole.STUDENT,
                authViewModel = authViewModel,
                navController = navController
            ) {
                MenuScreen(
                    vendorId = vendorId,
                    viewModel = studentViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToCart = { navController.navigate(NavRoutes.Cart.route) }
                )
            }
        }

        composable(NavRoutes.Cart.route) {
            RoleProtectedDestination(
                allowedRole = UserRole.STUDENT,
                authViewModel = authViewModel,
                navController = navController
            ) {
                CartScreen(
                    viewModel = studentViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToVendors = { navController.navigate(NavRoutes.Vendors.route) },
                    onOrderPlacedSuccess = { orderId ->
                        navController.navigate(NavRoutes.OrderStatus.createRoute(orderId)) {
                            popUpTo(NavRoutes.StudentHome.route)
                        }
                    }
                )
            }
        }

        composable(
            route = NavRoutes.OrderStatus.route,
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            RoleProtectedDestination(
                allowedRole = UserRole.STUDENT,
                authViewModel = authViewModel,
                navController = navController
            ) {
                OrderStatusScreen(
                    orderId = orderId,
                    viewModel = studentViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToHistory = { navController.navigate(NavRoutes.OrderHistory.route) },
                    onNavigateToHome = { navController.navigate(NavRoutes.StudentHome.route) }
                )
            }
        }

        composable(NavRoutes.OrderHistory.route) {
            RoleProtectedDestination(
                allowedRole = UserRole.STUDENT,
                authViewModel = authViewModel,
                navController = navController
            ) {
                OrderHistoryScreen(
                    viewModel = studentViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToOrderStatus = { orderId ->
                        navController.navigate(NavRoutes.OrderStatus.createRoute(orderId))
                    },
                    onNavigateToHome = { navController.navigate(NavRoutes.StudentHome.route) },
                    onNavigateToVendors = { navController.navigate(NavRoutes.Vendors.route) },
                    onNavigateToCart = { navController.navigate(NavRoutes.Cart.route) }
                )
            }
        }

        // --- VENDOR FLOW (Role: VENDOR) ---
        composable(NavRoutes.VendorDashboard.route) {
            RoleProtectedDestination(
                allowedRole = UserRole.VENDOR,
                authViewModel = authViewModel,
                navController = navController
            ) {
                VendorDashboardScreen(
                    viewModel = vendorViewModel,
                    onNavigateToFoodManagement = { navController.navigate(NavRoutes.FoodManagement.route) },
                    onNavigateToOrders = { navController.navigate(NavRoutes.VendorOrders.route) },
                    onLogout = onLogoutAction
                )
            }
        }

        composable(NavRoutes.FoodManagement.route) {
            RoleProtectedDestination(
                allowedRole = UserRole.VENDOR,
                authViewModel = authViewModel,
                navController = navController
            ) {
                FoodManagementScreen(
                    viewModel = vendorViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToDashboard = { navController.navigate(NavRoutes.VendorDashboard.route) },
                    onNavigateToOrders = { navController.navigate(NavRoutes.VendorOrders.route) }
                )
            }
        }

        composable(NavRoutes.VendorOrders.route) {
            RoleProtectedDestination(
                allowedRole = UserRole.VENDOR,
                authViewModel = authViewModel,
                navController = navController
            ) {
                VendorOrdersScreen(
                    viewModel = vendorViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToDashboard = { navController.navigate(NavRoutes.VendorDashboard.route) },
                    onNavigateToFoodManagement = { navController.navigate(NavRoutes.FoodManagement.route) }
                )
            }
        }

        // --- ADMIN FLOW (Role: ADMIN) ---
        composable(NavRoutes.AdminDashboard.route) {
            RoleProtectedDestination(
                allowedRole = UserRole.ADMIN,
                authViewModel = authViewModel,
                navController = navController
            ) {
                AdminDashboardScreen(
                    viewModel = adminViewModel,
                    onNavigateToUserManagement = { navController.navigate(NavRoutes.UserManagement.route) },
                    onNavigateToVendorManagement = { navController.navigate(NavRoutes.VendorManagement.route) },
                    onNavigateToReports = { navController.navigate(NavRoutes.Reports.route) },
                    onLogout = onLogoutAction
                )
            }
        }

        composable(NavRoutes.UserManagement.route) {
            RoleProtectedDestination(
                allowedRole = UserRole.ADMIN,
                authViewModel = authViewModel,
                navController = navController
            ) {
                UserManagementScreen(
                    viewModel = adminViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToDashboard = { navController.navigate(NavRoutes.AdminDashboard.route) },
                    onNavigateToVendors = { navController.navigate(NavRoutes.VendorManagement.route) },
                    onNavigateToReports = { navController.navigate(NavRoutes.Reports.route) }
                )
            }
        }

        composable(NavRoutes.VendorManagement.route) {
            RoleProtectedDestination(
                allowedRole = UserRole.ADMIN,
                authViewModel = authViewModel,
                navController = navController
            ) {
                VendorManagementScreen(
                    viewModel = adminViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToDashboard = { navController.navigate(NavRoutes.AdminDashboard.route) },
                    onNavigateToUsers = { navController.navigate(NavRoutes.UserManagement.route) },
                    onNavigateToReports = { navController.navigate(NavRoutes.Reports.route) }
                )
            }
        }

        composable(NavRoutes.Reports.route) {
            RoleProtectedDestination(
                allowedRole = UserRole.ADMIN,
                authViewModel = authViewModel,
                navController = navController
            ) {
                ReportsScreen(
                    viewModel = adminViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToDashboard = { navController.navigate(NavRoutes.AdminDashboard.route) },
                    onNavigateToUsers = { navController.navigate(NavRoutes.UserManagement.route) },
                    onNavigateToVendors = { navController.navigate(NavRoutes.VendorManagement.route) }
                )
            }
        }
    }
}

/**
 * Route Guard that prevents unauthorized cross-role access.
 * If unauthorized, immediately redirects the user to their permitted home destination.
 */
@Composable
private fun RoleProtectedDestination(
    allowedRole: UserRole,
    authViewModel: AuthViewModel,
    navController: NavHostController,
    content: @Composable () -> Unit
) {
    val isLoggedIn = authViewModel.isLoggedIn()
    val currentRole = authViewModel.getCurrentUserRole()

    LaunchedEffect(isLoggedIn, currentRole) {
        if (!isLoggedIn) {
            navController.navigate(NavRoutes.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        } else if (currentRole != allowedRole) {
            val redirectDestination = when (currentRole) {
                UserRole.STUDENT -> NavRoutes.StudentHome.route
                UserRole.VENDOR -> NavRoutes.VendorDashboard.route
                UserRole.ADMIN -> NavRoutes.AdminDashboard.route
                null -> NavRoutes.Login.route
            }
            navController.navigate(redirectDestination) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    if (isLoggedIn && currentRole == allowedRole) {
        content()
    }
}

/**
 * Route Guard for unauthenticated flows (Login / Register).
 * If the user is already authenticated, redirects them directly to their role dashboard.
 */
@Composable
private fun UnauthenticatedOnlyDestination(
    authViewModel: AuthViewModel,
    navController: NavHostController,
    content: @Composable () -> Unit
) {
    val isLoggedIn = authViewModel.isLoggedIn()
    val currentRole = authViewModel.getCurrentUserRole()

    LaunchedEffect(isLoggedIn, currentRole) {
        if (isLoggedIn && currentRole != null) {
            val redirectDestination = when (currentRole) {
                UserRole.STUDENT -> NavRoutes.StudentHome.route
                UserRole.VENDOR -> NavRoutes.VendorDashboard.route
                UserRole.ADMIN -> NavRoutes.AdminDashboard.route
            }
            navController.navigate(redirectDestination) {
                popUpTo(NavRoutes.Login.route) { inclusive = true }
            }
        }
    }

    if (!isLoggedIn) {
        content()
    }
}
