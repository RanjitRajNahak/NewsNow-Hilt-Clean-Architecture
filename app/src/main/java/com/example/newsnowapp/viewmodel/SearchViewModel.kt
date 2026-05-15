package com.example.newsnowapp.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsnowapp.data.local.Article
import com.example.newsnowapp.repository.NewsRepository
import kotlinx.coroutines.launch

class SearchViewModel(private val repository: NewsRepository) : ViewModel() {

    private val _searchResults = mutableStateOf<Resource<List<Article>>>(Resource.Success(emptyList()))
    val searchResults: State<Resource<List<Article>>> = _searchResults

    fun searchNews(query: String) {
        if (query.isEmpty()) return

        viewModelScope.launch {
            _searchResults.value = Resource.Loading()
            try {
                val result = repository.searchNews(query)
                if (result.isNotEmpty()) {
                    _searchResults.value = Resource.Success(result)
                } else {
                    _searchResults.value = Resource.Error("No results found for '$query'")
                }
            } catch (e: Exception) {
                _searchResults.value = Resource.Error(e.message ?: "Search failed")
            }
        }
    }
}

