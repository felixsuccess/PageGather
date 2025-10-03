package com.anou.pagegather.ui.feature.timer

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 统一计时服务 - 优化方案
 * 
 * 职责：
 * 1. 统一管理所有计时状态
 * 2. 提供简化的API接口
 * 3. 处理计时数据的持久化
 * 4. 支持多种计时模式
 */
@Singleton
class TimerService @Inject constructor() {
    
    // 当前活跃的计时会话
    private val _activeSession = MutableStateFlow<TimerSession?>(null)
    val activeSession: StateFlow<TimerSession?> = _activeSession.asStateFlow()
    
    // 计时历史记录
    private val _timerHistory = MutableStateFlow<List<TimerSession>>(emptyList())
    val timerHistory: StateFlow<List<TimerSession>> = _timerHistory.asStateFlow()
    
    /**
     * 开始新的计时会话
     */
    fun startSession(
        bookId: Long? = null,
        timerType: TimerType = TimerType.FORWARD
    ): String {
        val sessionId = generateSessionId()
        val session = TimerSession(
            id = sessionId,
            bookId = bookId,
            timerType = timerType,
            startTime = System.currentTimeMillis(),
            status = TimerStatus.RUNNING
        )
        
        _activeSession.value = session
        return sessionId
    }
    
    /**
     * 暂停当前会话
     */
    fun pauseSession() {
        _activeSession.value?.let { session ->
            if (session.status == TimerStatus.RUNNING) {
                val now = System.currentTimeMillis()
                val updatedSession = session.copy(
                    status = TimerStatus.PAUSED,
                    pausedAt = now,
                    totalPausedDuration = session.totalPausedDuration + (now - session.lastResumeTime)
                )
                _activeSession.value = updatedSession
            }
        }
    }
    
    /**
     * 恢复当前会话
     */
    fun resumeSession() {
        _activeSession.value?.let { session ->
            if (session.status == TimerStatus.PAUSED) {
                val updatedSession = session.copy(
                    status = TimerStatus.RUNNING,
                    lastResumeTime = System.currentTimeMillis()
                )
                _activeSession.value = updatedSession
            }
        }
    }
    
    /**
     * 停止当前会话并返回会话数据
     */
    fun stopSession(): TimerSession? {
        return _activeSession.value?.let { session ->
            val now = System.currentTimeMillis()
            val finalSession = session.copy(
                status = TimerStatus.STOPPED,
                endTime = now,
                totalDuration = calculateTotalDuration(session, now)
            )
            
            // 添加到历史记录
            _timerHistory.value = _timerHistory.value + finalSession
            
            // 清除活跃会话
            _activeSession.value = null
            
            finalSession
        }
    }
    
    /**
     * 获取当前会话的实时时长
     */
    fun getCurrentDuration(): Long {
        return _activeSession.value?.let { session ->
            calculateTotalDuration(session, System.currentTimeMillis())
        } ?: 0L
    }
    
    /**
     * 计算总时长
     */
    private fun calculateTotalDuration(session: TimerSession, currentTime: Long): Long {
        val baseTime = when (session.status) {
            TimerStatus.RUNNING -> currentTime - session.startTime - session.totalPausedDuration
            TimerStatus.PAUSED -> session.pausedAt - session.startTime - session.totalPausedDuration
            TimerStatus.STOPPED -> session.endTime - session.startTime - session.totalPausedDuration
            else -> 0L
        }
        return maxOf(0L, baseTime)
    }
    
    /**
     * 生成会话ID
     */
    private fun generateSessionId(): String {
        return "timer_${System.currentTimeMillis()}_${(1000..9999).random()}"
    }
}

/**
 * 计时会话数据类
 */
data class TimerSession(
    val id: String,
    val bookId: Long? = null,
    val timerType: TimerType = TimerType.FORWARD,
    val startTime: Long,
    val endTime: Long = 0L,
    val pausedAt: Long = 0L,
    val lastResumeTime: Long = startTime,
    val totalPausedDuration: Long = 0L,
    val totalDuration: Long = 0L,
    val status: TimerStatus = TimerStatus.IDLE,
    val startProgress: Double = 0.0,
    val endProgress: Double = 0.0,
    val notes: String = ""
)

/**
 * 计时器类型
 */
enum class TimerType {
    FORWARD,    // 正向计时
    REVERSE,    // 反向计时
    COUNTDOWN,  // 倒计时
    POMODORO    // 番茄钟
}

/**
 * 计时器状态
 */
enum class TimerStatus {
    IDLE,       // 空闲
    RUNNING,    // 运行中
    PAUSED,     // 暂停
    STOPPED     // 已停止
}