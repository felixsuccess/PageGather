package com.anou.pagegather.ui.feature.timer

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.unit.dp

/**
 * 番茄钟计时器实现
 * 
 * 特点：
 * - 25分钟工作 + 5分钟短休息 + 15分钟长休息（每4轮后）
 * - 自动切换工作和休息模式
 * - 专注力训练
 * - 规律休息提醒
 */
class PomodoroTimerScreenImpl : BaseTimerScreen() {
    
    override fun getScreenTitle(state: BaseTimerState): String {
        return when (state.pomodoroPhase) {
            PomodoroPhase.WORK -> "专注阅读"
            PomodoroPhase.SHORT_BREAK -> "短暂休息"
            PomodoroPhase.LONG_BREAK -> "长时间休息"
        }
    }
    
    override fun handleTimerTick(
        state: BaseTimerState,
        config: TimerConfig,
        hapticFeedback: androidx.compose.ui.hapticfeedback.HapticFeedback
    ): BaseTimerState {
        val newTime = state.currentTime + 1000
        val phaseComplete = newTime >= state.targetTime
        
        if (phaseComplete) {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
            
            return when (state.pomodoroPhase) {
                PomodoroPhase.WORK -> {
                    // 工作完成，进入休息
                    val isLongBreak = state.pomodoroRound % 4 == 0
                    state.copy(
                        currentTime = 0L,
                        pomodoroPhase = if (isLongBreak) PomodoroPhase.LONG_BREAK else PomodoroPhase.SHORT_BREAK,
                        targetTime = if (isLongBreak) config.pomodoroLongBreak else config.pomodoroShortBreak,
                        isRunning = config.autoStartBreaks
                    )
                }
                PomodoroPhase.SHORT_BREAK, PomodoroPhase.LONG_BREAK -> {
                    // 休息完成，进入工作
                    state.copy(
                        currentTime = 0L,
                        pomodoroPhase = PomodoroPhase.WORK,
                        targetTime = config.pomodoroWorkTime,
                        pomodoroRound = state.pomodoroRound + 1,
                        isRunning = config.autoStartWork
                    )
                }
            }
        }
        
        // 最后10秒提醒
        val remaining = state.targetTime - newTime
        if (remaining <= 10000 && remaining % 1000 == 0L && remaining > 0) {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
        
        return state.copy(currentTime = newTime)
    }
    
    override fun getDisplayTime(state: BaseTimerState, config: TimerConfig): Long {
        // 显示剩余时间
        return (state.targetTime - state.currentTime).coerceAtLeast(0L)
    }
    
    override fun getStatusText(state: BaseTimerState): String {
        val remaining = getDisplayTime(state, TimerConfig(TimerMode.POMODORO))
        
        return when {
            state.isRunning && !state.isPaused -> {
                when (state.pomodoroPhase) {
                    PomodoroPhase.WORK -> if (remaining <= 10000) "即将休息！" else "专注中"
                    PomodoroPhase.SHORT_BREAK -> if (remaining <= 10000) "准备工作！" else "休息中"
                    PomodoroPhase.LONG_BREAK -> if (remaining <= 10000) "准备工作！" else "长休息中"
                }
            }
            state.isPaused -> "已暂停"
            state.currentTime > 0 -> "已停止"
            else -> when (state.pomodoroPhase) {
                PomodoroPhase.WORK -> "准备专注"
                PomodoroPhase.SHORT_BREAK -> "准备休息"
                PomodoroPhase.LONG_BREAK -> "准备长休息"
            }
        }
    }
    
    @Composable
    override fun getStatusColor(state: BaseTimerState): Color {
        val remaining = getDisplayTime(state, TimerConfig(TimerMode.POMODORO))
        
        return when {
            remaining <= 10000 && state.isRunning -> MaterialTheme.colorScheme.tertiary // 最后10秒提醒
            state.isRunning && !state.isPaused -> {
                when (state.pomodoroPhase) {
                    PomodoroPhase.WORK -> MaterialTheme.colorScheme.primary
                    PomodoroPhase.SHORT_BREAK -> MaterialTheme.colorScheme.secondary
                    PomodoroPhase.LONG_BREAK -> MaterialTheme.colorScheme.tertiary
                }
            }
            state.isPaused -> MaterialTheme.colorScheme.outline
            else -> MaterialTheme.colorScheme.onSurfaceVariant
        }
    }
    
    override fun getCustomContent(state: BaseTimerState, config: TimerConfig): @Composable (() -> Unit)? {
        return {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 番茄钟轮次显示
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = when (state.pomodoroPhase) {
                            PomodoroPhase.WORK -> MaterialTheme.colorScheme.primaryContainer
                            PomodoroPhase.SHORT_BREAK -> MaterialTheme.colorScheme.secondaryContainer
                            PomodoroPhase.LONG_BREAK -> MaterialTheme.colorScheme.tertiaryContainer
                        }
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = when (state.pomodoroPhase) {
                                PomodoroPhase.WORK -> Icons.Default.MenuBook
                                PomodoroPhase.SHORT_BREAK -> Icons.Default.Coffee
                                PomodoroPhase.LONG_BREAK -> Icons.Default.RestaurantMenu
                            },
                            contentDescription = null,
                            tint = when (state.pomodoroPhase) {
                                PomodoroPhase.WORK -> MaterialTheme.colorScheme.onPrimaryContainer
                                PomodoroPhase.SHORT_BREAK -> MaterialTheme.colorScheme.onSecondaryContainer
                                PomodoroPhase.LONG_BREAK -> MaterialTheme.colorScheme.onTertiaryContainer
                            }
                        )
                        
                        Text(
                            text = "第 ${state.pomodoroRound} 轮",
                            style = MaterialTheme.typography.titleSmall,
                            color = when (state.pomodoroPhase) {
                                PomodoroPhase.WORK -> MaterialTheme.colorScheme.onPrimaryContainer
                                PomodoroPhase.SHORT_BREAK -> MaterialTheme.colorScheme.onSecondaryContainer
                                PomodoroPhase.LONG_BREAK -> MaterialTheme.colorScheme.onTertiaryContainer
                            }
                        )
                    }
                }
                
