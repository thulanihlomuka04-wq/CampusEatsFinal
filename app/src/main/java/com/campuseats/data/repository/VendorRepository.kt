package com.campuseats.data.repository

import com.campuseats.data.local.dao.FoodItemDao
import com.campuseats.data.local.dao.OrderDao
import com.campuseats.data.local.dao.VendorDao
import com.campuseats.data.local.entity.FoodItemEntity
import com.campuseats.data.local.entity.OrderEntity
import com.campuseats.data.local.entity.OrderStatus
import com.campuseats.data.local.entity.VendorEntity
import com.campuseats.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.UUID

interface VendorRepository {
    fun getVendorDetails(vendorId: String): Flow<VendorEntity?>
    fun getFoodItemsForVendor(vendorId: String): Flow<List<FoodItemEntity>>
    fun getOrdersForVendor(vendorId: String): Flow<List<OrderEntity>>
    suspend fun addFoodItem(
        vendorId: String,
        name: String,
        description: String,
        price: Double,
        category: String,
        isVegetarian: Boolean
    ): Resource<Unit>
    suspend fun updateFoodItem(item: FoodItemEntity): Resource<Unit>
    suspend fun toggleFoodAvailability(item: FoodItemEntity): Resource<Unit>
    suspend fun deleteFoodItem(item: FoodItemEntity): Resource<Unit>
    suspend fun updateOrderStatus(orderId: String, newStatus: OrderStatus): Resource<Unit>
}

class VendorRepositoryImpl(
    private val vendorDao: VendorDao,
    private val foodItemDao: FoodItemDao,
    private val orderDao: OrderDao
) : VendorRepository {

    override fun getVendorDetails(vendorId: String): Flow<VendorEntity?> =
        vendorDao.observeVendorById(vendorId)

    override fun getFoodItemsForVendor(vendorId: String): Flow<List<FoodItemEntity>> =
        foodItemDao.getMenuByVendor(vendorId)

    override fun getOrdersForVendor(vendorId: String): Flow<List<OrderEntity>> =
        orderDao.getOrdersByVendor(vendorId)

    override suspend fun addFoodItem(
        vendorId: String,
        name: String,
        description: String,
        price: Double,
        category: String,
        isVegetarian: Boolean
    ): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val item = FoodItemEntity(
                id = UUID.randomUUID().toString(),
                vendorId = vendorId,
                name = name.trim(),
                description = description.trim(),
                price = price,
                category = category.trim(),
                isAvailable = true,
                isVegetarian = isVegetarian
            )
            foodItemDao.insertFoodItem(item)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Could not add food item: ${e.localizedMessage ?: "Unknown error"}", e)
        }
    }

    override suspend fun updateFoodItem(item: FoodItemEntity): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            foodItemDao.updateFoodItem(item)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Could not update food item: ${e.localizedMessage}", e)
        }
    }

    override suspend fun toggleFoodAvailability(item: FoodItemEntity): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            foodItemDao.updateFoodItem(item.copy(isAvailable = !item.isAvailable))
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Could not change availability: ${e.localizedMessage}", e)
        }
    }

    override suspend fun deleteFoodItem(item: FoodItemEntity): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            foodItemDao.deleteFoodItem(item)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Could not remove item: ${e.localizedMessage}", e)
        }
    }

    override suspend fun updateOrderStatus(orderId: String, newStatus: OrderStatus): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            orderDao.updateOrderStatus(orderId, newStatus)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error("Could not update order status: ${e.localizedMessage}", e)
        }
    }
}
