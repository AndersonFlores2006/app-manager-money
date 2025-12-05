package com.example.gestor_money.data.repository

import com.example.gestor_money.data.local.dao.CategoryDao
import com.example.gestor_money.data.local.entities.CategoryEntity
import com.example.gestor_money.data.sync.EntityType
import com.example.gestor_money.data.sync.SyncManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(
    private val categoryDao: CategoryDao,
    private val syncManager: SyncManager,
    private val authRepository: AuthRepository,
    private val firestoreRepository: FirestoreRepository
) {
    private fun getUserId(): String = authRepository.getCurrentUserId() ?: "local_user"
    
    fun getAllCategories(): Flow<List<CategoryEntity>> {
        return categoryDao.getAllCategories(getUserId())
    }

    suspend fun getCategoryById(id: Long): CategoryEntity? {
        return categoryDao.getCategoryById(id)
    }

    suspend fun insertCategory(category: CategoryEntity): Long {
        val userId = getUserId()
        val categoryWithUser = category.copy(userId = userId)
        val id = categoryDao.insertCategory(categoryWithUser)
        
        // Sync with Firestore
        firestoreRepository.syncCategory(userId, categoryWithUser)
            .onSuccess { cloudId ->
                categoryDao.updateCloudId(id, cloudId)
            }
        
        syncManager.markForUpload(id, EntityType.CATEGORY)
        return id
    }

    suspend fun updateCategory(category: CategoryEntity) {
        val userId = getUserId()
        categoryDao.updateCategory(category)
        
        // Sync with Firestore
        firestoreRepository.syncCategory(userId, category)
        
        syncManager.markForUpload(category.id, EntityType.CATEGORY)
    }

    suspend fun deleteCategory(category: CategoryEntity) {
        val userId = getUserId()
        categoryDao.deleteCategory(category)
        
        // Delete from Firestore if it has cloudId
        category.cloudId?.let { cloudId ->
            firestoreRepository.deleteCategory(userId, cloudId)
        }
    }
}
