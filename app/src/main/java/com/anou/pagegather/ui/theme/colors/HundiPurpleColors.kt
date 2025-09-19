package com.anou.pagegather.ui.theme.colors

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * Hundi 紫色主题颜色定义
 * 优雅神秘，创意灵感的紫色主题
 */
object HundiPurpleColors {
    
    // 主色调 - 优雅紫色系
    private val Primary = Color(0xFF9C27B0)           // 主紫色
    private val PrimaryVariant = Color(0xFFBA68C8)    // 浅紫色变体
    private val PrimaryDark = Color(0xFF7B1FA2)       // 深紫色
    
    // 辅助色调 - 深灰色
    private val Secondary = Color(0xFF37474F)         // 深蓝灰色
    private val SecondaryVariant = Color(0xFF546E7A)  // 中蓝灰色
    private val SecondaryLight = Color(0xFF90A4AE)    // 浅蓝灰色
    
    // 强调色 - 深紫色
    private val Accent = Color(0xFF673AB7)           // 深紫色强调
    private val AccentLight = Color(0xFF9575CD)      // 浅深紫色
    
    // 状态色
    private val Success = Color(0xFF4CAF50)          // 成功绿色
    private val Error = Color(0xFFE57373)            // 柔和红色
    private val Warning = Color(0xFFFF9800)          // 橙色警告
    private val Info = Color(0xFF2196F3)             // 信息蓝色
    
    /**
     * Hundi 紫色亮色主题
     */
    val LightColorScheme = lightColorScheme(
        // 主色系
        primary = Primary,
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFF3E5F5),
        onPrimaryContainer = Color(0xFF4A148C),
        
        // 辅助色系
        secondary = Secondary,
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFFECEFF1),
        onSecondaryContainer = Color(0xFF263238),
        
        // 第三色系
        tertiary = Accent,
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Color(0xFFEDE7F6),
        onTertiaryContainer = Color(0xFF311B92),
        
        // 背景色系
        background = Color(0xFFFAFAFA),
        onBackground = Color(0xFF212121),
        surface = Color(0xFFFFFFFF),
        onSurface = Color(0xFF212121),
        surfaceVariant = Color(0xFFF8F5F9),
        onSurfaceVariant = Color(0xFF424242),
        
        // 错误色系
        error = Error,
        onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFFFFEBEE),
        onErrorContainer = Color(0xFFB71C1C),
        
        // 轮廓和其他
        outline = Color(0xFFE1BEE7),
        outlineVariant = Color(0xFFF3E5F5),
        scrim = Color(0xFF000000),
        inverseSurface = Color(0xFF2E2E2E),
        inverseOnSurface = Color(0xFFF5F5F5),
        inversePrimary = PrimaryVariant
    )
    
    /**
     * Hundi 紫色暗色主题
     */
    val DarkColorScheme = darkColorScheme(
        // 主色系
        primary = PrimaryVariant,
        onPrimary = Color(0xFF4A148C),
        primaryContainer = Color(0xFF7B1FA2),
        onPrimaryContainer = Color(0xFFE1BEE7),
        
        // 辅助色系
        secondary = SecondaryLight,
        onSecondary = Color(0xFF263238),
        secondaryContainer = Color(0xFF37474F),
        onSecondaryContainer = Color(0xFFCFD8DC),
        
        // 第三色系
        tertiary = AccentLight,
        onTertiary = Color(0xFF311B92),
        tertiaryContainer = Color(0xFF512DA8),
        onTertiaryContainer = Color(0xFFD1C4E9),
        
        // 背景色系
        background = Color(0xFF121212),
        onBackground = Color(0xFFE0E0E0),
        surface = Color(0xFF1E1E1E),
        onSurface = Color(0xFFE0E0E0),
        surfaceVariant = Color(0xFF3A2C3E),
        onSurfaceVariant = Color(0xFFBDBDBD),
        
        // 错误色系
        error = Color(0xFFEF5350),
        onError = Color(0xFF000000),
        errorContainer = Color(0xFF8E0000),
        onErrorContainer = Color(0xFFFFDAD6),
        
        // 轮廓和其他
        outline = Color(0xFF5A4A5E),
        outlineVariant = Color(0xFF3A2C3E),
        scrim = Color(0xFF000000),
        inverseSurface = Color(0xFFE0E0E0),
        inverseOnSurface = Color(0xFF2E2E2E),
        inversePrimary = Primary
    )
    
    /**
     * Hundi 紫色主题的扩展颜色
     */
    fun getExtendedColors(isDark: Boolean): com.anou.pagegather.ui.theme.ExtendedColors {
        return if (isDark) {
            com.anou.pagegather.ui.theme.ExtendedColors(
                // 容器颜色
                primaryContainer = Color(0xFF4A148C),
                secondaryContainer = Color(0xFF1C2328),
                tertiaryContainer = Color(0xFF3A2C3E),
                
                // 状态颜色
                success = Success,
                error = Error,
                warning = Warning,
                info = Info,
                
                // 文字颜色层次
                titleColor = Color(0xFFFFFFFF),
                bodyColor = Color(0xFFE0E0E0),
                subtitleColor = Color(0xFFCFD8DC),
                descriptionColor = Color(0xFFBDBDBD),
                
                // 功能颜色
                accentColor = Primary,
                bookmarkColor = Primary,
                readingProgress = Primary,
                noteHighlight = Color(0xFF3A2C3E),
                
                // 渐变颜色
                gradientStart = Primary,
                gradientEnd = Accent,
                gradientSecondary = Secondary,
                
                // 中性色
                neutral100 = Color(0xFF2C2C2C),
                neutral200 = Color(0xFF424242),
                neutral300 = Color(0xFF616161),
                neutral500 = Color(0xFF9E9E9E),
                neutral700 = Color(0xFFBDBDBD),
                neutral900 = Color(0xFFE0E0E0),
                
                // 边框和分割线
                borderColor = Color(0xFF424242),
                dividerColor = Color(0xFF2C2C2C),
                shadowColor = Color(0x1A000000)
            )
        } else {
            com.anou.pagegather.ui.theme.ExtendedColors(
                // 容器颜色
                primaryContainer = Color(0xFFE1BEE7),
                secondaryContainer = Color(0xFFCFD8DC),
                tertiaryContainer = Color(0xFFF3E5F5),
                
                // 状态颜色
                success = Success,
                error = Error,
                warning = Warning,
                info = Info,
                
                // 文字颜色层次
                titleColor = Color(0xFF212121),
                bodyColor = Color(0xFF424242),
                subtitleColor = Color(0xFF757575),
                descriptionColor = Color(0xFF9E9E9E),
                
                // 功能颜色
                accentColor = Primary,
                bookmarkColor = Primary,
                readingProgress = Primary,
                noteHighlight = Color(0xFFF3E5F5),
                
                // 渐变颜色
                gradientStart = Primary,
                gradientEnd = Accent,
                gradientSecondary = Secondary,
                
                // 中性色
                neutral100 = Color(0xFFF5F5F5),
                neutral200 = Color(0xFFEEEEEE),
                neutral300 = Color(0xFFE0E0E0),
                neutral500 = Color(0xFF9E9E9E),
                neutral700 = Color(0xFF616161),
                neutral900 = Color(0xFF212121),
                
                // 边框和分割线
                borderColor = Color(0xFFE0E0E0),
                dividerColor = Color(0xFFEEEEEE),
                shadowColor = Color(0x1A000000)
            )
        }
    }
}