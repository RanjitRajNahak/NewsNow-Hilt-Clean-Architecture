package com.example.newsnowapp.presentation.search

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsnowapp.domain.model.Article
import com.example.newsnowapp.domain.repository.NewsRepository
import com.example.newsnowapp.presentation.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: NewsRepository,
    @ApplicationContext private val context: Context
    ) : ViewModel() {

    private val sharedPreferences = context.getSharedPreferences("search_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_SEARCH_HISTORY = "search_history"
    }

    private val _searchResults = MutableStateFlow<Resource<List<Article>>>(Resource.Success(emptyList()))
    val searchResults: StateFlow<Resource<List<Article>>> = _searchResults.asStateFlow()
    private val _recentSearches = MutableStateFlow<List<String>>(loadRecentSearches())
    val recentSearches: StateFlow<List<String>> = _recentSearches.asStateFlow()

    fun searchNews(query: String) {
        if (query.isEmpty()) return

        viewModelScope.launch {
            _searchResults.value = Resource.Loading()
            try {
                val result = repository.searchNews(query)
                if (result.isNotEmpty()) {
                    _searchResults.value = Resource.Success(result)
                } else {
                    _searchResults.value = Resource.Success(emptyList())
                }
            } catch (e: Exception) {
                _searchResults.value = Resource.Error("Please check you Internet Connection")
            }
        }
    }

    fun addQueryToHistory(query: String) {
        val currentList = _recentSearches.value.toMutableList()
        currentList.remove(query)
        currentList.add(0, query)
        val updatedList = currentList.take(10)
        _recentSearches.value = updatedList
        saveRecentSearches(updatedList)
    }

    fun removeQueryFromHistory(query: String) {
        val currentList = _recentSearches.value.toMutableList()
        currentList.remove(query)
        _recentSearches.value = currentList
        saveRecentSearches(currentList)
    }

    private fun saveRecentSearches(list: List<String>) {
        val jsonArray = JSONArray(list)
        val jsonString = jsonArray.toString()
        sharedPreferences.edit().putString(KEY_SEARCH_HISTORY, jsonString).apply()
    }

    private fun loadRecentSearches(): List<String> {
        val jsonString = sharedPreferences.getString(KEY_SEARCH_HISTORY, null) ?: return emptyList()
        return try {
            val jsonArray = JSONArray(jsonString)
            val list = mutableListOf<String>()
            for (i in 0 until jsonArray.length()) {
                list.add(jsonArray.getString(i))
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }
}