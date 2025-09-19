package com.anou.pagegather.domain.theme

import android.content.Context
import android.content.res.Configuration
import com.anou.pagegather.data.preferences.ThemePreferences
import com.anou.pagegather.ui.theme.AppTheme
import com.anou.pagegather.ui.theme.ThemeMode
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
    

    
    init {
        loadSavedPreferences()
        observeSystemDarkMode()
    }
    
    /**
     * 设置主题
     */
    suspend fun setTheme(theme: AppTheme) {
        try {
            _currentTheme.value = theme
            themePreferences.saveTheme(theme)
            android.util.Log.d("ThemeManager", "Theme changed to: ${theme.displayName}")
        } catch (e: Exception) {
            android.util.Log.e("ThemeManager", "Failed to set theme: ${theme.displayName}", e)
            // 保持当前主题不变，不抛出异常以保持应用稳定性
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
            android.util.Log.e("ThemeManager", "Failed to set theme mode: ${mode.displayName}", e)
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
    }
    
    /**
     * 监听系统暗色模式变化
     */
    private fun observeSystemDarkMode() {
        // 初始化暗色模式状态
        updateDarkMode()
        
        // 注意：这里简化了系统暗色模式监听
        // 在实际应用中，可能需要使用 Configuration 变化监听器
        // 或者在 Activity 的 onConfigurationChanged 中调用 updateDarkMode()
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
}

