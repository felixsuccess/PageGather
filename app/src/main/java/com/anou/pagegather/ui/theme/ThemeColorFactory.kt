package com.anou.pagegather.ui.theme

import androidx.compose.material3.ColorScheme
import com.anou.pagegather.ui.theme.colors.*

/**
 * 主题颜色工厂
 * 根据主题和模式获取对应的颜色方案
 */

/**
 * 获取指定主题的颜色方案（带缓存）
 */
fun getColorSchemeForTheme(theme: AppTheme, isDark: Boolean): ColorScheme {
    return ThemeCache.getColorScheme(theme, isDark)
}

/**
 * 获取指定主题的颜色方案（内部实现，不使用缓存）
 */
internal fun getColorSchemeForThemeInternal(theme: AppTheme, isDark: Boolean): ColorScheme {
    return try {
        when (theme) {
            AppTheme.ELEGANT_WHITE -> if (isDark) ElegantWhiteColors.DarkColorScheme else ElegantWhiteColors.LightColorScheme
            AppTheme.HUNDI_ORANGE -> if (isDark) HundiOrangeColors.DarkColorScheme else HundiOrangeColors.LightColorScheme
            AppTheme.HUNDI_GREEN -> if (isDark) HundiGreenColors.DarkColorScheme else HundiGreenColors.LightColorScheme
            AppTheme.HUNDI_BLUE -> if (isDark) HundiBlueColors.DarkColorScheme else HundiBlueColors.LightColorScheme
            AppTheme.HUNDI_PURPLE -> if (isDark) HundiPurpleColors.DarkColorScheme else HundiPurpleColors.LightColorScheme
        }
    } catch (e: Exception) {
        android.util.Log.e("ThemeColorFactory", "Failed to get color scheme for theme $theme", e)
        // 回退到默认主题
        if (isDark) ElegantWhiteColors.DarkColorScheme else ElegantWhiteColors.LightColorScheme
    }
}

/**
 * 获取指定主题的扩展颜色（简化版本，直接返回Material扩展颜色）
 */
fun getExtendedColorsForTheme(theme: AppTheme, isDark: Boolean): com.anou.pagegather.ui.theme.ExtendedColors {
    return try {
        val simpleColors = when (theme) {
            AppTheme.ELEGANT_WHITE -> ElegantWhiteColors.getExtendedColors(isDark)
            AppTheme.HUNDI_ORANGE -> HundiOrangeColors.getExtendedColors(isDark)
            AppTheme.HUNDI_GREEN -> HundiGreenColors.getExtendedColors(isDark)
            AppTheme.HUNDI_BLUE -> HundiBlueColors.getExtendedColors(isDark)
            AppTheme.HUNDI_PURPLE -> HundiPurpleColors.getExtendedColors(isDark)
        }
        
        // 转换为 Material 扩展颜色
        com.anou.pagegather.ui.theme.ExtendedColors(
            // 容器颜色 - 使用合理的默认值
            primaryContainer = androidx.compose.ui.graphics.Color.Transparent,
            secondaryContainer = androidx.compose.ui.graphics.Color.Transparent,
            tertiaryContainer = androidx.compose.ui.graphics.Color.Transparent,
            
            // 状态颜色
            success = simpleColors.success,
            error = androidx.compose.ui.graphics.Color(0xFFE57373),
            warning = simpleColors.warning,
            info = simpleColors.info,
            
            // 文字颜色层次 - 使用合理的默认值
            titleColor = androidx.compose.ui.graphics.Color(0xFF212121),
            bodyColor = androidx.compose.ui.graphics.Color(0xFF212121),
            subtitleColor = androidx.compose.ui.graphics.Color(0xFF757575),
            descriptionColor = androidx.compose.ui.graphics.Color(0xFF9E9E9E),
            
            // 功能颜色
            accentColor = simpleColors.gradientStart,
            bookmarkColor = simpleColors.bookmarkColor,
            readingProgress = simpleColors.readingProgress,
            noteHighlight = simpleColors.noteHighlight,
            
            // 渐变颜色
            gradientStart = simpleColors.gradientStart,
            gradientEnd = simpleColors.gradientEnd,
            gradientSecondary = simpleColors.gradientEnd,
            
            // 中性色 - 使用合理的默认值
            neutral100 = androidx.compose.ui.graphics.Color(0xFFF5F5F5),
            neutral200 = androidx.compose.ui.graphics.Color(0xFFEEEEEE),
            neutral300 = androidx.compose.ui.graphics.Color(0xFFE0E0E0),
            neutral500 = androidx.compose.ui.graphics.Color(0xFF9E9E9E),
            neutral700 = androidx.compose.ui.graphics.Color(0xFF616161),
            neutral900 = androidx.compose.ui.graphics.Color(0xFF212121),
            
            // 边框和分割线
            borderColor = androidx.compose.ui.graphics.Color(0xFFE0E0E0),
            dividerColor = androidx.compose.ui.graphics.Color(0xFFEEEEEE),
            shadowColor = androidx.compose.ui.graphics.Color(0x1A000000)
        )
    } catch (e: Exception) {
        android.util.Log.e("ThemeColorFactory", "Failed to get extended colors for theme $theme", e)
        // 回退到默认扩展颜色
        getExtendedColorsForTheme(AppTheme.getDefault(), isDark)
    }
}