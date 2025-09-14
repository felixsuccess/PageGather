package com.anou.pagegather.ui.feature.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anou.pagegather.data.repository.ReadingRecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 阅读时长分布ViewModel
 * 负责管理阅读时长分布数据和状态
 */
@HiltViewModel
class ReadingDurationDistributionViewModel @Inject constructor(
    private val readingRecordRepository: ReadingRecordRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReadingDurationDistributionUiState())
    val uiState: StateFlow<ReadingDurationDistributionUiState> = _uiState.asStateFlow()

    init {
        loadReadingDurationData()
    }

    /**
     * 加载阅读时长分布数据
     */
    private fun loadReadingDurationData() {
        viewModelScope.launch {
            try {
                // 实现具体的阅读时长分布数据加载逻辑
                // 这里需要从ReadingRecordRepository获取数据并分析
                
                _uiState.value = ReadingDurationDistributionUiState(
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
     * 根据时间范围和时间粒度加载阅读时长分布数据
     * @param startDate 开始日期 (格式: yyyy-MM-dd)
     * @param endDate 结束日期 (格式: yyyy-MM-dd)
     * @param granularity 时间粒度
     */
    fun loadReadingDurationDataByDateRangeAndGranularity(
        startDate: String, 
        endDate: String, 
        granularity: TimeGranularity
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                
                // 使用基于时间粒度的统计方法获取阅读时长分布数据
                // 首先将Long映射转换为Int映射以适配UI状态
                val durationDataLong: Map<String, Long> = readingRecordRepository.getReadingDurationDistributionByTimeGranularity(
                    startDate, endDate, granularity
                )
                val durationData: Map<String, Int> = durationDataLong.mapValues { (_, value) -> value.toInt() }
                
                _uiState.value = ReadingDurationDistributionUiState(
                    durationData = durationData,
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
 * 阅读时长分布UI状态数据类
 */
data class ReadingDurationDistributionUiState(
    val durationData: Map<String, Int> = emptyMap(), // 时间点 -> 阅读时长(毫秒)
    val isLoading: Boolean = true,
    val error: String? = null
)