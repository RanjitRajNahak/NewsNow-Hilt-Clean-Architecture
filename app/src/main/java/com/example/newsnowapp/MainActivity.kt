package com.example.newsnowapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.app1.viewmodel.AuthViewModel
import com.example.newsnowapp.presentation.bookmarks.BookmarksViewModel
import com.example.newsnowapp.presentation.detail.DetailViewModel
import com.example.newsnowapp.presentation.home.HomeViewModel
import com.example.newsnowapp.presentation.navigation.NavGraph
import com.example.newsnowapp.presentation.search.SearchViewModel
//import com.example.newsnowapp.presentation.util.NewsViewModelFactory
import com.example.newsnowapp.presentation.theme.NewsNowAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val homeViewModel: HomeViewModel by viewModels()
    private val searchViewModel: SearchViewModel by viewModels()
    private val detailViewModel: DetailViewModel by viewModels()
    private val bookmarksViewModel: BookmarksViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

//        val apiService = RetrofitClient.api
//        val database = NewsDatabase.getInstance(applicationContext)
//        val newsDao = database.newsDao
//        val repository = NewsRepositoryImpl(api = apiService, newsDao = newsDao)

        //val factory = NewsViewModelFactory(repository, applicationContext)


        setContent {
            // 1. Initialize the theme state by automatically checking the phone system setting first
            val systemInDark = isSystemInDarkTheme()
            var isDarkTheme by remember { mutableStateOf(systemInDark) }
            NewsNowAppTheme(darkTheme = isDarkTheme) {

                val navController = rememberNavController()

                // This instance has the correct Activity/Application context attachment
                val authViewModel: AuthViewModel = viewModel()

                val isLoggedIn by authViewModel.isLoggedIn.collectAsState(initial = false)


                // Pass down the state and a lambda function to change it inside your App Navigation Graph
                Surface(color = MaterialTheme.colorScheme.background) {

                    NavGraph(
                        navController = navController,
                        //factory = factory,
                        homeViewModel = homeViewModel,
                        searchViewModel = searchViewModel,
                        detailViewModel = detailViewModel,
                        bookmarksViewModel = bookmarksViewModel,
                        // ◀ ADD THESE TWO PARAMETERS HERE
                        isDarkTheme = isDarkTheme,
                        onThemeToggle = { isDarkTheme = !isDarkTheme },
                        isLoggedIn = isLoggedIn,
                        authViewModel = authViewModel ) // <-- 2. Pass it down here safely
                }
            }
        }
    }
}
