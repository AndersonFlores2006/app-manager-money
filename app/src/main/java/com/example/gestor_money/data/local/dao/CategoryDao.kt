package com.example.gestor_money.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.gestor_money.data.local.entities.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories WHERE userId = :userId")
    fun getAllCategories(userId: String): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getCategoryById(id: Long): CategoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity): Long

    @Update
    suspend fun updateCategory(category: CategoryEntity)

    @Delete
    suspend fun deleteCategory(category: CategoryEntity)

    // Sync queries
    @Query("SELECT * FROM categories WHERE userId = :userId AND syncStatus != 'SYNCED'")
    suspend fun getPendingSyncCategories(userId: String): List<CategoryEntity>

    @Query("UPDATE categories SET syncStatus = :status, lastModified = :timestamp WHERE id = :id")
    suspend fun updateSyncStatus(id: Long, status: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE categories SET cloudId = :cloudId, syncStatus = 'SYNCED', lastModified = :timestamp WHERE id = :id")
    suspend fun updateCloudId(id: Long, cloudId: String, timestamp: Long = System.currentTimeMillis())

    @Query("SELECT * FROM categories WHERE userId = :userId")
    suspend fun getAllCategoriesSync(userId: String): List<CategoryEntity>
}
