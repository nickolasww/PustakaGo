package com.example.pustakago.ui.screen.news

import com.example.pustakago.data.model.NewsDto

data class NewsState(
    val searchQuery: String = "",
    val breakingNews: List<NewsDto> = emptyList(),
    val latestNews: List<NewsDto> = emptyList(),
    val trendingNews: List<NewsDto> = emptyList(),
    val categoryNews: Map<String, List<NewsDto>> = emptyMap(),
    val allNews: List<NewsDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoggedIn: Boolean = false,
    val userName: String? = null,
    val userEmail: String? = null,
    // Filter and sorting
    val selectedCategory: String = "",
    val sortBy: String = "latest", // latest, popular, trending
    // Pagination
    val isLoadingMore: Boolean = false,
    val hasMoreData: Boolean = true,
    // Selected news for detail
    val selectedNews: NewsDto? = null
)
