package com.example.pustakago.ui.screen.mark

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.pustakago.ui.navigation.Routes

@Composable
fun MarkScreen(navController: NavHostController) {
    Column {
        Text("Mark Screen")

        Button(onClick = {
            navController.navigate(Routes.LOGIN)
        }) {
            Text("Go To Login")
        }
    }
}