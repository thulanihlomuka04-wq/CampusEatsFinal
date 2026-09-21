package com.campuseats.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.campuseats.data.local.entity.VendorEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Campus Vendor records.
 */
@Dao
interface VendorDao {
    @Query("SELECT * FROM vendors ORDER BY name ASC")
    fun getAllVendors(): Flow<List<VendorEntity>>

    @Query("SELECT * FROM vendors WHERE isOpen = 1 ORDER BY rating DESC")
    fun getOpenVendors(): Flow<List<VendorEntity>>

    @Query("SELECT * FROM vendors WHERE id = :vendorId LIMIT 1")
    suspend fun getVendorById(vendorId: String): VendorEntity?

    @Query("SELECT * FROM vendors WHERE id = :vendorId LIMIT 1")
    fun observeVendorById(vendorId: String): Flow<VendorEntity?>

    @Query("SELECT COUNT(*) FROM vendors")
    fun getVendorCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVendor(vendor: VendorEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVendors(vendors: List<VendorEntity>)

    @Update
    suspend fun updateVendor(vendor: VendorEntity)

    @Delete
    suspend fun deleteVendor(vendor: VendorEntity)
}
