package com.campuseats.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.campuseats.data.local.entity.UserEntity
import com.campuseats.data.repository.AuthRepository
import com.campuseats.security.CredentialValidator
import com.campuseats.security.UserRole
import com.campuseats.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successUser: UserEntity? = null
)

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, passwordPlain: String) {
        // Client-side quick checks
        val emailValidation = CredentialValidator.validateEmail(email)
        if (!emailValidation.isValid) {
            _uiState.value = AuthUiState(errorMessage = emailValidation.errorMessage)
            return
        }

        val passwordValidation = CredentialValidator.validatePasswordForLogin(passwordPlain)
        if (!passwordValidation.isValid) {
            _uiState.value = AuthUiState(errorMessage = passwordValidation.errorMessage)
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            when (val result = authRepository.login(email, passwordPlain)) {
                is Resource.Success -> {
                    _uiState.value = AuthUiState(successUser = result.data)
                }
                is Resource.Error -> {
                    _uiState.value = AuthUiState(errorMessage = result.message)
                }
                is Resource.Loading -> {
                    _uiState.value = AuthUiState(isLoading = true)
                }
            }
        }
    }

    /**
     * Registers a new student. Registration strictly creates STUDENT accounts only.
     */
    fun registerStudent(
        email: String,
        passwordPlain: String,
        fullName: String,
        studentId: String,
        phoneNumber: String
    ) {
        val nameValidation = CredentialValidator.validateFullName(fullName)
        if (!nameValidation.isValid) {
            _uiState.value = AuthUiState(errorMessage = nameValidation.errorMessage)
            return
        }

        val emailValidation = CredentialValidator.validateEmail(email)
        if (!emailValidation.isValid) {
            _uiState.value = AuthUiState(errorMessage = emailValidation.errorMessage)
            return
        }

        val idValidation = CredentialValidator.validateStudentId(studentId)
        if (!idValidation.isValid) {
            _uiState.value = AuthUiState(errorMessage = idValidation.errorMessage)
            return
        }

        val passwordValidation = CredentialValidator.validatePasswordForRegistration(passwordPlain)
        if (!passwordValidation.isValid) {
            _uiState.value = AuthUiState(errorMessage = passwordValidation.errorMessage)
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            when (val result = authRepository.registerStudent(email, passwordPlain, fullName, studentId, phoneNumber)) {
                is Resource.Success -> {
                    _uiState.value = AuthUiState(successUser = result.data)
                }
                is Resource.Error -> {
                    _uiState.value = AuthUiState(errorMessage = result.message)
                }
                is Resource.Loading -> {
                    _uiState.value = AuthUiState(isLoading = true)
                }
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun resetState() {
        _uiState.value = AuthUiState()
    }

    fun getCurrentUserRole(): UserRole? = authRepository.getCurrentUserRole()

    fun isLoggedIn(): Boolean = authRepository.isLoggedIn()

    fun logout() {
        authRepository.logout()
        _uiState.value = AuthUiState()
    }
}
