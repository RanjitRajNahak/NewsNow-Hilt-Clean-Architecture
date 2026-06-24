package com.example.newsnowapp.data.mapper

import com.example.newsnowapp.data.local.bookmarks.ArticleEntity
import com.example.newsnowapp.data.remote.ArticleDto
import com.example.newsnowapp.domain.model.Article

// Extension function to transform Network data to Domain data
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
        category = category,
        isBookmarked = false
    )
}

// Extension function to convert Database Entity to Domain Model
fun ArticleEntity.toArticle(): Article {
    return Article(
        url = url,
        title = title,
        author = author,
        sourceName = sourceName,
        description = description,
        urlToImage = urlToImage,
        publishedAt = publishedAt,
        content = content,
        category = category,
        isBookmarked = true
    )
}

// Extension function to convert Domain Model to Database Entity
fun Article.toArticleEntity(): ArticleEntity {
    return ArticleEntity(
        url = url,
        title = title,
        author = author,
        sourceName = sourceName,
        description = description,
        urlToImage = urlToImage,
        publishedAt = publishedAt,
        content = content,
        category = category
    )
}