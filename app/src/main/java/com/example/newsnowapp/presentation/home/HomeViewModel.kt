package com.example.newsnowapp.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsnowapp.domain.model.Article
import com.example.newsnowapp.domain.repository.NewsRepository
import com.example.newsnowapp.presentation.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeArticles(
    val breakingArticles: Resource<List<Article>> = Resource.Loading(),
    val generalArticles: Resource<List<Article>> = Resource.Loading()
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: NewsRepository
) : ViewModel() {

    private val _homeArticles = MutableStateFlow(HomeArticles())
    val homeArticles: StateFlow<HomeArticles> = _homeArticles.asStateFlow()

    val categories = listOf(
        "General", "Business", "Technology", "Sports", "Health", "Entertainment", "Science"
    )

    private val _selectedCategory = MutableStateFlow("Business")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    fun fetchNews(category: String) {
        _selectedCategory.value = category

        viewModelScope.launch {

            _homeArticles.value = HomeArticles(
                breakingArticles = Resource.Loading(),
                generalArticles = Resource.Loading()
            )

            val breakingDeferred = async(Dispatchers.IO) { fetchBreakingNews(category.lowercase()) }
            val generalDeferred = async(Dispatchers.IO) { fetchGeneralNews(category.lowercase()) }

            val breakingResult = breakingDeferred.await()
            val generalResult = generalDeferred.await()

            _homeArticles.value = HomeArticles(
                breakingArticles = breakingResult,
                generalArticles = generalResult
            )
        }
    }

    private suspend fun fetchBreakingNews(category: String): Resource<List<Article>> {
        return try {
            val result = repository.getTopHeadlines(category)
            if (result.isNotEmpty()) {
                Resource.Success(result)
            } else {
                Resource.Success(emptyList())
            }
        } catch (e: Exception) {
            Resource.Error("Couldn't load trending news")
        }
    }

    private suspend fun fetchGeneralNews(category: String): Resource<List<Article>> {
        return try {
            val result = repository.searchNews(category)
            if (result.isNotEmpty()) {
                Resource.Success(result)
            } else {
                Resource.Success(emptyList())
            }
        } catch (e: Exception) {
            Resource.Error("Please Check your Internet Connection")
        }
    }
}