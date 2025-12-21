package com.example.pustakago.data.model

data class BookPageDto(
    val id: String = "",
    val booksId: String = "",
    val chapter: String = "",
    val content: String = "",
    val pageNumber: Int = 1,
    val title: String = ""
)
