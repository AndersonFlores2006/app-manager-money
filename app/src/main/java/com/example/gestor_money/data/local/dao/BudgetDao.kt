package com.example.gestor_money.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.gestor_money.data.local.entities.BudgetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    @Query("SELECT * FROM budgets WHERE userId = :userId AND month = :month AND year = :year")
    fun getBudgetsForMonth(userId: String, month: Int, year: Int): Flow<List<BudgetEntity>>

    @Query("SELECT * FROM budgets WHERE userId = :userId AND categoryId = :categoryId AND month = :month AND year = :year")
    suspend fun getBudgetForCategory(userId: String, categoryId: Long, month: Int, year: Int): BudgetEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: BudgetEntity): Long

    @Update
    suspend fun updateBudget(budget: BudgetEntity)

    @Delete
    suspend fun deleteBudget(budget: BudgetEntity)

    // Sync queries
    @Query("SELECT * FROM budgets WHERE userId = :userId AND syncStatus != 'SYNCED'")
    suspend fun getPendingSyncBudgets(userId: String): List<BudgetEntity>

    @Query("UPDATE budgets SET syncStatus = :status, lastModified = :timestamp WHERE id = :id")
    suspend fun updateSyncStatus(id: Long, status: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE budgets SET cloudId = :cloudId, syncStatus = 'SYNCED', lastModified = :timestamp WHERE id = :id")
    suspend fun updateCloudId(id: Long, cloudId: String, timestamp: Long = System.currentTimeMillis())
}
