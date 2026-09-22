package com.campuseats.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Room Relation data class linking an Order with its line items.
 */
data class OrderWithItems(
    @Embedded
    val order: OrderEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "orderId"
    )
    val items: List<OrderItemEntity> = emptyList()
)
