package com.example.gestor_money.presentation.screens.add_transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestor_money.data.local.entities.CategoryEntity
import com.example.gestor_money.data.repository.CategoryRepository
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
    private val addTransactionUseCase: AddTransactionUseCase,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddTransactionUiState())
    val uiState: StateFlow<AddTransactionUiState> = _uiState.asStateFlow()

    private val _categories = MutableStateFlow<List<CategoryEntity>>(emptyList())
    val categories: StateFlow<List<CategoryEntity>> = _categories.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            categoryRepository.getAllCategories().collect { categories ->
                _categories.value = categories
            }
        }
    }

    fun onAmountChange(amount: String) {
        _uiState.value = _uiState.value.copy(amount = amount)
    }

    fun onDescriptionChange(description: String) {
        _uiState.value = _uiState.value.copy(description = description)
    }

    fun onTypeChange(type: TransactionType) {
        _uiState.value = _uiState.value.copy(type = type)
    }

    fun onCategoryChange(categoryId: Long?) {
        _uiState.value = _uiState.value.copy(selectedCategoryId = categoryId)
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
                categoryId = state.selectedCategoryId,
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
    val selectedCategoryId: Long? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
