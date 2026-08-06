package com.example.newsnowapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.newsnowapp.presentation.auth.AuthViewModel
import com.example.newsnowapp.presentation.bookmarks.BookmarksViewModel
import com.example.newsnowapp.presentation.detail.DetailViewModel
import com.example.newsnowapp.presentation.home.HomeViewModel
import com.example.newsnowapp.presentation.navigation.NavGraph
import com.example.newsnowapp.presentation.search.SearchViewModel
import com.example.newsnowapp.presentation.theme.NewsNowAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()
    private val searchViewModel: SearchViewModel by viewModels()
    private val detailViewModel: DetailViewModel by viewModels()
    private val bookmarksViewModel: BookmarksViewModel by viewModels()

    // 1. Class level property holds the reference
    private var navController: NavHostController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val systemInDark = isSystemInDarkTheme()
            var isDarkTheme by remember { mutableStateOf(systemInDark) }

            NewsNowAppTheme(darkTheme = isDarkTheme) {

                // 2. Assign the controller to the class-level variable (NO 'val' keyword before navController!)
                val controller = rememberNavController().also {
                    navController = it
                }

                Surface(color = MaterialTheme.colorScheme.background) {
                    NavGraph(
                        navController = controller, // Use the assigned controller
                        authViewModel = authViewModel,
                        homeViewModel = homeViewModel,
                        searchViewModel = searchViewModel,
                        detailViewModel = detailViewModel,
                        bookmarksViewModel = bookmarksViewModel,
                        isDarkTheme = isDarkTheme,
                        onThemeToggle = { isDarkTheme = !isDarkTheme }
                    )
                }
            }
        }
    }

    // Handles deep links when the app is already running in the background
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        navController?.handleDeepLink(intent)
    }
}