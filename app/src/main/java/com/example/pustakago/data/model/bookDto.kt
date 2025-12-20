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
    val category: List<String> = emptyList(),
    // Publisher information
    val publisher: String = "",
    val isbn: String = "",
    val language: String = "",
    val format: String = "",
    // Review text field (Firestore structure)
    val reviewText: String = "",
    // Author information - match Firestore structure exactly
    val authorDetail: Map<String, String> = emptyMap(),
    // Rating distribution (Firestore field name: ratingDetail)
    val ratingDetail: Map<String, Int> = emptyMap()
) {
    // Helper function to get rating distribution (compatibility with existing code)
    val ratingDistribution: Map<String, Int>
        get() = ratingDetail

    // Helper function to get author name from authorDetail
    val authorDetailName: String
        get() = authorDetail["nama"] ?: author

    // Helper function to get bio from authorDetail
    val authorDetailBio: String
        get() = authorDetail["bio"] ?: ""
}
