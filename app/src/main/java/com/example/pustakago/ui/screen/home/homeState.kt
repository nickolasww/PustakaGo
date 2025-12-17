package com.example.pustakago.ui.screen.home

data class HomeState(
    val searchQuery: String = "",
    val scienceBooks: List<Book> = emptyList(),
    val philosophyBooks: List<Book> = emptyList(),
    val horrorBooks: List<Book> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
