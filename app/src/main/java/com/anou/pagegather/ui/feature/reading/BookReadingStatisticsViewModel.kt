package com.anou.pagegather.ui.feature.reading

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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 书籍阅读统计ViewModel
 * 负责管理书籍阅读统计的数据和状态
 */
@HiltViewModel
class BookReadingStatisticsViewModel @Inject constructor(
    private val readingRecordRepository: ReadingRecordRepository,
    private val bookRepository: BookRepository
) : ViewModel() {

    private val _bookStatistics = MutableStateFlow<List<BookReadingStatistics>>(emptyList())
    val bookStatistics: StateFlow<List<BookReadingStatistics>> = _bookStatistics.asStateFlow()

    init {
        loadBookStatistics()
    }

    /**
     * 加载所有书籍的阅读统计数据
     */
    private fun loadBookStatistics() {
        viewModelScope.launch {
            try {
                bookRepository.getAllBooks().collectLatest { books ->
                    // 为每本书籍计算统计数据
                    val statistics = books.map { book ->
                        calculateBookStatistics(book)
                    }
                    
                    _bookStatistics.value = statistics
                }
            } catch (e: Exception) {
                // 处理异常
                e.printStackTrace()
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