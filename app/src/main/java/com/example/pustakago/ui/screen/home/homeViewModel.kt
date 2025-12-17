package com.example.pustakago.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pustakago.data.remote.firebase.AuthDataSource
import com.example.pustakago.data.remote.firebase.FirestoreDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val authDataSource: AuthDataSource = AuthDataSource(),
    private val firestoreDataSource: FirestoreDataSource = FirestoreDataSource()
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
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            // Load books by category
            loadBooksByCategory("Sains") { books ->
                _state.update { it.copy(scienceBooks = books) }
            }

            loadBooksByCategory("Filsafat") { books ->
                _state.update { it.copy(philosophyBooks = books) }
            }

            loadBooksByCategory("Mencekam") { books ->
                _state.update { it.copy(horrorBooks = books) }
            }

            // Also load all books
            firestoreDataSource.getAllBooks().onSuccess { books ->
                _state.update { it.copy(allBooks = books, isLoading = false) }
            }.onFailure { e ->
                _state.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    private suspend fun loadBooksByCategory(category: String, onSuccess: (List<com.example.pustakago.data.model.BookDto>) -> Unit) {
        firestoreDataSource.getBooksByCategory(category).onSuccess { books ->
            onSuccess(books)
        }.onFailure { e ->
            _state.update { it.copy(error = e.message) }
        }
    }

    fun refreshBooks() {
        loadBooks()
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
