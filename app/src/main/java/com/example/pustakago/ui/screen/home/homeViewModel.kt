package com.example.pustakago.ui.screen.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HomeViewModel : ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        loadBooks()
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

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
