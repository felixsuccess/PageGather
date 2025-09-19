package com.anou.pagegather.domain.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anou.pagegather.ui.theme.AppTheme
import com.anou.pagegather.ui.theme.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 主题管理器 ViewModel
 * 为 UI 层提供主题管理功能的 ViewModel 包装
 */
@HiltViewModel
class ThemeManagerViewModel @Inject constructor(
    private val themeManager: ThemeManager
) : ViewModel() {
    
    /**
     * 当前主题状态
     */
    val currentTheme: StateFlow<AppTheme> = themeManager.currentTheme
    
    /**
     * 当前主题模式状态
     */
    val themeMode: StateFlow<ThemeMode> = themeManager.themeMode
    
    /**
     * 当前是否为暗色模式
     */
    val isDarkMode: StateFlow<Boolean> = themeManager.isDarkMode
    
    /**
     * 设置主题
     */
    fun setTheme(theme: AppTheme) {
        viewModelScope.launch {
            themeManager.setTheme(theme)
        }
    }
    
    /**
     * 设置主题模式
     */
    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            themeManager.setThemeMode(mode)
        }
    }
    
    /**
     * 处理系统配置变化
     */
    fun onConfigurationChanged() {
        themeManager.onConfigurationChanged()
    }
    
    /**
     * 获取当前选中的主题
     */
    fun getCurrentTheme(): AppTheme = currentTheme.value
    
    /**
     * 获取当前主题模式
     */
    fun getCurrentThemeMode(): ThemeMode = themeMode.value
    
    /**
     * 检查是否为暗色模式
     */
    fun isDarkModeActive(): Boolean = isDarkMode.value
}