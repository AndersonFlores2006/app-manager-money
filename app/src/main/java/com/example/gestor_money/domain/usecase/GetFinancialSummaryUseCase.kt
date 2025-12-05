package com.example.gestor_money.domain.usecase

import com.example.gestor_money.data.local.entities.TransactionEntity
import com.example.gestor_money.data.repository.TransactionRepository
import com.example.gestor_money.domain.model.FinancialSummary
import com.example.gestor_money.domain.model.Transaction
import com.example.gestor_money.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetFinancialSummaryUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    operator fun invoke(): Flow<FinancialSummary> {
        return combine(
            transactionRepository.getTotalByType("INCOME"),
            transactionRepository.getTotalByType("EXPENSE"),
            transactionRepository.getAllTransactions()
        ) { income, expense, transactions ->
            val totalIncome = income ?: 0.0
            val totalExpense = expense ?: 0.0
            val balance = totalIncome - totalExpense
            
            val recentTransactions = transactions.take(5).map { it.toDomain() }
            
            FinancialSummary(
                totalIncome = totalIncome,
                totalExpense = totalExpense,
                balance = balance,
                recentTransactions = recentTransactions
            )
        }
    }
    
    private fun TransactionEntity.toDomain(): Transaction {
        return Transaction(
            id = id,
            amount = amount,
            date = date,
            description = description,
            categoryId = categoryId,
            type = if (type == "INCOME") TransactionType.INCOME else TransactionType.EXPENSE
        )
    }
}
