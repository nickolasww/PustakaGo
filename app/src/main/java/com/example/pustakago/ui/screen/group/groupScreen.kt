package com.example.pustakago.ui.screen.group

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.pustakago.ui.navigation.Routes

@Composable
fun GroupScreen(navController: NavHostController) {
    Column {
        Text("Group Screen")

        Button(onClick = {
            navController.navigate(Routes.REGISTER)
        }) {
            Text("Go To Login")
        }
    }
}