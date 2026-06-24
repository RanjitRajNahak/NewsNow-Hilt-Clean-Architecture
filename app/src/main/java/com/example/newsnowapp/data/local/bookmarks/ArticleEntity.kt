package com.example.newsnowapp.data.local.bookmarks

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.newsnowapp.domain.model.Article

@Entity(tableName = "bookmarked_articles")
data class ArticleEntity(
    @PrimaryKey val url: String,
    val title: String,
    val author: String?,
    val sourceName: String,
    val description: String?,
    val urlToImage: String?,
    val publishedAt: String,
    val content: String?,
    val category: String
)

