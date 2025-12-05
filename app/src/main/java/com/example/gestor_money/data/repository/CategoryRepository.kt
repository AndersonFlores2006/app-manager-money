package com.example.gestor_money.data.repository

import com.example.gestor_money.data.local.dao.CategoryDao
import com.example.gestor_money.data.local.entities.CategoryEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(
    private val categoryDao: CategoryDao
) {
    fun getAllCategories(): Flow<List<CategoryEntity>> {
        return categoryDao.getAllCategories()
    }

    suspend fun getCategoryById(id: Long): CategoryEntity? {
        return categoryDao.getCategoryById(id)
    }

    suspend fun insertCategory(category: CategoryEntity): Long {
        return categoryDao.insertCategory(category)
    }

    suspend fun updateCategory(category: CategoryEntity) {
        categoryDao.updateCategory(category)
    }

    suspend fun deleteCategory(category: CategoryEntity) {
        categoryDao.deleteCategory(category)
    }
}
