package com.example.gestor_money.presentation.screens.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestor_money.domain.usecase.ExportToExcelUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val exportToExcelUseCase: ExportToExcelUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun exportToExcel() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isExporting = true, exportError = null)
            
            exportToExcelUseCase().fold(
                onSuccess = { uri ->
                    _uiState.value = _uiState.value.copy(
                        isExporting = false,
                        exportedUri = uri,
                        exportSuccess = true
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isExporting = false,
                        exportError = error.message ?: "Error al exportar"
                    )
                }
            )
        }
    }

    fun clearExportStatus() {
        _uiState.value = _uiState.value.copy(
            exportSuccess = false,
            exportError = null,
            exportedUri = null
        )
    }
}

data class SettingsUiState(
    val isExporting: Boolean = false,
    val exportSuccess: Boolean = false,
    val exportError: String? = null,
    val exportedUri: Uri? = null
)
