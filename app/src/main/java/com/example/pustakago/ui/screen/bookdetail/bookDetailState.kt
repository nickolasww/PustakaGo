package com.example.pustakago.ui.screen.bookdetail

import com.example.pustakago.data.model.BookDto

data class BookDetailState(
    val book: BookDto? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isBookmarked: Boolean = false,
    val isLoggedIn: Boolean = false
)
