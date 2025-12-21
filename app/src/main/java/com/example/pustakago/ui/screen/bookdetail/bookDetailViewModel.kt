package com.example.pustakago.ui.screen.bookdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pustakago.data.event.BookmarkEvent
import com.example.pustakago.data.event.EventBus
import com.example.pustakago.data.model.ReviewDto
import com.example.pustakago.data.remote.firebase.AuthDataSource
import com.example.pustakago.data.remote.firebase.FirestoreDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date

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
        loadBookReviews()
        checkUserReview()
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
                if (user != null) {
                    checkUserReview()
                    checkBookmarkStatus()
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
                        isLoading = false
                    )
                }
                // Check if book is bookmarked after loading
                checkBookmarkStatus()
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

    private fun loadBookReviews() {
        viewModelScope.launch {
            _state.update { it.copy(isReviewLoading = true) }

            firestoreDataSource.getBookReviews(bookId).onSuccess { reviews ->
                _state.update {
                    it.copy(
                        reviews = reviews,
                        isReviewLoading = false
                    )
                }
            }.onFailure { e ->
                _state.update {
                    it.copy(
                        error = e.message,
                        isReviewLoading = false
                    )
                }
            }
        }
    }

    private fun checkUserReview() {
        viewModelScope.launch {
            val currentUser = authDataSource.getCurrentUser()
            if (currentUser != null) {
                firestoreDataSource.getUserReviewForBook(bookId, currentUser.uid).onSuccess { review ->
                    _state.update {
                        it.copy(
                            userReview = review,
                            reviewText = review?.reviewText ?: "",
                            userRating = review?.rating ?: 0
                        )
                    }
                }
            }
        }
    }

    private fun checkBookmarkStatus() {
        viewModelScope.launch {
            val currentUser = authDataSource.getCurrentUser()
            if (currentUser != null) {
                firestoreDataSource.isBookmarked(currentUser.uid, bookId).onSuccess { isBookmarked ->
                    _state.update {
                        it.copy(isBookmarked = isBookmarked)
                    }
                }.onFailure { e ->
                    // Handle error silently or show to user
                    _state.update {
                        it.copy(error = "Gagal memuat status bookmark: ${e.message}")
                    }
                }
            }
        }
    }

    fun refreshBookDetail() {
        loadBookDetail()
        loadBookReviews()
        checkUserReview()
        checkBookmarkStatus()
    }

    fun toggleBookmark() {
        val currentUser = authDataSource.getCurrentUser()
        if (currentUser == null) {
            _state.update { it.copy(error = "Harus login untuk men-bookmark buku") }
            return
        }

        val currentState = _state.value
        val isCurrentlyBookmarked = currentState.isBookmarked

        _state.update {
            it.copy(isBookmarked = !isCurrentlyBookmarked)
        }

        // Implement bookmark logic with Firebase
        viewModelScope.launch {
            if (isCurrentlyBookmarked) {
                // Remove bookmark
                firestoreDataSource.removeBookmark(currentUser.uid, bookId).onSuccess {
                    _state.update {
                        it.copy(
                            isBookmarked = false,
                            error = null
                        )
                    }
                    // Emit event to notify other ViewModels
                    EventBus.emitBookmarkEvent(BookmarkEvent.BookmarkRemoved(bookId))
                }.onFailure { e ->
                    // Revert on failure
                    _state.update {
                        it.copy(
                            isBookmarked = true,
                            error = "Gagal menghapus bookmark: ${e.message}"
                        )
                    }
                }
            } else {
                // Add bookmark
                firestoreDataSource.addBookmark(currentUser.uid, bookId).onSuccess {
                    _state.update {
                        it.copy(
                            isBookmarked = true,
                            error = null
                        )
                    }
                    // Emit event to notify other ViewModels
                    EventBus.emitBookmarkEvent(BookmarkEvent.BookmarkAdded(bookId))
                }.onFailure { e ->
                    // Revert on failure
                    _state.update {
                        it.copy(
                            isBookmarked = false,
                            error = "Gagal menambahkan bookmark: ${e.message}"
                        )
                    }
                }
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    // Review functions
    fun showReviewDialog() {
        _state.update { it.copy(showReviewDialog = true) }
    }

    fun hideReviewDialog() {
        _state.update { it.copy(showReviewDialog = false) }
    }

    fun updateReviewText(text: String) {
        _state.update { it.copy(reviewText = text) }
    }

    fun updateUserRating(rating: Int) {
        _state.update { it.copy(userRating = rating) }
    }

    fun submitReview() {
        viewModelScope.launch {
            val currentUser = authDataSource.getCurrentUser()
            if (currentUser == null) {
                _state.update { it.copy(error = "Harus login untuk menulis ulasan") }
                return@launch
            }

            val currentState = _state.value
            if (currentState.userRating == 0) {
                _state.update { it.copy(error = "Harus memberikan rating") }
                return@launch
            }

            if (currentState.reviewText.isBlank()) {
                _state.update { it.copy(error = "Harus menulis ulasan") }
                return@launch
            }

            _state.update { it.copy(isSubmittingReview = true, error = null) }

            val review = ReviewDto(
                bookId = bookId,
                userId = currentUser.uid,
                userName = currentUser.displayName ?: "User",
                userAvatar = currentUser.photoUrl?.toString() ?: "",
                rating = currentState.userRating,
                reviewText = currentState.reviewText,
                timestamp = System.currentTimeMillis()
            )

            if (currentState.userReview != null) {
                // Update existing review
                firestoreDataSource.updateUserReview(
                    currentState.userReview.id,
                    currentState.userRating,
                    currentState.reviewText
                ).onSuccess {
                    firestoreDataSource.updateBookRating(bookId, currentState.userRating)
                    _state.update {
                        it.copy(
                            isSubmittingReview = false,
                            showReviewDialog = false,
                            userReview = review.copy(id = currentState.userReview.id)
                        )
                    }
                    loadBookReviews()
                    loadBookDetail()
                }.onFailure { e ->
                    _state.update {
                        it.copy(
                            isSubmittingReview = false,
                            error = e.message
                        )
                    }
                }
            } else {
                // Add new review
                firestoreDataSource.addReview(review).onSuccess { reviewId ->
                    firestoreDataSource.updateBookRating(bookId, currentState.userRating)
                    _state.update {
                        it.copy(
                            isSubmittingReview = false,
                            showReviewDialog = false,
                            userReview = review.copy(id = reviewId)
                        )
                    }
                    loadBookReviews()
                    loadBookDetail()
                }.onFailure { e ->
                    _state.update {
                        it.copy(
                            isSubmittingReview = false,
                            error = e.message
                        )
                    }
                }
            }
        }
    }

    fun editExistingReview() {
        val currentReview = _state.value.userReview
        if (currentReview != null) {
            _state.update {
                it.copy(
                    reviewText = currentReview.reviewText,
                    userRating = currentReview.rating,
                    showReviewDialog = true
                )
            }
        }
    }
}
