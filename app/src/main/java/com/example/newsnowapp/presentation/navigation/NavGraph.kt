package com.example.newsnowapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.app1.ui.screens.LoginScreen
import com.example.app1.ui.screens.SignUpScreen
import com.example.app1.viewmodel.AuthViewModel
import com.example.newsnowapp.domain.model.Article
import com.example.newsnowapp.presentation.bookmarks.BookmarksScreen
import com.example.newsnowapp.presentation.bookmarks.BookmarksViewModel
import com.example.newsnowapp.presentation.home.HomeScreen
import com.example.newsnowapp.presentation.detail.DetailScreen
import com.example.newsnowapp.presentation.detail.DetailViewModel
import com.example.newsnowapp.presentation.search.SearchViewModel
import com.example.newsnowapp.presentation.search.SearchScreen
import com.example.newsnowapp.presentation.home.HomeViewModel
//import com.example.newsnowapp.presentation.util.NewsViewModelFactory
import com.google.gson.Gson
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

sealed class Screen(val route: String) {

    object Login: Screen("login")
    object SignUp: Screen("signup")
    object Home : Screen("home")
    object Bookmarks : Screen("bookmarks")
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
    isLoggedIn: Boolean,
    authViewModel: AuthViewModel, // <-- 1. Accept it as a parameter here
    //factory: NewsViewModelFactory,
    homeViewModel: HomeViewModel,
    searchViewModel: SearchViewModel,
    detailViewModel: DetailViewModel,
    bookmarksViewModel: BookmarksViewModel,
    isDarkTheme: Boolean,        // ◀ ADD THIS
    onThemeToggle: () -> Unit    // ◀ ADD THIS
) {
    //val repository = factory.repository
    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) Screen.Home.route else Screen.Login.route
    ) {
        // 1. Login Screen
        composable(route = Screen.Login.route) {
            LoginScreen(
                authViewModel = authViewModel,
                onSignUpClick = {
                    navController.navigate(Screen.SignUp.route)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // 2. Sign Up Screen
        composable(route = Screen.SignUp.route) {
            SignUpScreen(
                authViewModel = authViewModel,
                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }
        // Home Screen
        composable(route = Screen.Home.route) {
            //val homeViewModel: HomeViewModel = viewModel(factory = factory)
            HomeScreen(
                viewModel = homeViewModel,
                isDarkTheme = isDarkTheme,       // ◀ PASS IT HERE
                onThemeToggle = onThemeToggle,   // ◀ PASS IT HERE
                onArticleClick = { article ->
                    navController.navigate(Screen.Detail.createRoute(article))
                },
                onSearchClick = {
                    navController.navigate(Screen.Search.route) // Navigate to search screen
                },
                onBookmarksClick = {
                    navController.navigate(Screen.Bookmarks.route) // ◀ Navigates to Bookmarks
                },
                onLogoutNavigation = {
                    // ◀ NEW: Navigates back to auth and clears home screen from the backstack history
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        // ◀ NEW: Bookmarks Screen Destination Route Entry Mapping block
        composable(route = Screen.Bookmarks.route) {
            // Note: Update your NewsViewModelFactory to return BookmarksViewModel when requested
            //val bookmarksViewModel: BookmarksViewModel = viewModel(factory = factory)
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

        // Detail Screen
        composable(route = Screen.Detail.route) { backStackEntry ->
            // 1. Get the encoded JSON string
            val encodedJson = backStackEntry.arguments?.getString("articleJson") ?: ""

            // 2. DECODE it back to normal JSON (this removes the '+' and '%20' signs)
            val decodedJson = URLDecoder.decode(encodedJson, StandardCharsets.UTF_8.toString())

            // 3. Convert it back to your Article object
            val article = Gson().fromJson(decodedJson, Article::class.java)
            DetailScreen(
                article = article,
                //repository = repository,
                viewModel = detailViewModel,
                onBack = { navController.popBackStack() })
        }

        // Search Screen
        composable(Screen.Search.route) {
            //val searchViewModel: SearchViewModel = viewModel(factory = factory)
            SearchScreen(viewModel = searchViewModel, onBackClick = {navController.popBackStack()}, onArticleClick = { article ->
                navController.navigate(Screen.Detail.createRoute(article))
            })
        }
    }
}