package com.anou.pagegather.ui.feature.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anou.pagegather.data.repository.BookRepository
import com.anou.pagegather.data.repository.NoteRepository
import com.anou.pagegather.data.repository.ReadingRecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * 统计概览ViewModel
 * 负责管理统计概览页面的数据和状态
 */
@HiltViewModel
class StatisticsOverviewViewModel @Inject constructor(
    private val readingRecordRepository: ReadingRecordRepository,
    private val bookRepository: BookRepository,
    private val noteRepository: NoteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatisticsOverviewUiState())
    val uiState: StateFlow<StatisticsOverviewUiState> = _uiState.asStateFlow()

    init {
        loadStatistics()
    }

    /**
     * 加载统计概览数据
     */
    private fun loadStatistics() {
        viewModelScope.launch {
            try {
                val todayReadingTime = readingRecordRepository.getTodayTotalReadingTime()
                val weekReadingTime = readingRecordRepository.getThisWeekTotalReadingTime()
                val monthReadingTime = readingRecordRepository.getThisMonthTotalReadingTime()
                val totalReadingTime = readingRecordRepository.getTotalReadingTime()
                val readingBooksCount = bookRepository.getReadingBooksCount()
                val finishedBooksCount = bookRepository.getFinishedBooksCount()
                val totalBooksCount = bookRepository.getTotalBooksCount()
                
                // 获取阅读天数（过去30天）
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val endDate = dateFormat.format(Date())
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.DAY_OF_MONTH, -30)
                val startDate = dateFormat.format(calendar.time)
                val readingDaysCount = readingRecordRepository.getReadingDaysCount(startDate, endDate)
                
                // 获取笔记总数
                val noteCount = noteRepository.getNoteCount()
                
                _uiState.value = StatisticsOverviewUiState(
                    todayReadingTime = todayReadingTime,
                    weekReadingTime = weekReadingTime,
                    monthReadingTime = monthReadingTime,
                    totalReadingTime = totalReadingTime,
                    readingBooksCount = readingBooksCount,
                    finishedBooksCount = finishedBooksCount,
                    totalBooksCount = totalBooksCount,
                    readingDaysCount = readingDaysCount,
                    noteCount = noteCount,
                    isLoading = false
                )
            } catch (e: Exception) {
                // 处理异常
                e.printStackTrace()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    /**
     * 根据时间范围加载统计数据
     */
    fun loadStatisticsByDateRange(startDate: String, endDate: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                
                val totalReadingTime = readingRecordRepository.getTotalReadingTimeByDateRange(startDate, endDate)
                val readingDaysCount = readingRecordRepository.getReadingDaysCount(startDate, endDate)
                
                // 获取在指定时间范围内有阅读记录的书籍ID
                val bookIds = readingRecordRepository.getBookIdsByDateRange(startDate, endDate)
                
                // 根据书籍ID获取完成的书籍数量
                var finishedBooksCount = 0
                if (bookIds.isNotEmpty()) {
                    // 获取这些书籍中状态为已完成的书籍数量
                    val finishedBooks = bookRepository.getBooksByIds(bookIds)
                    finishedBooksCount = finishedBooks.count { it.readStatus == 2 } // 2表示已完成
                }
                
                // 获取在指定时间范围内创建的笔记数量
                val noteCount = noteRepository.getAllNotes().first()?.count { note ->
                    try {
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val noteDate = Date(note.createdDate)
                        val start = dateFormat.parse(startDate)
                        val end = dateFormat.parse(endDate)
                        !note.isDeleted && noteDate.after(start) && (noteDate.before(end) || noteDate == end)
                    } catch (e: Exception) {
                        false
                    }
                } ?: 0
                
                _uiState.value = StatisticsOverviewUiState(
                    todayReadingTime = 0, // 在时间范围视图中不需要今日数据
                    weekReadingTime = 0,  // 在时间范围视图中不需要本周数据
                    monthReadingTime = 0, // 在时间范围视图中不需要本月数据
                    totalReadingTime = totalReadingTime,
                    readingBooksCount = 0, // 在时间范围视图中不显示在读书籍
                    finishedBooksCount = finishedBooksCount,
                    totalBooksCount = 0, // 在时间范围视图中不显示书籍总数
                    readingDaysCount = readingDaysCount,
                    noteCount = noteCount,
                    isLoading = false
                )
            } catch (e: Exception) {
                // 处理异常
                e.printStackTrace()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
}

/**
 * 统计概览UI状态数据类
 */
data class StatisticsOverviewUiState(
    val todayReadingTime: Long = 0,
    val weekReadingTime: Long = 0,
    val monthReadingTime: Long = 0,
    val totalReadingTime: Long = 0,
    val readingBooksCount: Int = 0,
    val finishedBooksCount: Int = 0,
    val totalBooksCount: Int = 0,
    val readingDaysCount: Int = 0,
    val noteCount: Int = 0,
    val isLoading: Boolean = true,
    val error: String? = null
)