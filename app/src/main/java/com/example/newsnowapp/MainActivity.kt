package com.example.newsnowapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.newsnowapp.data.remote.RetrofitClient
import com.example.newsnowapp.repository.NewsRepository
import com.example.newsnowapp.ui.navigation.NavGraph
import com.example.newsnowapp.viewmodel.SearchViewModel
import com.example.newsnowapp.viewmodel.NewsViewModelFactory
import com.example.newsnowapp.ui.theme.NewsNowAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
     
        val apiService = RetrofitClient.api
        val repository = NewsRepository(apiService)
        val factory = NewsViewModelFactory(repository)


        setContent {
            NewsNowAppTheme {
                val navController = rememberNavController()

                Surface(color = MaterialTheme.colorScheme.background) {

                    NavGraph(navController = navController, factory = factory)
                }
            }
        }
    }
}
