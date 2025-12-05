package com.example.gestor_money.domain.usecases

import com.example.gestor_money.data.repository.TransactionRepository // Asumiendo que existe un TransactionRepository
import com.example.gestor_money.presentation.screens.transactions.viewmodel.TransactionItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTransactionsUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {

    operator fun invoke(): Flow<List<TransactionItem>> {
        // Aquí iría la lógica para obtener las transacciones del repositorio,
        // y mapearlas a TransactionItem si es necesario.
        // Por ahora, simulamos que el repositorio retorna TransactionItem directamente.
        return transactionRepository.getAllTransactions()
    }
}
