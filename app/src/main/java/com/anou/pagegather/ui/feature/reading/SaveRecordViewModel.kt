package com.anou.pagegather.ui.feature.reading

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anou.pagegather.data.local.entity.BookEntity
import com.anou.pagegather.data.repository.BookRepository
import com.anou.pagegather.data.repository.ReadingRecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

data class SaveRecordUIState(
    val source: RecordSource = RecordSource.TIMER,
    val elapsedTime: Long = 0L,
    val startTime: Long = 0L,
    val selectedBook: BookEntity? = null,
    val startProgress: Double = 0.0,
    val endProgress: Double = 0.0,
    val markAsFinished: Boolean = false,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class SaveRecordViewModel @Inject constructor(
    private val readingRecordRepository: ReadingRecordRepository,
    private val bookRepository: BookRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SaveRecordUIState())
    val uiState: StateFlow<SaveRecordUIState> = _uiState.asStateFlow()

    fun initialize(
        source: RecordSource,
        elapsedTime: Long? = null,
        startTime: Long? = null,
        preSelectedBookId: Long? = null
    ) {
        viewModelScope.launch {
            var selectedBook: BookEntity? = null
            if (preSelectedBookId != null) {
                selectedBook = bookRepository.getBookById(preSelectedBookId)
            }
            
            _uiState.value = _uiState.value.copy(
                source = source,
                elapsedTime = elapsedTime ?: 0L,
                startTime = startTime ?: System.currentTimeMillis(),
                selectedBook = selectedBook,
                startProgress = selectedBook?.readPosition ?: 0.0,
                endProgress = selectedBook?.readPosition ?: 0.0
            )
        }
    }

    fun selectBook(book: BookEntity) {
        println("SaveRecordViewModel: 选中书籍 = ${book.name}, ID = ${book.id}")
        _uiState.value = _uiState.value.copy(
            selectedBook = book,
            startProgress = book.readPosition,
            endProgress = book.readPosition
        )
    }

    fun selectNewlyAddedBook(bookId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                println("SaveRecordViewModel: 尝试选择书籍ID = $bookId")
                val book = bookRepository.getBookById(bookId)
                withContext(Dispatchers.Main) {
                    if (book != null) {
                        println("SaveRecordViewModel: 找到书籍 = ${book.name}")
                        selectBook(book)
                    } else {
                        println("SaveRecordViewModel: 未找到书籍ID = $bookId")
                        _uiState.value = _uiState.value.copy(
                            errorMessage = "未找到ID为 $bookId 的书籍"
                        )
                    }
                }
            } catch (e: Exception) {
                println("SaveRecordViewModel: 选择书籍失败 = ${e.message}")
                withContext(Dispatchers.Main) {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "选择新添加书籍失败: ${e.message}"
                    )
                }
            }
        }
    }

    fun setStartProgress(progress: Double) {
        _uiState.value = _uiState.value.copy(startProgress = progress)
    }

    fun setEndProgress(progress: Double) {
        _uiState.value = _uiState.value.copy(endProgress = progress)
    }

    fun setMarkAsFinished(markAsFinished: Boolean) {
        _uiState.value = _uiState.value.copy(markAsFinished = markAsFinished)
    }



    fun saveRecord() {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value
                val selectedBook = currentState.selectedBook
                
                if (selectedBook == null) {
                    _uiState.value = currentState.copy(
                        errorMessage = "请选择要记录的书籍"
                    )
                    return@launch
                }

                _uiState.value = currentState.copy(isLoading = true)

                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val endTime = currentState.startTime + currentState.elapsedTime
                val date = dateFormat.format(Date(endTime))
                
                readingRecordRepository.addManualReadingRecord(
                    bookId = selectedBook.id,
                    startProgress = currentState.startProgress,
                    endProgress = currentState.endProgress,
                    duration = currentState.elapsedTime,
                    date = date,
                    notes = null
                )
                
                val updatedBook = selectedBook.copy(
                    readPosition = currentState.endProgress,
                    lastReadDate = endTime,
                    readStatus = if (currentState.markAsFinished) {
                        com.anou.pagegather.data.local.entity.ReadStatus.FINISHED.code
                    } else {
                        selectedBook.readStatus
                    },
                    readStatusChangedDate = if (currentState.markAsFinished) {
                        endTime
                    } else {
                        selectedBook.readStatusChangedDate
                    },
                    finishedDate = if (currentState.markAsFinished) {
                        endTime
                    } else {
                        selectedBook.finishedDate
                    }
                )
                
                bookRepository.updateBook(updatedBook)
                
                _uiState.value = currentState.copy(
                    isLoading = false,
                    isSaved = true
                )
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "保存阅读记录失败: ${e.message}"
                )
            }
        }
    }

    fun continueReading() {
        saveRecord()
    }

    fun getAllBooks() = bookRepository.getAllBooks()

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}