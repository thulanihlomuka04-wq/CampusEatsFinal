package com.campuseats.security

import java.util.regex.Pattern

/**
 * Result of credential validation containing status and descriptive feedback message.
 */
sealed class ValidationResult {
    object Success : ValidationResult()
    data class Error(val message: String) : ValidationResult()

    val isValid: Boolean get() = this is Success
    val errorMessage: String? get() = (this as? Error)?.message
}

/**
 * Validates user input credentials for authentication and registration.
 */
object CredentialValidator {

    private val EMAIL_REGEX: Pattern = Pattern.compile(
        "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,64}$"
    )

    fun validateEmail(email: String): ValidationResult {
        val trimmed = email.trim()
        if (trimmed.isEmpty()) {
            return ValidationResult.Error("Email address cannot be empty.")
        }
        if (!EMAIL_REGEX.matcher(trimmed).matches()) {
            return ValidationResult.Error("Please enter a valid email address (e.g. name@campuseats.com).")
        }
        return ValidationResult.Success
    }

    fun validatePasswordForLogin(password: String): ValidationResult {
        if (password.isEmpty()) {
            return ValidationResult.Error("Password cannot be empty.")
        }
        return ValidationResult.Success
    }

    fun validatePasswordForRegistration(password: String): ValidationResult {
        if (password.isEmpty()) {
            return ValidationResult.Error("Password cannot be empty.")
        }
        if (password.length < 6) {
            return ValidationResult.Error("Password must be at least 6 characters long.")
        }
        return ValidationResult.Success
    }

    fun validateFullName(fullName: String): ValidationResult {
        val trimmed = fullName.trim()
        if (trimmed.isEmpty()) {
            return ValidationResult.Error("Full name is required.")
        }
        if (trimmed.length < 2) {
            return ValidationResult.Error("Full name must be at least 2 characters.")
        }
        return ValidationResult.Success
    }

    fun validateStudentId(studentId: String): ValidationResult {
        val trimmed = studentId.trim()
        if (trimmed.isEmpty()) {
            return ValidationResult.Error("Student ID is required.")
        }
        return ValidationResult.Success
    }
}
