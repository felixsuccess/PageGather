package com.anou.pagegather.ui.theme

import android.util.Log

/**
 * 主题系统错误处理器
 * 提供统一的错误处理、日志记录和回退机制
 */
object ThemeErrorHandler {
    
    private const val TAG = "ThemeSystem"
    
    // 错误统计
    private var errorCount = 0
    private val errorHistory = mutableListOf<ThemeError>()
    
    /**
     * 处理主题加载错误
     */
    fun handleThemeLoadError(theme: AppTheme, isDark: Boolean, error: Throwable): AppTheme {
        val themeError = ThemeError(
            type = ThemeErrorType.THEME_LOAD_FAILED,
            message = "Failed to load theme: ${theme.displayName} (${if (isDark) "dark" else "light"})",
            theme = theme,
            isDark = isDark,
            exception = error,
            timestamp = System.currentTimeMillis()
        )
        
        recordError(themeError)
        
        // 回退到默认主题
        return AppTheme.getDefault().also {
            Log.w(TAG, "Theme load failed, falling back to default theme: ${it.displayName}", error)
        }
    }
    
    /**
     * 处理主题切换错误
     */
    fun handleThemeSwitchError(fromTheme: AppTheme, toTheme: AppTheme, error: Throwable): Boolean {
        val themeError = ThemeError(
            type = ThemeErrorType.THEME_SWITCH_FAILED,
            message = "Failed to switch theme from ${fromTheme.displayName} to ${toTheme.displayName}",
            theme = toTheme,
            isDark = null,
            exception = error,
            timestamp = System.currentTimeMillis()
        )
        
        recordError(themeError)
        
        Log.e(TAG, "Theme switch failed", error)
        return false
    }
    
    /**
     * 处理主题保存错误
     */
    fun handleThemeSaveError(theme: AppTheme, error: Throwable) {
        val themeError = ThemeError(
            type = ThemeErrorType.THEME_SAVE_FAILED,
            message = "Failed to save theme: ${theme.displayName}",
            theme = theme,
            isDark = null,
            exception = error,
            timestamp = System.currentTimeMillis()
        )
        
        recordError(themeError)
        
        Log.e(TAG, "Theme save failed", error)
    }
    
    /**
     * 处理颜色方案创建错误
     */
    fun handleColorSchemeError(theme: AppTheme, isDark: Boolean, error: Throwable) {
        val themeError = ThemeError(
            type = ThemeErrorType.COLOR_SCHEME_FAILED,
            message = "Failed to create color scheme for ${theme.displayName} (${if (isDark) "dark" else "light"})",
            theme = theme,
            isDark = isDark,
            exception = error,
            timestamp = System.currentTimeMillis()
        )
        
        recordError(themeError)
        
        Log.e(TAG, "Color scheme creation failed", error)
    }
    
    /**
     * 处理缓存错误
     */
    fun handleCacheError(operation: String, error: Throwable) {
        val themeError = ThemeError(
            type = ThemeErrorType.CACHE_ERROR,
            message = "Cache operation failed: $operation",
            theme = null,
            isDark = null,
            exception = error,
            timestamp = System.currentTimeMillis()
        )
        
        recordError(themeError)
        
        Log.w(TAG, "Cache error in operation: $operation", error)
    }
    
    /**
     * 记录错误
     */
    private fun recordError(error: ThemeError) {
        errorCount++
        errorHistory.add(error)
        
        // 保持错误历史记录在合理范围内
        if (errorHistory.size > 50) {
            errorHistory.removeAt(0)
        }
        
        // 如果错误过多，清除缓存重试
        if (errorCount % 10 == 0) {
            Log.w(TAG, "Multiple theme errors detected ($errorCount), clearing cache")
            try {
                ThemeCache.clearCache()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to clear cache during error recovery", e)
            }
        }
    }
    
    /**
     * 获取错误统计
     */
    fun getErrorStats(): ThemeErrorStats {
        val recentErrors = errorHistory.filter { 
            System.currentTimeMillis() - it.timestamp < 60_000 // 最近1分钟
        }
        
        val errorsByType = errorHistory.groupBy { it.type }
            .mapValues { it.value.size }
        
        return ThemeErrorStats(
            totalErrors = errorCount,
            recentErrors = recentErrors.size,
            errorsByType = errorsByType,
            lastError = errorHistory.lastOrNull()
        )
    }
    
    /**
     * 清除错误历史
     */
    fun clearErrorHistory() {
        errorHistory.clear()
        errorCount = 0
        Log.i(TAG, "Theme error history cleared")
    }
    
    /**
     * 检查系统健康状态
     */
    fun checkSystemHealth(): ThemeSystemHealth {
        val stats = getErrorStats()
        val cacheStats = ThemeCache.getCacheStats()
        
        val healthScore = when {
            stats.recentErrors == 0 && cacheStats.hitRate > 80f -> ThemeSystemHealth.EXCELLENT
            stats.recentErrors <= 2 && cacheStats.hitRate > 60f -> ThemeSystemHealth.GOOD
            stats.recentErrors <= 5 && cacheStats.hitRate > 40f -> ThemeSystemHealth.FAIR
            else -> ThemeSystemHealth.POOR
        }
        
        return healthScore
    }
}

/**
 * 主题错误类型
 */
enum class ThemeErrorType {
    THEME_LOAD_FAILED,      // 主题加载失败
    THEME_SWITCH_FAILED,    // 主题切换失败
    THEME_SAVE_FAILED,      // 主题保存失败
    COLOR_SCHEME_FAILED,    // 颜色方案创建失败
    CACHE_ERROR             // 缓存错误
}

/**
 * 主题错误数据类
 */
data class ThemeError(
    val type: ThemeErrorType,
    val message: String,
    val theme: AppTheme?,
    val isDark: Boolean?,
    val exception: Throwable,
    val timestamp: Long
)

/**
 * 主题错误统计
 */
data class ThemeErrorStats(
    val totalErrors: Int,
    val recentErrors: Int,
    val errorsByType: Map<ThemeErrorType, Int>,
    val lastError: ThemeError?
) {
    override fun toString(): String {
        return """
            主题系统错误统计:
            - 总错误数: $totalErrors
            - 最近错误数: $recentErrors
            - 按类型分组: $errorsByType
            - 最后错误: ${lastError?.message ?: "无"}
        """.trimIndent()
    }
}

/**
 * 主题系统健康状态
 */
enum class ThemeSystemHealth {
    EXCELLENT,  // 优秀
    GOOD,       // 良好
    FAIR,       // 一般
    POOR        // 较差
}