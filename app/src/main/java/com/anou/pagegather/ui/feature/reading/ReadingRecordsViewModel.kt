package com.anou.pagegather.ui.feature.reading

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anou.pagegather.data.local.entity.ReadingRecordEntity
import com.anou.pagegather.data.repository.ReadingRecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 阅读记录列表ViewModel
 * 负责管理阅读记录列表的数据和状态
 */
@HiltViewModel
class ReadingRecordsViewModel @Inject constructor(
    private val readingRecordRepository: ReadingRecordRepository
) : ViewModel() {

    // 筛选条件状态
    private val _filterDate = MutableStateFlow<String?>(null)

    /**
     * 阅读记录列表状态
     */
    val readingRecords: StateFlow<List<ReadingRecordEntity>> = combine(
        readingRecordRepository.getAllReadingRecords(),
        _filterDate
    ) { records, filterDate ->
        records
            .sortedByDescending { it.startTime }
            .filter { record ->
                // 日期筛选
                filterDate?.let { date ->
                    record.date == date
                } ?: true
            }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    /**
     * 设置日期筛选条件
     */
    fun setDateFilter(date: String?) {
        _filterDate.value = date
    }

    /**
     * 清除所有筛选条件
     */
    fun clearAllFilters() {
        _filterDate.value = null
    }

    /**
     * 删除阅读记录
     */
    fun deleteReadingRecord(record: ReadingRecordEntity) {
        viewModelScope.launch {
            readingRecordRepository.deleteReadingRecord(record)
        }
    }
}