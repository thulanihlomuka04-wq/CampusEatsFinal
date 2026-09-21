package com.campuseats.data.local.entity

/**
 * Status lifecycle of a student order within Campus Eats.
 */
enum class OrderStatus {
    PLACED,
    ACCEPTED,
    PREPARING,
    READY,
    COLLECTED,
    REJECTED;

    fun getDisplayName(): String {
        return when (this) {
            PLACED -> "Order Placed"
            ACCEPTED -> "Order Accepted"
            PREPARING -> "Kitchen Preparing"
            READY -> "Ready for Collection"
            COLLECTED -> "Collected"
            REJECTED -> "Rejected"
        }
    }
}
