package com.example.pustakago.ui.screen.bookpages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pustakago.data.remote.firebase.FirestoreDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BookPagesViewModel(private val bookId: String) : ViewModel() {

    private val firestoreDataSource = FirestoreDataSource()

    private val _state = MutableStateFlow(BookPagesState())
    val state: StateFlow<BookPagesState> = _state.asStateFlow()

    init {
        loadBookPages()
    }

    private fun loadBookPages() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val result = firestoreDataSource.getBookPages(bookId)

            result.fold(
                onSuccess = { pages ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            pages = pages,
                            totalPages = pages.size,
                            error = null
                        )
                    }
                },
                onFailure = { exception ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Terjadi kesalahan saat memuat halaman"
                        )
                    }
                }
            )
        }
    }

    fun nextPage() {
        if (_state.value.hasNextPage) {
            _state.update { it.copy(currentPageIndex = it.currentPageIndex + 1) }
        }
    }

    fun previousPage() {
        if (_state.value.hasPreviousPage) {
            _state.update { it.copy(currentPageIndex = it.currentPageIndex - 1) }
        }
    }

    fun goToPage(index: Int) {
        if (index in 0 until _state.value.pages.size) {
            _state.update { it.copy(currentPageIndex = index) }
        }
    }

    fun retry() {
        loadBookPages()
    }
}
