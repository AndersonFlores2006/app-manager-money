package com.example.gestor_money.presentation.screens.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestor_money.data.repository.TransactionRepository
import com.example.gestor_money.domain.model.TransactionType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<StatsUiState>(StatsUiState.Loading)
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    init {
        loadStats()
    }

    private fun loadStats() {
        viewModelScope.launch {
            transactionRepository.getAllTransactions().collect { transactions ->
                if (transactions.isEmpty()) {
                    _uiState.value = StatsUiState.Empty
                    return@collect
                }

                // Calculate stats
                val expenses = transactions.filter { it.type == "EXPENSE" }
                val incomes = transactions.filter { it.type == "INCOME" }

                // Group by category (using description as category for now)
                val expensesByCategory = expenses
                    .groupBy { it.description }
                    .mapValues { it.value.sumOf { tx -> tx.amount } }
                    .toList()
                    .sortedByDescending { it.second }

                val totalExpense = expenses.sumOf { it.amount }
                val totalIncome = incomes.sumOf { it.amount }

                // Calculate percentages
                val categoryData = expensesByCategory.map { (category, amount) ->
                    CategoryData(
                        name = category,
                        amount = amount,
                        percentage = if (totalExpense > 0) (amount / totalExpense * 100).toFloat() else 0f
                    )
                }

                _uiState.value = StatsUiState.Success(
                    totalIncome = totalIncome,
                    totalExpense = totalExpense,
                    categoryData = categoryData
                )
            }
        }
    }
}

sealed class StatsUiState {
    object Loading : StatsUiState()
    object Empty : StatsUiState()
    data class Success(
        val totalIncome: Double,
        val totalExpense: Double,
        val categoryData: List<CategoryData>
    ) : StatsUiState()
}

data class CategoryData(
    val name: String,
    val amount: Double,
    val percentage: Float
)
