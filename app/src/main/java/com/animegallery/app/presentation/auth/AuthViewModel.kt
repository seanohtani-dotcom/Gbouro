package com.animegallery.app.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.animegallery.app.domain.model.User
import com.animegallery.app.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        observeAuthState()
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            authRepository.getCurrentUser().collect { user ->
                _uiState.update {
                    it.copy(
                        user = user,
                        isAuthenticated = user != null
                    )
                }
            }
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            authRepository.signIn(email, password)
                .onSuccess { user ->
                    _uiState.update {
                        it.copy(
                            user = user,
                            isAuthenticated = true,
                            isLoading = false,
                            error = null
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = getErrorMessage(error)
                        )
                    }
                }
        }
    }

    fun register(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            authRepository.register(email, password)
                .onSuccess { user ->
                    _uiState.update {
                        it.copy(
                            user = user,
                            isAuthenticated = true,
                            isLoading = false,
                            error = null
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = getErrorMessage(error)
                        )
                    }
                }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            user = null,
                            isAuthenticated = false,
                            error = null
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(error = error.message)
                    }
                }
        }
    }

    private fun getErrorMessage(error: Throwable): String {
        return when {
            error.message?.contains("password") == true -> "Invalid email or password"
            error.message?.contains("email") == true -> "Invalid email format"
            error.message?.contains("network") == true -> "Network error. Please check your connection"
            error.message?.contains("already") == true -> "Email already in use"
            else -> error.message ?: "An error occurred"
        }
    }
}

data class AuthUiState(
    val user: User? = null,
    val isAuthenticated: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)
