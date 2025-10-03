package com.anou.pagegather.ui.feature.timer

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.unit.dp

/**
 * 倒计时器实现
 * 
 * 特点：
 * - 从设定时间开始向下倒计时
 * - 时间到达 00:00 自动结束
 * - 适合限制阅读时间
 * - 强制时间管理
 */
class CountdownTimerScreenImpl : BaseTimerScreen() {
    
    override fun getScreenTitle(state: BaseTimerState): String = "限时阅读"
    
    override fun handleTimerTick(
        state: BaseTimerState,
        config: TimerConfig,
        hapticFeedback: androidx.compose.ui.hapticfeedback.HapticFeedback
    ): BaseTimerState {
        val newTime = state.currentTime + 1000
        val remaining = config.targetDuration - newTime
        
        // 倒计时结束
        if (remaining <= 0) {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
            return state.copy(
                currentTime = config.targetDuration,
                isCompleted = true,
                isRunning = false
            )
        }
        
        // 最后10秒每秒震动提醒
        if (remaining <= 10000 && remaining % 1000 == 0L) {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
        
        return state.copy(currentTime = newTime)
    }
    
    override fun getDisplayTime(state: BaseTimerState, config: TimerConfig): Long {
        // 显示剩余时间
        return (config.targetDuration - state.currentTime).coerceAtLeast(0L)
    }
    
    override fun getStatusText(state: BaseTimerState): String {
        val remaining = getDisplayTime(state, TimerConfig(TimerMode.COUNTDOWN, state.targetTime))
        
        return when {
            state.isCompleted -> "时间到！"
            state.isRunning && !state.isPaused -> {
                if (remaining <= 10000) "即将结束！" else "倒计时中"
            }
            state.isPaused -> "已暂停"
            state.currentTime > 0 -> "已停止"
            else -> "准备开始"
        }
    }
    
    @Composable
    override fun getStatusColor(state: BaseTimerState): Color {
        val remaining = getDisplayTime(state, TimerConfig(TimerMode.COUNTDOWN, state.targetTime))
        
        return when {
            state.isCompleted -> MaterialTheme.colorScheme.error
            remaining <= 10000 && state.isRunning -> MaterialTheme.colorScheme.error // 最后10秒红色警告
            remaining <= 60000 && state.isRunning -> MaterialTheme.colorScheme.tertiary // 最后1分钟橙色提醒
            state.isRunning && !state.isPaused -> MaterialTheme.colorScheme.primary
            state.isPaused -> MaterialTheme.colorScheme.secondary
            else -> MaterialTheme.colorScheme.onSurfaceVariant
        }
    }
    
    override fun getCustomContent(state: BaseTimerState, config: TimerConfig): @Composable (() -> Unit)? {
        return {
            val remaining = getDisplayTime(state, config)
            val progress = ((config.targetDuration - remaining).toFloat() / config.targetDuration.toFloat() * 100).toInt()
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "已用时间: ${(progress)}%",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            // 时间警告
            if (remaining <= 60000 && remaining > 0 && state.isRunning) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (remaining <= 10000) "⚠️ 即将结束" else "⏰ 最后一分钟",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (remaining <= 10000) MaterialTheme.colorScheme.error 
                           else MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}

/**
 * 倒计时器主入口（带时长设置弹窗）
 */
@Composable
fun CountdownTimerScreen(
    entryContext: TimerEntryContext? = null,
    onNavigateBack: () -> Unit = {},
    onTimerComplete: (TimerResult) -> Unit = {}
) {
    var selectedMinutes by remember { mutableStateOf(25) }
    var showTimeSetup by remember { mutableStateOf(true) }
    
    if (showTimeSetup) {
        // 时长设置弹窗
        TimeSetupDialog(
            selectedMinutes = selectedMinutes,
            onMinutesChange = { selectedMinutes = it },
            onConfirm = { showTimeSetup = false },
            onDismiss = onNavigateBack
        )
    } else {
        // 实际的倒计时器
        CountdownTimerWithDuration(
            duration = selectedMinutes * 60 * 1000L,
            entryContext = entryContext,
            onNavigateBack = onNavigateBack,
            onTimerComplete = onTimerComplete
        )
    }
}

/**
 * 时长设置弹窗
 */
@Composable
private fun TimeSetupDialog(
    selectedMinutes: Int,
    onMinutesChange: (Int) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("设置倒计时时长") },
        text = {
            Column {
                Text("选择倒计时时长：${selectedMinutes} 分钟")
                Spacer(modifier = Modifier.height(16.dp))
                
                Slider(
                    value = selectedMinutes.toFloat(),
                    onValueChange = { onMinutesChange(it.toInt()) },
                    valueRange = 5f..120f,
                    steps = 22
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("5分钟", style = MaterialTheme.typography.bodySmall)
                    Text("2小时", style = MaterialTheme.typography.bodySmall)
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 快捷选择
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(15, 25, 45, 60).forEach { minutes ->
                        FilterChip(
                            onClick = { onMinutesChange(minutes) },
                            label = { Text("${minutes}分钟") },
                            selected = selectedMinutes == minutes,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("开始倒计时")
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
 * 带时长参数的倒计时器（内部实现）
 */
@Composable
private fun CountdownTimerWithDuration(
    duration: Long, // 倒计时时长（毫秒）
    entryContext: TimerEntryContext? = null,
    onNavigateBack: () -> Unit = {},
    onTimerComplete: (TimerResult) -> Unit = {}
) {
    val timerImpl = remember { CountdownTimerScreenImpl() }
    val config = remember(duration) { 
        TimerConfig(
            mode = TimerMode.COUNTDOWN,
            targetDuration = duration
        )
    }
    
    timerImpl.TimerScreen(
        config = config,
        entryContext = entryContext,
        onNavigateBack = onNavigateBack,
        onTimerComplete = onTimerComplete
    )
}