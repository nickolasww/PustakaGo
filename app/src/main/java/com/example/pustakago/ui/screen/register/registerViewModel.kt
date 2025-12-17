package com.example.pustakago.ui.screen.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pustakago.data.remote.firebase.AuthDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authDataSource: AuthDataSource = AuthDataSource()
) : ViewModel() {
    private val _state = MutableStateFlow(RegisterState())
    val state: StateFlow<RegisterState> = _state.asStateFlow()

    fun onNamaChange(nama: String) {
        _state.update { it.copy(nama = nama) }
    }

    fun onEmailChange(email: String) {
        _state.update { it.copy(email = email) }
    }

    fun onPasswordChange(password: String) {
        _state.update { it.copy(password = password) }
    }

    fun onRegister() {
        val currentState = _state.value

        if (currentState.nama.isBlank()) {
            _state.update { it.copy(error = "Nama harus diisi") }
            return
        }

        if (currentState.email.isBlank()) {
            _state.update { it.copy(error = "Email harus diisi") }
            return
        }

        if (currentState.password.isBlank()) {
            _state.update { it.copy(error = "Password harus diisi") }
            return
        }

        if (currentState.password.length < 6) {
            _state.update { it.copy(error = "Password minimal 6 karakter") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val result = authDataSource.register(
                currentState.nama,
                currentState.email,
                currentState.password
            )

            result.fold(
                onSuccess = {
                    _state.update { it.copy(isLoading = false, isSuccess = true) }
                },
                onFailure = { exception ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Registrasi gagal"
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
