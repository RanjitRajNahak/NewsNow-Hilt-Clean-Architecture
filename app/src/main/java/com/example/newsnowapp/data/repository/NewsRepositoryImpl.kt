package com.example.newsnowapp.data.repository

import com.example.newsnowapp.domain.model.Article
import com.example.newsnowapp.data.remote.NewsApiService
import com.example.newsnowapp.data.remote.ArticleDto
import com.example.newsnowapp.BuildConfig
import com.example.newsnowapp.data.local.bookmarkData.NewsDao
import com.example.newsnowapp.data.local.bookmarkData.toArticle
import com.example.newsnowapp.data.local.bookmarkData.toArticleEntity
import com.example.newsnowapp.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val api: NewsApiService,
    private val newsDao: NewsDao
): NewsRepository {
    private val apiKey = BuildConfig.API_KEY

    override suspend fun getTopHeadlines(category: String): List<Article> {
        return try {
            val response = api.getTopHeadlines(category = category, apiKey = apiKey)
            // Convert DTOs to our UI Model
            response.articles.map { dto -> dto.toArticle(category) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun searchNews(query: String): List<Article> {
        return try {
            val response = api.searchNews(query = query, apiKey = apiKey)
            response.articles.map { it.toArticle("Search") }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // ◀ NEW: Read bookmarks from DB and map the entities back into pure Domain Articles
    override fun getBookmarkedArticles(): Flow<List<Article>> {
        return newsDao.getAllBookmarks().map { entities ->
            entities.map { it.toArticle() }
        }
    }

    override suspend fun insertBookmark(article: Article) {
        newsDao.insertBookmark(article.toArticleEntity())
    }

    override suspend fun deleteBookmark(article: Article) {
        newsDao.deleteBookmark(article.toArticleEntity())
    }

    override suspend fun isArticleBookmarked(url: String): Boolean {
        return newsDao.getBookmarkByUrl(url) != null
    }

    override suspend fun clearAllBookmarks() {
        newsDao.deleteAllBookmarks() // ◀ NEW: Calls the fast query
    }
}

// Extension function to transform the data
fun ArticleDto.toArticle(category: String): Article {
    return Article(
        url = this.url,
        title = this.title,
        author = this.author ?: "Unknown",
        sourceName = this.source.name,
        description = this.description,
        urlToImage = this.urlToImage,
        publishedAt = this.publishedAt,
        content = this.content,
        category = category
    )
}