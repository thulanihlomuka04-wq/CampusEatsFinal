package com.campuseats.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Line item breakdown for each placed order.
 */
@Entity(
    tableName = "order_items",
    foreignKeys = [
        ForeignKey(
            entity = OrderEntity::class,
            parentColumns = ["id"],
            childColumns = ["orderId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["orderId"])]
)
data class OrderItemEntity(
    @PrimaryKey
    val id: String,
    val orderId: String,
    val foodItemId: String,
    val foodName: String,
    val unitPrice: Double,
    val quantity: Int
) {
    val price: Double get() = unitPrice
    val subtotal: Double get() = unitPrice * quantity
}
