package com.anou.pagegather.ui.feature.timer

import androidx.compose.runtime.Composable

/**
 * 计时器屏幕工厂
 * 
 * 提供统一的计时器创建接口，根据模式和配置创建相应的计时器
 */
object TimerScreenFactory {
    
    /**
     * 创建正向计时器
     */
    @Composable
    fun CreateForwardTimer(
        entryContext: TimerEntryContext? = null,
        onNavigateBack: () -> Unit = {},
        onTimerComplete: (TimerResult) -> Unit = {}
    ) {
        ForwardTimerScreen(
            entryContext = entryContext,
            onNavigateBack = onNavigateBack,
            onTimerComplete = onTimerComplete
        )
    }
    
    /**
     * 创建倒计时器
     */
    @Composable
    fun CreateCountdownTimer(
        entryContext: TimerEntryContext? = null,
        onNavigateBack: () -> Unit = {},
        onTimerComplete: (TimerResult) -> Unit = {}
    ) {
        CountdownTimerScreen(
            entryContext = entryContext,
            onNavigateBack = onNavigateBack,
            onTimerComplete = onTimerComplete
        )
    }
    
    /**
     * 创建番茄钟计时器
     */
    @Composable
    fun CreatePomodoroTimer(
        entryContext: TimerEntryContext? = null,
        onNavigateBack: () -> Unit = {},
        onTimerComplete: (TimerResult) -> Unit = {}
    ) {
        PomodoroTimerScreen(
            entryContext = entryContext,
            onNavigateBack = onNavigateBack,
            onTimerComplete = onTimerComplete
        )
    }
    
    /**
     * 根据模式创建计时器
     */
    @Composable
    fun CreateTimer(
        mode: TimerMode,
        config: TimerScreenConfig,
        entryContext: TimerEntryContext? = null,
        onNavigateBack: () -> Unit = {},
        onTimerComplete: (TimerResult) -> Unit = {}
    ) {
        when (mode) {
            TimerMode.FORWARD -> CreateForwardTimer(
                entryContext = entryContext,
                onNavigateBack = onNavigateBack,
                onTimerComplete = onTimerComplete
            )
            
            TimerMode.COUNTDOWN -> CreateCountdownTimer(
                entryContext = entryContext,
                onNavigateBack = onNavigateBack,
                onTimerComplete = onTimerComplete
            )
            
            TimerMode.POMODORO -> CreatePomodoroTimer(
                entryContext = entryContext,
                onNavigateBack = onNavigateBack,
                onTimerComplete = onTimerComplete
            )
        }
    }
}

/**
 * 计时器屏幕配置
 */
data class TimerScreenConfig(
    // 倒计时配置
    val countdownMinutes: Int = 30,
    
    // 番茄钟配置
    val pomodoroWorkMinutes: Int = 25,
    val pomodoroShortBreakMinutes: Int = 5,
    val pomodoroLongBreakMinutes: Int = 15,
    val autoStartBreaks: Boolean = false,
    val autoStartWork: Boolean = false
) {
    companion object {
        /**
         * 默认正向计时配置（无需配置）
         */
        fun forwardTimer() = TimerScreenConfig()
        
        /**
         * 默认倒计时配置
         */
        fun countdownTimer(minutes: Int = 30) = TimerScreenConfig(
            countdownMinutes = minutes
        )
        
        /**
         * 默认番茄钟配置
         */
        fun pomodoroTimer(
            workMinutes: Int = 25,
            shortBreakMinutes: Int = 5,
            longBreakMinutes: Int = 15,
            autoStart: Boolean = false
        ) = TimerScreenConfig(
            pomodoroWorkMinutes = workMinutes,
            pomodoroShortBreakMinutes = shortBreakMinutes,
            pomodoroLongBreakMinutes = longBreakMinutes,
            autoStartBreaks = autoStart,
            autoStartWork = autoStart
        )
    }
}

/**
 * 便捷的计时器创建扩展函数
 */
@Composable
fun TimerMode.CreateScreen(
    config: TimerScreenConfig = TimerScreenConfig(),
    entryContext: TimerEntryContext? = null,
    onNavigateBack: () -> Unit = {},
    onTimerComplete: (TimerResult) -> Unit = {}
) {
    TimerScreenFactory.CreateTimer(
        mode = this,
        config = config,
        entryContext = entryContext,
        onNavigateBack = onNavigateBack,
        onTimerComplete = onTimerComplete
    )
}