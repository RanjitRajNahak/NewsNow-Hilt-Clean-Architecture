package com.example.newsnowapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
import com.example.newsnowapp.data.local.NewsDatabase
import com.example.newsnowapp.data.remote.RetrofitClient
import com.example.newsnowapp.data.repository.NewsRepositoryImpl
import com.example.newsnowapp.presentation.navigation.NavGraph
import com.example.newsnowapp.presentation.util.NewsViewModelFactory
import com.example.newsnowapp.presentation.theme.NewsNowAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
     
        val apiService = RetrofitClient.api
        val database = NewsDatabase.getInstance(applicationContext)
        val newsDao = database.newsDao
        val repository = NewsRepositoryImpl(api = apiService, newsDao = newsDao)

        val factory = NewsViewModelFactory(repository, applicationContext)


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
