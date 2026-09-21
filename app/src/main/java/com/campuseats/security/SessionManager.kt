package com.campuseats.security

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Android-appropriate local session storage using SharedPreferences.
 * Stores strictly non-sensitive session metadata:
 * - Logged-in state
 * - User ID
 * - User Role
 * - User Name
 * Passwords are NEVER stored in session storage.
 */
class SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    private val _isLoggedIn = MutableStateFlow(prefs.getBoolean(KEY_IS_LOGGED_IN, false))
    val isLoggedInState: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _currentUserId = MutableStateFlow(prefs.getString(KEY_USER_ID, null))
    val currentUserId: StateFlow<String?> = _currentUserId.asStateFlow()

    private val _currentUserRole = MutableStateFlow(
        prefs.getString(KEY_USER_ROLE, null)?.let { UserRole.fromString(it) }
    )
    val currentUserRole: StateFlow<UserRole?> = _currentUserRole.asStateFlow()

    private val _currentUserName = MutableStateFlow(prefs.getString(KEY_USER_NAME, null))
    val currentUserName: StateFlow<String?> = _currentUserName.asStateFlow()

    /**
     * Persists an authenticated session into SharedPreferences.
     */
    fun saveSession(userId: String, role: UserRole, userName: String) {
        prefs.edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, true)
            putString(KEY_USER_ID, userId)
            putString(KEY_USER_ROLE, role.name)
            putString(KEY_USER_NAME, userName)
            apply()
        }

        _isLoggedIn.value = true
        _currentUserId.value = userId
        _currentUserRole.value = role
        _currentUserName.value = userName
    }

    /**
     * Clears all session keys upon logout.
     */
    fun clearSession() {
        prefs.edit().apply {
            clear()
            apply()
        }

        _isLoggedIn.value = false
        _currentUserId.value = null
        _currentUserRole.value = null
        _currentUserName.value = null
    }

    fun isLoggedIn(): Boolean = _isLoggedIn.value

    fun getUserId(): String? = _currentUserId.value

    fun getUserRole(): UserRole? = _currentUserRole.value

    fun getUserName(): String? = _currentUserName.value

    companion object {
        private const val PREF_NAME = "campus_eats_session"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_ROLE = "user_role"
        private const val KEY_USER_NAME = "user_name"
    }
}
