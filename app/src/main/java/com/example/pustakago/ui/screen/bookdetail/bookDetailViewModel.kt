package com.example.pustakago.ui.screen.bookdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pustakago.data.remote.firebase.AuthDataSource
import com.example.pustakago.data.remote.firebase.FirestoreDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BookDetailViewModel(
    private val bookId: String,
    private val authDataSource: AuthDataSource = AuthDataSource(),
    private val firestoreDataSource: FirestoreDataSource = FirestoreDataSource()
) : ViewModel() {
    private val _state = MutableStateFlow(BookDetailState())
    val state: StateFlow<BookDetailState> = _state.asStateFlow()

    init {
        checkAuthState()
        loadBookDetail()
        observeAuthState()
    }

    private fun checkAuthState() {
        val currentUser = authDataSource.getCurrentUser()
        _state.update {
            it.copy(isLoggedIn = currentUser != null)
        }
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            authDataSource.currentUser.collect { user ->
                _state.update {
                    it.copy(isLoggedIn = user != null)
                }
            }
        }
    }

    private fun loadBookDetail() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            firestoreDataSource.getBookById(bookId).onSuccess { book ->
                _state.update {
                    it.copy(
                        book = book,
                        isLoading = false,
                        isBookmarked = false // TODO: Check if book is bookmarked by user
                    )
                }
            }.onFailure { e ->
                _state.update {
                    it.copy(
                        error = e.message,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun refreshBookDetail() {
        loadBookDetail()
    }

    fun toggleBookmark() {
        _state.update {
            it.copy(isBookmarked = !it.isBookmarked)
        }
        // TODO: Implement bookmark logic with Firebase
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
