package com.campuseats.data.repository

import com.campuseats.data.local.dao.OrderDao
import com.campuseats.data.local.dao.UserDao
import com.campuseats.data.local.dao.VendorDao
import com.campuseats.data.local.entity.OrderEntity
import com.campuseats.data.local.entity.UserEntity
import com.campuseats.data.local.entity.VendorEntity
import com.campuseats.security.PasswordHasher
import com.campuseats.security.UserRole
import com.campuseats.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.UUID

interface AdminRepository {
    fun getAllUsers(): Flow<List<UserEntity>>
    fun getAllVendors(): Flow<List<VendorEntity>>
    fun getAllOrders(): Flow<List<OrderEntity>>
    fun getUserCount(): Flow<Int>
    fun getVendorCount(): Flow<Int>
    fun getTotalOrdersCount(): Flow<Int>
    fun getTotalRevenue(): Flow<Double?>

    suspend fun deleteUser(userId: String): Resource<Unit>
    suspend fun addVendor(
        name: String,
        description: String,
        campusLocation: String,
        openingHours: String,
        vendorEmail: String? = null,
        vendorPasswordPlain: String? = null
    ): Resource<Unit>
    suspend fun toggleVendorStatus(vendor: VendorEntity): Resource<Unit>
    suspend fun deleteVendor(vendor: VendorEntity): Resource<Unit>
}

class AdminRepositoryImpl(
    private val userDao: UserDao,
    private val vendorDao: VendorDao,
    private val orderDao: OrderDao
) : AdminRepository {

    override fun getAllUsers(): Flow<List<UserEntity>> = userDao.getAllUsers()

    override fun getAllVendors(): Flow<List<VendorEntity>> = vendorDao.getAllVendors()

    override fun getAllOrders(): Flow<List<OrderEntity>> = orderDao.getAllOrders()

    override fun getUserCount(): Flow<Int> = userDao.getUserCount()

    override fun getVendorCount(): Flow<Int> = vendorDao.getVendorCount()

    override fun getTotalOrdersCount(): Flow<Int> = orderDao.getTotalOrdersCount()

    override fun getTotalRevenue(): Flow<Double?> = orderDao.getTotalRevenueByStatus()

    override suspend fun deleteUser(userId: String): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            userDao.deleteUserById(userId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Could not delete user: ${e.localizedMessage}", e)
        }
    }

    override suspend fun addVendor(
        name: String,
        description: String,
        campusLocation: String,
        openingHours: String,
        vendorEmail: String?,
        vendorPasswordPlain: String?
    ): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val vendorId = UUID.randomUUID().toString()
            val newVendor = VendorEntity(
                id = vendorId,
                name = name.trim(),
                description = description.trim(),
                campusLocation = campusLocation.trim(),
                openingHours = openingHours.trim(),
                isOpen = true
            )
            vendorDao.insertVendor(newVendor)

            if (!vendorEmail.isNullOrBlank() && !vendorPasswordPlain.isNullOrBlank()) {
                val vendorUser = UserEntity(
                    id = "user_vendor_$vendorId",
                    email = vendorEmail.trim().lowercase(),
                    passwordHash = PasswordHasher.hash(vendorPasswordPlain),
                    fullName = name.trim(),
                    studentOrStaffId = "VND-" + vendorId.take(6).uppercase(),
                    role = UserRole.VENDOR,
                    phoneNumber = ""
                )
                userDao.insertUser(vendorUser)
            }

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Could not add vendor: ${e.localizedMessage}", e)
        }
    }

    override suspend fun toggleVendorStatus(vendor: VendorEntity): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            vendorDao.updateVendor(vendor.copy(isOpen = !vendor.isOpen))
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Could not change vendor status: ${e.localizedMessage}", e)
        }
    }

    override suspend fun deleteVendor(vendor: VendorEntity): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            vendorDao.deleteVendor(vendor)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Could not delete vendor: ${e.localizedMessage}", e)
        }
    }
}
