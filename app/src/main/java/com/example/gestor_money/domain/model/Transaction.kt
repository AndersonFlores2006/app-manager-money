package com.example.gestor_money.domain.model

data class Transaction(
    val id: Long = 0,
    val amount: Double,
    val date: Long,
    val description: String,
    val categoryId: Long?,
    val categoryName: String? = null,
    val categoryIcon: String? = null,
    val type: TransactionType
)

enum class TransactionType {
    INCOME,
    EXPENSE
}
