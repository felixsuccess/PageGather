package com.anou.pagegather.ui.feature.bookshelf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anou.pagegather.data.local.entity.BookEntity
import com.anou.pagegather.data.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookListViewModel @Inject constructor(
    private val bookRepository: BookRepository,
) : ViewModel() {
    private var _bookList = MutableStateFlow<List<BookEntity>>(emptyList())
    private val _isLoading = MutableStateFlow(true)
    private val _state = MutableStateFlow<BookListUIState>(BookListUIState.Loading)
    val state: StateFlow<BookListUIState> = _state
    private var currentPage = 0
    private val pageSize = 20
    private var isLastPage = false

    init {
        viewModelScope.launch {
            loadBooks()
        }
    }

    private suspend fun loadBooks() {
        _state.value = BookListUIState.Loading
        _isLoading.value = true
        currentPage = 0
        isLastPage = false
        try {
            bookRepository.getAllBooks().collect { books ->
                _bookList.value = books
                _state.value = when {
                    books.isEmpty() -> BookListUIState.Empty
                    else -> BookListUIState.Success(books)
                }
            }
        } catch (e: Exception) {
            _state.value = BookListUIState.Error("加载失败: ${e.localizedMessage}")
            _bookList.value = emptyList()
        } finally {
            _isLoading.value = false
        }
    }

    fun searchBooks(query: String) {
        viewModelScope.launch {
            _state.value = BookListUIState.Loading
            try {
                bookRepository.searchBooks(query).collect { books ->
                    _bookList.value = books
                    _state.value = when {
                        books.isEmpty() -> BookListUIState.Empty
                        else -> BookListUIState.Success(books)
                    }
                }
            } catch (e: Exception) {
                _state.value = BookListUIState.Error("搜索失败: ${e.localizedMessage}")
            }
        }
    }

    fun clearSearch() {
        viewModelScope.launch {
            loadBooks()
        }
    }

    fun loadMoreBooks() {
        if (_isLoading.value || isLastPage) return
        
        viewModelScope.launch {
            // Update state to show loading indicator
            val currentState = _state.value
            if (currentState is BookListUIState.Success) {
                _state.value = currentState.copy(isLoadingMore = true)
            }
            
            try {
                // Load more books using pagination
                bookRepository.getBooksPaged(currentPage + 1, pageSize).collect { moreBooks ->
                    // Update book list with new data
                    val currentBooks = _bookList.value
                    val updatedBooks = currentBooks + moreBooks
                    
                    // Update state with new books
                    _bookList.value = updatedBooks
                    
                    // Check if this is the last page
                    isLastPage = moreBooks.size < pageSize
                    
                    // Update current page
                    currentPage++
                    
                    // Update state to hide loading indicator
                    val updatedState = _state.value
                    if (updatedState is BookListUIState.Success) {
                        _state.value = updatedState.copy(
                            books = updatedBooks,
                            isLoadingMore = false
                        )
                    }
                }
            } catch (e: Exception) {
                // Handle error
                val updatedState = _state.value
                if (updatedState is BookListUIState.Success) {
                    _state.value = updatedState.copy(isLoadingMore = false)
                }
            }
        }
    }
}