                // 阶段说明
                Text(
                    text = when (state.pomodoroPhase) {
                        PomodoroPhase.WORK -> "📚 专注阅读时间"
                        PomodoroPhase.SHORT_BREAK -> "☕ 短暂休息时间"
                        PomodoroPhase.LONG_BREAK -> "🛋️ 长时间休息"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                // 下一阶段预告
                if (state.isRunning) {
                    val nextPhase = when (state.pomodoroPhase) {
                        PomodoroPhase.WORK -> {
                            if (state.pomodoroRound % 4 == 0) "长休息" else "短休息"
                        }
                        PomodoroPhase.SHORT_BREAK, PomodoroPhase.LONG_BREAK -> "专注阅读"
                    }
                    
                    Text(
                        text = "下一阶段: $nextPhase",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * 番茄钟计时器主入口（带设置弹窗）
 */
@Composable
fun PomodoroTimerScreen(
    entryContext: TimerEntryContext? = null,
    onNavigateBack: () -> Unit = {},
    onTimerComplete: (TimerResult) -> Unit = {}
) {
    var workMinutes by remember { mutableStateOf(25) }
    var shortBreakMinutes by remember { mutableStateOf(5) }
    var longBreakMinutes by remember { mutableStateOf(15) }
    var autoStartBreaks by remember { mutableStateOf(false) }
    var autoStartWork by remember { mutableStateOf(false) }
    var showSetup by remember { mutableStateOf(true) }
    
    if (showSetup) {
        // 番茄钟设置弹窗
        PomodoroSetupDialog(
            workMinutes = workMinutes,
            shortBreakMinutes = shortBreakMinutes,
            longBreakMinutes = longBreakMinutes,
            autoStartBreaks = autoStartBreaks,
            autoStartWork = autoStartWork,
            onWorkMinutesChange = { workMinutes = it },
            onShortBreakChange = { shortBreakMinutes = it },
            onLongBreakChange = { longBreakMinutes = it },
            onAutoStartBreaksChange = { autoStartBreaks = it },
            onAutoStartWorkChange = { autoStartWork = it },
            onConfirm = { showSetup = false },
            onDismiss = onNavigateBack
        )
    } else {
        // 实际的番茄钟计时器
        PomodoroTimerWithConfig(
            workMinutes = workMinutes,
            shortBreakMinutes = shortBreakMinutes,
            longBreakMinutes = longBreakMinutes,
            autoStartBreaks = autoStartBreaks,
            autoStartWork = autoStartWork,
            entryContext = entryContext,
            onNavigateBack = onNavigateBack,
            onTimerComplete = onTimerComplete
        )
    }
}

/**
 * 番茄钟设置弹窗
 */
@Composable
private fun PomodoroSetupDialog(
    workMinutes: Int,
    shortBreakMinutes: Int,
    longBreakMinutes: Int,
    autoStartBreaks: Boolean,
    autoStartWork: Boolean,
    onWorkMinutesChange: (Int) -> Unit,
    onShortBreakChange: (Int) -> Unit,
    onLongBreakChange: (Int) -> Unit,
    onAutoStartBreaksChange: (Boolean) -> Unit,
    onAutoStartWorkChange: (Boolean) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("番茄钟设置") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 工作时间设置
                Text("工作时间：${workMinutes} 分钟", style = MaterialTheme.typography.titleSmall)
                Slider(
                    value = workMinutes.toFloat(),
                    onValueChange = { onWorkMinutesChange(it.toInt()) },
                    valueRange = 15f..60f,
                    steps = 8
                )
                
                // 短休息时间设置
                Text("短休息：${shortBreakMinutes} 分钟", style = MaterialTheme.typography.titleSmall)
                Slider(
                    value = shortBreakMinutes.toFloat(),
                    onValueChange = { onShortBreakChange(it.toInt()) },
                    valueRange = 3f..15f,
                    steps = 11
                )
                
                // 长休息时间设置
                Text("长休息：${longBreakMinutes} 分钟", style = MaterialTheme.typography.titleSmall)
                Slider(
                    value = longBreakMinutes.toFloat(),
                    onValueChange = { onLongBreakChange(it.toInt()) },
                    valueRange = 10f..30f,
                    steps = 19
                )
                
                // 自动启动选项
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("自动开始休息")
                    Switch(
                        checked = autoStartBreaks,
                        onCheckedChange = onAutoStartBreaksChange
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("自动开始工作")
                    Switch(
                        checked = autoStartWork,
                        onCheckedChange = onAutoStartWorkChange
                    )
                }
                
                // 快捷预设
                Text("快捷预设：", style = MaterialTheme.typography.titleSmall)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        onClick = { 
                            onWorkMinutesChange(25)
                            onShortBreakChange(5)
                            onLongBreakChange(15)
                        },
                        label = { Text("经典") },
                        selected = workMinutes == 25 && shortBreakMinutes == 5 && longBreakMinutes == 15
                    )
                    FilterChip(
                        onClick = { 
                            onWorkMinutesChange(45)
                            onShortBreakChange(10)
                            onLongBreakChange(20)
                        },
                        label = { Text("长专注") },
                        selected = workMinutes == 45 && shortBreakMinutes == 10 && longBreakMinutes == 20
                    )
                    FilterChip(
                        onClick = { 
                            onWorkMinutesChange(15)
                            onShortBreakChange(3)
                            onLongBreakChange(10)
                        },
                        label = { Text("短专注") },
                        selected = workMinutes == 15 && shortBreakMinutes == 3 && longBreakMinutes == 10
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("开始番茄钟")
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
 * 带配置的番茄钟计时器（内部实现）
 */
@Composable
private fun PomodoroTimerWithConfig(
    workMinutes: Int,
    shortBreakMinutes: Int,
    longBreakMinutes: Int,
    autoStartBreaks: Boolean,
    autoStartWork: Boolean,
    entryContext: TimerEntryContext? = null,
    onNavigateBack: () -> Unit = {},
    onTimerComplete: (TimerResult) -> Unit = {}
) {
    val timerImpl = remember { PomodoroTimerScreenImpl() }
    val config = remember(workMinutes, shortBreakMinutes, longBreakMinutes, autoStartBreaks, autoStartWork) { 
        TimerConfig(
            mode = TimerMode.POMODORO,
            pomodoroWorkTime = workMinutes * 60 * 1000L,
            pomodoroShortBreak = shortBreakMinutes * 60 * 1000L,
            pomodoroLongBreak = longBreakMinutes * 60 * 1000L,
            autoStartBreaks = autoStartBreaks,
            autoStartWork = autoStartWork
        )
    }
    
    timerImpl.TimerScreen(
        config = config,
        entryContext = entryContext,
        onNavigateBack = onNavigateBack,
        onTimerComplete = onTimerComplete
    )
}