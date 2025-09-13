package com.anou.pagegather.ui.feature.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anou.pagegather.data.local.entity.BookEntity
import com.anou.pagegather.data.repository.BookRepository
import com.anou.pagegather.data.repository.ReadingRecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 阅读最久书籍统计ViewModel
 * 负责管理阅读最久书籍统计页面的数据和状态
 */
@HiltViewModel
class LongestReadingBooksViewModel @Inject constructor(
    private val readingRecordRepository: ReadingRecordRepository,
    private val bookRepository: BookRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LongestReadingBooksUiState())
    val uiState: StateFlow<LongestReadingBooksUiState> = _uiState.asStateFlow()

    /**
     * 加载阅读最久书籍数据
     */
    fun loadLongestReadingBooks() {
        viewModelScope.launch {
            try {
                // TODO: 实现获取阅读时长最长的书籍列表逻辑
                // 需要:
                // 1. 获取所有书籍的总阅读时长
                // 2. 按阅读时长排序
                // 3. 取前N本书籍
                
                _uiState.value = LongestReadingBooksUiState(
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
 * 阅读最久书籍统计UI状态数据类
 */
data class LongestReadingBooksUiState(
    val books: List<BookWithReadingTime> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

/**
 * 带阅读时长的书籍数据类
 */
data class BookWithReadingTime(
    val book: BookEntity,
    val totalReadingTime: Long
)