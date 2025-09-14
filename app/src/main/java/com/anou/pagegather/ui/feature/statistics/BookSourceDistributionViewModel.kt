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
 * 书籍来源分布ViewModel
 * 负责管理书籍来源分布数据和状态
 */
@HiltViewModel
class BookSourceDistributionViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val readingRecordRepository: ReadingRecordRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookSourceDistributionUiState())
    val uiState: StateFlow<BookSourceDistributionUiState> = _uiState.asStateFlow()

    init {
        loadBookSourceData()
    }

    /**
     * 加载书籍来源分布数据
     */
    private fun loadBookSourceData() {
        viewModelScope.launch {
            try {
                // 实现具体的书籍来源分布数据加载逻辑
                // 从BookRepository获取数据并分析
                val sourceData = bookRepository.getBookSourceDistribution()
                
                _uiState.value = BookSourceDistributionUiState(
                    sourceData = sourceData,
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
     * 根据时间范围加载书籍来源分布数据
     * @param startDate 开始日期 (格式: yyyy-MM-dd)
     * @param endDate 结束日期 (格式: yyyy-MM-dd)
     */
    fun loadBookSourceDataByDateRange(startDate: String, endDate: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                
                // 首先从ReadingRecordRepository获取指定时间范围内的书籍ID
                val bookIds = readingRecordRepository.getBookIdsByDateRange(startDate, endDate)
                
                // 然后从BookRepository获取这些书籍的来源分布
                val sourceData = bookRepository.getBookSourceDistributionByBookIds(bookIds)
                
                _uiState.value = BookSourceDistributionUiState(
                    sourceData = sourceData,
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
 * 书籍来源分布UI状态数据类
 */
data class BookSourceDistributionUiState(
    val sourceData: Map<String, Int> = emptyMap(), // 来源 -> 书籍数量
    val isLoading: Boolean = true,
    val error: String? = null
)