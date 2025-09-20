package com.anou.pagegather.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.anou.pagegather.ui.theme.colors.ElegantWhiteColors
import com.anou.pagegather.ui.theme.colors.HundiBlueColors
import com.anou.pagegather.ui.theme.colors.HundiGreenColors
import com.anou.pagegather.ui.theme.colors.HundiOrangeColors
import com.anou.pagegather.ui.theme.colors.HundiPurpleColors

// Hundi 风格深色主题
private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = TextOnPrimary,
    primaryContainer = Color(0xFF4A2C17), // 深色模式下的橙色容器
    onPrimaryContainer = Color(0xFFFFDCC7),
    
    secondary = Secondary,
    onSecondary = TextWhite,
    secondaryContainer = Color(0xFF1C2328),
    onSecondaryContainer = Color(0xFFCFD8DC),
    
    tertiary = Accent,
    onTertiary = Color(0xFF000000),
    tertiaryContainer = Color(0xFF4A3A00),
    onTertiaryContainer = Color(0xFFFFE082),
    
    background = BackgroundDark,
    onBackground = TextWhite,
    surface = SurfaceDark,
    onSurface = TextWhite,
    surfaceVariant = SurfaceDarkVariant,
    onSurfaceVariant = Color(0xFFCFD8DC),
    
    error = Error,
    onError = TextWhite,
    errorContainer = Color(0xFF4A1C1C),
    onErrorContainer = Color(0xFFFFDAD6),
    
    outline = Color(0xFF8F9699),
    outlineVariant = Color(0xFF3F484A),
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFFE1E3E3),
    inverseOnSurface = Color(0xFF2E3132),
    inversePrimary = Primary
)

// Hundi 风格浅色主题
private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = TextOnPrimary,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = Color(0xFF4A2C17),
    
    secondary = Secondary,
    onSecondary = TextWhite,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = Color(0xFF1C2328),
    
    tertiary = Accent,
    onTertiary = Color(0xFF000000),
    tertiaryContainer = TertiaryContainer,
    onTertiaryContainer = Color(0xFF4A3A00),
    
    background = BackgroundLight,
    onBackground = TextPrimary,
    surface = SurfaceLight,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = TextSecondary,
    
    error = Error,
    onError = TextWhite,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF4A1C1C),
    
    outline = BorderColor,
    outlineVariant = DividerColor,
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFF2E3132),
    inverseOnSurface = Color(0xFFE1E3E3),
    inversePrimary = Color(0xFFFFB59D)
)

