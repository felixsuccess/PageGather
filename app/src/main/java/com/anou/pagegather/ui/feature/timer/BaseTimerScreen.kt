package com.anou.pagegather.ui.feature.timer

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * 计时器模式枚举
 */
enum class TimerMode {
    FORWARD,        // 正向计时
    COUNTDOWN,      // 倒计时
    POMODORO        // 番茄钟
}

/**
 * 番茄钟阶段
 */
enum class PomodoroPhase {
    WORK,           // 工作阶段
    SHORT_BREAK,    // 短休息
    LONG_BREAK      // 长休息
}

/**
 * 通用计时器状态
 */
data class BaseTimerState(
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val currentTime: Long = 0L,
    val targetTime: Long = 0L,
    val startTime: Long = 0L,
    val pauseCount: Int = 0,
    val isCompleted: Boolean = false,
    val isOvertime: Boolean = false,
    val pomodoroPhase: PomodoroPhase = PomodoroPhase.WORK,
    val pomodoroRound: Int = 1
)

/**
 * 计时器配置
 */
data class TimerConfig(
    val mode: TimerMode,
    val targetDuration: Long = 0L,
    val pomodoroWorkTime: Long = 25 * 60 * 1000L,    // 25分钟
    val pomodoroShortBreak: Long = 5 * 60 * 1000L,   // 5分钟
    val pomodoroLongBreak: Long = 15 * 60 * 1000L,   // 15分钟
    val autoStartBreaks: Boolean = false,
    val autoStartWork: Boolean = false
)

/**
 * 计时器结果（扩展版）
 */
data class TimerResult(
    val duration: Long,
    val bookId: Long? = null,
    val success: Boolean = true,
    val notes: String = "",
    val pauseCount: Int = 0,
    val startTime: Long = System.currentTimeMillis(),
    val endTime: Long = System.currentTimeMillis(),
    val timerMode: String = "",
    val pomodoroRounds: Int = 0,
    val targetReached: Boolean = false
)

/**
 * 基础计时器抽象类
 * 
 * 提供通用的计时逻辑和UI组件，子类只需实现特定的业务逻辑
 */
abstract class BaseTimerScreen {
    
    /**
     * 抽象方法：获取屏幕标题
     */
    abstract fun getScreenTitle(state: BaseTimerState): String
    
    /**
     * 抽象方法：处理计时逻辑
     */
    abstract fun handleTimerTick(
        state: BaseTimerState, 
        config: TimerConfig,
        hapticFeedback: androidx.compose.ui.hapticfeedback.HapticFeedback
    ): BaseTimerState
    
    /**
     * 抽象方法：获取显示时间
     */
    abstract fun getDisplayTime(state: BaseTimerState, config: TimerConfig): Long
    
    /**
     * 抽象方法：获取状态文本
     */
    abstract fun getStatusText(state: BaseTimerState): String
    
    /**
     * 抽象方法：获取状态颜色
     */
    @Composable
    abstract fun getStatusColor(state: BaseTimerState): androidx.compose.ui.graphics.Color
    
    /**
     * 抽象方法：自定义UI内容（可选）
     */
    open fun getCustomContent(state: BaseTimerState, config: TimerConfig): @Composable (() -> Unit)? = null
    
    /**
     * 通用计时器界面
     */
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TimerScreen(
        config: TimerConfig,
        entryContext: TimerEntryContext? = null,
        onNavigateBack: () -> Unit = {},
        onTimerComplete: (TimerResult) -> Unit = {}
    ) {
        val hapticFeedback = LocalHapticFeedback.current
        
        // 通用状态管理
        var timerState by remember { 
            mutableStateOf(
                BaseTimerState(
                    targetTime = when (config.mode) {
                        TimerMode.FORWARD -> config.targetDuration
                        TimerMode.COUNTDOWN -> config.targetDuration
                        TimerMode.POMODORO -> config.pomodoroWorkTime
                    }
                )
            )
        }
        
        // 书籍信息
        var selectedBookId by remember { mutableStateOf(entryContext?.bookId) }
        var bookTitle by remember { mutableStateOf("") }
        
        // 通用计时逻辑
        LaunchedEffect(timerState.isRunning, timerState.isPaused) {
            if (timerState.isRunning && !timerState.isPaused) {
                if (timerState.startTime == 0L) {
                    timerState = timerState.copy(startTime = System.currentTimeMillis())
                }
                
                while (timerState.isRunning && !timerState.isPaused) {
                    delay(1000)
                    timerState = handleTimerTick(timerState, config, hapticFeedback)
                    
                    // 检查是否完成
                    if (timerState.isCompleted) {
                        onTimerComplete(createTimerResult(timerState, selectedBookId, config))
                        break
                    }
                }
            }
        }
        
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { 
                        Text(
                            text = getScreenTitle(timerState),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Medium
                        ) 
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "返回"
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 书籍信息卡片（通用）
                if (selectedBookId != null && bookTitle.isNotEmpty()) {
                    BookInfoCard(bookTitle = bookTitle)
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                // 主计时器显示（通用）
                TimerDisplayCard(
                    timerState = timerState,
                    config = config,
                    modifier = Modifier.weight(1f)
                )
                
                // 自定义内容（子类可扩展）
                getCustomContent(timerState, config)?.invoke()
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 控制按钮（通用）
                TimerControlButtons(
                    timerState = timerState,
                    config = config,
                    onStateChange = { newState -> timerState = newState },
                    onComplete = { 
                        onTimerComplete(createTimerResult(timerState, selectedBookId, config))
                    },
                    hapticFeedback = hapticFeedback
                )
            }
        }
    }
    
