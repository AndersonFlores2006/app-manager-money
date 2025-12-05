package com.example.gestor_money.data.repository

import com.example.gestor_money.data.local.dao.BudgetDao
import com.example.gestor_money.data.local.entities.BudgetEntity
import com.example.gestor_money.data.sync.EntityType
import com.example.gestor_money.data.sync.SyncManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BudgetRepository @Inject constructor(
    private val budgetDao: BudgetDao,
    private val syncManager: SyncManager,
    private val authRepository: AuthRepository,
    private val firestoreRepository: FirestoreRepository
) {
    private fun getUserId(): String = authRepository.getCurrentUserId() ?: "local_user"
    
    fun getBudgetsForMonth(month: Int, year: Int): Flow<List<BudgetEntity>> {
        return budgetDao.getBudgetsForMonth(getUserId(), month, year)
    }

    suspend fun getBudgetForCategory(categoryId: Long, month: Int, year: Int): BudgetEntity? {
        return budgetDao.getBudgetForCategory(getUserId(), categoryId, month, year)
    }

    suspend fun insertBudget(budget: BudgetEntity): Long {
        val userId = getUserId()
        val budgetWithUser = budget.copy(userId = userId)
        val id = budgetDao.insertBudget(budgetWithUser)
        
        // Sync with Firestore
        firestoreRepository.syncBudget(userId, budgetWithUser)
            .onSuccess { cloudId ->
                budgetDao.updateCloudId(id, cloudId)
            }
        
        syncManager.markForUpload(id, EntityType.BUDGET)
        return id
    }

    suspend fun updateBudget(budget: BudgetEntity) {
        val userId = getUserId()
        budgetDao.updateBudget(budget)
        
        // Sync with Firestore
        firestoreRepository.syncBudget(userId, budget)
        
        syncManager.markForUpload(budget.id, EntityType.BUDGET)
    }

    suspend fun deleteBudget(budget: BudgetEntity) {
        val userId = getUserId()
        budgetDao.deleteBudget(budget)
        
        // Delete from Firestore if it has cloudId
        budget.cloudId?.let { cloudId ->
            firestoreRepository.deleteBudget(userId, cloudId)
        }
    }
}
