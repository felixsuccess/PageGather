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
    val errorMessage: String? = null,
    val isBackToTimer: Boolean = false,
    val bookChanged: Boolean = false  // 添加一个字段来跟踪书籍是否被更改
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
        selectedBookId: Long? = null
    ) {
        viewModelScope.launch {
            var selectedBook: BookEntity? = null
            if (selectedBookId != null) {
                selectedBook = bookRepository.getBookById(selectedBookId)
            }
            
            // 对于手动记录，我们需要特殊处理
            val actualStartTime = if (source == RecordSource.MANUAL) {
                startTime ?: System.currentTimeMillis()
            } else {
                startTime ?: System.currentTimeMillis()
            }
            
            _uiState.value = _uiState.value.copy(
                source = source,
                elapsedTime = elapsedTime ?: 0L,
                startTime = actualStartTime,
                selectedBook = selectedBook,
                startProgress = selectedBook?.readPosition ?: 0.0,
                endProgress = selectedBook?.readPosition ?: 0.0
            )
        }
    }

    fun selectBook(book: BookEntity) {
        println("SaveRecordViewModel: 选中书籍 = ${book.name}, ID = ${book.id}")
        // 移除对全局变量的使用
        _uiState.value = _uiState.value.copy(
            selectedBook = book,
            startProgress = book.readPosition,
            endProgress = book.readPosition,
            bookChanged = true
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

    fun saveManualRecord(duration: Long, startTime: Long) {
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

                // 在IO线程中执行数据库操作
                withContext(Dispatchers.IO) {
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val endTime = startTime + duration
                    val date = dateFormat.format(Date(startTime)) // 使用开始时间的日期
                    
                    // 手动记录使用addManualReadingRecord方法，传递正确的startTime
                    readingRecordRepository.addManualReadingRecord(
                        bookId = selectedBook.id,
                        startProgress = currentState.startProgress,
                        endProgress = currentState.endProgress,
                        duration = duration,
                        startTime = startTime,
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
                }
                
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

                // 在IO线程中执行数据库操作
                withContext(Dispatchers.IO) {
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val endTime = currentState.startTime + currentState.elapsedTime
                    val date = dateFormat.format(Date(currentState.startTime))
                    
                    // 根据记录来源选择不同的保存方法
                    if (currentState.source == RecordSource.MANUAL) {
                        // 手动记录使用不同的方法
                        readingRecordRepository.addManualReadingRecord(
                            bookId = selectedBook.id,
                            startProgress = currentState.startProgress,
                            endProgress = currentState.endProgress,
                            duration = currentState.elapsedTime,
                            startTime = currentState.startTime,
                            date = date,
                            notes = null
                        )
                    } else {
                        // 计时器记录使用原有方法
                        readingRecordRepository.addManualReadingRecord(
                            bookId = selectedBook.id,
                            startProgress = currentState.startProgress,
                            endProgress = currentState.endProgress,
                            duration = currentState.elapsedTime,
                            startTime = currentState.startTime,
                            date = date,
                            notes = null
                        )
                    }
                    
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
                }
                
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
        _uiState.value = _uiState.value.copy(isBackToTimer = true)
    }

    fun getAllBooks() = bookRepository.getAllBooks()

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}