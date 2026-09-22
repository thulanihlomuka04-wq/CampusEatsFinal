package com.campuseats.data.repository

import com.campuseats.data.local.dao.FoodItemDao
import com.campuseats.data.local.dao.OrderDao
import com.campuseats.data.local.dao.OrderStatusCount
import com.campuseats.data.local.dao.PopularFoodItem
import com.campuseats.data.local.dao.UserDao
import com.campuseats.data.local.dao.VendorDao
import com.campuseats.data.local.dao.VendorItemCount
import com.campuseats.data.local.dao.VendorOrderStat
import com.campuseats.data.local.entity.OrderEntity
import com.campuseats.data.local.entity.OrderStatus
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
    fun getStudentCount(): Flow<Int>
    fun getVendorCount(): Flow<Int>
    fun getTotalOrdersCount(): Flow<Int>
    fun getPendingOrdersCount(): Flow<Int>
    fun getCompletedOrdersCount(): Flow<Int>
    fun getTotalRevenue(): Flow<Double?>
    fun getTotalOrderValue(): Flow<Double?>
    fun getOrderStatusCounts(): Flow<List<OrderStatusCount>>
    fun getOrdersPerVendor(): Flow<List<VendorOrderStat>>
    fun getPopularFoodItems(): Flow<List<PopularFoodItem>>
    fun getTotalFoodCountsGrouped(): Flow<List<VendorItemCount>>

    suspend fun toggleUserActiveStatus(userId: String, currentActive: Boolean): Resource<Unit>
    suspend fun deleteUser(userId: String): Resource<Unit>
    suspend fun addVendor(
        name: String,
        description: String,
        campusLocation: String,
        openingHours: String,
        vendorEmail: String? = null,
        vendorPasswordPlain: String? = null
    ): Resource<Unit>
    suspend fun updateVendor(
        vendorId: String,
        name: String,
        description: String,
        campusLocation: String,
        openingHours: String
    ): Resource<Unit>
    suspend fun assignVendorCredentials(
        vendorId: String,
        vendorName: String,
        email: String,
        passwordPlain: String
    ): Resource<Unit>
    suspend fun toggleVendorStatus(vendor: VendorEntity): Resource<Unit>
    suspend fun deleteVendor(vendor: VendorEntity): Resource<Unit>
}

