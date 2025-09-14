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
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * 图表ViewModel
 * 负责管理图表页面的数据和状态
 */
@HiltViewModel
class ChartsViewModel @Inject constructor(
    private val readingRecordRepository: ReadingRecordRepository,
    private val bookRepository: BookRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChartsUiState())
    val uiState: StateFlow<ChartsUiState> = _uiState.asStateFlow()

    /**
     * 加载图表数据
     */
    fun loadChartsData() {
        viewModelScope.launch {
            try {
                // 获取最近30天的阅读时长分布
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val endDate = dateFormat.format(Date())
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.DAY_OF_MONTH, -30)
                val startDate = dateFormat.format(calendar.time)
                
                // TODO: 实现获取阅读时长分布数据的逻辑
                
                // 获取阅读习惯时间分布
                // TODO: 实现获取阅读习惯时间分布数据的逻辑
                
                // 获取书籍类型分布
                val bookTypeData = bookRepository.getBookTypeDistribution()
                
                _uiState.value = ChartsUiState(
                    isLoading = false,
                    bookTypeData = bookTypeData
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
 * 图表UI状态数据类
 */
data class ChartsUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val bookTypeData: Map<String, Int> = emptyMap() // 类型 -> 书籍数量
)