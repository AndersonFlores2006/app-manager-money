package com.example.gestor_money.presentation.screens.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestor_money.domain.model.FinancialSummary
import com.example.gestor_money.domain.usecase.GetFinancialSummaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getFinancialSummaryUseCase: GetFinancialSummaryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadFinancialSummary()
    }

    private fun loadFinancialSummary() {
        viewModelScope.launch {
            getFinancialSummaryUseCase().collect { summary ->
                _uiState.value = HomeUiState.Success(summary)
            }
        }
    }
}

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(val summary: FinancialSummary) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}
