package com.example.pustakago.ui.screen.home

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.pustakago.ui.navigation.Routes

@Composable
fun HomeScreen(navController: NavHostController) {
    Column {
        Text("Home Screen")

        Button(onClick = {
            navController.navigate(Routes.LOGIN)
        }) {
            Text("Go To Login")
        }
    }
}