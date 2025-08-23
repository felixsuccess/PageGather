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
    
    // 分页相关状态
    private var currentPage = 0
    private val pageSize = 20
    private var isLoadingMore = false
    private var hasMoreData = true

    init {
        viewModelScope.launch {
            loadBooks()
        }
    }

    private suspend fun loadBooks() {
        _state.value = BookListUIState.Loading
        _isLoading.value = true
        currentPage = 0
        hasMoreData = true
        
        try {
            bookRepository.getBooksPaged(currentPage, pageSize).collect { books ->
                _bookList.value = books
                hasMoreData = books.size >= pageSize
                _state.value = when {
                    books.isEmpty() -> BookListUIState.Empty
                    else -> BookListUIState.Success(books, isLoadingMore = false)
                }
            }
        } catch (e: Exception) {
            _state.value = BookListUIState.Error("加载失败: ${e.localizedMessage}")
            _bookList.value = emptyList()
        } finally {
            _isLoading.value = false
        }
    }
    
    fun loadMoreBooks() {
        if (isLoadingMore || !hasMoreData) return
        
        viewModelScope.launch {
            isLoadingMore = true
            val currentState = _state.value
            if (currentState is BookListUIState.Success) {
                _state.value = currentState.copy(isLoadingMore = true)
            }
            
            try {
                currentPage++
                bookRepository.getBooksPaged(currentPage, pageSize).collect { newBooks ->
                    if (newBooks.isNotEmpty()) {
                        val allBooks = _bookList.value + newBooks
                        _bookList.value = allBooks
                        hasMoreData = newBooks.size >= pageSize
                        _state.value = BookListUIState.Success(allBooks, isLoadingMore = false)
                    } else {
                        hasMoreData = false
                        if (currentState is BookListUIState.Success) {
                            _state.value = currentState.copy(isLoadingMore = false)
                        }
                    }
                }
            } catch (e: Exception) {
                // 加载更多失败时，恢复到之前的状态
                currentPage--
                if (currentState is BookListUIState.Success) {
                    _state.value = currentState.copy(isLoadingMore = false)
                }
            } finally {
                isLoadingMore = false
            }
        }
    }

    fun searchBooks(query: String) {
        viewModelScope.launch {
            _state.value = BookListUIState.Loading
            try {
                bookRepository.searchBooks(query).collect { books ->
                    _bookList.value = books
                    // 搜索结果不支持分页加载
                    hasMoreData = false
                    _state.value = when {
                        books.isEmpty() -> BookListUIState.Empty
                        else -> BookListUIState.Success(books, isLoadingMore = false)
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
}