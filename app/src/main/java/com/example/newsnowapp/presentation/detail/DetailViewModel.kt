package com.example.newsnowapp.presentation.detail

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsnowapp.domain.model.Article
import com.example.newsnowapp.domain.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: NewsRepository
) : ViewModel() {

    private val _isBookmarked = mutableStateOf(false)
    val isBookmarked: State<Boolean> = _isBookmarked

    fun checkBookmarkStatus(url: String) {
        viewModelScope.launch {
            _isBookmarked.value = repository.isArticleBookmarked(url)
        }
    }

    fun toggleBookmark(article: Article, onComplete: (String) -> Unit) {
        viewModelScope.launch {
            if (_isBookmarked.value) {
                repository.deleteBookmark(article)
                _isBookmarked.value = false
                onComplete("Removed from Bookmarks")
            } else {
                repository.insertBookmark(article)
                _isBookmarked.value = true
                onComplete("Added to Bookmarks")
            }
        }
    }
}