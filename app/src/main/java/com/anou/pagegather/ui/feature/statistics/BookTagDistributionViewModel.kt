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
 * 书籍标签分布ViewModel
 * 负责管理书籍标签分布数据和状态
 */
@HiltViewModel
class BookTagDistributionViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val readingRecordRepository: ReadingRecordRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookTagDistributionUiState())
    val uiState: StateFlow<BookTagDistributionUiState> = _uiState.asStateFlow()

    init {
        loadBookTagData()
    }

    /**
     * 加载书籍标签分布数据
     */
    private fun loadBookTagData() {
        viewModelScope.launch {
            try {
                // 实现具体的书籍标签分布数据加载逻辑
                // 从BookRepository获取数据并分析
                val tagData = bookRepository.getBookTagDistribution()
                
                _uiState.value = BookTagDistributionUiState(
                    tagData = tagData,
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
     * 根据时间范围加载书籍标签分布数据
     * @param startDate 开始日期 (格式: yyyy-MM-dd)
     * @param endDate 结束日期 (格式: yyyy-MM-dd)
     */
    fun loadBookTagDataByDateRange(startDate: String, endDate: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                
                // 首先从ReadingRecordRepository获取指定时间范围内的书籍ID
                val bookIds = readingRecordRepository.getBookIdsByDateRange(startDate, endDate)
                
                // 然后从BookRepository获取这些书籍的标签分布
                val tagData = bookRepository.getBookTagDistributionByBookIds(bookIds)
                
                _uiState.value = BookTagDistributionUiState(
                    tagData = tagData,
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
 * 书籍标签分布UI状态数据类
 */
data class BookTagDistributionUiState(
    val tagData: Map<String, Int> = emptyMap(), // 标签 -> 书籍数量
    val isLoading: Boolean = true,
    val error: String? = null
)