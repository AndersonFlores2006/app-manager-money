package com.example.gestor_money.data.sync

import android.util.Log
import com.example.gestor_money.data.local.dao.BudgetDao
import com.example.gestor_money.data.local.dao.CategoryDao
import com.example.gestor_money.data.local.dao.TransactionDao
import com.example.gestor_money.data.local.entities.BudgetEntity
import com.example.gestor_money.data.local.entities.CategoryEntity
import com.example.gestor_money.data.local.entities.TransactionEntity
import com.example.gestor_money.data.remote.RemoteDataSource
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * SyncManager: Orchestrates synchronization between local Room database and Firebase
 *
 * Offline-First Strategy:
 * - Writes always go to Room first (instant response)
 * - Background sync pushes to cloud when online
 * - Reads always from Room (single source of truth)
 * - Conflict resolution: last-write-wins based on timestamp
 */
@Singleton
class SyncManager @Inject constructor(
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao,
    private val budgetDao: BudgetDao,
    private val remoteDataSource: RemoteDataSource,
    private val networkMonitor: NetworkMonitor,
    private val auth: FirebaseAuth
) {
    companion object {
        private const val TAG = "SyncManager"
    }

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    /**
     * Sync all pending local changes to the cloud
     * Called by WorkManager when online
     */
    suspend fun syncToCloud(): SyncResult {
        if (!networkMonitor.isCurrentlyConnected()) {
            Log.d(TAG, "No network connection, skipping sync")
            return SyncResult.NoNetwork
        }

        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.w(TAG, "No authenticated user, skipping sync")
            return SyncResult.NotAuthenticated
        }

        Log.d(TAG, "Starting sync to cloud...")

        var successCount = 0
        var errorCount = 0

        try {
            // Sync transactions
            val pendingTransactions = transactionDao.getPendingSyncTransactions(userId)
            Log.d(TAG, "Syncing ${pendingTransactions.size} transactions")

            pendingTransactions.forEach { transaction ->
                when (transaction.syncStatus) {
                    SyncStatus.PENDING_UPLOAD.name -> {
                        val result = if (transaction.cloudId == null) {
                            remoteDataSource.uploadTransaction(userId, transaction)
                        } else {
                            remoteDataSource.updateTransaction(userId, transaction)
                        }

                        result.onSuccess { cloudId ->
                            transactionDao.updateCloudId(transaction.id, cloudId as String)
                            successCount++
                        }.onFailure {
                            errorCount++
                            Log.e(TAG, "Failed to sync transaction ${transaction.id}", it)
                        }
                    }

                    SyncStatus.PENDING_DELETE.name -> {
                        transaction.cloudId?.let { cloudId ->
                            remoteDataSource.deleteTransaction(userId, cloudId).onSuccess {
                                successCount++
                            }.onFailure {
                                errorCount++
                                Log.e(TAG, "Failed to delete transaction ${transaction.id}", it)
                            }
                        }
                    }
                }
            }

            // Sync categories
            val pendingCategories = categoryDao.getPendingSyncCategories(userId)
            Log.d(TAG, "Syncing ${pendingCategories.size} categories")

            pendingCategories.forEach { category ->
                when (category.syncStatus) {
                    SyncStatus.PENDING_UPLOAD.name -> {
                        val result = if (category.cloudId == null) {
                            remoteDataSource.uploadCategory(userId, category)
                        } else {
                            remoteDataSource.updateCategory(userId, category)
                        }

                        result.onSuccess { cloudId ->
                            categoryDao.updateCloudId(category.id, cloudId as String)
                            successCount++
                        }.onFailure {
                            errorCount++
                            Log.e(TAG, "Failed to sync category ${category.id}", it)
                        }
                    }

                    SyncStatus.PENDING_DELETE.name -> {
                        category.cloudId?.let { cloudId ->
                            remoteDataSource.deleteCategory(userId, cloudId).onSuccess {
                                successCount++
                            }.onFailure {
                                errorCount++
                                Log.e(TAG, "Failed to delete category ${category.id}", it)
                            }
                        }
                    }
                }
            }

            // Sync budgets
            val pendingBudgets = budgetDao.getPendingSyncBudgets(userId)
            Log.d(TAG, "Syncing ${pendingBudgets.size} budgets")

            pendingBudgets.forEach { budget ->
                when (budget.syncStatus) {
                    SyncStatus.PENDING_UPLOAD.name -> {
                        val result = if (budget.cloudId == null) {
                            remoteDataSource.uploadBudget(userId, budget)
                        } else {
                            remoteDataSource.updateBudget(userId, budget)
                        }

                        result.onSuccess { cloudId ->
                            budgetDao.updateCloudId(budget.id, cloudId as String)
                            successCount++
                        }.onFailure {
                            errorCount++
                            Log.e(TAG, "Failed to sync budget ${budget.id}", it)
                        }
                    }

                    SyncStatus.PENDING_DELETE.name -> {
                        budget.cloudId?.let { cloudId ->
                            remoteDataSource.deleteBudget(userId, cloudId).onSuccess {
                                successCount++
                            }.onFailure {
                                errorCount++
                                Log.e(TAG, "Failed to delete budget ${budget.id}", it)
                            }
                        }
                    }
                }
            }

            Log.d(TAG, "Sync completed: $successCount succeeded, $errorCount failed")
            return if (errorCount == 0) {
                SyncResult.Success(successCount)
            } else {
                SyncResult.PartialSuccess(successCount, errorCount)
            }

        } catch (e: Exception) {
            Log.e(TAG, "Sync failed with exception", e)
            return SyncResult.Error(e)
        }
    }

    /**
     * Fetch data from cloud and merge with local database
     * Used on first app launch or when user wants to restore data
     */
    suspend fun syncFromCloud(): SyncResult {
        if (!networkMonitor.isCurrentlyConnected()) {
            return SyncResult.NoNetwork
        }

        val userId = auth.currentUser?.uid ?: return SyncResult.NotAuthenticated

        return try {
            Log.d(TAG, "Fetching data from cloud...")

            // Fetch all data from cloud
            val transactionsResult = remoteDataSource.getAllTransactions(userId)
            val categoriesResult = remoteDataSource.getAllCategories(userId)
            val budgetsResult = remoteDataSource.getAllBudgets(userId)

            var syncedCount = 0

            // Merge transactions
            transactionsResult.onSuccess { cloudTransactions ->
                cloudTransactions.forEach { cloudTx ->
                    // Check if we already have this item locally
                    val existingTransaction = transactionDao.getAllTransactions(userId).first()
                        .find { it.cloudId == cloudTx.cloudId }

                    if (existingTransaction == null) {
                        // New item from cloud, insert it
                        transactionDao.insertTransaction(cloudTx.copy(userId = userId))
                        syncedCount++
                    } else if (cloudTx.lastModified > existingTransaction.lastModified) {
                        // Cloud version is newer, update local
                        transactionDao.updateTransaction(
                            cloudTx.copy(id = existingTransaction.id, userId = userId)
                        )
                        syncedCount++
                    }
                }
            }

            // Merge categories
            categoriesResult.onSuccess { cloudCategories ->
                cloudCategories.forEach { cloudCat ->
                    val existingCategory = categoryDao.getAllCategories(userId).first()
                        .find { it.cloudId == cloudCat.cloudId }

                    if (existingCategory == null) {
                        categoryDao.insertCategory(cloudCat.copy(userId = userId))
                        syncedCount++
                    } else if (cloudCat.lastModified > existingCategory.lastModified) {
                        categoryDao.updateCategory(
                            cloudCat.copy(id = existingCategory.id, userId = userId)
                        )
                        syncedCount++
                    }
                }
            }

            // Merge budgets
            budgetsResult.onSuccess { cloudBudgets ->
                cloudBudgets.forEach { cloudBudget ->
                    val existingBudget = budgetDao.getBudgetsForMonth(
                        userId,
                        cloudBudget.month,
                        cloudBudget.year
                    ).first().find { it.cloudId == cloudBudget.cloudId }

                    if (existingBudget == null) {
                        budgetDao.insertBudget(cloudBudget.copy(userId = userId))
                        syncedCount++
                    } else if (cloudBudget.lastModified > existingBudget.lastModified) {
                        budgetDao.updateBudget(
                            cloudBudget.copy(id = existingBudget.id, userId = userId)
                        )
                        syncedCount++
                    }
                }
            }

            Log.d(TAG, "Cloud sync completed: $syncedCount items synced")
            SyncResult.Success(syncedCount)

        } catch (e: Exception) {
            Log.e(TAG, "Error syncing from cloud", e)
            SyncResult.Error(e)
        }
    }

    /**
     * Mark an item as pending upload
     */
    fun markForUpload(entityId: Long, entityType: EntityType) {
        scope.launch {
            try {
                when (entityType) {
                    EntityType.TRANSACTION -> transactionDao.updateSyncStatus(
                        entityId,
                        SyncStatus.PENDING_UPLOAD.name
                    )

                    EntityType.CATEGORY -> categoryDao.updateSyncStatus(
                        entityId,
                        SyncStatus.PENDING_UPLOAD.name
                    )

                    EntityType.BUDGET -> budgetDao.updateSyncStatus(
                        entityId,
                        SyncStatus.PENDING_UPLOAD.name
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error marking entity for upload", e)
            }
        }
    }
}

/**
 * Result of a sync operation
 */
sealed class SyncResult {
    data class Success(val itemsSynced: Int) : SyncResult()
    data class PartialSuccess(val succeeded: Int, val failed: Int) : SyncResult()
    data class Error(val exception: Exception) : SyncResult()
    object NoNetwork : SyncResult()
    object NotAuthenticated : SyncResult()
}

/**
 * Type of entity being synced
 */
enum class EntityType {
    TRANSACTION,
    CATEGORY,
    BUDGET
}
