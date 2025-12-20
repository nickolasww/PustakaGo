package com.example.pustakago.ui.screen.mark

import com.example.pustakago.data.model.BookDto

data class MarkState(
    val bookmarkedBooks: List<BookDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoggedIn: Boolean = false,
    val userName: String? = null,
    val userEmail: String? = null,
    val searchQuery: String = ""
)
