package com.example.pustakago.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pustakago.data.remote.firebase.AuthDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val authDataSource: AuthDataSource = AuthDataSource()
) : ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        loadBooks()
        checkAuthState()
        observeAuthState()
    }

    private fun checkAuthState() {
        val currentUser = authDataSource.getCurrentUser()
        _state.update {
            it.copy(
                isLoggedIn = currentUser != null,
                userName = currentUser?.displayName,
                userEmail = currentUser?.email
            )
        }
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            authDataSource.currentUser.collect { user ->
                _state.update {
                    it.copy(
                        isLoggedIn = user != null,
                        userName = user?.displayName,
                        userEmail = user?.email
                    )
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _state.update { it.copy(searchQuery = query) }
    }

    private fun loadBooks() {
        _state.update {
            it.copy(
                scienceBooks = getScienceBooks(),
                philosophyBooks = getPhilosophyBooks(),
                horrorBooks = getHorrorBooks()
            )
        }
    }

    fun logout() {
        authDataSource.logout()
        _state.update {
            it.copy(
                isLoggedIn = false,
                userName = null,
                userEmail = null
            )
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
