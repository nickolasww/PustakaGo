package com.example.pustakago.ui.screen.group

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pustakago.data.remote.firebase.AuthDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GroupViewModel(
    private val authDataSource: AuthDataSource = AuthDataSource()
) : ViewModel() {
    private val _state = MutableStateFlow(GroupState())
    val state: StateFlow<GroupState> = _state.asStateFlow()

    init {
        loadGroups()
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

    private fun loadGroups() {
        _state.update {
            it.copy(groups = getSampleGroups())
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
