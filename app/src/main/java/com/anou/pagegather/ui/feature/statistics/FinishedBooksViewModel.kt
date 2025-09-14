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
 * 读完书籍ViewModel
 * 负责管理读完书籍数据和状态
 */
@HiltViewModel
class FinishedBooksViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val readingRecordRepository: ReadingRecordRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FinishedBooksUiState())
    val uiState: StateFlow<FinishedBooksUiState> = _uiState.asStateFlow()

    init {
        loadFinishedBooksData()
    }

    /**
     * 加载读完书籍数据
     */
    private fun loadFinishedBooksData() {
        viewModelScope.launch {
            try {
                // 实现具体的读完书籍数据加载逻辑
                // 这里需要从BookRepository获取数据并分析
                
                _uiState.value = FinishedBooksUiState(
                    isLoading = false
                    // TODO: 设置实际的数据
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
     * 根据时间范围加载读完书籍数据
     * @param startDate 开始日期 (格式: yyyy-MM-dd)
     * @param endDate 结束日期 (格式: yyyy-MM-dd)
     */
    fun loadFinishedBooksDataByDateRange(
        startDate: String, 
        endDate: String
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                
                // 首先从ReadingRecordRepository获取指定时间范围内的书籍ID
                val bookIds = readingRecordRepository.getBookIdsByDateRange(startDate, endDate)
                
                // 然后从BookRepository获取这些书籍中已完成的书籍数量
                val finishedBooksCount = bookRepository.getFinishedBooksCountByBookIds(bookIds)
                
                _uiState.value = FinishedBooksUiState(
                    finishedBooksCount = finishedBooksCount,
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
 * 读完书籍UI状态数据类
 */
data class FinishedBooksUiState(
    val finishedBooksCount: Int = 0, // 读完的书籍数量
    val isLoading: Boolean = true,
    val error: String? = null
)