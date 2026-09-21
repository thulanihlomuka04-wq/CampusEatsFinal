package com.campuseats.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room Entity representing an order placed by a student to a vendor.
 */
@Entity(
    tableName = "orders",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["studentId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = VendorEntity::class,
            parentColumns = ["id"],
            childColumns = ["vendorId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["studentId"]),
        Index(value = ["vendorId"])
    ]
)
data class OrderEntity(
    @PrimaryKey
    val id: String,
    val orderNumber: String,
    val studentId: String,
    val studentName: String,
    val vendorId: String,
    val vendorName: String,
    val totalAmount: Double,
    val status: OrderStatus = OrderStatus.PLACED,
    val notes: String = "",
    val pickupPin: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val estimatedPickupMinutes: Int = 15
)
