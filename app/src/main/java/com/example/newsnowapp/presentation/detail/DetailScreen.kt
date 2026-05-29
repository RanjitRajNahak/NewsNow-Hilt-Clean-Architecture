package com.example.newsnowapp.presentation.detail

import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.example.newsnowapp.domain.model.Article
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    article: Article,
    viewModel: DetailViewModel, // ◀ Updated: Replaced repository with DetailViewModel
    onBack: () -> Unit
) {
    var showWebView by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Check bookmark status safely through ViewModel when opening the article
    LaunchedEffect(article.url) {
        viewModel.checkBookmarkStatus(article.url)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            if (!showWebView) {
                DetailBottomBar(
                    isBookmarked = viewModel.isBookmarked.value,
                    onReadFullClick = { showWebView = true },
                    onBookmarkToggle = {
                        viewModel.toggleBookmark(article) { message ->
                            scope.launch {
                                snackbarHostState.showSnackbar(message)
                            }
                        }
                    }
                )
            }
        }
    ) { padding ->
        if (showWebView) {
            var webView: WebView? by remember { mutableStateOf(null) }

            BackHandler(enabled = showWebView) {
                if (webView?.canGoBack() == true) {
                    webView?.goBack()
                } else {
                    showWebView = false
                }
            }

            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                TopAppBar(
                    title = { Text("Full Article", style = MaterialTheme.typography.titleSmall) },
                    navigationIcon = {
                        IconButton(onClick = {
                            if (webView?.canGoBack() == true) {
                                webView?.goBack()
                            } else {
                                showWebView = false
                            }
                        }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
                AndroidView(
                    factory = { context ->
                        WebView(context).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            webViewClient = WebViewClient()
                            settings.javaScriptEnabled = true
                            loadUrl(article.url)
                            webView = this
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(padding)
                    .padding(bottom = 80.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
                    AsyncImage(
                        model = article.urlToImage,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize().background(Color.DarkGray),
                        contentScale = ContentScale.Crop
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.background(Color.Black.copy(alpha = 0.3f), CircleShape)
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
                        }
                    }
                }

                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = article.category.uppercase(),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = article.title,
                        style = MaterialTheme.typography.headlineLarge,
                        lineHeight = 32.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.LightGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(article.sourceName.take(1))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = article.sourceName, fontWeight = FontWeight.Bold)
                            Text(
                                text = "${article.publishedAt.take(10)} • 4 min read",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = article.description ?: "No description available.",
                        style = MaterialTheme.typography.bodyLarge,
                        lineHeight = 26.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun DetailBottomBar(
    isBookmarked: Boolean,
    onReadFullClick: () -> Unit,
    onBookmarkToggle: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp).navigationBarsPadding(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onReadFullClick,
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Read Full Article ↗")
            }
            Spacer(modifier = Modifier.width(16.dp))
            FilledIconButton(
                onClick = onBookmarkToggle,
                modifier = Modifier.size(50.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = "Toggle Saved State"
                )
            }
        }
    }
}