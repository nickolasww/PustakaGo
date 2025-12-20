package com.example.pustakago.ui.screen.bookdetail

import com.example.pustakago.data.model.BookDto
import com.example.pustakago.data.model.ReviewDto

data class BookDetailState(
    val book: BookDto? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isBookmarked: Boolean = false,
    val isLoggedIn: Boolean = false,
    // Review related state
    val reviews: List<ReviewDto> = emptyList(),
    val isReviewLoading: Boolean = false,
    val isSubmittingReview: Boolean = false,
    val showReviewDialog: Boolean = false,
    val userReview: ReviewDto? = null,
    val reviewText: String = "",
    val userRating: Int = 0
)
