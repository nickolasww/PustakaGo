package com.example.pustakago.ui.screen.register

data class RegisterState(
    val nama: String = "",
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)
