package com.example.pustakago.data.model

data class ReviewDto(
    val id: String = "",
    val bookId: String = "",
    val userId: String = "",
    val userName: String = "",
    val userAvatar: String = "",
    val rating: Int = 0,
    val reviewText: String = "",
    val timestamp: Long = 0L
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "bookId" to bookId,
            "userId" to userId,
            "userName" to userName,
            "userAvatar" to userAvatar,
            "rating" to rating,
            "reviewText" to reviewText,
            "timestamp" to timestamp
        )
    }
}
