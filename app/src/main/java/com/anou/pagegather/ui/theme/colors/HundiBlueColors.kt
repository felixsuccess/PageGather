package com.anou.pagegather.ui.theme.colors

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * Hundi 蓝色主题颜色定义
 * 专业冷静，商务风格的蓝色主题
 */
object HundiBlueColors {
    
    // 主色调 - 专业蓝色系
    private val Primary = Color(0xFF2196F3)           // 主蓝色
    private val PrimaryVariant = Color(0xFF42A5F5)    // 浅蓝色变体
    private val PrimaryDark = Color(0xFF1976D2)       // 深蓝色
    
    // 辅助色调 - 深灰色
    private val Secondary = Color(0xFF37474F)         // 深蓝灰色
    private val SecondaryVariant = Color(0xFF546E7A)  // 中蓝灰色
    private val SecondaryLight = Color(0xFF90A4AE)    // 浅蓝灰色
    
    // 强调色 - 青色
    private val Accent = Color(0xFF00BCD4)           // 青色强调
    private val AccentLight = Color(0xFF4DD0E1)      // 浅青色
    
    // 状态色
    private val Success = Color(0xFF4CAF50)          // 成功绿色
    private val Error = Color(0xFFE57373)            // 柔和红色
    private val Warning = Color(0xFFFF9800)          // 橙色警告
    private val Info = Color(0xFF2196F3)             // 信息蓝色
    
    /**
     * Hundi 蓝色亮色主题
     */
    val LightColorScheme = lightColorScheme(
        // 主色系
        primary = Primary,
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFE3F2FD),
        onPrimaryContainer = Color(0xFF0D47A1),
        
        // 辅助色系
        secondary = Secondary,
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFFECEFF1),
        onSecondaryContainer = Color(0xFF263238),
        
        // 第三色系
        tertiary = Accent,
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Color(0xFFE0F7FA),
        onTertiaryContainer = Color(0xFF006064),
        
        // 背景色系
        background = Color(0xFFFAFAFA),
        onBackground = Color(0xFF212121),
        surface = Color(0xFFFFFFFF),
        onSurface = Color(0xFF212121),
        surfaceVariant = Color(0xFFE8F4FD),
        onSurfaceVariant = Color(0xFF424242),
        
        // 错误色系
        error = Error,
        onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFFFFEBEE),
        onErrorContainer = Color(0xFFB71C1C),
        
        // 轮廓和其他
        outline = Color(0xFFBBDEFB),
        outlineVariant = Color(0xFFE3F2FD),
        scrim = Color(0xFF000000),
        inverseSurface = Color(0xFF2E2E2E),
        inverseOnSurface = Color(0xFFF5F5F5),
        inversePrimary = PrimaryVariant
    )
    
    /**
     * Hundi 蓝色暗色主题
     */
    val DarkColorScheme = darkColorScheme(
        // 主色系
        primary = PrimaryVariant,
        onPrimary = Color(0xFF0D47A1),
        primaryContainer = Color(0xFF1976D2),
        onPrimaryContainer = Color(0xFFBBDEFB),
        
        // 辅助色系
        secondary = SecondaryLight,
        onSecondary = Color(0xFF263238),
        secondaryContainer = Color(0xFF37474F),
        onSecondaryContainer = Color(0xFFCFD8DC),
        
        // 第三色系
        tertiary = AccentLight,
        onTertiary = Color(0xFF006064),
        tertiaryContainer = Color(0xFF00838F),
        onTertiaryContainer = Color(0xFFB2EBF2),
        
        // 背景色系
        background = Color(0xFF121212),
        onBackground = Color(0xFFE0E0E0),
        surface = Color(0xFF1E1E1E),
        onSurface = Color(0xFFE0E0E0),
        surfaceVariant = Color(0xFF2C3A42),
        onSurfaceVariant = Color(0xFFBDBDBD),
        
        // 错误色系
        error = Color(0xFFEF5350),
        onError = Color(0xFF000000),
        errorContainer = Color(0xFF8E0000),
        onErrorContainer = Color(0xFFFFDAD6),
        
        // 轮廓和其他
        outline = Color(0xFF4A5A6A),
        outlineVariant = Color(0xFF2C3A42),
        scrim = Color(0xFF000000),
        inverseSurface = Color(0xFFE0E0E0),
        inverseOnSurface = Color(0xFF2E2E2E),
        inversePrimary = Primary
    )
    
    /**
     * Hundi 蓝色主题的扩展颜色
     */
    fun getExtendedColors(isDark: Boolean): com.anou.pagegather.ui.theme.ExtendedColors {
        return if (isDark) {
            com.anou.pagegather.ui.theme.ExtendedColors(
                // 容器颜色
                primaryContainer = Color(0xFF0D47A1),
                secondaryContainer = Color(0xFF1C2328),
                tertiaryContainer = Color(0xFF2C3A42),
                
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
                noteHighlight = Color(0xFF2C3A42),
                
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
                primaryContainer = Color(0xFFBBDEFB),
                secondaryContainer = Color(0xFFCFD8DC),
                tertiaryContainer = Color(0xFFE3F2FD),
                
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
                noteHighlight = Color(0xFFE3F2FD),
                
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