package com.anou.pagegather.ui.feature.bookshelf

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anou.pagegather.data.local.entity.BookEntity
import com.anou.pagegather.data.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class BookEditViewModel @Inject  constructor(
    private val repository: BookRepository
) : ViewModel() {
    private val _book = MutableStateFlow<BookEntity?>(null)
    val book: StateFlow<BookEntity?> = _book

    fun loadBook(bookId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val fetchedBook = repository.getBookById(bookId)
            withContext(Dispatchers.Main) {
                _book.value = fetchedBook
            }
        }
    }

    fun saveBook(book: BookEntity, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.runInTransaction {
                try {
                    if (book.id == 0L) {
                        val insertedId = repository.insertBook(book)
                        Log.d("BookEdit", "Inserted book with id: $insertedId")
                    } else {
                        repository.updateBook(book)
                        Log.d("BookEdit", "Updated book with id: ${book.id}")
                    }
                    withContext(Dispatchers.Main) {
                        onSuccess()
                    }
                } catch (e: Exception) {
                    Log.e("BookEdit", "Save failed: ${e.stackTraceToString()}")
                }
            }
        }
    }

    fun deleteBook(note: BookEntity) = viewModelScope.launch {
        repository.deleteBook(note)
    }
}