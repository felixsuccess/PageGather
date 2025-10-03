package com.anou.pagegather.ui.feature.timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anou.pagegather.data.local.entity.BookEntity
import com.anou.pagegather.data.local.entity.ReadingRecordEntity
import com.anou.pagegather.data.local.entity.RecordType
import com.anou.pagegather.data.repository.BookRepository
import com.anou.pagegather.data.repository.ReadingRecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * 计时器管理器 - 简化版本
 * 
 * 优势：
 * 1. 统一管理计时和保存逻辑
 * 2. 减少页面间的数据传递
 * 3. 简化状态管理
 * 4. 保持现有接口兼容
 */
@HiltViewModel
class TimerManager @Inject constructor(
    private val timerService: TimerService,
    private val bookRepository: BookRepository,
    private val readingRecordRepository: ReadingRecordRepository
) : ViewModel() {
    
    // UI状态
    private val _uiState = MutableStateFlow(TimerManagerState())
    val uiState: StateFlow<TimerManagerState> = _uiState.asStateFlow()
    
    // 组合计时服务状态和UI状态
    val combinedState = combine(
        timerService.activeSession,
        _uiState
    ) { session, uiState ->
        uiState.copy(
            currentSession = session,
            isTimerRunning = session?.status == TimerStatus.RUNNING,
            currentDuration = if (session != null) timerService.getCurrentDuration() else 0L
        )
    }
    
    /**
     * 开始计时 - 兼容现有接口
     */
    fun startTimer(bookId: Long? = null) {
        viewModelScope.launch {
            try {
                val sessionId = timerService.startSession(bookId, TimerType.FORWARD)
                _uiState.value = _uiState.value.copy(
                    currentSessionId = sessionId,
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "启动计时失败: ${e.message}"
                )
            }
        }
    }
    
    /**
     * 暂停计时 - 兼容现有接口
     */
    fun pauseTimer() {
        timerService.pauseSession()
    }
    
    /**
     * 恢复计时 - 兼容现有接口
     */
    fun resumeTimer() {
        timerService.resumeSession()
    }
    
    /**
     * 停止计时并自动保存 - 简化版本
     */
    fun stopAndSaveTimer(
        startProgress: Double = 0.0,
        endProgress: Double = 0.0,
        notes: String = ""
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                
                val session = timerService.stopSession()
                if (session != null && session.bookId != null) {
                    val record = ReadingRecordEntity(
                        bookId = session.bookId,
                        startTime = session.startTime,
                        endTime = session.endTime,
                        duration = session.totalDuration,
                        startProgress = startProgress,
                        endProgress = endProgress,
                        notes = notes,
                        recordType = RecordType.PRECISE.ordinal,
                        date = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date(session.startTime))
                    )
                    
                    // 在 IO 线程中执行数据库操作
                    withContext(kotlinx.coroutines.Dispatchers.IO) {
                        readingRecordRepository.insertReadingRecord(record)
                    }
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        lastSavedRecord = record,
                        showSaveSuccess = true
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        showSaveDialog = true,
                        pendingSession = session
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "保存记录失败: ${e.message}"
                )
            }
        }
    }
    
    /**
     * 手动保存记录 - 兼容现有复杂保存逻辑
     */
    fun saveRecordManually(
        bookId: Long,
        startProgress: Double,
        endProgress: Double,
        notes: String = "",
        duration: Long = 0L,
        startTime: Long = System.currentTimeMillis() - duration,
        endTime: Long = System.currentTimeMillis()
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                
                // 优先使用 pendingSession，如果没有则使用传入的参数
                val session = _uiState.value.pendingSession
                val actualStartTime = session?.startTime ?: startTime
                val actualEndTime = session?.endTime ?: endTime
                val actualDuration = session?.totalDuration ?: duration
                
                val record = ReadingRecordEntity(
                    bookId = bookId,
                    startTime = actualStartTime,
                    endTime = actualEndTime,
                    duration = actualDuration,
                    startProgress = startProgress,
                    endProgress = endProgress,
                    notes = notes,
                    recordType = RecordType.PRECISE.ordinal,
                    date = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date(actualStartTime))
                )
                
                // 在 IO 线程中执行数据库操作
                withContext(kotlinx.coroutines.Dispatchers.IO) {
                    // 保存阅读记录
                    readingRecordRepository.insertReadingRecord(record)
                    
                    // 更新书籍阅读进度
                    val book = bookRepository.getBookById(bookId)
                    if (book != null) {
                        val updatedBook = book.copy(
                            readPosition = endProgress,
                            lastReadDate = actualEndTime
                        )
                        bookRepository.updateBook(updatedBook)
                    }
                }
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    lastSavedRecord = record,
                    showSaveSuccess = true,
                    showSaveDialog = false,
                    pendingSession = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "保存记录失败: ${e.message}"
                )
            }
        }
    }
    
    /**
     * 清除状态
     */
    fun clearState() {
        _uiState.value = TimerManagerState()
    }
    
    /**
     * 清除错误
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    /**
     * 获取书籍信息
     */
    suspend fun getBookById(bookId: Long): BookEntity? {
        return try {
            bookRepository.getBookById(bookId)
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * 重置保存成功状态
     */
    fun resetSaveSuccessState() {
        _uiState.value = _uiState.value.copy(showSaveSuccess = false)
    }
    

    
    /**
     * 格式化时间显示 - 兼容现有接口
     */
    fun formatTime(timeInMs: Long): String {
        val totalSeconds = timeInMs / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        
        return if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }
}

/**
 * 计时器管理器状态
 */
data class TimerManagerState(
    val currentSessionId: String? = null,
    val currentSession: TimerSession? = null,
    val isTimerRunning: Boolean = false,
    val currentDuration: Long = 0L,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showSaveDialog: Boolean = false,
    val showSaveSuccess: Boolean = false,
    val pendingSession: TimerSession? = null,
    val lastSavedRecord: ReadingRecordEntity? = null
)