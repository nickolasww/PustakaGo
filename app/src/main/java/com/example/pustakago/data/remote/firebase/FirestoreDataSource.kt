package com.example.pustakago.data.remote.firebase

import com.example.pustakago.data.model.BookDto
import com.example.pustakago.data.model.BookPageDto
import com.example.pustakago.data.model.ReviewDto
import com.example.pustakago.data.model.NewsDto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Date

class FirestoreDataSource {
    private val firestore = FirebaseFirestore.getInstance()
    private val booksCollection = firestore.collection("books")
    private val newsCollection = firestore.collection("news")

    suspend fun getAllBooks(): Result<List<BookDto>> {
        return try {
            val snapshot = booksCollection.get().await()
            val books = snapshot.documents.mapNotNull { doc ->
                doc.toObject(BookDto::class.java)?.copy(id = doc.id)
            }
            Result.success(books)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getBooksByCategory(category: String): Result<List<BookDto>> {
        return try {
            val snapshot = booksCollection
                .whereArrayContains("category", category)
                .get()
                .await()
            val books = snapshot.documents.mapNotNull { doc ->
                doc.toObject(BookDto::class.java)?.copy(id = doc.id)
            }
            Result.success(books)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getBookById(bookId: String): Result<BookDto?> {
        return try {
            val doc = booksCollection.document(bookId).get().await()
            val book = doc.toObject(BookDto::class.java)?.copy(id = doc.id)
            Result.success(book)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchBooks(query: String): Result<List<BookDto>> {
        return try {
            val snapshot = booksCollection.get().await()
            val books = snapshot.documents.mapNotNull { doc ->
                doc.toObject(BookDto::class.java)?.copy(id = doc.id)
            }.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.author.contains(query, ignoreCase = true)
            }
            Result.success(books)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Review functions - updated to match Firestore structure where reviews are stored in book documents
    suspend fun getBookReviews(bookId: String): Result<List<ReviewDto>> {
        return try {
            val bookDoc = booksCollection.document(bookId).get().await()
            val book = bookDoc.toObject(BookDto::class.java) ?: return Result.failure(Exception("Book not found"))

            val reviews = mutableListOf<ReviewDto>()

            if (book.description.isNotEmpty()) {
                reviews.add(
                    ReviewDto(
                        id = bookId,
                        bookId = bookId,
                        userId = "sample-user",
                        userName = "Sample User",
                        userAvatar = "",
                        rating = book.rating.toInt(),
                        reviewText = "Sample review from book data",
                        timestamp = System.currentTimeMillis()
                    )
                )
            }

            Result.success(reviews)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addReview(review: ReviewDto): Result<String> {
        return try {
            val bookDoc = booksCollection.document(review.bookId).get().await()
            val currentBook = bookDoc.toObject(BookDto::class.java) ?: return Result.failure(Exception("Book not found"))

            val newReviewCount = currentBook.reviewCount + 1
            val newTotalRating = (currentBook.rating * currentBook.reviewCount) + review.rating
            val newAverageRating = newTotalRating.toDouble() / newReviewCount

            val currentDistribution = currentBook.ratingDetail.toMutableMap()
            val starKey = review.rating.toString()
            currentDistribution[starKey] = (currentDistribution[starKey] ?: 0) + 1

            val updates = hashMapOf(
                "rating" to newAverageRating,
                "reviewCount" to newReviewCount,
                "ratingDetail" to currentDistribution,
                "reviewText" to review.reviewText // Store latest review text in book document
            )

            booksCollection.document(review.bookId).update(updates).await()
            Result.success(review.bookId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateBookRating(bookId: String, newRating: Int): Result<Unit> {
        return try {
            // Get current book data
            val bookDoc = booksCollection.document(bookId).get().await()
            val currentBook = bookDoc.toObject(BookDto::class.java) ?: return Result.failure(Exception("Book not found"))

            // Calculate new rating distribution
            val currentDistribution = currentBook.ratingDetail.toMutableMap()
            val currentTotalRating = currentBook.rating * currentBook.reviewCount
            val currentReviewCount = currentBook.reviewCount

            // Update distribution
            val starKey = newRating.toString()
            currentDistribution[starKey] = (currentDistribution[starKey] ?: 0) + 1

            // Calculate new average rating
            val newTotalRating = currentTotalRating + newRating
            val newReviewCount = currentReviewCount + 1
            val newAverageRating = newTotalRating.toDouble() / newReviewCount

            // Update book document (use ratingDetail field to match Firestore structure)
            val updates = hashMapOf(
                "rating" to newAverageRating,
                "reviewCount" to newReviewCount,
                "ratingDetail" to currentDistribution
            )

            booksCollection.document(bookId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserReviewForBook(bookId: String, userId: String): Result<ReviewDto?> {
        return try {
            val bookDoc = booksCollection.document(bookId).get().await()
            val book = bookDoc.toObject(BookDto::class.java) ?: return Result.failure(Exception("Book not found"))
            Result.success(null)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUserReview(
        reviewId: String,
        newRating: Int,
        newReviewText: String
    ): Result<Unit> {
        return try {
            val updates: Map<String, Any> = mapOf(
                "rating" to newRating,
                "reviewText" to newReviewText
            )

           booksCollection
                .document(reviewId)
                .update(updates)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Bookmark functions
    suspend fun addBookmark(userId: String, bookId: String): Result<Unit> {
        return try {
            val bookmarksCollection = firestore.collection("users").document(userId).collection("bookmarks")
            bookmarksCollection.document(bookId).set(hashMapOf(
                "bookId" to bookId,
                "timestamp" to System.currentTimeMillis()
            )).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeBookmark(userId: String, bookId: String): Result<Unit> {
        return try {
            val bookmarksCollection = firestore.collection("users").document(userId).collection("bookmarks")
            bookmarksCollection.document(bookId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserBookmarks(userId: String): Result<List<String>> {
        return try {
            val bookmarksCollection = firestore.collection("users").document(userId).collection("bookmarks")
            val snapshot = bookmarksCollection.get().await()
            val bookmarkIds = snapshot.documents.map { it.id }
            Result.success(bookmarkIds)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun isBookmarked(userId: String, bookId: String): Result<Boolean> {
        return try {
            val bookmarksCollection = firestore.collection("users").document(userId).collection("bookmarks")
            val doc = bookmarksCollection.document(bookId).get().await()
            Result.success(doc.exists())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Book Pages functions
    private val bookPagesCollection = firestore.collection("books_pages")

    suspend fun getBookPages(bookId: String): Result<List<BookPageDto>> {
        return try {
            val snapshot = bookPagesCollection
                .whereEqualTo("booksId", bookId)
                .get()
                .await()
            val pages = snapshot.documents.mapNotNull { doc ->
                doc.toObject(BookPageDto::class.java)?.copy(id = doc.id)
            }.sortedBy { it.pageNumber } // Sort locally to avoid composite index requirement
            Result.success(pages)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getBookPage(bookId: String, pageNumber: Int): Result<BookPageDto?> {
        return try {
            val snapshot = bookPagesCollection
                .whereEqualTo("booksId", bookId)
                .whereEqualTo("pageNumber", pageNumber)
                .get()
                .await()
            val page = snapshot.documents.firstOrNull()?.let { doc ->
                doc.toObject(BookPageDto::class.java)?.copy(id = doc.id)
            }
            Result.success(page)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllNews(): Result<List<NewsDto>> {
        return try {
            val snapshot = newsCollection.get().await()
            val news = snapshot.documents.mapNotNull { doc ->
                doc.toObject(NewsDto::class.java)?.copy(id = doc.id)
            }
            Result.success(news)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getBreakingNews(): Result<List<NewsDto>> {
        return try {
            val snapshot = newsCollection
                .whereEqualTo("isBreaking", true)
                .get()
                .await()
            val news = snapshot.documents.mapNotNull { doc ->
                doc.toObject(NewsDto::class.java)?.copy(id = doc.id)
            }
            Result.success(news)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getLatestNews(): Result<List<NewsDto>> {
        return try {
            val snapshot = newsCollection
                .orderBy("publishedAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .await()
            val news = snapshot.documents.mapNotNull { doc ->
                doc.toObject(NewsDto::class.java)?.copy(id = doc.id)
            }
            Result.success(news)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTrendingNews(): Result<List<NewsDto>> {
        return try {
            val snapshot = newsCollection
                .orderBy("viewCount", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .await()
            val news = snapshot.documents.mapNotNull { doc ->
                doc.toObject(NewsDto::class.java)?.copy(id = doc.id)
            }
            Result.success(news)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getNewsByCategory(category: String): Result<List<NewsDto>> {
        return try {
            val snapshot = newsCollection
                .whereEqualTo("category", category)
                .orderBy("publishedAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()
            val news = snapshot.documents.mapNotNull { doc ->
                doc.toObject(NewsDto::class.java)?.copy(id = doc.id)
            }
            Result.success(news)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getNewsById(newsId: String): Result<NewsDto?> {
        return try {
            val doc = newsCollection.document(newsId).get().await()
            val news = doc.toObject(NewsDto::class.java)?.copy(id = doc.id)
            Result.success(news)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchNews(query: String): Result<List<NewsDto>> {
        return try {
            val snapshot = newsCollection.get().await()
            val news = snapshot.documents.mapNotNull { doc ->
                doc.toObject(NewsDto::class.java)?.copy(id = doc.id)
            }.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.content.contains(query, ignoreCase = true) ||
                        it.category.contains(query, ignoreCase = true)
            }
            Result.success(news)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun incrementNewsViewCount(newsId: String): Result<Unit> {
        return try {
            val docRef = newsCollection.document(newsId)
            val doc = docRef.get().await()
            val currentViewCount = doc.getLong("viewCount") ?: 0
            docRef.update("viewCount", currentViewCount + 1).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createSampleNews(newsList: List<NewsDto>): Result<Unit> {
        return try {
            newsList.forEach { news ->
                newsCollection.document(news.id).set(news)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
