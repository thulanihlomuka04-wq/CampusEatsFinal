package com.campuseats.data.network.model

import com.campuseats.security.UserRole

/**
 * Data Transfer Objects for remote network authentication requests.
 */
data class LoginRequestDto(
    val email: String,
    val passwordHash: String
)

data class RegisterRequestDto(
    val email: String,
    val passwordHash: String,
    val fullName: String,
    val studentOrStaffId: String,
    val role: UserRole,
    val phoneNumber: String
)

data class AuthResponseDto(
    val token: String,
    val userId: String,
    val fullName: String,
    val email: String,
    val role: UserRole
)
