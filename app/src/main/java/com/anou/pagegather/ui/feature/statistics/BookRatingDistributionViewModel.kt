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
 * 书籍评分分布ViewModel
 * 负责管理书籍评分分布数据和状态
 */
@HiltViewModel
class BookRatingDistributionViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val readingRecordRepository: ReadingRecordRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookRatingDistributionUiState())
    val uiState: StateFlow<BookRatingDistributionUiState> = _uiState.asStateFlow()

    init {
        loadBookRatingData()
    }

    /**
     * 加载书籍评分分布数据
     */
    private fun loadBookRatingData() {
        viewModelScope.launch {
            try {
                // 实现具体的书籍评分分布数据加载逻辑
                // 从BookRepository获取数据并分析
                val ratingData = bookRepository.getBookRatingDistribution()
                
                _uiState.value = BookRatingDistributionUiState(
                    ratingData = ratingData,
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
     * 根据时间范围加载书籍评分分布数据
     * @param startDate 开始日期 (格式: yyyy-MM-dd)
     * @param endDate 结束日期 (格式: yyyy-MM-dd)
     */
    fun loadBookRatingDataByDateRange(startDate: String, endDate: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                
                // 首先从ReadingRecordRepository获取指定时间范围内的书籍ID
                val bookIds = readingRecordRepository.getBookIdsByDateRange(startDate, endDate)
                
                // 然后从BookRepository获取这些书籍的评分分布
                val ratingData = bookRepository.getBookRatingDistributionByBookIds(bookIds)
                
                _uiState.value = BookRatingDistributionUiState(
                    ratingData = ratingData,
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
 * 书籍评分分布UI状态数据类
 */
data class BookRatingDistributionUiState(
    val ratingData: Map<String, Int> = emptyMap(), // 评分 -> 书籍数量
    val isLoading: Boolean = true,
    val error: String? = null
)