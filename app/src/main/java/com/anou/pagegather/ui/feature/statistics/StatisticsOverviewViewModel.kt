package com.anou.pagegather.ui.feature.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anou.pagegather.data.repository.BookRepository
import com.anou.pagegather.data.repository.ReadingRecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 统计概览ViewModel
 * 负责管理统计概览页面的数据和状态
 */
@HiltViewModel
class StatisticsOverviewViewModel @Inject constructor(
    private val readingRecordRepository: ReadingRecordRepository,
    private val bookRepository: BookRepository
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
                val readingBooksCount = bookRepository.getReadingBooksCount()
                
                _uiState.value = StatisticsOverviewUiState(
                    todayReadingTime = todayReadingTime,
                    weekReadingTime = weekReadingTime,
                    monthReadingTime = monthReadingTime,
                    readingBooksCount = readingBooksCount,
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
    val readingBooksCount: Int = 0,
    val isLoading: Boolean = true,
    val error: String? = null
)