package com.example.pustakago.ui.screen.group

data class GroupState(
    val searchQuery: String = "",
    val groups: List<ReadingGroup> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoggedIn: Boolean = false,
    val userName: String? = null,
    val userEmail: String? = null
)
