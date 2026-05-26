package com.example.newsnowapp.presentation.bookmarks

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsnowapp.domain.model.Article
import com.example.newsnowapp.domain.repository.NewsRepository
import kotlinx.coroutines.launch

class BookmarksViewModel(
    private val repository: NewsRepository
) : ViewModel() {

    private val _bookmarkedArticles = mutableStateOf<List<Article>>(emptyList())
    val bookmarkedArticles: State<List<Article>> = _bookmarkedArticles

    init {
        // Collect real-time updates from Room Database Flow
        viewModelScope.launch {
            repository.getBookmarkedArticles().collect { articles ->
                _bookmarkedArticles.value = articles
            }
        }
    }

    // ◀ NEW: Function to trigger the repository delete operation
    fun clearAllBookmarks() {
        viewModelScope.launch {
            repository.clearAllBookmarks()
        }
    }

    fun deleteBookmark(article: Article) {
        viewModelScope.launch {
            repository.deleteBookmark(article)
        }
    }
}