package com.example.newsnowapp.domain.repository

import com.example.newsnowapp.domain.model.Article
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    // API Actions
    suspend fun getTopHeadlines(category: String): List<Article>
    suspend fun searchNews(query: String): List<Article>

    // ◀ NEW: Local Bookmark Operations
    fun getBookmarkedArticles(): Flow<List<Article>>
    suspend fun insertBookmark(article: Article)
    suspend fun deleteBookmark(article: Article)
    suspend fun isArticleBookmarked(url: String): Boolean
    suspend fun clearAllBookmarks()
}