package com.example.gestor_money.data.repository

import com.example.gestor_money.data.local.dao.TransactionDao
import com.example.gestor_money.data.local.entities.TransactionEntity
import com.example.gestor_money.data.sync.EntityType
import com.example.gestor_money.data.sync.SyncManager
import com.example.gestor_money.domain.model.TransactionType
import com.example.gestor_money.presentation.screens.transactions.viewmodel.TransactionItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao,
    private val syncManager: SyncManager,
    private val authRepository: AuthRepository,
    private val firestoreRepository: FirestoreRepository
) {

    private fun getUserId(): String = authRepository.getCurrentUserId() ?: "local_user"

    fun getAllTransactions(): Flow<List<TransactionItem>> {
        return transactionDao.getAllTransactions(getUserId()).map {
            it.map { entity -> entity.toTransactionItem() }
        }
    }

    suspend fun addTransaction(transaction: TransactionEntity): Long {
        val userId = getUserId()
        val transactionWithUser = transaction.copy(userId = userId)
        val id = transactionDao.insertTransaction(transactionWithUser)
        
        // Sync with Firestore
        firestoreRepository.syncTransaction(userId, transactionWithUser)
            .onSuccess { cloudId ->
                transactionDao.updateCloudId(id, cloudId)
            }
        
        syncManager.markForUpload(id, EntityType.TRANSACTION)
        return id
    }

    fun getTotalByType(type: String): Flow<Double?> {
        return transactionDao.getTotalByType(getUserId(), type)
    }

    suspend fun updateTransaction(transaction: TransactionEntity) {
        val userId = getUserId()
        transactionDao.updateTransaction(transaction)
        
        // Sync with Firestore
        firestoreRepository.syncTransaction(userId, transaction)
        
        syncManager.markForUpload(transaction.id, EntityType.TRANSACTION)
    }

    suspend fun deleteTransaction(transaction: TransactionEntity) {
        val userId = getUserId()
        transactionDao.deleteTransaction(transaction)
        
        // Delete from Firestore if it has cloudId
        transaction.cloudId?.let { cloudId ->
            firestoreRepository.deleteTransaction(userId, cloudId)
        }
    }

    suspend fun deleteTransaction(id: Long) {
        val entity = transactionDao.getTransactionById(id)
        entity?.let { 
            deleteTransaction(it)
        }
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
