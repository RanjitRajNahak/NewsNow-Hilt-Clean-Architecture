package com.example.newsnowapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.newsnowapp.data.local.Article
import com.example.newsnowapp.viewmodel.Resource
import com.example.newsnowapp.ui.components.ArticleCard
import com.example.newsnowapp.viewmodel.SearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onArticleClick: (Article) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val searchState = viewModel.searchResults.value

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Search News") })
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
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
                singleLine = true
            )

            // Results List
            Box(modifier = Modifier.fillMaxSize()) {
                when (searchState) {
                    is Resource.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                    is Resource.Success -> {
                        LazyColumn {
                            items(searchState.data ?: emptyList()) { article ->
                                ArticleCard(article = article, onClick = { onArticleClick(article) })
                            }
                        }
                    }
                    is Resource.Error -> Text(searchState.message ?: "Error", Modifier.align(Alignment.Center))
                }
            }
        }
    }
}