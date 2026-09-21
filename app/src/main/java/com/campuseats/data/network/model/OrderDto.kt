package com.campuseats.data.network.model

import com.campuseats.data.local.entity.OrderStatus

/**
 * Data Transfer Objects for remote order transactions.
 */
data class OrderItemDto(
    val foodItemId: String,
    val foodName: String,
    val unitPrice: Double,
    val quantity: Int
)

data class CreateOrderRequestDto(
    val studentId: String,
    val vendorId: String,
    val items: List<OrderItemDto>,
    val notes: String
)

data class OrderResponseDto(
    val orderId: String,
    val orderNumber: String,
    val studentId: String,
    val vendorId: String,
    val totalAmount: Double,
    val status: OrderStatus,
    val pickupPin: String,
    val createdAt: Long
)
