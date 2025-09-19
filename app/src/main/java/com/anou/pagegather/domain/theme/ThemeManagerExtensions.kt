package com.anou.pagegather.domain.theme

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anou.pagegather.ui.theme.AppTheme
import com.anou.pagegather.ui.theme.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 主题管理器 ViewModel
 * 简化版本，只提供基本的主题管理功能
 */
@HiltViewModel
class ThemeManagerViewModel @Inject constructor(
    private val themeManager: ThemeManager
) : ViewModel() {
    
    val currentTheme = themeManager.currentTheme
    val themeMode = themeManager.themeMode
    val isDarkMode = themeManager.isDarkMode
    
    fun setTheme(theme: AppTheme) {
        viewModelScope.launch {
            themeManager.setTheme(theme)
        }
    }
    
    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            themeManager.setThemeMode(mode)
        }
    }
}