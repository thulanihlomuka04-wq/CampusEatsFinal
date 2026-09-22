package com.campuseats.data.repository

import com.campuseats.data.local.dao.UserDao
import com.campuseats.data.local.entity.UserEntity
import com.campuseats.security.CredentialValidator
import com.campuseats.security.PasswordHasher
import com.campuseats.security.SessionManager
import com.campuseats.security.UserRole
import com.campuseats.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

/**
 * Repository interface governing Authentication and User sessions.
 */
interface AuthRepository {
    suspend fun login(email: String, passwordPlain: String): Resource<UserEntity>
    suspend fun registerStudent(
        email: String,
        passwordPlain: String,
        fullName: String,
        studentId: String,
        phoneNumber: String
    ): Resource<UserEntity>
    fun logout()
    fun getCurrentUserRole(): UserRole?
    fun getCurrentUserId(): String?
    fun isLoggedIn(): Boolean
}

class AuthRepositoryImpl(
    private val userDao: UserDao,
    private val sessionManager: SessionManager
) : AuthRepository {

    override suspend fun login(email: String, passwordPlain: String): Resource<UserEntity> = withContext(Dispatchers.IO) {
        try {
            // Validate email
            val emailValidation = CredentialValidator.validateEmail(email)
            if (!emailValidation.isValid) {
                return@withContext Resource.Error(emailValidation.errorMessage ?: "Invalid email address.")
            }

            // Validate password
            val passwordValidation = CredentialValidator.validatePasswordForLogin(passwordPlain)
            if (!passwordValidation.isValid) {
                return@withContext Resource.Error(passwordValidation.errorMessage ?: "Invalid password.")
            }

            val normalizedEmail = email.trim().lowercase()
            val user = userDao.getUserByEmail(normalizedEmail)
                ?: return@withContext Resource.Error("No account registered with this email address. Please check your spelling or register.")

            if (!PasswordHasher.verify(passwordPlain, user.passwordHash)) {
                return@withContext Resource.Error("Incorrect password. Please verify your credentials and try again.")
            }

            if (!user.isActive) {
                return@withContext Resource.Error("This account has been disabled by an administrator. Please contact campus administration.")
            }

            // Persist session into SharedPreferences (passwords are NEVER stored)
            sessionManager.saveSession(
                userId = user.id,
                role = user.role,
                userName = user.fullName
            )

            Resource.Success(user)
        } catch (e: Exception) {
            Resource.Error("Authentication failed: ${e.localizedMessage ?: "Unknown error"}", e)
        }
    }

    override suspend fun registerStudent(
        email: String,
        passwordPlain: String,
        fullName: String,
        studentId: String,
        phoneNumber: String
    ): Resource<UserEntity> = withContext(Dispatchers.IO) {
        try {
            // Validate Full Name
            val nameValidation = CredentialValidator.validateFullName(fullName)
            if (!nameValidation.isValid) {
                return@withContext Resource.Error(nameValidation.errorMessage ?: "Invalid full name.")
            }

            // Validate Email
            val emailValidation = CredentialValidator.validateEmail(email)
            if (!emailValidation.isValid) {
                return@withContext Resource.Error(emailValidation.errorMessage ?: "Invalid email address.")
            }

            // Validate Student ID
            val idValidation = CredentialValidator.validateStudentId(studentId)
            if (!idValidation.isValid) {
                return@withContext Resource.Error(idValidation.errorMessage ?: "Student ID is required.")
            }

            // Validate Password
            val passwordValidation = CredentialValidator.validatePasswordForRegistration(passwordPlain)
            if (!passwordValidation.isValid) {
                return@withContext Resource.Error(passwordValidation.errorMessage ?: "Invalid password.")
            }

            val normalizedEmail = email.trim().lowercase()
            val existing = userDao.getUserByEmail(normalizedEmail)
            if (existing != null) {
                return@withContext Resource.Error("An account with this campus email already exists. Please sign in.")
            }

            // Requirement: Registration must always create a STUDENT account
            val newUser = UserEntity(
                id = UUID.randomUUID().toString(),
                email = normalizedEmail,
                passwordHash = PasswordHasher.hash(passwordPlain),
                fullName = fullName.trim(),
                studentOrStaffId = studentId.trim(),
                role = UserRole.STUDENT,
                phoneNumber = phoneNumber.trim()
            )
            userDao.insertUser(newUser)

            // Automatically start student session upon registration
            sessionManager.saveSession(
                userId = newUser.id,
                role = newUser.role,
                userName = newUser.fullName
            )

            Resource.Success(newUser)
        } catch (e: Exception) {
            Resource.Error("Registration failed: ${e.localizedMessage ?: "Unknown error"}", e)
        }
    }

    override fun logout() {
        sessionManager.clearSession()
    }

    override fun getCurrentUserRole(): UserRole? = sessionManager.getUserRole()

    override fun getCurrentUserId(): String? = sessionManager.getUserId()

    override fun isLoggedIn(): Boolean = sessionManager.isLoggedIn()
}
