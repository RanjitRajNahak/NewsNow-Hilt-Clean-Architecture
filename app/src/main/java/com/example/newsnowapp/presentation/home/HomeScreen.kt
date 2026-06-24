package com.example.newsnowapp.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.newsnowapp.domain.model.Article
import com.example.newsnowapp.presentation.components.ArticleCard
import com.example.newsnowapp.presentation.util.Resource
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    onArticleClick: (Article) -> Unit,
    onSearchClick: () -> Unit,
    onBookmarksClick: () -> Unit,
    onLogoutNavigation: () -> Unit
) {
    val homeArticles by viewModel.homeArticles.collectAsState()
    val breakingState = homeArticles.breakingArticles
    val generalState = homeArticles.generalArticles
    val selectedCat by viewModel.selectedCategory.collectAsState()

    var menuExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (breakingState !is Resource.Success && generalState !is Resource.Success) {
            viewModel.fetchNews("General")
        }
    }

    Scaffold(
        topBar = {
            Column {
                CenterAlignedTopAppBar(
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
                                        Icon(Icons.Default.Logout, contentDescription = "Logout")
                                    },
                                    onClick = {
                                        onLogoutNavigation()
                                        menuExpanded = false
                                    }
                                )
                            }
                        }
                    },
                    title = { Text("NewsNow", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary) },
                    actions = {

                        IconButton(onClick = onThemeToggle) {
                            Icon(
                                imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = if (isDarkTheme) "Switch to Light Mode" else "Switch to Dark Mode"
                            )
                        }
                        IconButton(onClick = onSearchClick) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                    }
                )

                SecondaryScrollableTabRow(
                    selectedTabIndex = viewModel.categories.indexOf(selectedCat),
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.primary,
                    edgePadding = 16.dp
                ) {
                    viewModel.categories.forEach { category ->
                        Tab(
                            selected = selectedCat == category,
                            onClick = {
                                viewModel.fetchNews(category) },
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = onBookmarksClick,
                containerColor = MaterialTheme.colorScheme.surface, // Background Color
                contentColor = MaterialTheme.colorScheme.primary  // Contrast to Background
            ) {
                Icon(
                    imageVector = Icons.Default.Bookmark,
                    contentDescription = "Open Bookmarks"
                )
            }
        }

    ) { padding ->

        val isRefreshing = false

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                viewModel.fetchNews(selectedCat) },
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {

            Column(modifier = Modifier.padding()) {
                if (breakingState is Resource.Success && !breakingState.data.isNullOrEmpty()) {
                    val carouselState = rememberCarouselState { breakingState.data.size }

                    LaunchedEffect(key1 = breakingState.data.size) {
                        val totalItems = breakingState.data.size
                        if (totalItems > 1) {
                            while (true) {
                                delay(3000)

                                val nextItem = (carouselState.currentItem + 1) % totalItems

                                carouselState.animateScrollToItem(nextItem)
                            }
                        }
                    }

                    Column(modifier = Modifier.padding(top = 10.dp, bottom = 10.dp)) {
                        Text(
                            text = "Breaking News",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                        )

                        HorizontalMultiBrowseCarousel(
                            state = carouselState,
                            preferredItemWidth = 320.dp,
                            itemSpacing = 8.dp,
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            modifier = Modifier.height(180.dp)
                                .background(MaterialTheme.colorScheme.background)
                        ) { index ->
                            val article = breakingState.data[index]

                            Card(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable { onArticleClick(article) },
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Box(modifier = Modifier.fillMaxSize()) {

                                    AsyncImage(
                                        model = article.urlToImage,
                                        contentDescription = article.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )

                                    Surface(
                                        color = Color.Black.copy(alpha = 0.5f),
                                        modifier = Modifier.fillMaxSize()
                                    ) {}

                                    Column(
                                        modifier = Modifier
                                            .align(Alignment.BottomStart)
                                            .padding(12.dp)
                                    ) {
                                        Text(
                                            text = article.sourceName,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.primary,
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

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outline,
                    thickness = 1.dp
                )

                Box(modifier = Modifier.fillMaxSize()) {

                    when (generalState) {
                        is Resource.Loading -> {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        }

                        is Resource.Success -> {
                            LazyColumn() {
                                items(generalState.data ?: emptyList()) { article ->
                                    ArticleCard(
                                        article = article,
                                        onClick = { onArticleClick(article) }
                                    )
                                }
                            }
                        }

                        is Resource.Error -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp)
                                    .align(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surfaceVariant), //slight variant or grayish from surface
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SearchOff,
                                        contentDescription = null,
                                        modifier = Modifier.size(40.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Spacer(modifier = Modifier.height(20.dp))
                                Text(
                                    text = generalState.message ?: "Unknown Error",
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

