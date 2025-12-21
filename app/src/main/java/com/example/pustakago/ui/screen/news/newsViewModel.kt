package com.example.pustakago.ui.screen.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pustakago.data.model.NewsDto
import com.example.pustakago.data.remote.firebase.AuthDataSource
import com.example.pustakago.data.remote.firebase.FirestoreDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date

class NewsViewModel(
    private val authDataSource: AuthDataSource = AuthDataSource(),
    private val firestoreDataSource: FirestoreDataSource = FirestoreDataSource()
) : ViewModel() {
    private val _state = MutableStateFlow(NewsState())
    val state: StateFlow<NewsState> = _state.asStateFlow()

    init {
        loadNews()
        checkAuthState()
        observeAuthState()
    }

    private fun checkAuthState() {
        val currentUser = authDataSource.getCurrentUser()
        _state.update {
            it.copy(
                isLoggedIn = currentUser != null,
                userName = currentUser?.displayName,
                userEmail = currentUser?.email
            )
        }
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            authDataSource.currentUser.collect { user ->
                _state.update {
                    it.copy(
                        isLoggedIn = user != null,
                        userName = user?.displayName,
                        userEmail = user?.email
                    )
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _state.update { it.copy(searchQuery = query) }
        if (query.isNotEmpty()) {
            searchNews(query)
        } else {
            loadNews()
        }
    }

    private fun searchNews(query: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            firestoreDataSource.searchNews(query).onSuccess { newsList ->
                _state.update {
                    it.copy(
                        allNews = newsList,
                        latestNews = newsList.sortedByDescending { it.publishedAt },
                        isLoading = false,
                        error = null
                    )
                }
            }.onFailure { e ->
                _state.update {
                    it.copy(
                        error = e.message,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun loadNews() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            // Load different types of news
            loadBreakingNews()
            loadLatestNews()
            loadTrendingNews()
            loadCategoryNews()
            loadAllNews()
        }
    }

    private suspend fun loadBreakingNews() {
        firestoreDataSource.getBreakingNews().onSuccess { newsList ->
            _state.update { it.copy(breakingNews = newsList) }
        }.onFailure { e ->
            _state.update { it.copy(error = e.message) }
        }
    }

    private suspend fun loadLatestNews() {
        firestoreDataSource.getLatestNews().onSuccess { newsList ->
            _state.update { it.copy(latestNews = newsList) }
        }.onFailure { e ->
            _state.update { it.copy(error = e.message) }
        }
    }

    private suspend fun loadTrendingNews() {
        firestoreDataSource.getTrendingNews().onSuccess { newsList ->
            _state.update { it.copy(trendingNews = newsList) }
        }.onFailure { e ->
            _state.update { it.copy(error = e.message) }
        }
    }

    private suspend fun loadCategoryNews() {
        val categories = listOf("Politik", "Ekonomi", "Olahraga", "Teknologi", "Hiburan", "Kesehatan")
        val categoryNewsMap = mutableMapOf<String, List<NewsDto>>()

        categories.forEach { category ->
            firestoreDataSource.getNewsByCategory(category).onSuccess { newsList ->
                categoryNewsMap[category] = newsList
                _state.update { it.copy(categoryNews = categoryNewsMap.toMap()) }
            }.onFailure { e ->
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    private suspend fun loadAllNews() {
        firestoreDataSource.getAllNews().onSuccess { newsList ->
            _state.update {
                it.copy(
                    allNews = newsList,
                    isLoading = false,
                    error = null
                )
            }
        }.onFailure { e ->
            _state.update {
                it.copy(
                    error = e.message,
                    isLoading = false
                )
            }
        }
    }

    fun onCategorySelected(category: String) {
        _state.update { it.copy(selectedCategory = category) }
        if (category.isNotEmpty()) {
            loadNewsByCategory(category)
        }
    }

    private fun loadNewsByCategory(category: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            firestoreDataSource.getNewsByCategory(category).onSuccess { newsList ->
                _state.update {
                    it.copy(
                        allNews = newsList,
                        isLoading = false,
                        error = null
                    )
                }
            }.onFailure { e ->
                _state.update {
                    it.copy(
                        error = e.message,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onSortByChanged(sortBy: String) {
        _state.update { it.copy(sortBy = sortBy) }
        val currentNews = _state.value.allNews
        val sortedNews = when (sortBy) {
            "latest" -> currentNews.sortedByDescending { it.publishedAt }
            "popular" -> currentNews.sortedByDescending { it.viewCount }
            "trending" -> currentNews.sortedByDescending { it.viewCount }
            else -> currentNews
        }
        _state.update { it.copy(allNews = sortedNews) }
    }

    fun onNewsSelected(news: NewsDto) {
        _state.update { it.copy(selectedNews = news) }
        // Increment view count
        viewModelScope.launch {
            firestoreDataSource.incrementNewsViewCount(news.id)
        }
    }

    fun clearSelectedNews() {
        _state.update { it.copy(selectedNews = null) }
    }

    fun refreshNews() {
        loadNews()
    }

    fun logout() {
        authDataSource.logout()
        _state.update {
            it.copy(
                isLoggedIn = false,
                userName = null,
                userEmail = null
            )
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    // Create sample news data for testing
    fun createSampleNews() {
        viewModelScope.launch {
            val sampleNews = listOf(
                NewsDto(
                    id = "1",
                    title = "Breaking: Teknologi AI Terbaru Diluncurkan",
                    content = "Konten lengkap berita tentang peluncuran teknologi AI terbaru...",
                    summary = "Teknologi AI terbaru dengan kemampuan luar biasa telah diluncurkan hari ini.",
                    author = "John Doe",
                    publishedAt = Date(),
                    imageUrl = "https://example.com/image1.jpg",
                    category = "Teknologi",
                    tags = listOf("AI", "Teknologi", "Inovasi"),
                    source = "Tech News",
                    readTime = 5,
                    viewCount = 1250,
                    isBreaking = true,
                    location = "Jakarta"
                ),
                NewsDto(
                    id = "2",
                    title = "Olahraga: Timnas Menang Telak 3-0",
                    content = "Timnas Indonesia berhasil mengalahkan lawan dengan skor 3-0 dalam pertandingan semalam...",
                    summary = "Timnas Indonesia meraih kemenangan telak 3-0 dalam pertandingan internasional.",
                    author = "Jane Smith",
                    publishedAt = Date(),
                    imageUrl = "https://example.com/image2.jpg",
                    category = "Olahraga",
                    tags = listOf("Sepak Bola", "Timnas", "Kemenangan"),
                    source = "Sports Daily",
                    readTime = 3,
                    viewCount = 890,
                    isBreaking = false,
                    location = "Stadium GBK"
                ),
                NewsDto(
                    id = "3",
                    title = "Ekonomi: Pasar Saham Mengalami Kenaikan",
                    content = "Pasar saham Indonesia mengalami kenaikan signifikan sebesar 2.5% hari ini...",
                    summary = "Pasar saham Indonesia naik 2.5% Didorong oleh optimisme investor.",
                    author = "Bob Johnson",
                    publishedAt = Date(),
                    imageUrl = "https://example.com/image3.jpg",
                    category = "Ekonomi",
                    tags = listOf("Saham", "Ekonomi", "Investasi"),
                    source = "Business Times",
                    readTime = 4,
                    viewCount = 567,
                    isBreaking = false,
                    location = "Bursa Efek Indonesia"
                )
            )

            firestoreDataSource.createSampleNews(sampleNews).onSuccess {
                loadNews()
            }
        }
    }
}
