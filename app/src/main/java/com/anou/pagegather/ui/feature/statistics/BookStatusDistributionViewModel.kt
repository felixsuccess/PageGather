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
 * 书籍状态分布ViewModel
 * 负责管理书籍状态分布数据和状态
 */
@HiltViewModel
class BookStatusDistributionViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val readingRecordRepository: ReadingRecordRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookStatusDistributionUiState())
    val uiState: StateFlow<BookStatusDistributionUiState> = _uiState.asStateFlow()

    init {
        loadBookStatusData()
    }

    /**
     * 加载书籍状态分布数据
     */
    private fun loadBookStatusData() {
        viewModelScope.launch {
            try {
                // 实现具体的书籍状态分布数据加载逻辑
                // 从BookRepository获取数据并分析
                val statusData = bookRepository.getBookStatusDistribution()
                
                _uiState.value = BookStatusDistributionUiState(
                    statusData = statusData,
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
     * 根据时间范围加载书籍状态分布数据
     * @param startDate 开始日期 (格式: yyyy-MM-dd)
     * @param endDate 结束日期 (格式: yyyy-MM-dd)
     */
    fun loadBookStatusDataByDateRange(startDate: String, endDate: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                
                // 首先从ReadingRecordRepository获取指定时间范围内的书籍ID
                val bookIds = readingRecordRepository.getBookIdsByDateRange(startDate, endDate)
                
                // 然后从BookRepository获取这些书籍的状态分布
                val statusData = bookRepository.getBookStatusDistributionByBookIds(bookIds)
                
                _uiState.value = BookStatusDistributionUiState(
                    statusData = statusData,
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
 * 书籍状态分布UI状态数据类
 */
data class BookStatusDistributionUiState(
    val statusData: Map<String, Int> = emptyMap(), // 状态 -> 书籍数量
    val isLoading: Boolean = true,
    val error: String? = null
)