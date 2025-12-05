package com.example.gestor_money.domain.model

data class FinancialSummary(
    val totalIncome: Double,
    val totalExpense: Double,
    val balance: Double,
    val recentTransactions: List<Transaction>
)