    /**
     * 通用书籍信息卡片
     */
    @Composable
    private fun BookInfoCard(bookTitle: String) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "正在阅读",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = bookTitle,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
    
    /**
     * 通用计时器显示卡片
     */
    @Composable
    private fun TimerDisplayCard(
        timerState: BaseTimerState,
        config: TimerConfig,
        modifier: Modifier = Modifier
    ) {
        Card(
            modifier = modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (timerState.isRunning && !timerState.isPaused) 
                    MaterialTheme.colorScheme.primaryContainer 
                else MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // 主时间显示
                Text(
                    text = formatTime(getDisplayTime(timerState, config)),
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center,
                    color = getStatusColor(timerState)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 状态文本
                Text(
                    text = getStatusText(timerState),
                    style = MaterialTheme.typography.titleMedium,
                    color = getStatusColor(timerState)
                )
                
                // 统计信息
                if (timerState.currentTime > 0 || timerState.pauseCount > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (timerState.pauseCount > 0) "暂停 ${timerState.pauseCount} 次" else "🔥 连续专注",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
    
    /**
     * 通用控制按钮
     */
    @Composable
    private fun TimerControlButtons(
        timerState: BaseTimerState,
        config: TimerConfig,
        onStateChange: (BaseTimerState) -> Unit,
        onComplete: () -> Unit,
        hapticFeedback: androidx.compose.ui.hapticfeedback.HapticFeedback
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when {
                !timerState.isRunning && !timerState.isPaused && timerState.currentTime == 0L -> {
                    // 初始状态：开始按钮
                    Button(
                        onClick = { 
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            onStateChange(timerState.copy(isRunning = true, isPaused = false))
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("开始")
                    }
                }
                
                timerState.isRunning && !timerState.isPaused -> {
                    // 运行状态：暂停和完成按钮
                    Button(
                        onClick = { 
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onStateChange(timerState.copy(isPaused = true, pauseCount = timerState.pauseCount + 1))
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Icon(Icons.Default.Pause, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("暂停")
                    }
                    
                    OutlinedButton(
                        onClick = { 
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            onStateChange(timerState.copy(isRunning = false, isPaused = false, isCompleted = true))
                            onComplete()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Stop, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("完成")
                    }
                }
                
                timerState.isPaused -> {
                    // 暂停状态：继续和完成按钮
                    Button(
                        onClick = { 
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            onStateChange(timerState.copy(isPaused = false))
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("继续")
                    }
                    
                    OutlinedButton(
                        onClick = { 
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            onStateChange(timerState.copy(isRunning = false, isPaused = false, isCompleted = true))
                            onComplete()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Stop, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("完成")
                    }
                }
            }
        }
    }
    
    /**
     * 创建计时结果
     */
    private fun createTimerResult(
        state: BaseTimerState,
        bookId: Long?,
        config: TimerConfig
    ): TimerResult {
        return TimerResult(
            duration = state.currentTime,
            bookId = bookId,
            success = state.isCompleted,
            pauseCount = state.pauseCount,
            startTime = state.startTime,
            endTime = System.currentTimeMillis(),
            timerMode = config.mode.name,
            pomodoroRounds = if (config.mode == TimerMode.POMODORO) state.pomodoroRound else 0,
            targetReached = when (config.mode) {
                TimerMode.FORWARD -> config.targetDuration > 0 && state.currentTime >= config.targetDuration
                TimerMode.COUNTDOWN -> state.currentTime >= config.targetDuration
                TimerMode.POMODORO -> true // 番茄钟总是达到目标
            }
        )
    }
    
    /**
     * 格式化时间显示
     */
    protected fun formatTime(timeInMs: Long): String {
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