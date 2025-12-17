package com.example.pustakago.ui.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pustakago.data.remote.firebase.AuthDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authDataSource: AuthDataSource = AuthDataSource()
) : ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun onEmailChange(email: String) {
        _state.update { it.copy(email = email) }
    }

    fun onPasswordChange(password: String) {
        _state.update { it.copy(password = password) }
    }

    fun onRememberMeChange(rememberMe: Boolean) {
        _state.update { it.copy(rememberMe = rememberMe) }
    }

    fun onLogin() {
        val currentState = _state.value

        if (currentState.email.isBlank() || currentState.password.isBlank()) {
            _state.update { it.copy(error = "Email dan password harus diisi") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val result = authDataSource.login(currentState.email, currentState.password)

            result.fold(
                onSuccess = {
                    _state.update { it.copy(isLoading = false, isSuccess = true) }
                },
                onFailure = { exception ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Login gagal"
                        )
                    }
                }
            )
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    fun resetState() {
        _state.update { it.copy(isSuccess = false) }
    }
}
