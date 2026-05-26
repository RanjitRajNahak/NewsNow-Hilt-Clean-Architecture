package com.example.newsnowapp.presentation.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsnowapp.domain.model.Article
import com.example.newsnowapp.domain.repository.NewsRepository
import com.example.newsnowapp.presentation.util.Resource
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: NewsRepository) : ViewModel() {

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
}