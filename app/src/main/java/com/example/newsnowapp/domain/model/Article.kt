package com.example.newsnowapp.domain.model

data class Article(
    val url: String,
    val title: String,
    val author: String?,
    val sourceName: String,
    val description: String?,
    val urlToImage: String?,
    val publishedAt: String,
    val content: String?,
    val category: String,
    val isBookmarked: Boolean = false
)