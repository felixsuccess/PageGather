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
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/**
 * 计时器模式枚举
 */
enum class TimerMode {
    FORWARD,    // 正向计时
    COUNTDOWN,  // 倒计时
    POMODORO    // 番茄钟
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
 * 阅读计时页面
 * 
 * 特点：
 * - 支持三种计时模式：正向计时、倒计时、番茄钟
 * - TopBar 显示当前模式和切换按钮
 * - 初始状态可切换，计时中锁定模式
 * - 统一的UI和交互体验
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadingTimerScreen(
    entryContext: TimerEntryContext? = null,
    onNavigateBack: () -> Unit = {},
    onTimerComplete: (TimerResult) -> Unit = {}
) {
    val hapticFeedback = LocalHapticFeedback.current
    
    // 计时器模式和配置
    var currentMode by remember { mutableStateOf(TimerMode.FORWARD) }
    var showModeMenu by remember { mutableStateOf(false) }
    var showTimeSettingDialog by remember { mutableStateOf(false) }
    
    // 用户自定义时长设置
    var countdownMinutes by remember { mutableStateOf(25) }
    var pomodoroWorkMinutes by remember { mutableStateOf(25) }
    var pomodoroShortBreakMinutes by remember { mutableStateOf(5) }
    var pomodoroLongBreakMinutes by remember { mutableStateOf(15) }
    
    // 计时器状态 - 使用完整的状态结构
    var timerState by remember { 
        mutableStateOf(
            BaseTimerState(
                targetTime = when (currentMode) {
                    TimerMode.FORWARD -> 0L
                    TimerMode.COUNTDOWN -> 25 * 60 * 1000L
                    TimerMode.POMODORO -> 25 * 60 * 1000L
                }
            )
        )
    }
    
    // 计时器配置 - 使用用户设置的时长
    val config = remember(currentMode, countdownMinutes, pomodoroWorkMinutes, pomodoroShortBreakMinutes, pomodoroLongBreakMinutes) {
        TimerConfig(
            mode = currentMode,
            targetDuration = when (currentMode) {
                TimerMode.FORWARD -> 0L
                TimerMode.COUNTDOWN -> countdownMinutes * 60 * 1000L
                TimerMode.POMODORO -> pomodoroWorkMinutes * 60 * 1000L
            },
            pomodoroWorkTime = pomodoroWorkMinutes * 60 * 1000L,
            pomodoroShortBreak = pomodoroShortBreakMinutes * 60 * 1000L,
            pomodoroLongBreak = pomodoroLongBreakMinutes * 60 * 1000L
        )
    }
    
    // 书籍信息
    var selectedBookId by remember { mutableStateOf(entryContext?.bookId) }
    
    // 是否可以切换模式（只在初始状态允许）
    val canSwitchMode = !timerState.isRunning && timerState.currentTime == 0L
    
    // 根据模式重置状态
    LaunchedEffect(currentMode, config) {
        if (canSwitchMode) {
            timerState = BaseTimerState(
                targetTime = config.targetDuration,
                pomodoroPhase = PomodoroPhase.WORK,
                pomodoroRound = 1
            )
        }
    }
    
    // 计时逻辑 - 完整实现
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
                    val result = createTimerResult(timerState, selectedBookId, config)
                    onTimerComplete(result)
                    break
                }
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "阅读计时"
                        )
                        Icon(
                            imageVector = when(currentMode) {
                                TimerMode.FORWARD -> Icons.Default.Timer
                                TimerMode.COUNTDOWN -> Icons.Default.HourglassEmpty  
                                TimerMode.POMODORO -> Icons.Default.LocalCafe
                            },
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text =  when(currentMode) {
                                TimerMode.FORWARD -> "正向计时"
                                TimerMode.COUNTDOWN -> "倒计时"
                                TimerMode.POMODORO -> "番茄钟"
                            }
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                actions = {
                    Box {
                        IconButton(
                            onClick = { showModeMenu = true },
                            enabled = canSwitchMode
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwapHoriz,
                                contentDescription = "切换计时器",
                                tint = if (canSwitchMode) {
                                    MaterialTheme.colorScheme.onSurface
                                } else {
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                }
                            )
                        }
                        
                        DropdownMenu(
                            expanded = showModeMenu,
                            onDismissRequest = { showModeMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { 
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Timer,
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Text("正向计时")
                                    }
                                },
                                onClick = { 
                                    currentMode = TimerMode.FORWARD
                                    showModeMenu = false 
                                }
                            )
                            DropdownMenuItem(
                                text = { 
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.HourglassEmpty,
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Text("倒计时")
                                    }
                                },
                                onClick = { 
                                    showModeMenu = false
                                    if (currentMode != TimerMode.COUNTDOWN) {
                                        currentMode = TimerMode.COUNTDOWN
                                        showTimeSettingDialog = true
                                    }
                                }
                            )
                            DropdownMenuItem(
                                text = { 
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.LocalCafe,
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Text("番茄钟")
                                    }
                                },
                                onClick = { 
                                    showModeMenu = false
                                    if (currentMode != TimerMode.POMODORO) {
                                        currentMode = TimerMode.POMODORO
                                        showTimeSettingDialog = true
                                    }
                                }
                            )
                        }
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
            Spacer(modifier = Modifier.height(32.dp))
            
            // 计时器显示
            TimerDisplay(
                timerState = timerState,
                config = config,
                modifier = Modifier.weight(1f)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // 控制按钮
            TimerControls(
                timerState = timerState,
                onStart = { 
                    timerState = timerState.copy(isRunning = true, isPaused = false)
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                },
                onPause = { 
                    timerState = timerState.copy(isPaused = true, pauseCount = timerState.pauseCount + 1)
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                },
                onResume = { 
                    timerState = timerState.copy(isPaused = false)
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                },
                onStop = {
                    val result = createTimerResult(timerState, selectedBookId, config)
                    onTimerComplete(result)
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // 时长设置对话框
        if (showTimeSettingDialog) {
            TimeSettingDialog(
                currentMode = currentMode,
                countdownMinutes = countdownMinutes,
                pomodoroWorkMinutes = pomodoroWorkMinutes,
                pomodoroShortBreakMinutes = pomodoroShortBreakMinutes,
                pomodoroLongBreakMinutes = pomodoroLongBreakMinutes,
                onCountdownMinutesChange = { countdownMinutes = it },
                onPomodoroWorkMinutesChange = { pomodoroWorkMinutes = it },
                onPomodoroShortBreakMinutesChange = { pomodoroShortBreakMinutes = it },
                onPomodoroLongBreakMinutesChange = { pomodoroLongBreakMinutes = it },
                onConfirm = { showTimeSettingDialog = false },
                onDismiss = { 
                    showTimeSettingDialog = false
                    // 如果用户取消设置，恢复到正向计时
                    if (currentMode != TimerMode.FORWARD) {
                        currentMode = TimerMode.FORWARD
                    }
                }
            )
        }
    }
}

/**
 * 计时器显示组件
 */
@Composable
private fun TimerDisplay(
    timerState: BaseTimerState,
    config: TimerConfig,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 时间显示
        val displayTime = getDisplayTime(timerState, config)
        
        Text(
            text = formatTime(displayTime),
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Light,
            color = getStatusColor(timerState)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 状态文字
        Text(
            text = getStatusText(timerState, config),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        // 番茄钟阶段显示
        if (config.mode == TimerMode.POMODORO) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "第 ${timerState.pomodoroRound} 轮 - ${getPomodoroPhaseText(timerState.pomodoroPhase)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        // 暂停次数显示
        if (timerState.pauseCount > 0) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "暂停 ${timerState.pauseCount} 次",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        // 进度条（倒计时和番茄钟显示）
        if (config.mode != TimerMode.FORWARD && timerState.targetTime > 0) {
            Spacer(modifier = Modifier.height(24.dp))
            
            val progress = if (timerState.targetTime > 0) {
                (timerState.currentTime.toFloat() / timerState.targetTime.toFloat()).coerceIn(0f, 1f)
            } else 0f
            
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        // 超时提示
        if (timerState.isOvertime) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "已超过目标时间",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

/**
 * 计时器控制按钮
 */
@Composable
private fun TimerControls(
    timerState: BaseTimerState,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onStop: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        when {
            !timerState.isRunning && timerState.currentTime == 0L -> {
                // 初始状态
                Button(
                    onClick = onStart,
                    modifier = Modifier.width(120.dp)
                ) {
                    Text("开始")
                }
            }
            timerState.isRunning && !timerState.isPaused -> {
                // 运行中
                OutlinedButton(
                    onClick = onPause,
                    modifier = Modifier.width(100.dp)
                ) {
                    Text("暂停")
                }
                Button(
                    onClick = onStop,
                    modifier = Modifier.width(100.dp)
                ) {
                    Text("完成")
                }
            }
            timerState.isPaused -> {
                // 暂停中
                Button(
                    onClick = onResume,
                    modifier = Modifier.width(100.dp)
                ) {
                    Text("继续")
                }
                OutlinedButton(
                    onClick = onStop,
                    modifier = Modifier.width(100.dp)
                ) {
                    Text("完成")
                }
            }
            timerState.isCompleted -> {
                // 已完成
                Button(
                    onClick = onStart,
                    modifier = Modifier.width(120.dp)
                ) {
                    Text("重新开始")
                }
            }
        }
    }
}

/**
 * 格式化时间显示
 */
private fun formatTime(timeInMs: Long): String {
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

/**
 * 处理计时器每秒更新
 */
private fun handleTimerTick(
    state: BaseTimerState,
    config: TimerConfig,
    hapticFeedback: androidx.compose.ui.hapticfeedback.HapticFeedback
): BaseTimerState {
    val newTime = state.currentTime + 1000
    
    return when (config.mode) {
        TimerMode.FORWARD -> {
            val isOvertime = config.targetDuration > 0 && newTime > config.targetDuration
            state.copy(
                currentTime = newTime,
                isOvertime = isOvertime
            )
        }
        TimerMode.COUNTDOWN -> {
            if (newTime >= state.targetTime) {
                // 倒计时结束
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                state.copy(
                    currentTime = state.targetTime,
                    isCompleted = true,
                    isRunning = false
                )
            } else {
                state.copy(currentTime = newTime)
            }
        }
        TimerMode.POMODORO -> {
            if (newTime >= state.targetTime) {
                // 番茄钟阶段结束
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                handlePomodoroPhaseComplete(state, config)
            } else {
                state.copy(currentTime = newTime)
            }
        }
    }
}

/**
 * 处理番茄钟阶段完成
 */
private fun handlePomodoroPhaseComplete(
    state: BaseTimerState,
    config: TimerConfig
): BaseTimerState {
    return when (state.pomodoroPhase) {
        PomodoroPhase.WORK -> {
            // 工作阶段结束，进入休息
            val isLongBreak = state.pomodoroRound % 4 == 0
            val nextPhase = if (isLongBreak) PomodoroPhase.LONG_BREAK else PomodoroPhase.SHORT_BREAK
            val nextTargetTime = if (isLongBreak) config.pomodoroLongBreak else config.pomodoroShortBreak
            
            state.copy(
                currentTime = 0L,
                targetTime = nextTargetTime,
                pomodoroPhase = nextPhase,
                isRunning = config.autoStartBreaks,
                isCompleted = !config.autoStartBreaks
            )
        }
        PomodoroPhase.SHORT_BREAK, PomodoroPhase.LONG_BREAK -> {
            // 休息阶段结束，进入下一轮工作
            state.copy(
                currentTime = 0L,
                targetTime = config.pomodoroWorkTime,
                pomodoroPhase = PomodoroPhase.WORK,
                pomodoroRound = state.pomodoroRound + 1,
                isRunning = config.autoStartWork,
                isCompleted = !config.autoStartWork
            )
        }
    }
}

/**
 * 获取显示时间
 */
private fun getDisplayTime(state: BaseTimerState, config: TimerConfig): Long {
    return when (config.mode) {
        TimerMode.FORWARD -> state.currentTime
        TimerMode.COUNTDOWN -> maxOf(0L, state.targetTime - state.currentTime)
        TimerMode.POMODORO -> maxOf(0L, state.targetTime - state.currentTime)
    }
}

/**
 * 获取状态颜色
 */
@Composable
private fun getStatusColor(state: BaseTimerState): androidx.compose.ui.graphics.Color {
    return when {
        state.isRunning && !state.isPaused -> MaterialTheme.colorScheme.primary
        state.isPaused -> MaterialTheme.colorScheme.secondary
        state.isOvertime -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurface
    }
}

/**
 * 获取状态文字
 */
private fun getStatusText(state: BaseTimerState, config: TimerConfig): String {
    return when {
        !state.isRunning && state.currentTime == 0L -> "准备开始"
        state.isRunning && !state.isPaused -> when (config.mode) {
            TimerMode.FORWARD -> if (state.isOvertime) "超时计时中" else "计时中"
            TimerMode.COUNTDOWN -> "倒计时中"
            TimerMode.POMODORO -> when (state.pomodoroPhase) {
                PomodoroPhase.WORK -> "专注中"
                PomodoroPhase.SHORT_BREAK -> "短休息中"
                PomodoroPhase.LONG_BREAK -> "长休息中"
            }
        }
        state.isPaused -> "已暂停"
        state.isCompleted -> "已完成"
        else -> "准备开始"
    }
}

/**
 * 获取番茄钟阶段文字
 */
private fun getPomodoroPhaseText(phase: PomodoroPhase): String {
    return when (phase) {
        PomodoroPhase.WORK -> "工作时间"
        PomodoroPhase.SHORT_BREAK -> "短休息"
        PomodoroPhase.LONG_BREAK -> "长休息"
    }
}

/**
 * 创建计时器结果
 */
private fun createTimerResult(
    state: BaseTimerState,
    bookId: Long?,
    config: TimerConfig
): TimerResult {
    val endTime = System.currentTimeMillis()
    return TimerResult(
        duration = state.currentTime,
        bookId = bookId,
        success = true,
        pauseCount = state.pauseCount,
        startTime = state.startTime,
        endTime = endTime,
        timerMode = config.mode.name,
        pomodoroRounds = if (config.mode == TimerMode.POMODORO) state.pomodoroRound else 0,
        targetReached = when (config.mode) {
            TimerMode.FORWARD -> config.targetDuration > 0 && state.currentTime >= config.targetDuration
            TimerMode.COUNTDOWN -> state.currentTime >= state.targetTime
            TimerMode.POMODORO -> state.isCompleted
        }
    )
}/**

 * 时长设置对话框
 */
@Composable
private fun TimeSettingDialog(
    currentMode: TimerMode,
    countdownMinutes: Int,
    pomodoroWorkMinutes: Int,
    pomodoroShortBreakMinutes: Int,
    pomodoroLongBreakMinutes: Int,
    onCountdownMinutesChange: (Int) -> Unit,
    onPomodoroWorkMinutesChange: (Int) -> Unit,
    onPomodoroShortBreakMinutesChange: (Int) -> Unit,
    onPomodoroLongBreakMinutesChange: (Int) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = when (currentMode) {
                    TimerMode.COUNTDOWN -> "设置倒计时时长"
                    TimerMode.POMODORO -> "设置番茄钟时长"
                    else -> "设置时长"
                }
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when (currentMode) {
                    TimerMode.COUNTDOWN -> {
                        TimeSettingItem(
                            label = "倒计时时长",
                            value = countdownMinutes,
                            onValueChange = onCountdownMinutesChange,
                            range = 1..120
                        )
                    }
                    TimerMode.POMODORO -> {
                        TimeSettingItem(
                            label = "工作时长",
                            value = pomodoroWorkMinutes,
                            onValueChange = onPomodoroWorkMinutesChange,
                            range = 5..60
                        )
                        TimeSettingItem(
                            label = "短休息时长",
                            value = pomodoroShortBreakMinutes,
                            onValueChange = onPomodoroShortBreakMinutesChange,
                            range = 1..30
                        )
                        TimeSettingItem(
                            label = "长休息时长",
                            value = pomodoroLongBreakMinutes,
                            onValueChange = onPomodoroLongBreakMinutesChange,
                            range = 5..60
                        )
                    }
                    else -> {
                        Text("正向计时无需设置时长")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

/**
 * 时长设置项
 */
@Composable
private fun TimeSettingItem(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange
) {
    Column {
        Text(
            text = "$label: $value 分钟",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 减少按钮
            IconButton(
                onClick = { 
                    if (value > range.first) {
                        onValueChange(value - 1)
                    }
                },
                enabled = value > range.first
            ) {
                Icon(Icons.Default.Remove, contentDescription = "减少")
            }
            
            // 显示当前值
            Text(
                text = "$value",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.width(40.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            // 增加按钮
            IconButton(
                onClick = { 
                    if (value < range.last) {
                        onValueChange(value + 1)
                    }
                },
                enabled = value < range.last
            ) {
                Icon(Icons.Default.Add, contentDescription = "增加")
            }
        }
    }
}



