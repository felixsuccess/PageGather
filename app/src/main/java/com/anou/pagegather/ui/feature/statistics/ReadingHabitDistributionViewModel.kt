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
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * 阅读习惯时间分布ViewModel
 * 负责管理阅读习惯时间分布数据和状态
 */
@HiltViewModel
class ReadingHabitDistributionViewModel @Inject constructor(
    private val readingRecordRepository: ReadingRecordRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReadingHabitDistributionUiState())
    val uiState: StateFlow<ReadingHabitDistributionUiState> = _uiState.asStateFlow()

    init {
        loadReadingHabitData()
    }

    /**
     * 加载阅读习惯时间分布数据
     */
    private fun loadReadingHabitData() {
        viewModelScope.launch {
            try {
                // 默认加载最近30天的数据
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val endDate = dateFormat.format(Date())
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.DAY_OF_MONTH, -30)
                val startDate = dateFormat.format(calendar.time)
                
                loadReadingHabitDataByDateRange(startDate, endDate)
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
     * 根据时间范围加载阅读习惯时间分布数据
     * @param startDate 开始日期 (格式: yyyy-MM-dd)
     * @param endDate 结束日期 (格式: yyyy-MM-dd)
     */
    fun loadReadingHabitDataByDateRange(startDate: String, endDate: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                
                // 使用ReadingRecordRepository获取按小时统计的阅读习惯时间分布数据
                // 这里统计的是在一天中各个小时的阅读次数，而不是阅读时长
                val habitData: Map<String, Int> = readingRecordRepository.getReadingHabitDataByDateRange(
                    startDate, endDate
                )
                
                _uiState.value = ReadingHabitDistributionUiState(
                    habitData = habitData,
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
 * 阅读习惯时间分布UI状态数据类
 */
data class ReadingHabitDistributionUiState(
    val habitData: Map<String, Int> = emptyMap(), // 时间段 -> 阅读次数
    val isLoading: Boolean = true,
    val error: String? = null
)