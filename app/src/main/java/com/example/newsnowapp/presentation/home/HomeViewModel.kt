package com.example.newsnowapp.presentation.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsnowapp.data.repository.AuthRepository
import com.example.newsnowapp.domain.model.Article
import com.example.newsnowapp.domain.repository.NewsRepository
import com.example.newsnowapp.presentation.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: NewsRepository,
    private val authRepository: AuthRepository // ◀ NEW: Inject your Auth repository here
    ) : ViewModel() {

    private val _articles = mutableStateOf<Resource<List<Article>>>(Resource.Loading())
    val articles: State<Resource<List<Article>>> = _articles


    //The list of categories for the tabs
    val categories = listOf(
        "General", "Business", "Technology", "Sports", "Health", "Entertainment", "Science"
    )

    //Track the selected tab
    private val _selectedCategory = mutableStateOf("General")
    val selectedCategory: State<String> = _selectedCategory

    fun fetchNews(category: String) {
        _selectedCategory.value = category
        viewModelScope.launch {
            _articles.value = Resource.Loading()
            val result = repository.getTopHeadlines(category.lowercase())
            if (result.isNotEmpty()) {
                _articles.value = Resource.Success(result)
            } else {
                _articles.value = Resource.Error("Couldn't fetch $category news")
            }
        }
    }

    // ◀ NEW: Separate state to hold trending carousel articles
    private val _trendingArticles = mutableStateOf<Resource<List<Article>>>(Resource.Loading())
    val trendingArticles: State<Resource<List<Article>>> = _trendingArticles

    // ◀ NEW: Fetches top general breaking news once on startup
    fun fetchTrendingNews() {
        viewModelScope.launch {
            _trendingArticles.value = Resource.Loading()
            // We use 'general' or an empty query to pull the absolute latest top breaking news
            val result = repository.getTopHeadlines("general")
            if (result.isNotEmpty()) {
                // Take the top 5-7 articles to keep the carousel snappy and relevant
                _trendingArticles.value = Resource.Success(result.take(7))
            } else {
                _trendingArticles.value = Resource.Error("Couldn't load trending news")
            }
        }
    }

    // ◀ NEW: Handle Session Deletion on a background thread Coroutine
    fun logout(onLogoutComplete: () -> Unit) {
        viewModelScope.launch {
            authRepository.logoutUser() // Clears DataStore values via SessionManager
            onLogoutComplete() // Triggers navigation route back to login screen
        }
    }
}