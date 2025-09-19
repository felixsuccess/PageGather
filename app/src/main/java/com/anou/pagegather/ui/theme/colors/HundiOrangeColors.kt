package com.anou.pagegather.ui.theme.colors

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * Hundi 橙色主题颜色定义
 * 温暖活力，充满能量的橙色主题
 * 基于现有的 Color.kt 文件
 */
object HundiOrangeColors {
    
    // 主色调 - 温暖橙色系 (来自现有 Color.kt)
    private val Primary = Color(0xFFFF6B35)           // 主橙色 - 温暖活力
    private val PrimaryVariant = Color(0xFFFF8A65)    // 浅橙色变体
    private val PrimaryDark = Color(0xFFE65100)       // 深橙色
    
    // 辅助色调 - 现代灰色系
    private val Secondary = Color(0xFF37474F)         // 深蓝灰色
    private val SecondaryVariant = Color(0xFF546E7A)  // 中蓝灰色
    private val SecondaryLight = Color(0xFF90A4AE)    // 浅蓝灰色
    
    // 强调色 - 活力色彩
    private val Accent = Color(0xFFFFAB40)           // 琥珀色强调
    private val AccentSecondary = Color(0xFF26C6DA)   // 青色强调
    
    // 状态色系统
    private val Success = Color(0xFF4CAF50)           // 成功 - 绿色
    private val Error = Color(0xFFE57373)             // 错误 - 柔和红色
    private val Warning = Color(0xFFFFB74D)           // 警告 - 橙黄色
    private val Info = Color(0xFF64B5F6)              // 信息 - 蓝色
    
    /**
     * Hundi 橙色亮色主题
     */
    val LightColorScheme = lightColorScheme(
        // 主色系
        primary = Primary,
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFFFE0B2),
        onPrimaryContainer = Color(0xFF4A2C17),
        
        // 辅助色系
        secondary = Secondary,
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFFECEFF1),
        onSecondaryContainer = Color(0xFF1C2328),
        
        // 第三色系
        tertiary = Accent,
        onTertiary = Color(0xFF000000),
        tertiaryContainer = Color(0xFFFFF3E0),
        onTertiaryContainer = Color(0xFF4A3A00),
        
        // 背景色系
        background = Color(0xFFFAFAFA),
        onBackground = Color(0xFF212121),
        surface = Color(0xFFFFFFFF),
        onSurface = Color(0xFF212121),
        surfaceVariant = Color(0xFFFFF8F5),
        onSurfaceVariant = Color(0xFF757575),
        
        // 错误色系
        error = Error,
        onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFFFFDAD6),
        onErrorContainer = Color(0xFF4A1C1C),
        
        // 轮廓和其他
        outline = Color(0xFFE0E0E0),
        outlineVariant = Color(0xFFEEEEEE),
        scrim = Color(0xFF000000),
        inverseSurface = Color(0xFF2E3132),
        inverseOnSurface = Color(0xFFE1E3E3),
        inversePrimary = Color(0xFFFFB59D)
    )
    
    /**
     * Hundi 橙色暗色主题
     */
    val DarkColorScheme = darkColorScheme(
        // 主色系
        primary = Primary,
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFF4A2C17),
        onPrimaryContainer = Color(0xFFFFDCC7),
        
        // 辅助色系
        secondary = Secondary,
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFF1C2328),
        onSecondaryContainer = Color(0xFFCFD8DC),
        
        // 第三色系
        tertiary = Accent,
        onTertiary = Color(0xFF000000),
        tertiaryContainer = Color(0xFF4A3A00),
        onTertiaryContainer = Color(0xFFFFE082),
        
        // 背景色系
        background = Color(0xFF121212),
        onBackground = Color(0xFFFFFFFF),
        surface = Color(0xFF1E1E1E),
        onSurface = Color(0xFFFFFFFF),
        surfaceVariant = Color(0xFF2C2C2C),
        onSurfaceVariant = Color(0xFFCFD8DC),
        
        // 错误色系
        error = Error,
        onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFF4A1C1C),
        onErrorContainer = Color(0xFFFFDAD6),
        
        // 轮廓和其他
        outline = Color(0xFF8F9699),
        outlineVariant = Color(0xFF3F484A),
        scrim = Color(0xFF000000),
        inverseSurface = Color(0xFFE1E3E3),
        inverseOnSurface = Color(0xFF2E3132),
        inversePrimary = Primary
    )
    
    /**
     * Hundi 橙色主题的扩展颜色
     */
    fun getExtendedColors(isDark: Boolean) = ExtendedColors(
        success = Success,
        warning = Warning,
        info = Info,
        bookmarkColor = Primary,
        readingProgress = Success,
        noteHighlight = if (isDark) Color(0xFF4A3A00) else Color(0xFFFFF59D),
        gradientStart = Primary,
        gradientEnd = Accent
    )
}