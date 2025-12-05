package com.example.gestor_money.presentation.screens.chat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestor_money.data.ai.AiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val aiRepository: AiRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun sendMessage(message: String) {
        if (message.isBlank()) return

        val userMessage = ChatMessage(
            content = message,
            isUser = true,
            timestamp = System.currentTimeMillis()
        )

        _uiState.value = _uiState.value.copy(
            messages = _uiState.value.messages + userMessage,
            isLoading = true,
            currentInput = ""
        )

        viewModelScope.launch {
            aiRepository.sendMessage(message).fold(
                onSuccess = { response ->
                    val assistantMessage = ChatMessage(
                        content = response,
                        isUser = false,
                        timestamp = System.currentTimeMillis()
                    )
                    _uiState.value = _uiState.value.copy(
                        messages = _uiState.value.messages + assistantMessage,
                        isLoading = false
                    )
                },
                onFailure = { error ->
                    val errorMessage = ChatMessage(
                        content = "Error: ${error.message ?: "No se pudo conectar con el asistente"}",
                        isUser = false,
                        timestamp = System.currentTimeMillis(),
                        isError = true
                    )
                    _uiState.value = _uiState.value.copy(
                        messages = _uiState.value.messages + errorMessage,
                        isLoading = false
                    )
                }
            )
        }
    }

    fun getInvestmentAdvice() {
        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            aiRepository.getInvestmentAdvice().fold(
                onSuccess = { advice ->
                    val adviceMessage = ChatMessage(
                        content = advice,
                        isUser = false,
                        timestamp = System.currentTimeMillis()
                    )
                    _uiState.value = _uiState.value.copy(
                        messages = _uiState.value.messages + adviceMessage,
                        isLoading = false
                    )
                },
                onFailure = { error ->
                    val errorMessage = ChatMessage(
                        content = "Error al obtener consejos: ${error.message}",
                        isUser = false,
                        timestamp = System.currentTimeMillis(),
                        isError = true
                    )
                    _uiState.value = _uiState.value.copy(
                        messages = _uiState.value.messages + errorMessage,
                        isLoading = false
                    )
                }
            )
        }
    }

    fun updateInput(input: String) {
        _uiState.value = _uiState.value.copy(currentInput = input)
    }

    fun clearChat() {
        aiRepository.clearHistory()
        _uiState.value = ChatUiState()
    }
}

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val currentInput: String = "",
    val isLoading: Boolean = false
)

data class ChatMessage(
    val content: String,
    val isUser: Boolean,
    val timestamp: Long,
    val isError: Boolean = false
)
