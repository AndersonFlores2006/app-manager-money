package com.example.gestor_money.data.repository

import com.example.gestor_money.data.local.dao.BudgetDao
import com.example.gestor_money.data.local.entities.BudgetEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BudgetRepository @Inject constructor(
    private val budgetDao: BudgetDao
) {
    fun getBudgetsForMonth(month: Int, year: Int): Flow<List<BudgetEntity>> {
        return budgetDao.getBudgetsForMonth(month, year)
    }

    suspend fun getBudgetForCategory(categoryId: Long, month: Int, year: Int): BudgetEntity? {
        return budgetDao.getBudgetForCategory(categoryId, month, year)
    }

    suspend fun insertBudget(budget: BudgetEntity): Long {
        return budgetDao.insertBudget(budget)
    }

    suspend fun updateBudget(budget: BudgetEntity) {
        budgetDao.updateBudget(budget)
    }

    suspend fun deleteBudget(budget: BudgetEntity) {
        budgetDao.deleteBudget(budget)
    }
}
