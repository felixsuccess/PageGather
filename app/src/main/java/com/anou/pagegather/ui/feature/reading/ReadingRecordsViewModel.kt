package com.anou.pagegather.ui.feature.reading

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anou.pagegather.data.local.entity.ReadingRecordEntity
import com.anou.pagegather.data.repository.ReadingRecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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

    /**
     * 阅读记录列表状态
     */
    val readingRecords: StateFlow<List<ReadingRecordEntity>> = readingRecordRepository
        .getAllReadingRecords()
        .map { records -> records.sortedByDescending { it.startTime } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /**
     * 删除阅读记录
     */
    fun deleteReadingRecord(record: ReadingRecordEntity) {
        viewModelScope.launch {
            readingRecordRepository.deleteReadingRecord(record)
        }
    }
}