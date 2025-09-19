package com.anou.pagegather.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * 应用主题枚举
 * 定义所有可用的主题选项
 */
enum class AppTheme(
    val id: String,
    val displayName: String,
    val description: String,
    val primaryColor: Color,
    val emoji: String,
    val isDefault: Boolean = false
) {
    ELEGANT_WHITE(
        id = "elegant_white",
        displayName = "典雅白",
        description = "简洁优雅，极简设计的白色主题",
        primaryColor = Color(0xFF37474F),
        emoji = "🤍",
        isDefault = true
    ),
    
    HUNDI_ORANGE(
        id = "hundi_orange",
        displayName = "Hundi 橙色",
        description = "温暖活力，充满能量的橙色主题",
        primaryColor = Color(0xFFFF6B35),
        emoji = "🧡"
    ),
    
    HUNDI_GREEN(
        id = "hundi_green", 
        displayName = "Hundi 绿色",
        description = "自然清新，护眼舒适的绿色主题",
        primaryColor = Color(0xFF4CAF50),
        emoji = "💚"
    ),
    
    HUNDI_BLUE(
        id = "hundi_blue",
        displayName = "Hundi 蓝色", 
        description = "专业冷静，商务风格的蓝色主题",
        primaryColor = Color(0xFF2196F3),
        emoji = "💙"
    ),
    
    HUNDI_PURPLE(
        id = "hundi_purple",
        displayName = "Hundi 紫色",
        description = "优雅神秘，创意灵感的紫色主题", 
        primaryColor = Color(0xFF9C27B0),
        emoji = "💜"
    );
    
    companion object {
        /**
         * 根据 ID 获取主题
         */
        fun fromId(id: String): AppTheme {
            return values().find { it.id == id } ?: getDefault()
        }
        
        /**
         * 获取所有主题列表
         */
        fun getAllThemes(): List<AppTheme> = values().toList()
        
        /**
         * 获取默认主题
         */
        fun getDefault(): AppTheme = values().find { it.isDefault } ?: ELEGANT_WHITE
    }
}

/**
 * 主题模式枚举
 * 定义亮色/暗色模式选项
 */
enum class ThemeMode(val id: String, val displayName: String) {
    LIGHT("light", "亮色模式"),
    DARK("dark", "暗色模式"),
    SYSTEM("system", "跟随系统");
    
    companion object {
        fun fromId(id: String): ThemeMode {
            return values().find { it.id == id } ?: SYSTEM
        }
        
        fun getDefault(): ThemeMode = SYSTEM
    }
}

/**
 * 主题配置数据类
 */
data class ThemeConfig(
    val theme: AppTheme,
    val mode: ThemeMode,
    val isDarkMode: Boolean
)

/**
 * 主题选择状态
 */
data class ThemeSelectionUiState(
    val availableThemes: List<AppTheme> = AppTheme.getAllThemes(),
    val currentTheme: AppTheme = AppTheme.getDefault(),
    val currentMode: ThemeMode = ThemeMode.getDefault(),
    val isDarkMode: Boolean = false,
    val isLoading: Boolean = false
)