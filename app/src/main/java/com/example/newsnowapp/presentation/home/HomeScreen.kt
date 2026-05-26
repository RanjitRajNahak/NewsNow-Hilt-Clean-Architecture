package com.example.newsnowapp.presentation.home

import android.R
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsnowapp.domain.model.Article
import com.example.newsnowapp.domain.repository.NewsRepository
import com.example.newsnowapp.presentation.util.Resource
import com.example.newsnowapp.presentation.components.ArticleCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel,
               onArticleClick: (Article) -> Unit,
               onSearchClick: () -> Unit,
               onBookmarksClick: () -> Unit) {
    val state = viewModel.articles.value
    val selectedCat = viewModel.selectedCategory.value

    // This runs only ONCE when the HomeScreen enters the Composition
    LaunchedEffect(Unit) {
        if (state !is Resource.Success) {
            viewModel.fetchNews("General")
        }
    }
    var menuExpanded by remember {mutableStateOf(false)}
    Scaffold(
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    actions = {
                        IconButton(onClick = onBookmarksClick) {
                            Icon(Icons.Default.Bookmark, contentDescription = "Open Bookmarks")
                        }
                        IconButton(onClick = onSearchClick) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                    },
                    title = { Text("NewsNow", style = MaterialTheme.typography.headlineLarge) }
                )

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
        }

    ) { padding ->
        val isRefreshing = state is Resource.Loading && state.data != null

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.fetchNews(selectedCat) },
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

