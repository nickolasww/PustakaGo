package com.example.pustakago.data.event

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

object EventBus {
    private val _bookmarkEvents = Channel<BookmarkEvent>(Channel.UNLIMITED)
    val bookmarkEvents: Flow<BookmarkEvent> = _bookmarkEvents.receiveAsFlow()

    suspend fun emitBookmarkEvent(event: BookmarkEvent) {
        _bookmarkEvents.send(event)
    }
}

sealed class BookmarkEvent {
    data class BookmarkAdded(val bookId: String) : BookmarkEvent()
    data class BookmarkRemoved(val bookId: String) : BookmarkEvent()
}
