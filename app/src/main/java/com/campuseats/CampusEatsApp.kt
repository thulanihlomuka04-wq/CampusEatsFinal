package com.campuseats

import android.app.Application
import com.campuseats.data.local.database.AppDatabase
import com.campuseats.data.repository.AdminRepository
import com.campuseats.data.repository.AdminRepositoryImpl
import com.campuseats.data.repository.AuthRepository
import com.campuseats.data.repository.AuthRepositoryImpl
import com.campuseats.data.repository.StudentRepository
import com.campuseats.data.repository.StudentRepositoryImpl
import com.campuseats.data.repository.VendorRepository
import com.campuseats.data.repository.VendorRepositoryImpl
import com.campuseats.security.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * Base Application class for Campus Eats.
 * Sets up dependency injection for the Room SQLite Database, Repositories, and SessionManager.
 */
class CampusEatsApp : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val database by lazy { AppDatabase.getDatabase(this, applicationScope) }
    val sessionManager by lazy { SessionManager(this) }

    val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(
            userDao = database.userDao(),
            sessionManager = sessionManager
        )
    }

    val studentRepository: StudentRepository by lazy {
        StudentRepositoryImpl(
            vendorDao = database.vendorDao(),
            foodItemDao = database.foodItemDao(),
            orderDao = database.orderDao()
        )
    }

    val vendorRepository: VendorRepository by lazy {
        VendorRepositoryImpl(
            vendorDao = database.vendorDao(),
            foodItemDao = database.foodItemDao(),
            orderDao = database.orderDao()
        )
    }

    val adminRepository: AdminRepository by lazy {
        AdminRepositoryImpl(
            userDao = database.userDao(),
            vendorDao = database.vendorDao(),
            orderDao = database.orderDao()
        )
    }
}
