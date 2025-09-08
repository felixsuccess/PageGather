package com.anou.pagegather.ui.feature.bookshelf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anou.pagegather.data.local.entity.BookEntity
import com.anou.pagegather.data.model.BookReadingStatistics
import com.anou.pagegather.data.repository.BookRepository
import com.anou.pagegather.data.repository.ReadingRecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 书籍相关数据ViewModel
 * 负责管理书籍详情页面相关数据Tab的数据和状态
 */
@HiltViewModel
class BookRelatedDataViewModel @Inject constructor(
    private val readingRecordRepository: ReadingRecordRepository,
    private val bookRepository: BookRepository
) : ViewModel() {

    private val _bookStatistics = MutableStateFlow<BookReadingStatistics?>(null)
    val bookStatistics: StateFlow<BookReadingStatistics?> = _bookStatistics.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    /**
     * 加载指定书籍的阅读统计数据
     */
    fun loadBookStatistics(bookId: Long) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                // 获取书籍信息
                val book = bookRepository.getBookById(bookId)
                if (book != null) {
                    val statistics = calculateBookStatistics(book)
                    _bookStatistics.value = statistics
                } else {
                    _error.value = "未找到书籍信息"
                }
            } catch (e: Exception) {
                _error.value = "加载数据失败: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * 计算单本书籍的阅读统计数据
     */
    private suspend fun calculateBookStatistics(book: BookEntity): BookReadingStatistics {
        val totalReadingTime = readingRecordRepository.getTotalReadingTimeByBookId(book.id)
        val readingRecordCount = readingRecordRepository.getReadingRecordCountByBookId(book.id)
        val averageReadingTime = readingRecordRepository.getAverageReadingTimeByBookId(book.id)
        val lastReadingTime = readingRecordRepository.getLastReadingTimeByBookId(book.id)
        
        // 计算阅读进度（假设进度存储在book.readPosition中）
        val readingProgress = book.readPosition
        
        return BookReadingStatistics(
            bookId = book.id,
            bookName = book.name ?: "未知书籍",
            totalReadingTime = totalReadingTime,
            readingRecordCount = readingRecordCount,
            averageReadingTime = averageReadingTime,
            lastReadingTime = lastReadingTime,
            readingProgress = readingProgress
        )
    }
}