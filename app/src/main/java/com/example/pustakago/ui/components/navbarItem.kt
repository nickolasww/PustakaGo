package com.example.pustakago.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Group
import androidx.compose.ui.graphics.vector.ImageVector



sealed class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem("home", "Home", Icons.Default.Home)
    object Group : BottomNavItem("group", "Group", Icons.Default.Group)
    object Mark : BottomNavItem("mark", "Mark", Icons.Default.Bookmark)
    object Profile: BottomNavItem("profile", "Profile", Icons.Default.Bookmark)
}