class AdminRepositoryImpl(
    private val userDao: UserDao,
    private val vendorDao: VendorDao,
    private val orderDao: OrderDao,
    private val foodItemDao: FoodItemDao
) : AdminRepository {

    override fun getAllUsers(): Flow<List<UserEntity>> = userDao.getAllUsers()

    override fun getAllVendors(): Flow<List<VendorEntity>> = vendorDao.getAllVendors()

    override fun getAllOrders(): Flow<List<OrderEntity>> = orderDao.getAllOrders()

    override fun getUserCount(): Flow<Int> = userDao.getUserCount()

    override fun getStudentCount(): Flow<Int> = userDao.getUserCountByRole(UserRole.STUDENT)

    override fun getVendorCount(): Flow<Int> = vendorDao.getVendorCount()

    override fun getTotalOrdersCount(): Flow<Int> = orderDao.getTotalOrdersCount()

    override fun getPendingOrdersCount(): Flow<Int> = orderDao.getPendingOrdersCount()

    override fun getCompletedOrdersCount(): Flow<Int> = orderDao.getCompletedOrdersCount()

    override fun getTotalRevenue(): Flow<Double?> = orderDao.getTotalRevenueByStatus(OrderStatus.COLLECTED)

    override fun getTotalOrderValue(): Flow<Double?> = orderDao.getTotalOrderValue()

    override fun getOrderStatusCounts(): Flow<List<OrderStatusCount>> = orderDao.getOrderCountByStatus()

    override fun getOrdersPerVendor(): Flow<List<VendorOrderStat>> = orderDao.getOrdersPerVendor()

    override fun getPopularFoodItems(): Flow<List<PopularFoodItem>> = orderDao.getPopularFoodItems()

    override fun getTotalFoodCountsGrouped(): Flow<List<VendorItemCount>> = foodItemDao.getTotalFoodCountsGrouped()

    override suspend fun toggleUserActiveStatus(userId: String, currentActive: Boolean): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            userDao.setUserActiveStatus(userId, !currentActive)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Could not update user status: ${e.localizedMessage}", e)
        }
    }

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
            val cleanEmail = vendorEmail?.trim()?.lowercase() ?: ""
            val newVendor = VendorEntity(
                id = vendorId,
                name = name.trim(),
                description = description.trim(),
                campusLocation = campusLocation.trim(),
                openingHours = openingHours.trim().ifBlank { "08:00 - 17:00" },
                isOpen = true,
                vendorEmail = cleanEmail
            )
            vendorDao.insertVendor(newVendor)

            if (cleanEmail.isNotBlank() && !vendorPasswordPlain.isNullOrBlank()) {
                val existing = userDao.getUserByEmail(cleanEmail)
                if (existing != null) {
                    // Update existing or update password
                    userDao.updateUser(
                        existing.copy(
                            passwordHash = PasswordHasher.hash(vendorPasswordPlain),
                            fullName = name.trim(),
                            role = UserRole.VENDOR
                        )
                    )
                } else {
                    val vendorUser = UserEntity(
                        id = "user_vendor_$vendorId",
                        email = cleanEmail,
                        passwordHash = PasswordHasher.hash(vendorPasswordPlain),
                        fullName = name.trim(),
                        studentOrStaffId = "VND-" + vendorId.take(6).uppercase(),
                        role = UserRole.VENDOR,
                        phoneNumber = "",
                        isActive = true
                    )
                    userDao.insertUser(vendorUser)
                }
            }

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Could not add vendor: ${e.localizedMessage}", e)
        }
    }

    override suspend fun updateVendor(
        vendorId: String,
        name: String,
        description: String,
        campusLocation: String,
        openingHours: String
    ): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val existing = vendorDao.getVendorById(vendorId)
                ?: return@withContext Resource.Error("Vendor not found.")
            val updated = existing.copy(
                name = name.trim(),
                description = description.trim(),
                campusLocation = campusLocation.trim(),
                openingHours = openingHours.trim().ifBlank { existing.openingHours }
            )
            vendorDao.updateVendor(updated)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Could not update vendor: ${e.localizedMessage}", e)
        }
    }

    override suspend fun assignVendorCredentials(
        vendorId: String,
        vendorName: String,
        email: String,
        passwordPlain: String
    ): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val normalizedEmail = email.trim().lowercase()
            if (normalizedEmail.isBlank()) {
                return@withContext Resource.Error("Email address cannot be empty.")
            }
            if (passwordPlain.isBlank()) {
                return@withContext Resource.Error("Password cannot be empty.")
            }

            // Update vendor email field
            vendorDao.updateVendorEmail(vendorId, normalizedEmail)

            // Look up if user already exists
            val existingUser = userDao.getUserByEmail(normalizedEmail)
            if (existingUser != null) {
                userDao.updateUser(
                    existingUser.copy(
                        passwordHash = PasswordHasher.hash(passwordPlain),
                        fullName = vendorName.trim(),
                        role = UserRole.VENDOR,
                        isActive = true
                    )
                )
            } else {
                val newVendorUser = UserEntity(
                    id = "user_vendor_$vendorId",
                    email = normalizedEmail,
                    passwordHash = PasswordHasher.hash(passwordPlain),
                    fullName = vendorName.trim(),
                    studentOrStaffId = "VND-" + vendorId.take(6).uppercase(),
                    role = UserRole.VENDOR,
                    phoneNumber = "",
                    isActive = true
                )
                userDao.insertUser(newVendorUser)
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Could not assign vendor credentials: ${e.localizedMessage}", e)
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
