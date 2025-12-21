package com.example.pustakago.ui.screen.bookpages

import com.example.pustakago.data.model.BookPageDto

data class BookPagesState(
    val isLoading: Boolean = true,
    val pages: List<BookPageDto> = emptyList(),
    val currentPageIndex: Int = 0,
    val error: String? = null,
    val totalPages: Int = 0
) {
    val currentPage: BookPageDto?
        get() = pages.getOrNull(currentPageIndex)

    val hasNextPage: Boolean
        get() = currentPageIndex < pages.size - 1

    val hasPreviousPage: Boolean
        get() = currentPageIndex > 0
}
