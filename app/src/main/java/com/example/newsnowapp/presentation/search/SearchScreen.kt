package com.example.newsnowapp.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
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
fun SearchScreen(
    viewModel: SearchViewModel,
    onBackClick: () -> Unit,
    onArticleClick: (Article) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val searchState = viewModel.searchResults.value

    // ◀ NEW: Reference history lists directly out from view model state properties
    val recentSearches = viewModel.recentSearches.value

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Search News") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                })
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            // ... Inside your SearchScreen Composable function ...
            val keyboardController = LocalSoftwareKeyboardController.current // ◀ ADD THIS to hide keyboard manually
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                placeholder = { Text("Search topics...") },
                trailingIcon = {
                    IconButton(onClick = { viewModel.searchNews(searchQuery) }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                },
                singleLine = true,

                // ◀ NEW: Configure keyboard to show a Magnifying Glass / Search icon instead of generic tick
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search
                ),

                // ◀ NEW: Handle the keyboard action click event
                keyboardActions = KeyboardActions(
                    onSearch = {
                        if (searchQuery.isNotBlank()) {
                            viewModel.searchNews(searchQuery) // Trigger search execution
                            keyboardController?.hide()        // Dismiss the keyboard cleanly
                        }
                    }
                )
            )

            // ◀ NEW: Recent Searches Section (Horizontal Chips Row Container)
            if (recentSearches.isNotEmpty()) {
                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Text(
                        text = "Recent Searches",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.0.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        recentSearches.forEach { queryText ->
                            // Custom Input Chip design matching Material 3 architecture standards
                            InputChip(
                                selected = false,
                                onClick = {
                                    searchQuery = queryText // Populate search bar input field
                                    viewModel.searchNews(queryText) // Force executing query action
                                },
                                label = { Text(queryText) },
                                trailingIcon = {
                                    IconButton(
                                        onClick = { viewModel.removeQueryFromHistory(queryText) },
                                        modifier = Modifier.size(16.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Remove search term",
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Results List
            Box(modifier = Modifier.fillMaxSize()) {
                when (searchState) {
                    is Resource.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                    is Resource.Success -> {
                        val articles = searchState.data ?: emptyList()

                        if (articles.isEmpty() && searchQuery.isNotEmpty()) {
                            // ◀ NEW: Requirement 4 — Custom No Results illustration layout
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp)
                                    .align(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                // Large Magnifier Illustration style icon element
                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surfaceVariant),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SearchOff,
                                        contentDescription = null,
                                        modifier = Modifier.size(40.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "No results for '$searchQuery'",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Check your spelling or try searching for another keyword.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            // Show results standard data rows list when available
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(bottom = 16.dp)
                            ) {
                                items(articles) { article ->
                                    ArticleCard(
                                        article = article,
                                        onClick = { onArticleClick(article) }
                                    )
                                }
                            }
                        }
                    }
                    is Resource.Error -> Text(searchState.message ?: "Error", Modifier.align(Alignment.Center))
                }
            }
        }
    }
}

