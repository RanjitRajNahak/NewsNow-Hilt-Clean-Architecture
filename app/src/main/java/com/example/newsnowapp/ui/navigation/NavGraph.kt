package com.example.newsnowapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.newsnowapp.data.local.Article
import com.example.newsnowapp.ui.screens.HomeScreen
import com.example.newsnowapp.ui.screens.DetailScreen
import com.example.newsnowapp.viewmodel.SearchViewModel
import com.example.newsnowapp.ui.screens.SearchScreen
import com.example.newsnowapp.viewmodel.HomeViewModel
import com.example.newsnowapp.viewmodel.NewsViewModelFactory
import com.google.gson.Gson
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Detail : Screen("detail/{articleJson}") {
        fun createRoute(article: Article): String {
            // Convert the whole article to a single String
            val json = Gson().toJson(article)
            val encodedJson = URLEncoder.encode(json, StandardCharsets.UTF_8.toString())
            return "detail/$encodedJson"
        }
    }
    object Search : Screen("search")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    factory: NewsViewModelFactory
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        // Home Screen
        composable(route = Screen.Home.route) {
            val homeViewModel: HomeViewModel = viewModel(factory = factory)
            HomeScreen(viewModel = homeViewModel,
                onArticleClick = { article ->
                    navController.navigate(Screen.Detail.createRoute(article))
                },
                onSearchClick = {
                    navController.navigate(Screen.Search.route) // Navigate to search screen
                })
        }

        // Detail Screen
        composable(route = Screen.Detail.route) { backStackEntry ->
            // 1. Get the encoded JSON string
            val encodedJson = backStackEntry.arguments?.getString("articleJson") ?: ""

            // 2. DECODE it back to normal JSON (this removes the '+' and '%20' signs)
            val decodedJson = URLDecoder.decode(encodedJson, StandardCharsets.UTF_8.toString())

            // 3. Convert it back to your Article object
            val article = Gson().fromJson(decodedJson, Article::class.java)
            DetailScreen(article = article, onBack = { navController.popBackStack() })
        }

        // Search Screen
        composable(Screen.Search.route) {
            val searchViewModel: SearchViewModel = viewModel(factory = factory)
            SearchScreen(viewModel = searchViewModel) { article ->
                navController.navigate(Screen.Detail.createRoute(article))
            }
        }
    }
}