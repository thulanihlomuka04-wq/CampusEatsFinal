package com.campuseats.data.repository

import com.campuseats.data.local.dao.FoodItemDao
import com.campuseats.data.local.dao.OrderDao
import com.campuseats.data.local.dao.VendorDao
import com.campuseats.data.local.entity.FoodItemEntity
import com.campuseats.data.local.entity.OrderEntity
import com.campuseats.data.local.entity.OrderItemEntity
import com.campuseats.data.local.entity.OrderStatus
import com.campuseats.data.local.entity.VendorEntity
import com.campuseats.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID
import kotlin.random.Random

data class CartItem(
    val foodItem: FoodItemEntity,
    val quantity: Int
)

interface StudentRepository {
    fun getAllVendors(): Flow<List<VendorEntity>>
    suspend fun getVendorById(vendorId: String): VendorEntity?
    fun getMenuForVendor(vendorId: String): Flow<List<FoodItemEntity>>
    fun getAvailableFoodCounts(): Flow<Map<String, Int>>
    fun getStudentOrders(studentId: String): Flow<List<OrderEntity>>
    fun getOrderById(orderId: String): Flow<OrderEntity?>
    fun getOrderItems(orderId: String): Flow<List<OrderItemEntity>>

    // Cart management
    val cartItems: StateFlow<List<CartItem>>
    val currentCartVendorId: StateFlow<String?>
    fun addToCart(item: FoodItemEntity)
    fun removeFromCart(itemId: String)
    fun updateQuantity(itemId: String, quantity: Int)
    fun clearCart()

    suspend fun placeOrder(
        studentId: String,
        studentName: String,
        notes: String
    ): Resource<OrderEntity>
}

class StudentRepositoryImpl(
    private val vendorDao: VendorDao,
    private val foodItemDao: FoodItemDao,
    private val orderDao: OrderDao
) : StudentRepository {

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    override val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _currentCartVendorId = MutableStateFlow<String?>(null)
    override val currentCartVendorId: StateFlow<String?> = _currentCartVendorId.asStateFlow()

    override fun getAllVendors(): Flow<List<VendorEntity>> = vendorDao.getAllVendors()

    override suspend fun getVendorById(vendorId: String): VendorEntity? = withContext(Dispatchers.IO) {
        vendorDao.getVendorById(vendorId)
    }

    override fun getMenuForVendor(vendorId: String): Flow<List<FoodItemEntity>> =
        foodItemDao.getMenuByVendor(vendorId)

    override fun getAvailableFoodCounts(): Flow<Map<String, Int>> =
        foodItemDao.getAvailableFoodCountsGrouped().map { list ->
            list.associate { it.vendorId to it.count }
        }

    override fun getStudentOrders(studentId: String): Flow<List<OrderEntity>> =
        orderDao.getOrdersByStudent(studentId)

    override fun getOrderById(orderId: String): Flow<OrderEntity?> =
        orderDao.getOrderById(orderId)

    override fun getOrderItems(orderId: String): Flow<List<OrderItemEntity>> =
        orderDao.getOrderItems(orderId)

    override fun addToCart(item: FoodItemEntity) {
        if (!item.isAvailable) return

        val currentVendor = _currentCartVendorId.value
        // If adding from a different vendor, reset cart to new vendor's items
        if (currentVendor != null && currentVendor != item.vendorId) {
            _cartItems.value = listOf(CartItem(foodItem = item, quantity = 1))
            _currentCartVendorId.value = item.vendorId
            return
        }

        _currentCartVendorId.value = item.vendorId
        val current = _cartItems.value.toMutableList()
        val index = current.indexOfFirst { it.foodItem.id == item.id }
        if (index != -1) {
            val existing = current[index]
            current[index] = existing.copy(quantity = existing.quantity + 1)
        } else {
            current.add(CartItem(foodItem = item, quantity = 1))
        }
        _cartItems.value = current
    }

    override fun removeFromCart(itemId: String) {
        val current = _cartItems.value.filterNot { it.foodItem.id == itemId }
        _cartItems.value = current
        if (current.isEmpty()) {
            _currentCartVendorId.value = null
        }
    }

    override fun updateQuantity(itemId: String, quantity: Int) {
        if (quantity <= 0) {
            removeFromCart(itemId)
            return
        }
        val current = _cartItems.value.toMutableList()
        val index = current.indexOfFirst { it.foodItem.id == itemId }
        if (index != -1) {
            current[index] = current[index].copy(quantity = quantity)
            _cartItems.value = current
        }
    }

    override fun clearCart() {
        _cartItems.value = emptyList()
        _currentCartVendorId.value = null
    }

    override suspend fun placeOrder(
        studentId: String,
        studentName: String,
        notes: String
    ): Resource<OrderEntity> = withContext(Dispatchers.IO) {
        val items = _cartItems.value
        val vendorId = _currentCartVendorId.value

        if (items.isEmpty() || vendorId == null) {
            return@withContext Resource.Error("Cart is empty. Please select food items first.")
        }

        val vendor = vendorDao.getVendorById(vendorId)
            ?: return@withContext Resource.Error("Vendor details not found.")

        try {
            val orderId = UUID.randomUUID().toString()
            val orderNumber = "CE-${Random.nextInt(1000, 9999)}"
            val pickupPin = "%04d".format(Random.nextInt(0, 10000))
            val total = items.sumOf { it.foodItem.price * it.quantity }

            val orderEntity = OrderEntity(
                id = orderId,
                orderNumber = orderNumber,
                studentId = studentId,
                studentName = studentName,
                vendorId = vendorId,
                vendorName = vendor.name,
                totalAmount = total,
                status = OrderStatus.PLACED,
                notes = notes,
                pickupPin = pickupPin,
                createdAt = System.currentTimeMillis(),
                estimatedPickupMinutes = vendor.estimatedPrepTimeMinutes
            )

            val orderItemEntities = items.map {
                OrderItemEntity(
                    id = UUID.randomUUID().toString(),
                    orderId = orderId,
                    foodItemId = it.foodItem.id,
                    foodName = it.foodItem.name,
                    unitPrice = it.foodItem.price,
                    quantity = it.quantity
                )
            }

            orderDao.placeOrderWithItems(orderEntity, orderItemEntities)
            clearCart()
            Resource.Success(orderEntity)
        } catch (e: Exception) {
            Resource.Error("Failed to place order: ${e.localizedMessage ?: "Unknown error"}", e)
        }
    }
}
