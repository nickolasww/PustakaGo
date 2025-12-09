package com.example.pustakago

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.pustakago.ui.theme.PustakaGoTheme
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.pustakago.ui.components.Navbar
import com.example.pustakago.ui.navigation.NavigationGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PustakaGoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
                val  navController = rememberNavController()
                Scaffold(
                    bottomBar = { Navbar(navController)}
                ) { innerPadding ->
                    NavigationGraph(
                        navController = navController,
                        contentPadding = innerPadding
                    )
                    }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PustakaGoTheme {
        Greeting("Android")
    }
}