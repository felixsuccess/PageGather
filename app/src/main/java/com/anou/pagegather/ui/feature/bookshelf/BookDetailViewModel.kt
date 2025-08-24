package com.anou.pagegather.ui.feature.bookshelf


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anou.pagegather.data.local.entity.BookEntity
import com.anou.pagegather.data.local.entity.BookSourceEntity
import com.anou.pagegather.data.repository.BookRepository
import com.anou.pagegather.data.repository.BookSourceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class BookDetailViewModel @Inject constructor(
    private val repository: BookRepository,
    private val bookSourceRepository: BookSourceRepository
) : ViewModel() {
    private val _book = MutableStateFlow<BookEntity?>(null)
    val book: StateFlow<BookEntity?> = _book
    
    private val _bookSource = MutableStateFlow<BookSourceEntity?>(null)
    val bookSource: StateFlow<BookSourceEntity?> = _bookSource

    fun loadBook(bookId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val fetchedBook = repository.getBookById(bookId)
            withContext(Dispatchers.Main) {
                _book.value = fetchedBook
            }
            
            // 加载书籍来源信息
            fetchedBook?.let { book ->
                val source = bookSourceRepository.getBookSourceById(book.bookSourceId.toLong())
                withContext(Dispatchers.Main) {
                    _bookSource.value = source
                }
            }
        }
    }
}

