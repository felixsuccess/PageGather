package com.anou.pagegather.ui.feature.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anou.pagegather.data.repository.BookRepository
import com.anou.pagegather.data.repository.NoteRepository
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
 * 年度报告ViewModel
 * 负责管理年度报告页面的数据和状态
 */
@HiltViewModel
class AnnualReportViewModel @Inject constructor(
    private val readingRecordRepository: ReadingRecordRepository,
    private val bookRepository: BookRepository,
    private val noteRepository: NoteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnnualReportUiState())
    val uiState: StateFlow<AnnualReportUiState> = _uiState.asStateFlow()

    /**
     * 加载年度报告数据
     */
    fun loadAnnualReportData(year: Int) {
        viewModelScope.launch {
            try {
                // 获取指定年份的开始和结束日期
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val startDate = "$year-01-01"
                val endDate = "$year-12-31"
                
                // 获取该年度的总阅读时长
                val totalReadingTime = readingRecordRepository.getTotalReadingTimeByDateRange(startDate, endDate)
                
                // 获取该年度的阅读天数
                val readingDaysCount = readingRecordRepository.getReadingDaysCount(startDate, endDate)
                
                // 获取该年度读完的书籍数量
                // TODO: 需要实现根据完成日期筛选书籍的功能
                
                // 获取该年度的笔记数量
                // TODO: 需要实现根据创建日期筛选笔记的功能
                
                _uiState.value = AnnualReportUiState(
                    year = year,
                    totalReadingTime = totalReadingTime,
                    readingDaysCount = readingDaysCount,
                    finishedBooksCount = 0, // TODO: 实现此功能
                    noteCount = 0, // TODO: 实现此功能
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
 * 年度报告UI状态数据类
 */
data class AnnualReportUiState(
    val year: Int = Calendar.getInstance().get(Calendar.YEAR),
    val totalReadingTime: Long = 0,
    val readingDaysCount: Int = 0,
    val finishedBooksCount: Int = 0,
    val noteCount: Int = 0,
    val isLoading: Boolean = true,
    val error: String? = null
)