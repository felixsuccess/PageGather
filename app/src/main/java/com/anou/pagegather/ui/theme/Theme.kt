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
    // 禁用动态颜色以确保主题一致性
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = getColorSchemeForTheme(theme, darkTheme)
    val extendedColors = getExtendedColorsForTheme(theme, darkTheme).toMaterialExtendedColors()

    ProvideExtendedColors(extendedColors = extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
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