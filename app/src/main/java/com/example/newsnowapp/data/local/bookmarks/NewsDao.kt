package com.example.newsnowapp.data.local.bookmarks

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(article: ArticleEntity)

    @Delete
    suspend fun deleteBookmark(article: ArticleEntity)

    @Query("SELECT * FROM bookmarked_articles ORDER BY publishedAt DESC")
    fun getAllBookmarks(): Flow<List<ArticleEntity>>

    @Query("SELECT * FROM bookmarked_articles WHERE url = :url")
    suspend fun getBookmarkByUrl(url: String): ArticleEntity?

    @Query("DELETE FROM bookmarked_articles")
    suspend fun deleteAllBookmarks()
}