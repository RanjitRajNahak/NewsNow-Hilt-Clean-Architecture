package com.example.newsnowapp.data.repository

import com.example.newsnowapp.BuildConfig
import com.example.newsnowapp.data.local.bookmarks.NewsDao
import com.example.newsnowapp.data.mapper.toArticle
import com.example.newsnowapp.data.mapper.toArticleEntity
import com.example.newsnowapp.data.remote.NewsApiService
import com.example.newsnowapp.domain.model.Article
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
        newsDao.deleteAllBookmarks()
    }
}