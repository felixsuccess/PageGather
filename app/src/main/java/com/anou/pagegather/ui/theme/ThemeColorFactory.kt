package com.anou.pagegather.ui.theme

import androidx.compose.material3.ColorScheme
import com.anou.pagegather.ui.theme.colors.*

/**
 * 主题颜色工厂
 * 根据主题和模式获取对应的颜色方案
 */

/**
 * 获取指定主题的颜色方案
 */
fun getColorSchemeForTheme(theme: AppTheme, isDark: Boolean): ColorScheme {
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
 * 获取指定主题的扩展颜色
 */
fun getExtendedColorsForTheme(theme: AppTheme, isDark: Boolean): com.anou.pagegather.ui.theme.ExtendedColors {
    return try {
        when (theme) {
            AppTheme.ELEGANT_WHITE -> ElegantWhiteColors.getExtendedColors(isDark)
            AppTheme.HUNDI_ORANGE -> HundiOrangeColors.getExtendedColors(isDark)
            AppTheme.HUNDI_GREEN -> HundiGreenColors.getExtendedColors(isDark)
            AppTheme.HUNDI_BLUE -> HundiBlueColors.getExtendedColors(isDark)
            AppTheme.HUNDI_PURPLE -> HundiPurpleColors.getExtendedColors(isDark)
        }
    } catch (e: Exception) {
        android.util.Log.e("ThemeColorFactory", "Failed to get extended colors for theme $theme", e)
        // 回退到默认主题
        ElegantWhiteColors.getExtendedColors(isDark)
    }
}