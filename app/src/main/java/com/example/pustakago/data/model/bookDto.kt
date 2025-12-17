package com.example.pustakago.data.model

data class BookDto(
    val id: String = "",
    val title: String = "",
    val author: String = "",
    val year: Int = 0,
    val rating: Double = 0.0,
    val reviewCount: Int = 0,
    val pages: Int = 0,
    val description: String = "",
    val imageUrl: String = "",
    val category: List<String> = emptyList()
)
