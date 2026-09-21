package com.campuseats.security

/**
 * The three distinct user roles supported across the Campus Eats platform.
 */
enum class UserRole {
    STUDENT,
    VENDOR,
    ADMIN;

    companion object {
        fun fromString(role: String?): UserRole {
            return entries.firstOrNull { it.name.equals(role, ignoreCase = true) } ?: STUDENT
        }
    }
}
