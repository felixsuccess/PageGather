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

    init {
        viewModelScope.launch {
            loadBooks()
        }
    }

    private suspend fun loadBooks() {
        _state.value = BookListUIState.Loading
        _isLoading.value = true
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
            _state.value = BookListUIState.Success(_bookList.value)
            // _state.value = when {
            //     books.isEmpty() -> BookListState.Empty
            //     else -> BookListState.Success(books)
            // }
        }
    }


}