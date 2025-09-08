package com.anou.pagegather.ui.feature.bookshelf

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anou.pagegather.data.local.entity.ReadingRecordEntity
import com.anou.pagegather.data.repository.ReadingRecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 书籍阅读历史ViewModel
 * 负责管理特定书籍的阅读记录数据
 */
@HiltViewModel
class BookReadingHistoryViewModel @Inject constructor(
    private val readingRecordRepository: ReadingRecordRepository
) : ViewModel() {

    private val _readingRecords = MutableStateFlow<List<ReadingRecordEntity>>(emptyList())
    val readingRecords: StateFlow<List<ReadingRecordEntity>> = _readingRecords.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    /**
     * 加载指定书籍的阅读记录
     */
    fun loadReadingRecords(bookId: Long) {
        Log.d("BookReadingHistoryViewModel", "Loading reading records for bookId: $bookId")
        viewModelScope.launch {
            readingRecordRepository.getReadingRecordsByBookId(bookId)
                .onStart {
                    Log.d("BookReadingHistoryViewModel", "Starting to load reading records for bookId: $bookId")
                    _isLoading.value = true
                    _error.value = null
                }
                .catch { e ->
                    Log.e("BookReadingHistoryViewModel", "Error loading reading records for bookId: $bookId", e)
                    _error.value = e.message
                    _isLoading.value = false
                }
                .onEach { records ->
                    Log.d("BookReadingHistoryViewModel", "Collected ${records.size} reading records for bookId: $bookId")
                    _readingRecords.value = records
                    Log.d("BookReadingHistoryViewModel", "Updated reading records state with ${records.size} items")
                    // 在每次收集到数据后，将加载状态设置为false
                    _isLoading.value = false
                }
                .collect() // 空收集，因为我们只关心onEach中的副作用
        }
    }

    /**
     * 删除阅读记录
     */
    fun deleteReadingRecord(record: ReadingRecordEntity) {
        viewModelScope.launch {
            try {
                readingRecordRepository.deleteReadingRecord(record)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
}