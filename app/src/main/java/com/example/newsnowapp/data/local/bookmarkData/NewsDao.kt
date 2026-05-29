package com.example.newsnowapp.data.local.bookmarkData

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsDao {

    // Saves an article bookmark. If it already exists, replace it to update data.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(article: ArticleEntity)

    // Removes an article bookmark
    @Delete
    suspend fun deleteBookmark(article: ArticleEntity)

    // Emits an updated list of all bookmarked articles automatically when data changes
    @Query("SELECT * FROM bookmarked_articles ORDER BY publishedAt DESC")
    fun getAllBookmarks(): Flow<List<ArticleEntity>>

    // Checks if a specific article is bookmarked (returns null if not found)
    @Query("SELECT * FROM bookmarked_articles WHERE url = :url")
    suspend fun getBookmarkByUrl(url: String): ArticleEntity?

    @Query("DELETE FROM bookmarked_articles")
    suspend fun deleteAllBookmarks()
}