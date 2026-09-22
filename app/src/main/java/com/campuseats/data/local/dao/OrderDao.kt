package com.campuseats.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.campuseats.data.local.entity.OrderEntity
import com.campuseats.data.local.entity.OrderItemEntity
import com.campuseats.data.local.entity.OrderStatus
import com.campuseats.data.local.entity.OrderWithItems
import kotlinx.coroutines.flow.Flow

data class OrderStatusCount(
    val status: OrderStatus,
    val count: Int
)

data class VendorOrderStat(
    val vendorId: String,
    val orderCount: Int,
    val totalRevenue: Double
)

data class PopularFoodItem(
    val foodName: String,
    val totalQuantity: Int,
    val totalRevenue: Double
)

/**
 * Data Access Object for Orders and line items.
 */
@Dao
interface OrderDao {
    @Query("SELECT * FROM orders WHERE studentId = :studentId ORDER BY createdAt DESC")
    fun getOrdersByStudent(studentId: String): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE vendorId = :vendorId ORDER BY createdAt DESC")
    fun getOrdersByVendor(vendorId: String): Flow<List<OrderEntity>>

    @Transaction
    @Query("SELECT * FROM orders WHERE vendorId = :vendorId ORDER BY createdAt DESC")
    fun getOrdersWithItemsByVendor(vendorId: String): Flow<List<OrderWithItems>>

    @Query("SELECT * FROM orders WHERE id = :orderId LIMIT 1")
    fun getOrderById(orderId: String): Flow<OrderEntity?>

    @Query("SELECT * FROM orders WHERE id = :orderId LIMIT 1")
    suspend fun getOrderByIdOnce(orderId: String): OrderEntity?

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    fun getOrderItems(orderId: String): Flow<List<OrderItemEntity>>

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    suspend fun getOrderItemsOnce(orderId: String): List<OrderItemEntity>

    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Query("SELECT COUNT(*) FROM orders")
    fun getTotalOrdersCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM orders WHERE status IN ('PLACED', 'ACCEPTED', 'PREPARING')")
    fun getPendingOrdersCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM orders WHERE status = 'COLLECTED'")
    fun getCompletedOrdersCount(): Flow<Int>

    @Query("SELECT SUM(totalAmount) FROM orders")
    fun getTotalOrderValue(): Flow<Double?>

    @Query("SELECT SUM(totalAmount) FROM orders WHERE status = :status")
    fun getTotalRevenueByStatus(status: OrderStatus = OrderStatus.COLLECTED): Flow<Double?>

    @Query("SELECT status, COUNT(*) as count FROM orders GROUP BY status")
    fun getOrderCountByStatus(): Flow<List<OrderStatusCount>>

    @Query("SELECT vendorId, COUNT(*) as orderCount, SUM(totalAmount) as totalRevenue FROM orders GROUP BY vendorId")
    fun getOrdersPerVendor(): Flow<List<VendorOrderStat>>

    @Query("SELECT foodName, SUM(quantity) as totalQuantity, SUM(unitPrice * quantity) as totalRevenue FROM order_items GROUP BY foodName ORDER BY totalQuantity DESC LIMIT 10")
    fun getPopularFoodItems(): Flow<List<PopularFoodItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItems(items: List<OrderItemEntity>)

    @Update
    suspend fun updateOrder(order: OrderEntity)

    @Query("UPDATE orders SET status = :status WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: String, status: OrderStatus)

    @Transaction
    suspend fun placeOrderWithItems(order: OrderEntity, items: List<OrderItemEntity>) {
        insertOrder(order)
        insertOrderItems(items)
    }
}
