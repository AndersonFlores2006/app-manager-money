package com.example.gestor_money.domain.usecase

import com.example.gestor_money.data.local.entities.TransactionEntity
import com.example.gestor_money.data.repository.TransactionRepository
import com.example.gestor_money.domain.model.Transaction
import javax.inject.Inject

class AddTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(transaction: Transaction): Result<Long> {
        return try {
            val entity = TransactionEntity(
                amount = transaction.amount,
                date = transaction.date,
                description = transaction.description,
                categoryId = transaction.categoryId,
                type = transaction.type.name
            )
            val id = transactionRepository.insertTransaction(entity)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
