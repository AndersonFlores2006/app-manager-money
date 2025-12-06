package com.example.gestor_money.data.repository

import android.util.Log
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
        val userId = getUserId()
        Log.d("CategoryRepository", "Getting all categories for user: $userId")
        return categoryDao.getAllCategories(userId)
    }

    suspend fun getCategoryById(id: Long): CategoryEntity? {
        return categoryDao.getCategoryById(id)
    }

    suspend fun insertCategory(category: CategoryEntity): Long {
        val userId = getUserId()
        Log.d("CategoryRepository", "Inserting category: ${category.name} for user: $userId")
        val categoryWithUser = category.copy(userId = userId)
        val id = categoryDao.insertCategory(categoryWithUser)
        Log.d("CategoryRepository", "Category inserted with id: $id")

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
        Log.d("CategoryRepository", "Updating category: ${category.name}")
        categoryDao.updateCategory(category)

        // Sync with Firestore
        firestoreRepository.syncCategory(userId, category)

        syncManager.markForUpload(category.id, EntityType.CATEGORY)
    }

    suspend fun deleteCategory(category: CategoryEntity) {
        val userId = getUserId()
        Log.d("CategoryRepository", "Deleting category: ${category.name}")
        categoryDao.deleteCategory(category)

        // Delete from Firestore if it has cloudId
        category.cloudId?.let { cloudId ->
            firestoreRepository.deleteCategory(userId, cloudId)
        }
    }

    suspend fun createDefaultCategories() {
        val userId = getUserId()
        val existingCategories = categoryDao.getAllCategoriesSync(userId)
        Log.d("CategoryRepository", "Creating default categories for user: $userId, existing: ${existingCategories.size}")

        // Only create default categories if user has none
        if (existingCategories.isEmpty()) {
            val defaultCategories = listOf(
                CategoryEntity(name = "Alimentación", icon = "🍽️", color = 0xFFFF6B6B.toInt(), type = "EXPENSE", userId = userId),
                CategoryEntity(name = "Transporte", icon = "🚗", color = 0xFF4ECDC4.toInt(), type = "EXPENSE", userId = userId),
                CategoryEntity(name = "Entretenimiento", icon = "🎬", color = 0xFF45B7D1.toInt(), type = "EXPENSE", userId = userId),
                CategoryEntity(name = "Salud", icon = "🏥", color = 0xFF96CEB4.toInt(), type = "EXPENSE", userId = userId),
                CategoryEntity(name = "Educación", icon = "📚", color = 0xFFFFEAA7.toInt(), type = "EXPENSE", userId = userId),
                CategoryEntity(name = "Salario", icon = "💼", color = 0xFFDDA0DD.toInt(), type = "INCOME", userId = userId),
                CategoryEntity(name = "Freelance", icon = "💻", color = 0xFF98D8C8.toInt(), type = "INCOME", userId = userId),
                CategoryEntity(name = "Inversiones", icon = "📈", color = 0xFFF7DC6F.toInt(), type = "INCOME", userId = userId)
            )

            defaultCategories.forEach { category ->
                categoryDao.insertCategory(category)
            }
            Log.d("CategoryRepository", "Default categories created")
        } else {
            Log.d("CategoryRepository", "Default categories already exist, skipping creation")
        }
    }
}