@Composable
fun PageGatherTheme(
    theme: AppTheme = AppTheme.getDefault(),
    darkTheme: Boolean = isSystemInDarkTheme(),
    // 启用动态颜色支持
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = getColorSchemeForTheme(theme, darkTheme, dynamicColor)
    val extendedColors = getExtendedColorsForTheme(theme, darkTheme)

    ProvideExtendedColors(extendedColors = extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

/**
 * 获取指定主题的颜色方案（支持动态颜色）
 */
@Composable
fun getColorSchemeForTheme(theme: AppTheme, isDark: Boolean, useDynamicColor: Boolean = false): androidx.compose.material3.ColorScheme {
    // 检查是否支持动态颜色且启用动态颜色
    if (useDynamicColor && theme.supportsDynamicColor) {
        // 检查Android版本是否支持动态颜色（API 31+）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val context = LocalContext.current
            return if (isDark) {
                dynamicDarkColorScheme(context)
            } else {
                dynamicLightColorScheme(context)
            }
        }
    }
    
    // 使用缓存的传统颜色方案
    return ThemeCache.getColorScheme(theme, isDark)
}

/**
 * 获取指定主题的颜色方案（非Composable版本，不支持动态颜色）
 */
fun getColorSchemeForThemeNonComposable(theme: AppTheme, isDark: Boolean): androidx.compose.material3.ColorScheme {
    // 使用缓存的传统颜色方案
    return ThemeCache.getColorScheme(theme, isDark)
}

/**
 * 获取指定主题的扩展颜色（简化版本，直接返回Material扩展颜色）
 */
fun getExtendedColorsForTheme(theme: AppTheme, isDark: Boolean): ExtendedColors {
    return try {
        val simpleColors = when (theme) {
            AppTheme.ELEGANT_WHITE -> ElegantWhiteColors.getExtendedColors(isDark)
            AppTheme.HUNDI_ORANGE -> HundiOrangeColors.getExtendedColors(isDark)
            AppTheme.HUNDI_GREEN -> HundiGreenColors.getExtendedColors(isDark)
            AppTheme.HUNDI_BLUE -> HundiBlueColors.getExtendedColors(isDark)
            AppTheme.HUNDI_PURPLE -> HundiPurpleColors.getExtendedColors(isDark)
        }

        // 转换为 Material 扩展颜色
        ExtendedColors(
            // 容器颜色 - 使用合理的默认值
            primaryContainer = Color.Transparent,
            secondaryContainer = Color.Transparent,
            tertiaryContainer = Color.Transparent,

            // 状态颜色
            success = simpleColors.success,
            error = Color(0xFFE57373),
            warning = simpleColors.warning,
            info = simpleColors.info,

            // 文字颜色层次 - 使用合理的默认值
            titleColor = Color(0xFF212121),
            bodyColor = Color(0xFF212121),
            subtitleColor = Color(0xFF757575),
            descriptionColor = Color(0xFF9E9E9E),

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
    } catch (e: Exception) {
        android.util.Log.e("ThemeColorFactory", "Failed to get extended colors for theme $theme", e)
        // 回退到默认扩展颜色
        getExtendedColorsForTheme(AppTheme.getDefault(), isDark)
    }
}

// Hundi 风格扩展颜色系统
data class ExtendedColors(
    // 容器颜色
    val primaryContainer: Color,
    val secondaryContainer: Color,
    val tertiaryContainer: Color,
    
    // 状态颜色
    val success: Color,
    val error: Color,
    val warning: Color,
    val info: Color,
    
    // 文字颜色层次
    val titleColor: Color,
    val bodyColor: Color,
    val subtitleColor: Color,
    val descriptionColor: Color,
    
    // 功能颜色
    val accentColor: Color,
    val bookmarkColor: Color,
    val readingProgress: Color,
    val noteHighlight: Color,
    
    // 渐变颜色
    val gradientStart: Color,
    val gradientEnd: Color,
    val gradientSecondary: Color,
    
    // 中性色
    val neutral100: Color,
    val neutral200: Color,
    val neutral300: Color,
    val neutral500: Color,
    val neutral700: Color,
    val neutral900: Color,
    
    // 边框和分割线
    val borderColor: Color,
    val dividerColor: Color,
    val shadowColor: Color
)

// 创建CompositionLocal
private val LocalExtendedColors = staticCompositionLocalOf {
    ExtendedColors(
        primaryContainer = Color.Unspecified,
        secondaryContainer = Color.Unspecified,
        tertiaryContainer = Color.Unspecified,
        success = Color.Unspecified,
        error = Color.Unspecified,
        warning = Color.Unspecified,
        info = Color.Unspecified,
        titleColor = Color.Unspecified,
        bodyColor = Color.Unspecified,
        subtitleColor = Color.Unspecified,
        descriptionColor = Color.Unspecified,
        accentColor = Color.Unspecified,
        bookmarkColor = Color.Unspecified,
        readingProgress = Color.Unspecified,
        noteHighlight = Color.Unspecified,
        gradientStart = Color.Unspecified,
        gradientEnd = Color.Unspecified,
        gradientSecondary = Color.Unspecified,
        neutral100 = Color.Unspecified,
        neutral200 = Color.Unspecified,
        neutral300 = Color.Unspecified,
        neutral500 = Color.Unspecified,
        neutral700 = Color.Unspecified,
        neutral900 = Color.Unspecified,
        borderColor = Color.Unspecified,
        dividerColor = Color.Unspecified,
        shadowColor = Color.Unspecified
    )
}

// 提供组合函数
@Composable
fun ProvideExtendedColors(
    extendedColors: ExtendedColors,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalExtendedColors provides extendedColors) {
        content()
    }
}

// 扩展MaterialTheme以访问自定义颜色
val MaterialTheme.extendedColors: ExtendedColors
    @Composable
    @ReadOnlyComposable
    get() = LocalExtendedColors.current