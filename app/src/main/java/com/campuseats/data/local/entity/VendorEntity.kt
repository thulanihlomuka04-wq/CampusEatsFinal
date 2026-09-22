package com.campuseats.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Entity representing campus food vendors / cafes / stalls.
 */
@Entity(tableName = "vendors")
data class VendorEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val campusLocation: String,
    val rating: Double = 4.5,
    val isOpen: Boolean = true,
    val contactNumber: String = "",
    val openingHours: String = "08:00 - 17:00",
    val estimatedPrepTimeMinutes: Int = 15,
    val vendorEmail: String = ""
)
