package com.example.pustakago.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun Navbar (navController: NavHostController){
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Group,
        BottomNavItem.Mark
    )

    NavigationBar (
        modifier = Modifier
            .clip(RoundedCornerShape(topStart = 30.dp,
                topEnd = 30.dp,
                bottomStart = 0.dp,
                bottomEnd = 0.dp))
    ){
        val currentRoute = navController.currentDestination?.route

        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}