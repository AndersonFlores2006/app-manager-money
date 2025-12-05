package com.example.gestor_money.data.remote

import com.example.gestor_money.data.local.entities.BudgetEntity
import com.example.gestor_money.data.local.entities.CategoryEntity
import com.example.gestor_money.data.local.entities.TransactionEntity

/**
 * Interface para operaciones con la base de datos en la nube
 */
interface RemoteDataSource {
    // Transaction operations
    suspend fun uploadTransaction(userId: String, transaction: TransactionEntity): Result<String>
    suspend fun updateTransaction(userId: String, transaction: TransactionEntity): Result<Unit>
    suspend fun deleteTransaction(userId: String, cloudId: String): Result<Unit>
    suspend fun getAllTransactions(userId: String): Result<List<TransactionEntity>>

    // Category operations
    suspend fun uploadCategory(userId: String, category: CategoryEntity): Result<String>
    suspend fun updateCategory(userId: String, category: CategoryEntity): Result<Unit>
    suspend fun deleteCategory(userId: String, cloudId: String): Result<Unit>
    suspend fun getAllCategories(userId: String): Result<List<CategoryEntity>>

    // Budget operations
    suspend fun uploadBudget(userId: String, budget: BudgetEntity): Result<String>
    suspend fun updateBudget(userId: String, budget: BudgetEntity): Result<Unit>
    suspend fun deleteBudget(userId: String, cloudId: String): Result<Unit>
    suspend fun getAllBudgets(userId: String): Result<List<BudgetEntity>>
}
