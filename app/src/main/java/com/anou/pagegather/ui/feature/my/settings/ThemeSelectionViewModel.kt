package com.anou.pagegather.ui.feature.my.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anou.pagegather.domain.theme.ThemeManager
import com.anou.pagegather.ui.theme.AppTheme
import com.anou.pagegather.ui.theme.ThemeMode
import com.anou.pagegather.ui.theme.ThemeSelectionUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 主题选择 ViewModel
 * 管理主题选择界面的状态和业务逻辑
 */
@HiltViewModel
class ThemeSelectionViewModel @Inject constructor(
    private val themeManager: ThemeManager
) : ViewModel() {
    
    // UI 状态
    private val _uiState = MutableStateFlow(ThemeSelectionUiState())
    val uiState = _uiState.asStateFlow()
    
    init {
        // 监听主题管理器的状态变化
        observeThemeChanges()
    }
    
    /**
     * 选择主题
     */
    fun selectTheme(theme: AppTheme) {
        viewModelScope.launch {
            try {
                // 设置加载状态
                _uiState.value = _uiState.value.copy(isLoading = true)
                
                // 应用主题
                themeManager.setTheme(theme)
                
                Log.d("ThemeSelectionViewModel", "Theme selected: ${theme.displayName}")
            } catch (e: Exception) {
                Log.e("ThemeSelectionViewModel", "Failed to select theme: ${theme.displayName}", e)
            } finally {
                // 清除加载状态
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }
    
    /**
     * 选择主题模式
     */
    fun selectThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            try {
                // 设置加载状态
                _uiState.value = _uiState.value.copy(isLoading = true)
                
                // 应用主题模式
                themeManager.setThemeMode(mode)
                
                Log.d("ThemeSelectionViewModel", "Theme mode selected: ${mode.displayName}")
            } catch (e: Exception) {
                Log.e("ThemeSelectionViewModel", "Failed to select theme mode: ${mode.displayName}", e)
            } finally {
                // 清除加载状态
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }
    
    /**
     * 设置动态颜色偏好
     */
    fun setDynamicColor(enabled: Boolean) {
        viewModelScope.launch {
            try {
                // 设置加载状态
                _uiState.value = _uiState.value.copy(isLoading = true)
                
                // 应用动态颜色偏好
                themeManager.setDynamicColor(enabled)
                
                Log.d("ThemeSelectionViewModel", "Dynamic color preference set to: $enabled")
            } catch (e: Exception) {
                Log.e("ThemeSelectionViewModel", "Failed to set dynamic color preference", e)
            } finally {
                // 清除加载状态
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }
    
    /**
     * 预览主题
     */
    fun previewTheme(theme: AppTheme) {
        // 立即预览主题，不保存到偏好设置
        selectTheme(theme)
    }
    
    /**
     * 获取当前选中的主题
     */
    fun getCurrentTheme(): AppTheme {
        return _uiState.value.currentTheme
    }
    
    /**
     * 获取当前主题模式
     */
    fun getCurrentThemeMode(): ThemeMode {
        return _uiState.value.currentMode
    }
    
    /**
     * 检查主题是否被选中
     */
    fun isThemeSelected(theme: AppTheme): Boolean {
        return _uiState.value.currentTheme == theme
    }
    
    /**
     * 检查主题模式是否被选中
     */
    fun isThemeModeSelected(mode: ThemeMode): Boolean {
        return _uiState.value.currentMode == mode
    }
    
    /**
     * 监听主题管理器的状态变化
     */
    private fun observeThemeChanges() {
        viewModelScope.launch {
            // 合并所有主题相关的状态流
            combine(
                themeManager.currentTheme,
                themeManager.themeMode,
                themeManager.isDarkMode,
                themeManager.useDynamicColor
            ) { theme, mode, isDark, useDynamicColor ->
                Quadruple(theme, mode, isDark, useDynamicColor)
            }.collect { (theme, mode, isDark, useDynamicColor) ->
                // 更新 UI 状态
                _uiState.value = _uiState.value.copy(
                    currentTheme = theme,
                    currentMode = mode,
                    isDarkMode = isDark,
                    useDynamicColor = useDynamicColor,
                    availableThemes = AppTheme.getAllThemes()
                )
            }
        }
    }
    
    /**
     * 四元组数据类，用于合并多个流
     */
    private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
}