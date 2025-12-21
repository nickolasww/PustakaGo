package com.example.pustakago.ui.screen.mark

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pustakago.data.event.BookmarkEvent
import com.example.pustakago.data.event.EventBus
import com.example.pustakago.data.model.BookDto
import com.example.pustakago.data.remote.firebase.AuthDataSource
import com.example.pustakago.data.remote.firebase.FirestoreDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MarkViewModel(
    private val authDataSource: AuthDataSource = AuthDataSource(),
    private val firestoreDataSource: FirestoreDataSource = FirestoreDataSource()
) : ViewModel() {
    private val _state = MutableStateFlow(MarkState())
    val state: StateFlow<MarkState> = _state.asStateFlow()

    init {
        checkAuthState()
        observeAuthState()
        observeBookmarkEvents()
        loadBookmarkedBooks()
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
                if (user != null) {
                    loadBookmarkedBooks()
                } else {
                    _state.update { it.copy(bookmarkedBooks = emptyList()) }
                }
            }
        }
    }

    /**
     * Listen for bookmark events from other ViewModels (like BookDetailViewModel)
     * This enables real-time synchronization between bookmark actions
     */
    private fun observeBookmarkEvents() {
        viewModelScope.launch {
            EventBus.bookmarkEvents.collect { event ->
                when (event) {
                    is BookmarkEvent.BookmarkAdded -> {
                        // A book was bookmarked from another screen (e.g., BookDetail)
                        // We need to refresh our bookmarked books list
                        loadBookmarkedBooks()
                    }
                    is BookmarkEvent.BookmarkRemoved -> {
                        // A book was unbookmarked from another screen (e.g., BookDetail)
                        // Remove it from our local list immediately for better UX
                        _state.update { currentState ->
                            currentState.copy(
                                bookmarkedBooks = currentState.bookmarkedBooks.filter { book ->
                                    book.id != event.bookId
                                }
                            )
                        }
                        // Also refresh from server to ensure consistency
                        loadBookmarkedBooks()
                    }
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _state.update { it.copy(searchQuery = query) }
    }

    private fun loadBookmarkedBooks() {
        val currentUser = authDataSource.getCurrentUser()
        if (currentUser == null) {
            _state.update { it.copy(bookmarkedBooks = emptyList()) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                // Get bookmarked book IDs from user's bookmarks collection
                val bookmarksResult = firestoreDataSource.getUserBookmarks(currentUser.uid)
                if (bookmarksResult.isSuccess) {
                    val bookmarkedBookIds = bookmarksResult.getOrDefault(emptyList())

                    // Fetch full book details for each bookmarked ID
                    val bookmarkedBooks = mutableListOf<BookDto>()
                    for (bookId in bookmarkedBookIds) {
                        val bookResult = firestoreDataSource.getBookById(bookId)
                        if (bookResult.isSuccess) {
                            bookResult.getOrNull()?.let { book ->
                                bookmarkedBooks.add(book)
                            }
                        }
                    }

                    _state.update {
                        it.copy(
                            bookmarkedBooks = bookmarkedBooks,
                            isLoading = false,
                            error = null
                        )
                    }
                } else {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = "Gagal memuat buku bookmark"
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Terjadi kesalahan"
                    )
                }
            }
        }
    }

    fun removeBookmark(bookId: String) {
        val currentUser = authDataSource.getCurrentUser()
        if (currentUser == null) return

        viewModelScope.launch {
            try {
                val result = firestoreDataSource.removeBookmark(currentUser.uid, bookId)
                if (result.isSuccess) {
                    // Remove from local state
                    _state.update {
                        it.copy(
                            bookmarkedBooks = it.bookmarkedBooks.filter { book -> book.id != bookId }
                        )
                    }

                    // Emit event to notify other ViewModels
                    EventBus.emitBookmarkEvent(BookmarkEvent.BookmarkRemoved(bookId))
                } else {
                    _state.update {
                        it.copy(error = "Gagal menghapus bookmark")
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(error = e.message ?: "Terjadi kesalahan")
                }
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    fun refreshBookmarks() {
        loadBookmarkedBooks()
    }
}
