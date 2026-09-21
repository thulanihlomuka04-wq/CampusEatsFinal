package com.campuseats.data.local.database

import androidx.room.TypeConverter
import com.campuseats.data.local.entity.OrderStatus
import com.campuseats.security.UserRole

/**
 * Room Type Converters for Enums and complex types.
 */
class Converters {
    @TypeConverter
    fun fromUserRole(role: UserRole): String = role.name

    @TypeConverter
    fun toUserRole(value: String): UserRole = UserRole.fromString(value)

    @TypeConverter
    fun fromOrderStatus(status: OrderStatus): String = status.name

    @TypeConverter
    fun toOrderStatus(value: String): OrderStatus = try {
        when (value) {
            "PENDING" -> OrderStatus.PLACED
            "READY_FOR_PICKUP" -> OrderStatus.READY
            "COMPLETED" -> OrderStatus.COLLECTED
            "CANCELLED" -> OrderStatus.REJECTED
            else -> OrderStatus.valueOf(value)
        }
    } catch (e: Exception) {
        OrderStatus.PLACED
    }
}
