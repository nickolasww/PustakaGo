package com.example.pustakago.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.pustakago.ui.screen.group.GroupScreen
import com.example.pustakago.ui.screen.home.HomeScreen
import com.example.pustakago.ui.screen.mark.MarkScreen
import com.example.pustakago.ui.screen.login.LoginScreen
import com.example.pustakago.ui.screen.register.RegisterScreen
import com.example.pustakago.ui.screen.bookdetail.BookDetailScreen

@Composable
fun NavigationGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        modifier = modifier.padding(contentPadding)
    ) {
        composable(Routes.HOME) {
            HomeScreen(navController)
        }
        composable(Routes.GROUP) {
            GroupScreen(navController)
        }
        composable(Routes.MARK) {
            MarkScreen(navController)
        }
        composable(Routes.LOGIN) {
            LoginScreen(navController)
        }
        composable(Routes.REGISTER){
            RegisterScreen(navController)
        }
        composable(
            route = "${Routes.BOOK_DETAIL}/{${Routes.BOOK_DETAIL_ARG}}",
            arguments = listOf(
                navArgument(Routes.BOOK_DETAIL_ARG) { type = androidx.navigation.NavType.StringType }
            )
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString(Routes.BOOK_DETAIL_ARG) ?: ""
            BookDetailScreen(
                bookId = bookId,
                navController = navController
            )
        }
    }
}
