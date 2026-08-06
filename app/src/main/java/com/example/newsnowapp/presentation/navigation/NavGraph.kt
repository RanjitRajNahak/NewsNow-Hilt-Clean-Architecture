package com.example.newsnowapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.newsnowapp.domain.model.Article
import com.example.newsnowapp.presentation.auth.AuthViewModel
import com.example.newsnowapp.presentation.auth.LoginScreen
import com.example.newsnowapp.presentation.auth.SignUpScreen
import com.example.newsnowapp.presentation.bookmarks.BookmarksScreen
import com.example.newsnowapp.presentation.bookmarks.BookmarksViewModel
import com.example.newsnowapp.presentation.detail.DetailScreen
import com.example.newsnowapp.presentation.detail.DetailViewModel
import com.example.newsnowapp.presentation.home.HomeScreen
import com.example.newsnowapp.presentation.home.HomeViewModel
import com.example.newsnowapp.presentation.search.SearchScreen
import com.example.newsnowapp.presentation.search.SearchViewModel
import com.google.gson.Gson
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import androidx.navigation.navDeepLink

sealed class Screen(val route: String) {
    object Login: Screen("login")
    object SignUp: Screen("signup")
    object Home : Screen("home")
    object Bookmarks : Screen("bookmarks")
    object Detail : Screen("detail/{articleJson}") {
        fun createRoute(article: Article): String {
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
    authViewModel: AuthViewModel,
    homeViewModel: HomeViewModel,
    searchViewModel: SearchViewModel,
    detailViewModel: DetailViewModel,
    bookmarksViewModel: BookmarksViewModel,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit
) {
    val userIsLoggedIn = authViewModel.checkLoginStatus()

    NavHost(
        navController = navController,
        startDestination = if (userIsLoggedIn) Screen.Home.route else Screen.Login.route
    ) {
        // Login Screen
        composable(route = Screen.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToSignUp = {
                    navController.navigate(Screen.SignUp.route) {
                        popUpTo(Screen.Login.route) {inclusive = true}
                    }
                }
            )
        }
        // Signup Screen
        composable(route = Screen.SignUp.route) {
            SignUpScreen(
                viewModel = authViewModel,
                onSignUpSuccess = {
                    navController.navigate(Screen.Login.route){
                        popUpTo(Screen.SignUp.route) {inclusive = true}
                } },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route){
                    popUpTo(Screen.Login.route) {inclusive = true}
                } }
            )
        }
        // Home Screen
        composable(route = Screen.Home.route) {
            HomeScreen(
                viewModel = homeViewModel,
                isDarkTheme = isDarkTheme,
                onThemeToggle = onThemeToggle,
                onArticleClick = { article ->
                    navController.navigate(Screen.Detail.createRoute(article))
                },
                onSearchClick = {
                    navController.navigate(Screen.Search.route)
                },
                onBookmarksClick = {
                    navController.navigate(Screen.Bookmarks.route)
                },
                onLogoutNavigation = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Screen.Bookmarks.route) {
            BookmarksScreen(
                viewModel = bookmarksViewModel,
                onArticleClick = { article ->
                    navController.navigate(Screen.Detail.createRoute(article))
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // Detail Screen inside NavGraph.kt
        composable(
            route = Screen.Detail.route,
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "newsnow://${Screen.Detail.route}"
                }
            )
        ) { backStackEntry ->
            val article = try {
                val encodedJson = backStackEntry.arguments?.getString("articleJson") ?: ""
                // Double decode to handle both NavGraph encoding and Deep Link Uri escaping
                val decodedJson = URLDecoder.decode(encodedJson, StandardCharsets.UTF_8.toString())
                Gson().fromJson(decodedJson, Article::class.java)
            } catch (e: Exception) {
                // Fallback dummy article if deep link serialization was corrupted by URI query symbols
                Article(
                    url = "https://newsapi.org",
                    title = "Error loading article",
                    author = "",
                    sourceName = "Error",
                    description = "Could not parse deep link content.",
                    urlToImage = "",
                    publishedAt = "",
                    content = "",
                    category = "News"
                )
            }

            DetailScreen(
                article = article,
                viewModel = detailViewModel,
                onBack = {
                    // If we deep-linked into the app, back should take us to Home
                    if (navController.previousBackStackEntry == null) {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    } else {
                        navController.popBackStack()
                    }
                }
            )
        }

        // Search Screen
        composable(Screen.Search.route) {
            SearchScreen(
                viewModel = searchViewModel,
                onBackClick = { navController.popBackStack() },
                onArticleClick = { article ->
                navController.navigate(Screen.Detail.createRoute(article))
            })
        }
    }
}