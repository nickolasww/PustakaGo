package com.example.pustakago.data.remote.firebase

import com.example.pustakago.data.model.BookDto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreDataSource {
    private val firestore = FirebaseFirestore.getInstance()
    private val booksCollection = firestore.collection("books")

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
}
