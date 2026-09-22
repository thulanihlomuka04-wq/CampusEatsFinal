package com.campuseats.navigation

/**
 * Route declarations for all top-level destinations in Campus Eats.
 */
sealed class NavRoutes(val route: String) {
    // Auth destinations
    data object Login : NavRoutes("auth_login")
    data object Register : NavRoutes("auth_register")

    // Student destinations
    data object StudentHome : NavRoutes("student_home")
    data object Vendors : NavRoutes("student_vendors")
    data object Menu : NavRoutes("student_menu/{vendorId}") {
        fun createRoute(vendorId: String) = "student_menu/$vendorId"
    }
    data object Cart : NavRoutes("student_cart")
    data object OrderStatus : NavRoutes("student_order_status/{orderId}") {
        fun createRoute(orderId: String) = "student_order_status/$orderId"
    }
    data object OrderHistory : NavRoutes("student_order_history")

    // Vendor destinations
    data object VendorDashboard : NavRoutes("vendor_dashboard")
    data object FoodManagement : NavRoutes("vendor_food_management")
    data object VendorOrders : NavRoutes("vendor_orders")

    // Admin destinations
    data object AdminDashboard : NavRoutes("admin_dashboard")
    data object UserManagement : NavRoutes("admin_user_management")
    data object VendorManagement : NavRoutes("admin_vendor_management")
    data object Reports : NavRoutes("admin_reports")

    // Demonstration destinations (HTTP Network & XML Processing Demos)
    data object RemoteDataDemo : NavRoutes("remote_data_demo")
    data object XmlDataDemo : NavRoutes("xml_data_demo")
}
