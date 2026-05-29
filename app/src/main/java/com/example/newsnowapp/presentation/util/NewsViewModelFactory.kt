//package com.example.newsnowapp.presentation.util
//
//import android.content.Context
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import com.example.newsnowapp.data.local.bookmarkData.NewsDao
//import com.example.newsnowapp.domain.repository.NewsRepository
//import com.example.newsnowapp.presentation.bookmarks.BookmarksViewModel
//import com.example.newsnowapp.presentation.home.HomeViewModel
//import com.example.newsnowapp.presentation.search.SearchViewModel
//
//class NewsViewModelFactory(
//    val repository: NewsRepository,
//    private val context: Context // ◀ Add context here
//) : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        return when {
//            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
//                HomeViewModel(repository) as T
//            }
//            modelClass.isAssignableFrom(SearchViewModel::class.java) -> {
//                SearchViewModel(repository, context) as T
//            }
//            modelClass.isAssignableFrom(BookmarksViewModel::class.java) -> {
//                BookmarksViewModel(repository) as T
//            }
//            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
//        }
//    }
//}