package com.example.newsnowapp.presentation.home

import android.R
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
import com.example.app1.viewmodel.AuthViewModel
import com.example.newsnowapp.domain.model.Article
import com.example.newsnowapp.domain.repository.NewsRepository
import com.example.newsnowapp.presentation.util.Resource
import com.example.newsnowapp.presentation.components.ArticleCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel,
               isDarkTheme: Boolean,        // ◀ ADD THIS
               onThemeToggle: () -> Unit,    // ◀ ADD THIS
               onArticleClick: (Article) -> Unit,
               onSearchClick: () -> Unit,
               onBookmarksClick: () -> Unit,
               onLogoutNavigation: () -> Unit // ◀ NEW: Callback to route user back to Login screen
     ) {
    val state = viewModel.articles.value
    val trendingState = viewModel.trendingArticles.value // ◀ NEW: Observe trending data
    val selectedCat = viewModel.selectedCategory.value

    // ◀ NEW: Local state to track if the dropdown menu is open or closed
    var menuExpanded by remember { mutableStateOf(false) }

    // This runs only ONCE when the HomeScreen enters the Composition
    LaunchedEffect(Unit) {
        if (state !is Resource.Success) {
            viewModel.fetchNews("General")
        }
        // ◀ NEW: Fetch trending news alongside standard news on startup
        if (trendingState !is Resource.Success) {
            viewModel.fetchTrendingNews()
        }
    }

    Scaffold(
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    // ◀ NEW: Left Navigation Menu Anchor with Dropdown
                    navigationIcon = {
                        Box {
                            IconButton(onClick = { menuExpanded = true }) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Open Profile Menu"
                                )
                            }
                            DropdownMenu(
                                expanded = menuExpanded,
                                onDismissRequest = { menuExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Logout") },
                                    leadingIcon = {
                                        Icon(Icons.Default.Settings, contentDescription = "Logout")
                                    },
                                    onClick = {
                                        menuExpanded = false
                                        // Trigger ViewModel logout and then pass navigation upwards
                                        viewModel.logout {
                                            onLogoutNavigation()
                                        }
                                    }
                                )
                            }
                        }
                    },
                    actions = {
                        // ◀ NEW: MANUAL THEME TOGGLE BUTTON
                        IconButton(onClick = onThemeToggle) {
                            Icon(
                                imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = if (isDarkTheme) "Switch to Light Mode" else "Switch to Dark Mode"
                            )
                        }
                        IconButton(onClick = onSearchClick) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                    },
                    title = { Text("NewsNow", style = MaterialTheme.typography.headlineLarge) }
                )

                // ◀ NEW: TRENDING NEWS IMAGE CAROUSEL SECTION
                if (trendingState is Resource.Success && !trendingState.data.isNullOrEmpty()) {
                    val carouselState = rememberCarouselState { trendingState.data.size }

                    // ◀ NEW: 4-Second Auto-Scroll Timer Engine
                    LaunchedEffect(key1 = trendingState.data.size) {
                        val totalItems = trendingState.data.size
                        if (totalItems > 1) {
                            while (true) {
                                kotlinx.coroutines.delay(4000) // Wait exactly 4 seconds

                                // 1. Fixed property: read current position using .currentItem
                                val nextItem = (carouselState.currentItem + 1) % totalItems

                                // 2. Fixed method: call .animateScrollToItem instead of page
                                carouselState.animateScrollToItem(nextItem)
                            }
                        }
                    }

                    Column(modifier = Modifier.padding(bottom = 8.dp)) {
                        Text(
                            text = "Trending News",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                        )

                        HorizontalMultiBrowseCarousel(
                            state = carouselState,
                            preferredItemWidth = 320.dp, // Sets the width layout profile for each item card
                            itemSpacing = 8.dp,
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            modifier = Modifier.height(180.dp)
                        ) { index ->
                            val article = trendingState.data[index]

                            // Wrapping card with full click handler to open details screen
                            Card(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable { onArticleClick(article) },
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    // Main Headline Background Image Image
                                    AsyncImage(
                                        model = article.urlToImage,
                                        contentDescription = article.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )

                                    // Gradient Shader Box to make overlapping text highly readable
                                    Surface(
                                        color = Color.Black.copy(alpha = 0.45f),
                                        modifier = Modifier.fillMaxSize()
                                    ) {}

                                    // Article Details Text placed over the bottom of the card
                                    Column(
                                        modifier = Modifier
                                            .align(Alignment.BottomStart)
                                            .padding(12.dp)
                                    ) {
                                        Text(
                                            text = article.sourceName,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.primaryContainer,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = article.title,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                ScrollableTabRow(
                    selectedTabIndex = viewModel.categories.indexOf(selectedCat),
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
                    edgePadding = 16.dp,
                    divider = {}
                ) {
                    viewModel.categories.forEach { category ->
                        Tab(
                            selected = selectedCat == category,
                            onClick = { viewModel.fetchNews(category) },
                            text = {
                                Text(
                                    text = category,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (selectedCat == category) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }
            }
        },
        // ◀ NEW: FLOATING ACTION BUTTON IMPLEMENTATION
        floatingActionButton = {
            FloatingActionButton(
                onClick = onBookmarksClick,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.Bookmark,
                    contentDescription = "Open Bookmarks"
                )
            }
        }

    ) { padding ->
        val isRefreshing = state is Resource.Loading && state.data != null

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.fetchNews(selectedCat)
                viewModel.fetchTrendingNews() // ◀ Pull to refresh updates the carousel too!
                 },
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            when (state) {
                is Resource.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is Resource.Success -> {
                    LazyColumn(contentPadding = PaddingValues(bottom = 16.dp)) {
                        items(state.data ?: emptyList()) { article ->
                            ArticleCard(
                                article = article,
                                onClick = { onArticleClick(article) }
                            )
                        }
                    }
                }
                is Resource.Error -> {
                    Text(
                        text = state.message ?: "Unknown Error",
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

