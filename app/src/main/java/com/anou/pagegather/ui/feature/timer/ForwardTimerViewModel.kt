package com.anou.pagegather.ui.feature.timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anou.pagegather.data.local.entity.BookEntity
import com.anou.pagegather.data.local.entity.ReadingRecordEntity
import com.anou.pagegather.data.repository.BookRepository
import com.anou.pagegather.data.repository.ReadingRecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * 正向计时器UI状态
 */
data class ForwardTimerUIState(
    val status: TimerStatus = TimerStatus.IDLE,
    val elapsedTime: Long = 0L,           // 已用时间（毫秒）
    val selectedBook: BookEntity? = null,  // 选中的书籍
    val currentProgress: Double = 0.0,    // 当前阅读进度
    val startProgress: Double = 0.0,      // 开始阅读进度
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val currentReadingRecord: ReadingRecordEntity? = null,
    // 新增字段用于暂存计时数据和控制保存对话框显示
    val tempStartTime: Long = 0L,         // 暂存的开始时间
    val tempPausedDuration: Long = 0L,    // 暂存的暂停时长
    val showSaveDialog: Boolean = false   // 是否显示保存对话框
)

@HiltViewModel
class ForwardTimerViewModel @Inject constructor(
    private val readingRecordRepository: ReadingRecordRepository,
    private val bookRepository: BookRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForwardTimerUIState())
    val uiState: StateFlow<ForwardTimerUIState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var startTime: Long = 0L
    private var pausedDuration: Long = 0L // 累计暂停时间

    /**
     * 选择书籍
     */
    fun selectBook(book: BookEntity) {
        println("ForwardTimerViewModel: selectBook called with book id = ${book.id}, name = ${book.name}")
        _uiState.value = _uiState.value.copy(
            selectedBook = book,
            currentProgress = book.readPosition,
            startProgress = book.readPosition
        )
        println("ForwardTimerViewModel: UI state updated, selectedBook id = ${_uiState.value.selectedBook?.id}")
    }

    /**
     * 选择新添加的书籍
     */
    fun selectNewlyAddedBook(bookId: Long) {
        println("ForwardTimerViewModel: selectNewlyAddedBook called with bookId = $bookId")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val book = bookRepository.getBookById(bookId)
                withContext(Dispatchers.Main) {
                    if (book != null) {
                        println("ForwardTimerViewModel: Book found, name = ${book.name}")
                        selectBook(book)
                    } else {
                        println("ForwardTimerViewModel: Book not found for id = $bookId")
                        _uiState.value = _uiState.value.copy(
                            errorMessage = "未找到ID为 $bookId 的书籍"
                        )
                    }
                }
            } catch (e: Exception) {
                println("ForwardTimerViewModel: Error selecting book: ${e.message}")
                withContext(Dispatchers.Main) {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "选择新添加书籍失败: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * 设置当前阅读进度
     */
    fun setCurrentProgress(progress: Double) {
        _uiState.value = _uiState.value.copy(currentProgress = progress)
    }

    /**
     * 开始计时
     */
    fun startTimer() {
        val currentState = _uiState.value

        when (currentState.status) {
            TimerStatus.IDLE -> {
                startTimerWithoutRecord()
            }
            TimerStatus.PAUSED -> {
                // 从暂停状态恢复
                resumeTimer()
            }
            else -> {
                // 其他状态不处理
                return
            }
        }
    }

    /**
     * 开始计时
     */
    private fun startTimerWithoutRecord() {
        _uiState.value = _uiState.value.copy(
            status = TimerStatus.RUNNING,
            tempStartTime = System.currentTimeMillis(),
            tempPausedDuration = 0L
        )
        startTime = System.currentTimeMillis()
        pausedDuration = 0L
        startTimerJob()
    }

    /**
     * 暂停计时
     */
    fun pauseTimer() {
        if (_uiState.value.status == TimerStatus.RUNNING) {
            timerJob?.cancel()
            // 计算当前已用时间（在累加暂停时间之前）
            val currentTime = System.currentTimeMillis()
            val totalElapsed = currentTime - startTime - pausedDuration
            
            // 记录暂停时间
            pausedDuration += currentTime - startTime
            
            _uiState.value = _uiState.value.copy(
                status = TimerStatus.PAUSED,
                elapsedTime = totalElapsed
            )
        }
    }

    /**
     * 停止计时
     */
    fun stopTimer() {
        val currentState = _uiState.value
        
        // 打印调试信息
        println("ForwardTimerViewModel: stopTimer called")
        println("ForwardTimerViewModel: current selectedBook = ${currentState.selectedBook?.id}")
        
        // 检查是否有选中的书籍
        if (currentState.selectedBook == null) {
            println("ForwardTimerViewModel: Warning - No book selected when stopping timer")
        }
        
        // 如果计时器正在运行，先暂停计时器，执行保存逻辑
        if (currentState.status == TimerStatus.RUNNING) {
            timerJob?.cancel()
            // 计算总时长（在累加暂停时间之前）
            val currentTime = System.currentTimeMillis()
            val totalElapsed = currentTime - startTime - pausedDuration
            
            // 记录暂停时间
            pausedDuration += currentTime - startTime
            
            // 设置为PAUSED状态并显示保存对话框
            _uiState.value = currentState.copy(
                status = TimerStatus.PAUSED,
                elapsedTime = totalElapsed,
                tempStartTime = if (currentState.tempStartTime == 0L) startTime else currentState.tempStartTime,
                tempPausedDuration = pausedDuration,
                showSaveDialog = true
            )
        } 
        // 如果计时器已经是暂停状态，直接执行保存逻辑，将计时器状态设置为PAUSED（而不是STOPPED）
        else if (currentState.status == TimerStatus.PAUSED) {
            _uiState.value = currentState.copy(
                status = TimerStatus.PAUSED,
                showSaveDialog = true
            )
        }
    }

    /**
     * 保存计时记录
     */
    fun saveTimerRecord(bookId: Long?, startProgress: Double, endProgress: Double, notes: String?, markAsFinished: Boolean = false) {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value
                val startTime = currentState.tempStartTime
                val endTime = System.currentTimeMillis()
                val duration = endTime - startTime - currentState.tempPausedDuration
                
                if (bookId != null) {
                    // 创建阅读记录
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val date = dateFormat.format(Date(endTime))
                    
                    readingRecordRepository.addManualReadingRecord(
                        bookId = bookId,
                        startProgress = startProgress,
                        endProgress = endProgress,
                        duration = duration,
                        date = date,
                        notes = notes
                    )
                    
                    // 更新书籍进度
                    val book = bookRepository.getBookById(bookId)
                    if (book != null) {
                        val updatedBook = book.copy(
                            readPosition = endProgress,
                            lastReadDate = endTime,
                            // 如果标记为完成，更新阅读状态
                            readStatus = if (markAsFinished) com.anou.pagegather.data.local.entity.ReadStatus.FINISHED.code else book.readStatus,
                            readStatusChangedDate = if (markAsFinished) endTime else book.readStatusChangedDate,
                            finishedDate = if (markAsFinished) endTime else book.finishedDate
                        )
                        bookRepository.updateBook(updatedBook)
                    }
                }
                
                // 重置状态
                resetTimer()
                _uiState.value = _uiState.value.copy(showSaveDialog = false)
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "保存阅读记录失败: ${e.message}",
                    showSaveDialog = false
                )
            }
        }
    }

    /**
     * 继续阅读
     */
    fun continueReading(book: BookEntity?) {
        if (book != null) {
            // 获取当前状态
            val currentState = _uiState.value
            
            // 设置选中的书籍
            _uiState.value = currentState.copy(
                selectedBook = book,
                startProgress = book.readPosition,
                currentProgress = book.readPosition
            )
        }
        // 从暂停状态恢复计时
        resumeTimer()
    }

    /**
     * 取消保存计时记录
     */
    fun cancelSaveRecord() {
        // 取消保存，直接重置计时器
        resetTimer()
        _uiState.value = _uiState.value.copy(showSaveDialog = false)
    }

    /**
     * 重置计时器
     */
    private fun resetTimer() {
        timerJob?.cancel()
        startTime = 0L
        pausedDuration = 0L
        
        _uiState.value = ForwardTimerUIState()
    }

    /**
     * 完全重置计时器（包括类型和目标时间）
     */
    fun fullReset() {
        timerJob?.cancel()
        startTime = 0L
        pausedDuration = 0L
        _uiState.value = ForwardTimerUIState()
    }

    /**
     * 从暂停状态恢复计时
     */
    private fun resumeTimer() {
        _uiState.value = _uiState.value.copy(status = TimerStatus.RUNNING)
        // 重新设置开始时间，考虑已暂停的时间
        startTime = System.currentTimeMillis() - pausedDuration
        // 重置pausedDuration为0，因为我们在startTime中已经考虑了暂停时间
        pausedDuration = 0L
        startTimerJob()
    }

    /**
     * 启动计时任务
     */
    private fun startTimerJob() {
        timerJob = viewModelScope.launch {
            while (_uiState.value.status == TimerStatus.RUNNING) {
                delay(1000) // 每秒更新一次
                
                val currentTime = System.currentTimeMillis()
                val totalElapsed = currentTime - startTime - pausedDuration
                
                val currentState = _uiState.value
                
                _uiState.value = currentState.copy(elapsedTime = totalElapsed)
            }
        }
    }

    /**
     * 计时器结束处理
     */
    private fun onTimerFinished() {
        viewModelScope.launch {
            saveReadingRecord()
            // 可以在这里添加通知、声音等提醒
        }
    }

    /**
     * 保存阅读记录
     */
    private suspend fun saveReadingRecord() {
        try {
            val currentState = _uiState.value
            val record = currentState.currentReadingRecord ?: return
            
            readingRecordRepository.endReadingSession(
                recordId = record.id,
                endProgress = currentState.currentProgress
            )
            
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "保存阅读记录失败: ${e.message}"
            )
        }
    }

    /**
     * 清除错误信息
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    /**
     * 清除保存对话框状态
     */
    fun clearSaveDialog() {
        _uiState.value = _uiState.value.copy(showSaveDialog = false)
    }

    /**
     * 获取所有书籍（用于选择）
     */
    fun getAllBooks() = bookRepository.getAllBooks()

    /**
     * 格式化时间显示
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

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}