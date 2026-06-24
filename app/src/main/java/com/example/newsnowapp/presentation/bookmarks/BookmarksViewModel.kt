package com.example.newsnowapp.presentation.bookmarks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsnowapp.domain.model.Article
import com.example.newsnowapp.domain.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarksViewModel @Inject constructor(
    private val repository: NewsRepository
) : ViewModel() {

    private val _bookmarkedArticles = MutableStateFlow<List<Article>>(emptyList())
    val bookmarkedArticles: StateFlow<List<Article>> = _bookmarkedArticles.asStateFlow()

    fun getBookmarkedArticles() {
        viewModelScope.launch {
            repository.getBookmarkedArticles().collect { articles ->
                _bookmarkedArticles.value = articles
            }
        }
    }


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