package com.example.gestor_money.presentation.screens.transactions.viewmodel

import com.example.gestor_money.domain.model.TransactionType

data class TransactionItem(
    val id: Long,
    val description: String,
    val amount: Double,
    val type: TransactionType,
    val date: Long
)
