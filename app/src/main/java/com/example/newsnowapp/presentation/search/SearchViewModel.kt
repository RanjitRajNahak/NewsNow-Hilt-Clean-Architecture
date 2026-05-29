package com.example.newsnowapp.presentation.search

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsnowapp.domain.model.Article
import com.example.newsnowapp.domain.repository.NewsRepository
import com.example.newsnowapp.presentation.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import org.json.JSONArray
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: NewsRepository,
    @ApplicationContext private val context: Context
    ) : ViewModel() {

    private val sharedPreferences = context.getSharedPreferences("search_prefs", Context.MODE_PRIVATE)

    private val _searchResults = mutableStateOf<Resource<List<Article>>>(Resource.Success(emptyList()))
    val searchResults: State<Resource<List<Article>>> = _searchResults

    // ◀ 2. Initialize the state by loading saved history from SharedPreferences instantly
    private val _recentSearches = mutableStateOf<List<String>>(loadRecentSearches())
    val recentSearches: State<List<String>> = _recentSearches

    fun searchNews(query: String) {
        if (query.isEmpty()) return

        // ◀ NEW: Save search query to history list
        addQueryToHistory(query.trim())

        viewModelScope.launch {
            _searchResults.value = Resource.Loading()
            try {
                val result = repository.searchNews(query)
                if (result.isNotEmpty()) {
                    _searchResults.value = Resource.Success(result)
                } else {
                    // Send an empty success indicator instead of an error,
                    // this allows us to handle the proper "No results" UI explicitly
                    _searchResults.value = Resource.Success(emptyList())
                }
            } catch (e: Exception) {
                _searchResults.value = Resource.Error(e.message ?: "Search failed")
            }
        }

        // ◀ NEW: Appends search terms to the history stack cleanly without duplicates
    }

    private fun addQueryToHistory(query: String) {
        val currentList = _recentSearches.value.toMutableList()
        currentList.remove(query) // Remove it if it exists to bump it to the front
        currentList.add(0, query) // Insert newest query at the beginning
        val updatedList = currentList.take(10) // Restrict history memory length to 10 items
        _recentSearches.value = updatedList
        saveRecentSearches(updatedList) // ◀ 3. Save changes to SharedPreferences
    }

    // ◀ NEW: Dismiss individual query chips from history list
    fun removeQueryFromHistory(query: String) {
        val currentList = _recentSearches.value.toMutableList()
        currentList.remove(query)
        _recentSearches.value = currentList
        saveRecentSearches(currentList) // ◀ 4. Save changes to SharedPreferences
    }

    // ◀ 5. Helper function to serialize list into a JSON string and store it
    private fun saveRecentSearches(list: List<String>) {
        val jsonArray = JSONArray(list)
        sharedPreferences.edit().putString("recent_searches_key", jsonArray.toString()).apply()
    }

    // ◀ 6. Helper function to parse JSON string back into a standard list on initialization
    private fun loadRecentSearches(): List<String> {
        val jsonString = sharedPreferences.getString("recent_searches_key", null) ?: return emptyList()
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