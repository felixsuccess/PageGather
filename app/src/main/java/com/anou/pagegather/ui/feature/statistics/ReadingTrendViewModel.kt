package com.anou.pagegather.ui.feature.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
 * 阅读趋势ViewModel
 * 负责管理阅读趋势数据和状态
 */
@HiltViewModel
class ReadingTrendViewModel @Inject constructor(
    private val readingRecordRepository: ReadingRecordRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReadingTrendUiState())
    val uiState: StateFlow<ReadingTrendUiState> = _uiState.asStateFlow()

    init {
        loadReadingTrendData()
    }

    /**
     * 加载阅读趋势数据
     */
    private fun loadReadingTrendData() {
        viewModelScope.launch {
            try {
                // 获取最近30天的阅读趋势数据
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val endDate = dateFormat.format(Date())
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.DAY_OF_MONTH, -30)
                val startDate = dateFormat.format(calendar.time)
                
                val trendData = readingRecordRepository.getReadingTrendDataByDateRange(startDate, endDate)
                
                _uiState.value = ReadingTrendUiState(
                    trendData = trendData,
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
     * 根据时间范围加载阅读趋势数据
     * @param startDate 开始日期 (格式: yyyy-MM-dd)
     * @param endDate 结束日期 (格式: yyyy-MM-dd)
     */
    fun loadReadingTrendDataByDateRange(startDate: String, endDate: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                
                val trendData = readingRecordRepository.getReadingTrendDataByDateRange(startDate, endDate)
                
                _uiState.value = ReadingTrendUiState(
                    trendData = trendData,
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
     * 根据时间范围和时间粒度加载阅读趋势数据
     * @param startDate 开始日期 (格式: yyyy-MM-dd)
     * @param endDate 结束日期 (格式: yyyy-MM-dd)
     * @param granularity 时间粒度
     */
    fun loadReadingTrendDataByTimeGranularity(startDate: String, endDate: String, granularity: TimeGranularity) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                
                val trendData = readingRecordRepository.getReadingTrendDataByTimeGranularity(startDate, endDate, granularity)
                
                _uiState.value = ReadingTrendUiState(
                    trendData = trendData,
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
 * 阅读趋势UI状态数据类
 */
data class ReadingTrendUiState(
    val trendData: Map<String, Long> = emptyMap(), // 时间点 -> 阅读时长(毫秒)
    val isLoading: Boolean = true,
    val error: String? = null
)