package com.anou.pagegather.ui.feature.timer

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType

/**
 * 正向计时器实现 - 混合架构版本
 * 
 * 特点：
 * - 从 00:00 开始向上计时
 * - 可设置目标时间作为提醒
 * - 可以超过目标时间继续计时
 * - 记录实际阅读时长
 * - 基于 BaseTimerScreen 架构，代码复用率高
 */
class ForwardTimerScreenImpl : BaseTimerScreen() {
    
    override fun getScreenTitle(state: BaseTimerState): String = "阅读计时"
    
    override fun handleTimerTick(
        state: BaseTimerState,
        config: TimerConfig,
        hapticFeedback: androidx.compose.ui.hapticfeedback.HapticFeedback
    ): BaseTimerState {
        val newTime = state.currentTime + 1000
        
        return state.copy(
            currentTime = newTime
        )
    }
    
    override fun getDisplayTime(state: BaseTimerState, config: TimerConfig): Long {
        return state.currentTime
    }
    
    override fun getStatusText(state: BaseTimerState): String {
        return when {
            state.isRunning && !state.isPaused -> "计时中"
            state.isPaused -> "已暂停"
            state.currentTime > 0 -> "已停止"
            else -> "准备开始"
        }
    }
    
    @Composable
    override fun getStatusColor(state: BaseTimerState): Color {
        return when {
            state.isRunning && !state.isPaused -> MaterialTheme.colorScheme.primary
            state.isPaused -> MaterialTheme.colorScheme.secondary
            else -> MaterialTheme.colorScheme.onSurfaceVariant
        }
    }
    
    override fun getCustomContent(state: BaseTimerState, config: TimerConfig): @Composable (() -> Unit)? {
        // 正向计时器不需要额外的自定义内容，保持简洁
        return null
    }
}

/**
 * 正向计时器 - 极简版本
 * 
 * 特点：
 * - 直接开始计时，无需设置
 * - 纯粹记录阅读时长
 * - 简单直接的用户体验
 */
@Composable
fun ForwardTimerScreen(
    entryContext: TimerEntryContext? = null,
    onNavigateBack: () -> Unit = {},
    onTimerComplete: (TimerResult) -> Unit = {}
) {
    val timerImpl = remember { ForwardTimerScreenImpl() }
    val config = remember { 
        TimerConfig(
            mode = TimerMode.FORWARD,
            targetDuration = 0L // 正向计时不需要目标时间
        )
    }
    
    timerImpl.TimerScreen(
        config = config,
        entryContext = entryContext,
        onNavigateBack = onNavigateBack,
        onTimerComplete = onTimerComplete
    )
}