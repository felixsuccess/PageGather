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
 * 书籍分组分布ViewModel
 * 负责管理书籍分组分布数据和状态
 */
@HiltViewModel
class BookGroupDistributionViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val readingRecordRepository: ReadingRecordRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookGroupDistributionUiState())
    val uiState: StateFlow<BookGroupDistributionUiState> = _uiState.asStateFlow()

    init {
        loadBookGroupData()
    }

    /**
     * 加载书籍分组分布数据
     */
    private fun loadBookGroupData() {
        viewModelScope.launch {
            try {
                // 实现具体的书籍分组分布数据加载逻辑
                // 从BookRepository获取数据并分析
                val groupData = bookRepository.getBookGroupDistribution()
                
                _uiState.value = BookGroupDistributionUiState(
                    groupData = groupData,
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
     * 根据时间范围加载书籍分组分布数据
     * @param startDate 开始日期 (格式: yyyy-MM-dd)
     * @param endDate 结束日期 (格式: yyyy-MM-dd)
     */
    fun loadBookGroupDataByDateRange(startDate: String, endDate: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                
                // 首先从ReadingRecordRepository获取指定时间范围内的书籍ID
                val bookIds = readingRecordRepository.getBookIdsByDateRange(startDate, endDate)
                
                // 然后从BookRepository获取这些书籍的分组分布
                val groupData = bookRepository.getBookGroupDistributionByBookIds(bookIds)
                
                _uiState.value = BookGroupDistributionUiState(
                    groupData = groupData,
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
 * 书籍分组分布UI状态数据类
 */
data class BookGroupDistributionUiState(
    val groupData: Map<String, Int> = emptyMap(), // 分组 -> 书籍数量
    val isLoading: Boolean = true,
    val error: String? = null
)