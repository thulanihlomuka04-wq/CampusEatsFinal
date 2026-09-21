package com.campuseats.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.campuseats.security.UserRole

/**
 * Room Entity representing authenticated platform users across all 3 roles.
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val email: String,
    val passwordHash: String,
    val fullName: String,
    val studentOrStaffId: String,
    val role: UserRole,
    val phoneNumber: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
