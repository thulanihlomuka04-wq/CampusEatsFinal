package com.campuseats.utils

/**
 * A generic state container for handling Loading, Success, and Error states across ViewModels and Repositories.
 */
sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val message: String, val cause: Throwable? = null) : Resource<Nothing>()
    data object Loading : Resource<Nothing>()
}
