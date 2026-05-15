package com.example.newsnowapp.repository

import com.example.newsnowapp.data.local.Article
import com.example.newsnowapp.data.remote.NewsApiService
import com.example.newsnowapp.data.remote.ArticleDto
import com.example.newsnowapp.BuildConfig

class NewsRepository(
    private val api: NewsApiService
) {
    private val apiKey = BuildConfig.API_KEY

    suspend fun getTopHeadlines(category: String): List<Article> {
        return try {
            val response = api.getTopHeadlines(category = category, apiKey = apiKey)
            // Convert DTOs to our UI Model
            response.articles.map { dto -> dto.toArticle(category) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun searchNews(query: String): List<Article> {
        return try {
            val response = api.searchNews(query = query, apiKey = apiKey)
            response.articles.map { it.toArticle("Search") }
        } catch (e: Exception) {
            emptyList()
        }
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