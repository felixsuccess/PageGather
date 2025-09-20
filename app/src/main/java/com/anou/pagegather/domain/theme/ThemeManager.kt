package com.anou.pagegather.domain.theme

import android.content.Context
import android.content.res.Configuration
import com.anou.pagegather.data.preferences.ThemePreferences
import com.anou.pagegather.ui.theme.AppTheme
import com.anou.pagegather.ui.theme.CacheStats
import com.anou.pagegather.ui.theme.ThemeCache
import com.anou.pagegather.ui.theme.ThemeErrorHandler
import com.anou.pagegather.ui.theme.ThemeErrorStats
import com.anou.pagegather.ui.theme.ThemeMode
import com.anou.pagegather.ui.theme.ThemeSystemHealth
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 主题管理器
 * 负责管理应用的主题状态、主题切换和持久化存储
 */
@Singleton
class ThemeManager @Inject constructor(
    private val themePreferences: ThemePreferences,
    @ApplicationContext private val context: Context
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    // 当前主题状态
    private val _currentTheme = MutableStateFlow(AppTheme.getDefault())
    val currentTheme = _currentTheme.asStateFlow()
    
    // 当前主题模式状态
    private val _themeMode = MutableStateFlow(ThemeMode.getDefault())
    val themeMode = _themeMode.asStateFlow()
    
    // 当前是否为暗色模式
    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode = _isDarkMode.asStateFlow()
    
    // 是否使用动态颜色
    private val _useDynamicColor = MutableStateFlow(false)
    val useDynamicColor = _useDynamicColor.asStateFlow()
    
    init {
        loadSavedPreferences()
        observeSystemDarkMode()
        preloadThemes()
    }
    
    /**
     * 设置主题
     */
    suspend fun setTheme(theme: AppTheme) {
        val currentTheme = _currentTheme.value
        try {
            _currentTheme.value = theme
            themePreferences.saveTheme(theme)
            android.util.Log.d("ThemeManager", "Theme changed to: ${theme.displayName}")
        } catch (e: Exception) {
            // 使用错误处理器处理主题切换错误
            ThemeErrorHandler.handleThemeSwitchError(currentTheme, theme, e)
            
            // 尝试回退到默认主题
            val fallbackTheme = ThemeErrorHandler.handleThemeLoadError(theme, _isDarkMode.value, e)
            if (fallbackTheme != theme) {
                try {
                    _currentTheme.value = fallbackTheme
                    themePreferences.saveTheme(fallbackTheme)
                } catch (fallbackError: Exception) {
                    android.util.Log.e("ThemeManager", "Failed to set fallback theme", fallbackError)
                }
            }
        }
    }
    
    /**
     * 设置主题模式
     */
    suspend fun setThemeMode(mode: ThemeMode) {
        try {
            _themeMode.value = mode
            themePreferences.saveThemeMode(mode)
            updateDarkMode()
            android.util.Log.d("ThemeManager", "Theme mode changed to: ${mode.displayName}")
        } catch (e: Exception) {
            ThemeErrorHandler.handleThemeSaveError(_currentTheme.value, e)
            // 保持当前模式不变
        }
    }
    
    /**
     * 设置动态颜色偏好
     */
    suspend fun setDynamicColor(enabled: Boolean) {
        try {
            _useDynamicColor.value = enabled
            themePreferences.saveDynamicColor(enabled)
            android.util.Log.d("ThemeManager", "Dynamic color preference changed to: $enabled")
        } catch (e: Exception) {
            ThemeErrorHandler.handleThemeSaveError(_currentTheme.value, e)
            // 保持当前设置不变
        }
    }
    
    /**
     * 检查是否为系统暗色模式
     */
    fun isSystemInDarkTheme(): Boolean {
        return when (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            Configuration.UI_MODE_NIGHT_NO -> false
            else -> false
        }
    }
    
    /**
     * 加载保存的偏好设置
     */
    private fun loadSavedPreferences() {
        scope.launch {
            try {
                // 加载保存的主题
                themePreferences.getTheme()
                    .catch { exception ->
                        android.util.Log.e("ThemeManager", "Failed to load saved theme", exception)
                        emit(AppTheme.getDefault())
                    }
                    .collect { theme ->
                        _currentTheme.value = theme
                    }
            } catch (e: Exception) {
                android.util.Log.e("ThemeManager", "Failed to load theme preferences", e)
                _currentTheme.value = AppTheme.getDefault()
            }
        }
        
        scope.launch {
            try {
                // 加载保存的主题模式
                themePreferences.getThemeMode()
                    .catch { exception ->
                        android.util.Log.e("ThemeManager", "Failed to load saved theme mode", exception)
                        emit(ThemeMode.getDefault())
                    }
                    .collect { mode ->
                        _themeMode.value = mode
                        updateDarkMode()
                    }
            } catch (e: Exception) {
                android.util.Log.e("ThemeManager", "Failed to load theme mode preferences", e)
                _themeMode.value = ThemeMode.getDefault()
                updateDarkMode()
            }
        }
        
        scope.launch {
            try {
                // 加载保存的动态颜色偏好设置
                themePreferences.getDynamicColor()
                    .catch { exception ->
                        android.util.Log.e("ThemeManager", "Failed to load dynamic color preference", exception)
                        emit(false)
                    }
                    .collect { useDynamicColor ->
                        _useDynamicColor.value = useDynamicColor
                    }
            } catch (e: Exception) {
                android.util.Log.e("ThemeManager", "Failed to load dynamic color preference", e)
                _useDynamicColor.value = false
            }
        }
    }
    
    /**
     * 监听系统暗色模式变化
     */
    private fun observeSystemDarkMode() {
        // 初始化暗色模式状态
        updateDarkMode()
        
        // 系统暗色模式变化将通过 MainActivity 的 onConfigurationChanged 
        // 和 Compose 的 isSystemInDarkTheme() 自动处理
    }
    
    /**
     * 更新暗色模式状态
     */
    private fun updateDarkMode() {
        val isDark = when (_themeMode.value) {
            ThemeMode.LIGHT -> false
            ThemeMode.DARK -> true
            ThemeMode.SYSTEM -> isSystemInDarkTheme()
        }
        _isDarkMode.value = isDark
    }
    
    /**
     * 处理系统配置变化
     * 应该在 Activity 的 onConfigurationChanged 中调用
     */
    fun onConfigurationChanged() {
        if (_themeMode.value == ThemeMode.SYSTEM) {
            updateDarkMode()
        }
    }
    
    /**
     * 预加载所有主题以提升性能
     */
    private fun preloadThemes() {
        scope.launch {
            try {
                ThemeCache.preloadAllThemes()
                android.util.Log.d("ThemeManager", "All themes preloaded successfully")
            } catch (e: Exception) {
                android.util.Log.e("ThemeManager", "Failed to preload themes", e)
            }
        }
    }
    
    /**
     * 获取缓存统计信息
     */
    fun getCacheStats(): CacheStats {
        return ThemeCache.getCacheStats()
    }
    
    /**
     * 获取错误统计信息
     */
    fun getErrorStats(): ThemeErrorStats {
        return ThemeErrorHandler.getErrorStats()
    }
    
    /**
     * 检查系统健康状态
     */
    fun getSystemHealth(): ThemeSystemHealth {
        return ThemeErrorHandler.checkSystemHealth()
    }
    
    /**
     * 清除错误历史
     */
    fun clearErrorHistory() {
        ThemeErrorHandler.clearErrorHistory()
    }
}