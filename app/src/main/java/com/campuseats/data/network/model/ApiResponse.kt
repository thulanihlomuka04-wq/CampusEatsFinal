package com.campuseats.data.network.model

/**
 * Standard REST API Response format for backend network synchronization.
 */
data class ApiResponse<T>(
    val success: Boolean,
    val message: String? = null,
    val data: T? = null
)
