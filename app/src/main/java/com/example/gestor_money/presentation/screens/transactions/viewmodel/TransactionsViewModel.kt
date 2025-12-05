package com.example.gestor_money.presentation.screens.transactions.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestor_money.domain.model.TransactionType
import com.example.gestor_money.domain.usecase.DeleteTransactionUseCase
import com.example.gestor_money.domain.usecases.GetTransactionsUseCase // Asumiendo que este caso de uso existe
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val getTransactionsUseCase: GetTransactionsUseCase, // Inyectar el caso de uso para obtener transacciones
    private val deleteTransactionUseCase: DeleteTransactionUseCase
) : ViewModel() {

    private val _transactions = MutableStateFlow<List<TransactionItem>>(emptyList())
    val transactions: StateFlow<List<TransactionItem>> = _transactions.asStateFlow()

    init {
        loadTransactions()
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            try {
                // Asumiendo que GetTransactionsUseCase retorna un Flow de lista de TransactionItem
                getTransactionsUseCase().collect { transactionList ->
                    _transactions.value = transactionList
                }
            } catch (e: Exception) {
                // Manejar el error, por ejemplo, mostrando un mensaje al usuario
                println("Error loading transactions: ${e.message}")
                _transactions.value = emptyList() // O un estado de error
            }
        }
    }

    // Aquí podrías agregar funciones para eliminar, editar, filtrar, etc.
    fun deleteTransaction(id: Long) {
        viewModelScope.launch {
            deleteTransactionUseCase(id)
            loadTransactions() // Recargar la lista después de eliminar
        }
    }
    // fun filterTransactions(type: TransactionType?) { ... }
}
