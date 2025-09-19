package com.anou.pagegather.ui.theme.colors

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * 典雅白主题颜色定义
 * 简洁优雅，极简设计的白色主题
 */
object ElegantWhiteColors {
    
    // 主色调 - 优雅灰色系
    private val Primary = Color(0xFF37474F)           // 深蓝灰色
    private val PrimaryVariant = Color(0xFF546E7A)    // 中蓝灰色
    private val PrimaryLight = Color(0xFF90A4AE)      // 浅蓝灰色
    
    // 辅助色调 - 中性灰色
    private val Secondary = Color(0xFF616161)         // 中灰色
    private val SecondaryVariant = Color(0xFF757575)  // 浅中灰色
    private val SecondaryLight = Color(0xFF9E9E9E)    // 浅灰色
    
    // 强调色 - 精致蓝色
    private val Accent = Color(0xFF5C6BC0)           // 优雅蓝色
    private val AccentLight = Color(0xFF9FA8DA)      // 浅优雅蓝色
    
    // 状态色
    private val Success = Color(0xFF66BB6A)          // 柔和绿色
    private val Error = Color(0xFFEF5350)            // 柔和红色
    private val Warning = Color(0xFFFF9800)          // 橙色警告
    private val Info = Color(0xFF42A5F5)             // 信息蓝色
    
    /**
     * 典雅白亮色主题
     */
    val LightColorScheme = lightColorScheme(
        // 主色系
        primary = Primary,
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFECEFF1),
        onPrimaryContainer = Color(0xFF263238),
        
        // 辅助色系
        secondary = Secondary,
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFFF5F5F5),
        onSecondaryContainer = Color(0xFF424242),
        
        // 第三色系
        tertiary = Accent,
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Color(0xFFE8EAF6),
        onTertiaryContainer = Color(0xFF3F51B5),
        
        // 背景色系
        background = Color(0xFFFFFFFF),
        onBackground = Color(0xFF212121),
        surface = Color(0xFFFAFAFA),
        onSurface = Color(0xFF212121),
        surfaceVariant = Color(0xFFF5F5F5),
        onSurfaceVariant = Color(0xFF757575),
        
        // 错误色系
        error = Error,
        onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFFFFEBEE),
        onErrorContainer = Color(0xFFB71C1C),
        
        // 轮廓和其他
        outline = Color(0xFFE0E0E0),
        outlineVariant = Color(0xFFEEEEEE),
        scrim = Color(0xFF000000),
        inverseSurface = Color(0xFF2E2E2E),
        inverseOnSurface = Color(0xFFF5F5F5),
        inversePrimary = PrimaryLight
    )
    
    /**
     * 典雅白暗色主题
     */
    val DarkColorScheme = darkColorScheme(
        // 主色系
        primary = PrimaryLight,
        onPrimary = Color(0xFF263238),
        primaryContainer = Color(0xFF37474F),
        onPrimaryContainer = Color(0xFFCFD8DC),
        
        // 辅助色系
        secondary = SecondaryLight,
        onSecondary = Color(0xFF424242),
        secondaryContainer = Color(0xFF616161),
        onSecondaryContainer = Color(0xFFE0E0E0),
        
        // 第三色系
        tertiary = AccentLight,
        onTertiary = Color(0xFF3F51B5),
        tertiaryContainer = Color(0xFF5C6BC0),
        onTertiaryContainer = Color(0xFFE8EAF6),
        
        // 背景色系
        background = Color(0xFF121212),
        onBackground = Color(0xFFE0E0E0),
        surface = Color(0xFF1E1E1E),
        onSurface = Color(0xFFE0E0E0),
        surfaceVariant = Color(0xFF2C2C2C),
        onSurfaceVariant = Color(0xFFBDBDBD),
        
        // 错误色系
        error = Color(0xFFEF5350),
        onError = Color(0xFF000000),
        errorContainer = Color(0xFF8E0000),
        onErrorContainer = Color(0xFFFFDAD6),
        
        // 轮廓和其他
        outline = Color(0xFF424242),
        outlineVariant = Color(0xFF2C2C2C),
        scrim = Color(0xFF000000),
        inverseSurface = Color(0xFFE0E0E0),
        inverseOnSurface = Color(0xFF2E2E2E),
        inversePrimary = Primary
    )
    
    /**
     * 典雅白主题的扩展颜色
     */
    fun getExtendedColors(isDark: Boolean) = if (isDark) {
        ExtendedColors(
            success = Success,
            warning = Warning,
            info = Info,
            bookmarkColor = Accent,
            readingProgress = Success,
            noteHighlight = Color(0xFF4A4A4A),
            gradientStart = Primary,
            gradientEnd = PrimaryLight
        )
    } else {
        ExtendedColors(
            success = Success,
            warning = Warning,
            info = Info,
            bookmarkColor = Accent,
            readingProgress = Success,
            noteHighlight = Color(0xFFFFF9C4),
            gradientStart = Primary,
            gradientEnd = PrimaryLight
        )
    }
}

/**
 * 扩展颜色数据类
 */
data class ExtendedColors(
    val success: Color,
    val warning: Color,
    val info: Color,
    val bookmarkColor: Color,
    val readingProgress: Color,
    val noteHighlight: Color,
    val gradientStart: Color,
    val gradientEnd: Color
)