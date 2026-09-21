package com.campuseats.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room Entity representing individual menu items sold by campus vendors.
 */
@Entity(
    tableName = "food_items",
    foreignKeys = [
        ForeignKey(
            entity = VendorEntity::class,
            parentColumns = ["id"],
            childColumns = ["vendorId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["vendorId"])]
)
data class FoodItemEntity(
    @PrimaryKey
    val id: String,
    val vendorId: String,
    val name: String,
    val description: String,
    val price: Double,
    val category: String, // e.g. "Breakfast", "Burgers", "Drinks", "Snacks"
    val isAvailable: Boolean = true,
    val calories: Int? = null,
    val isVegetarian: Boolean = false
)
