package com.example.gestor_money.presentation.screens.add_transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestor_money.domain.model.Transaction
import com.example.gestor_money.domain.model.TransactionType
import com.example.gestor_money.domain.usecase.AddTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val addTransactionUseCase: AddTransactionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddTransactionUiState())
    val uiState: StateFlow<AddTransactionUiState> = _uiState.asStateFlow()

    fun onAmountChange(amount: String) {
        _uiState.value = _uiState.value.copy(amount = amount)
    }

    fun onDescriptionChange(description: String) {
        _uiState.value = _uiState.value.copy(description = description)
    }

    fun onTypeChange(type: TransactionType) {
        _uiState.value = _uiState.value.copy(type = type)
    }

    fun saveTransaction(onSuccess: () -> Unit) {
        val state = _uiState.value
        
        if (state.amount.toDoubleOrNull() == null || state.amount.toDouble() <= 0) {
            _uiState.value = state.copy(error = "Ingresa un monto válido")
            return
        }
        
        if (state.description.isBlank()) {
            _uiState.value = state.copy(error = "Ingresa una descripción")
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, error = null)
            
            val transaction = Transaction(
                amount = state.amount.toDouble(),
                date = System.currentTimeMillis(),
                description = state.description,
                categoryId = null,
                type = state.type
            )
            
            addTransactionUseCase(transaction).fold(
                onSuccess = {
                    _uiState.value = AddTransactionUiState()
                    onSuccess()
                },
                onFailure = { error ->
                    _uiState.value = state.copy(
                        isLoading = false,
                        error = error.message ?: "Error al guardar"
                    )
                }
            )
        }
    }
}

data class AddTransactionUiState(
    val amount: String = "",
    val description: String = "",
    val type: TransactionType = TransactionType.EXPENSE,
    val isLoading: Boolean = false,
    val error: String? = null
)
