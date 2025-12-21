package com.example.pustakago.data.model

import java.util.Date

data class NewsDto(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val summary: String = "",
    val author: String = "",
    val publishedAt: Date = Date(),
    val imageUrl: String = "",
    val category: String = "",
    val tags: List<String> = emptyList(),
    val source: String = "",
    val readTime: Int = 0, // dalam menit
    val viewCount: Int = 0,
    val isBreaking: Boolean = false,
    val isPublished: Boolean = true,
    // Additional fields for enhanced functionality
    val location: String = "",
    val relatedNews: List<String> = emptyList(),
    val externalUrl: String = ""
) {
    val formattedDate: String
        get() = android.text.format.DateFormat.format("dd MMM yyyy", publishedAt).toString()

    val formattedTime: String
        get() = android.text.format.DateFormat.format("HH:mm", publishedAt).toString()

    val relativeTime: String
        get() {
            val now = Date()
            val diffInMillis = now.time - publishedAt.time
            val diffInHours = diffInMillis / (1000 * 60 * 60)
            val diffInDays = diffInMillis / (1000 * 60 * 60 * 24)

            return when {
                diffInHours < 1 -> "Baru saja"
                diffInHours < 24 -> "$diffInHours jam yang lalu"
                diffInDays < 7 -> "$diffInDays hari yang lalu"
                else -> formattedDate
            }
        }
}
