package com.example.newsnowapp.data.local.bookmarkData

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.newsnowapp.domain.model.Article

@Entity(tableName = "bookmarked_articles")
data class ArticleEntity(
    @PrimaryKey val url: String, // Web URL acts as a perfect unique identifier
    val title: String,
    val author: String?,
    val sourceName: String,
    val description: String?,
    val urlToImage: String?,
    val publishedAt: String,
    val content: String?,
    val category: String
)

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
        isBookmarked = true // If it is in this table, it is bookmarked
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