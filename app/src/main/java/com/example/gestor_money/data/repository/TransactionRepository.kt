package com.example.gestor_money.data.repository

import com.example.gestor_money.data.local.dao.TransactionDao // Asumiendo que existe un TransactionDao
import com.example.gestor_money.data.local.entities.TransactionEntity // Asumiendo que existe TransactionEntity
import com.example.gestor_money.domain.model.TransactionType
import com.example.gestor_money.presentation.screens.transactions.viewmodel.TransactionItem
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao
) {

    fun getAllTransactions(): Flow<List<TransactionItem>> {
        return transactionDao.getAllTransactions().map {
            it.map { entity -> entity.toTransactionItem() }
        }
    }

    suspend fun addTransaction(transaction: TransactionEntity): Long {
        return transactionDao.insertTransaction(transaction)
    }

    fun getTotalByType(type: String): Flow<Double?> {
        return transactionDao.getTotalByType(type)
    }

    suspend fun updateTransaction(transaction: TransactionEntity) {
        transactionDao.updateTransaction(transaction)
    }

    suspend fun deleteTransaction(transaction: TransactionEntity) {
        transactionDao.deleteTransaction(transaction)
    }

    suspend fun deleteTransaction(id: Long) {
        val entity = transactionDao.getTransactionById(id)
        entity?.let { transactionDao.deleteTransaction(it) }
    }
}

// Extension function para mapear de Entity a TransactionItem (o viceversa si es necesario)
fun TransactionEntity.toTransactionItem(): TransactionItem {
    return TransactionItem(
        id = this.id,
        description = this.description,
        amount = this.amount,
        type = TransactionType.valueOf(this.type),
        date = this.date
    )
}

// NOTA: También necesitarías una función para mapear de TransactionItem a TransactionEntity
// fun TransactionItem.toTransactionEntity(): TransactionEntity { ... }
