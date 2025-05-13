package com.anou.pagegather.ui.feature.bookshelf


import com.anou.pagegather.data.local.entity.BookEntity

sealed class BookListUIState {
    object Loading : BookListUIState()
    data class Success(val books: List<BookEntity>) : BookListUIState()
    data class Error(val message: String) : BookListUIState()
    object Empty : BookListUIState()
}