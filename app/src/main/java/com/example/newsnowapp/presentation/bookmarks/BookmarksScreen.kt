package com.example.newsnowapp.presentation.bookmarks

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.newsnowapp.domain.model.Article
import com.example.newsnowapp.presentation.components.ArticleCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarksScreen(
    viewModel: BookmarksViewModel,
    onArticleClick: (Article) -> Unit,
    onBackClick: () -> Unit
) {
    val bookmarkedList = viewModel.bookmarkedArticles.value

    // ◀ NEW: State to show/hide confirmation dialog
    var showDeleteDialog by remember { mutableStateOf(false) }

    // ◀ 3. FIX: Define the missing coroutine scope here
    val scope = rememberCoroutineScope()

    // ◀ NEW: Confirmation Pop-Up Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Clear All Bookmarks?") },
            text = { Text("Are you sure you want to delete all saved articles? This cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearAllBookmarks()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Clear All", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Saved Bookmarks", style = MaterialTheme.typography.headlineMedium) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Go back")
                    }
                },
                // ◀ NEW: Adds the Trash Sweep icon at top-right, visible only if items exist
                actions = {
                    if (bookmarkedList.isNotEmpty()) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "Clear all bookmarks"
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        if (bookmarkedList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No saved articles yet.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {// Use url as key to keep animations stable and prevent items jumping around
                items(items = bookmarkedList, key = { it.url }) { article ->

                    // 1. Maintain the state of the swipe gesture for this item
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { dismissValue ->
                            when (dismissValue) {
                                SwipeToDismissBoxValue.EndToStart -> {
                                    // Trigger deletion when swiped completely from right to left
                                    scope.launch {
                                        viewModel.deleteBookmark(article)
                                    }
                                    true
                                }
                                else -> false
                            }
                        }
                    )

                    // 2. Wrap your card layout inside the Swipe container
                    SwipeToDismissBox(
                        state = dismissState,
                        enableDismissFromStartToEnd = false, // Disables swiping from left to right
                        enableDismissFromEndToStart = true,  // Enables swiping from right to left
                        backgroundContent = {
                            // Determine background color based on swipe progress
                            val color by animateColorAsState(
                                targetValue = when (dismissState.targetValue) {
                                    SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.errorContainer
                                    else -> Color.Transparent

                                },
                                label = "BackgroundColorAnimation"
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(color)
                                    .padding(horizontal = 20.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete Bookmark",
                                        tint = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                            }
                        }
                    ) {
                        // 3. The actual foreground item content
                        ArticleCard(
                            article = article,
                            onClick = { onArticleClick(article) }
                        )
                    }
                }
            }
        }
    }
}