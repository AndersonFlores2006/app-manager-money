package com.example.gestor_money.data.repository

import com.example.gestor_money.data.local.dao.TransactionDao
import com.example.gestor_money.data.local.entities.TransactionEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao
) {
    fun getAllTransactions(): Flow<List<TransactionEntity>> {
        return transactionDao.getAllTransactions()
    }

    fun getTransactionsByDateRange(startDate: Long, endDate: Long): Flow<List<TransactionEntity>> {
        return transactionDao.getTransactionsByDateRange(startDate, endDate)
    }

    fun getTransactionsByCategory(categoryId: Long): Flow<List<TransactionEntity>> {
        return transactionDao.getTransactionsByCategory(categoryId)
    }

    suspend fun insertTransaction(transaction: TransactionEntity): Long {
        return transactionDao.insertTransaction(transaction)
    }

    suspend fun updateTransaction(transaction: TransactionEntity) {
        transactionDao.updateTransaction(transaction)
    }

    suspend fun deleteTransaction(transaction: TransactionEntity) {
        transactionDao.deleteTransaction(transaction)
    }

    fun getTotalByType(type: String): Flow<Double?> {
        return transactionDao.getTotalByType(type)
    }
}
