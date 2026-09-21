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
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Orders and line items.
 */
@Dao
interface OrderDao {
    @Query("SELECT * FROM orders WHERE studentId = :studentId ORDER BY createdAt DESC")
    fun getOrdersByStudent(studentId: String): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE vendorId = :vendorId ORDER BY createdAt DESC")
    fun getOrdersByVendor(vendorId: String): Flow<List<OrderEntity>>

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

    @Query("SELECT SUM(totalAmount) FROM orders WHERE status = :status")
    fun getTotalRevenueByStatus(status: OrderStatus = OrderStatus.COLLECTED): Flow<Double?>

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
