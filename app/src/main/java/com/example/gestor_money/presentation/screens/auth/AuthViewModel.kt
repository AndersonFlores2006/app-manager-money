package com.example.gestor_money.presentation.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestor_money.data.repository.AuthRepository
import com.example.gestor_money.data.repository.AuthResult
import com.example.gestor_money.data.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAuthenticated: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        checkAuthState()
    }

    private fun checkAuthState() {
        viewModelScope.launch {
            // Now observeAuthState returns a Flow<FirebaseUser?>
            authRepository.observeAuthState().collect { user ->
                _uiState.value = _uiState.value.copy(
                    isAuthenticated = user != null,
                    isLoading = false, // Reset loading state once auth state is known
                    error = null // Clear any previous errors on auth state change
                )
            }
        }
    }

    fun signIn(email: String, password: String) {
        if (!validateEmail(email)) {
            _uiState.value = _uiState.value.copy(error = "Email inválido")
            return
        }

        if (!validatePassword(password)) {
            _uiState.value = _uiState.value.copy(error = "La contraseña debe tener al menos 6 caracteres")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            when (val result = authRepository.signIn(email, password)) {
                is AuthResult.Success -> {
                    // AuthState listener will update isAuthenticated, just clear loading/error
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = null
                    )
                }

                is AuthResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }

    fun signUp(email: String, password: String, confirmPassword: String) {
        if (!validateEmail(email)) {
            _uiState.value = _uiState.value.copy(error = "Email inválido")
            return
        }

        if (!validatePassword(password)) {
            _uiState.value = _uiState.value.copy(error = "La contraseña debe tener al menos 6 caracteres")
            return
        }

        if (password != confirmPassword) {
            _uiState.value = _uiState.value.copy(error = "Las contraseñas no coinciden")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            when (val result = authRepository.signUp(email, password)) {
                is AuthResult.Success -> {
                    // Create default categories for new user
                    launch {
                        categoryRepository.createDefaultCategories()
                    }
                    // AuthState listener will update isAuthenticated, just clear loading/error
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = null
                    )
                }

                is AuthResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }

    fun signInAnonymously() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            when (val result = authRepository.signInAnonymously()) {
                is AuthResult.Success -> {
                    // Create default categories for anonymous user
                    launch {
                        categoryRepository.createDefaultCategories()
                    }
                    // AuthState listener will update isAuthenticated, just clear loading/error
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = null
                    )
                }

                is AuthResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            // AuthState listener will update isAuthenticated to false
            _uiState.value = AuthUiState(isAuthenticated = false) // Reset to initial state after signOut
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    private fun validateEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun validatePassword(password: String): Boolean {
        return password.length >= 6
    }
}
