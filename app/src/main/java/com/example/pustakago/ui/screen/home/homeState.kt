package com.example.pustakago.ui.screen.home

import com.example.pustakago.data.model.BookDto

data class HomeState(
    val searchQuery: String = "",
    val scienceBooks: List<BookDto> = emptyList(),
    val philosophyBooks: List<BookDto> = emptyList(),
    val horrorBooks: List<BookDto> = emptyList(),
    val allBooks: List<BookDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoggedIn: Boolean = false,
    val userName: String? = null,
    val userEmail: String? = null
)
