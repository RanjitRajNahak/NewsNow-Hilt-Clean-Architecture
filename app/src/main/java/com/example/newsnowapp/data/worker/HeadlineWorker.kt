package com.example.newsnowapp.data.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.newsnowapp.BuildConfig
import com.example.newsnowapp.R
import com.example.newsnowapp.domain.model.Article
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class HeadlineWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        Log.d("HeadlineWorker", "Worker started executing!")

        try {
            val apiKey = BuildConfig.API_KEY
            val urlString = "https://newsapi.org/v2/top-headlines?country=us&category=business&apiKey=$apiKey"

            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("User-Agent", "NewsNowApp/1.0")
            connection.setRequestProperty("Accept", "application/json")

            val responseCode = connection.responseCode
            val responseText = if (responseCode in 200..299) {
                connection.inputStream.bufferedReader().use { it.readText() }
            } else {
                val errorText = connection.errorStream?.bufferedReader()?.use { it.readText() }
                throw Exception("HTTP $responseCode: $errorText")
            }

            val articlesArray = JSONObject(responseText).getJSONArray("articles")

            if (articlesArray.length() > 0) {
                val randomIndex = (0 until articlesArray.length()).random()
                val articleJson = articlesArray.getJSONObject(randomIndex)

                // 1. Map raw JSON fields into your actual Domain Model
                val article = Article(
                    url = articleJson.optString("url", ""),
                    title = articleJson.optString("title", "No Title"),
                    author = articleJson.optNullString("author"), // Safe nullable parsing
                    sourceName = articleJson.getJSONObject("source").optString("name", "Unknown"),
                    description = articleJson.optNullString("description"),
                    urlToImage = articleJson.optNullString("urlToImage"),
                    publishedAt = articleJson.optString("publishedAt", ""),
                    content = articleJson.optNullString("content"),
                    category = "Business", // Since we are specifically querying the business endpoint here
                    isBookmarked = false
                )

                // 2. Deliver notification with deep link intent attached
                showNotificationWithDeepLink(article)
            }
            Result.success()

        } catch (e: Exception) {
            Log.e("HeadlineWorker", "Worker failed!", e)
            Result.retry()
        }
    }

    private fun showNotificationWithDeepLink(article: Article) {
        val channelId = "news_headlines_channel"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Daily Headlines",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        // 1. Serialize and URL Encode the Article model exactly like HomeScreen.kt
        val json = Gson().toJson(article)
        val encodedJson = URLEncoder.encode(json, StandardCharsets.UTF_8.toString())

        // 2. Build the exact Uri mapped in our NavGraph (newsnow://detail/{articleJson})
        val deepLinkUri = Uri.parse("newsnow://detail/$encodedJson")

        // 3. Create deep-link notification Intent
        val intent = Intent(Intent.ACTION_VIEW, deepLinkUri).apply {
            // Ensures that if the app is already open, it routes nicely
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        // 4. Wrap it inside a PendingIntent
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 5. Build and present the notification
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.logo_news)
            .setContentTitle(article.sourceName)
            .setContentText(article.title)
            .setStyle(NotificationCompat.BigTextStyle().bigText(article.title))
            .setContentIntent(pendingIntent) // Tap action
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true) // Dismiss from bar after click
            .build()

        notificationManager.notify((System.currentTimeMillis() % 100000).toInt(), notification)
    }

    // Helper extension to handle nullable JSON fields gracefully
    fun JSONObject.optNullString(name: String): String? {
        if (this.isNull(name)) return null
        return this.optString(name, null)
    }
}