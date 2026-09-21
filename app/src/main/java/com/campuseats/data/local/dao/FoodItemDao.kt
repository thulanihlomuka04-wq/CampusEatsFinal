package com.campuseats.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.campuseats.data.local.entity.FoodItemEntity
import kotlinx.coroutines.flow.Flow

data class VendorItemCount(
    val vendorId: String,
    val count: Int
)

/**
 * Data Access Object for Food items and menu categories.
 */
@Dao
interface FoodItemDao {
    @Query("SELECT * FROM food_items WHERE vendorId = :vendorId ORDER BY category, name ASC")
    fun getMenuByVendor(vendorId: String): Flow<List<FoodItemEntity>>

    @Query("SELECT * FROM food_items WHERE vendorId = :vendorId AND isAvailable = 1 ORDER BY name ASC")
    fun getAvailableMenuByVendor(vendorId: String): Flow<List<FoodItemEntity>>

    @Query("SELECT COUNT(*) FROM food_items WHERE vendorId = :vendorId AND isAvailable = 1")
    fun getAvailableCountByVendor(vendorId: String): Flow<Int>

    @Query("SELECT vendorId, COUNT(*) as count FROM food_items WHERE isAvailable = 1 GROUP BY vendorId")
    fun getAvailableFoodCountsGrouped(): Flow<List<VendorItemCount>>

    @Query("SELECT * FROM food_items WHERE id = :itemId LIMIT 1")
    suspend fun getFoodItemById(itemId: String): FoodItemEntity?

    @Query("SELECT DISTINCT category FROM food_items WHERE vendorId = :vendorId")
    fun getCategoriesForVendor(vendorId: String): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodItem(item: FoodItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodItems(items: List<FoodItemEntity>)

    @Update
    suspend fun updateFoodItem(item: FoodItemEntity)

    @Delete
    suspend fun deleteFoodItem(item: FoodItemEntity)
}
