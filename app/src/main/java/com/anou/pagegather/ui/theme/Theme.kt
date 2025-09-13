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

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    secondary = Secondary,
    tertiary = Accent,
    background = BackgroundDark,
    surface = SurfaceDark,
    onPrimary = TextWhite,
    onSecondary = TextWhite,
    onBackground = TextWhite,
    onSurface = TextWhite,
    primaryContainer = PrimaryContainer,
    secondaryContainer = SecondaryContainer
)

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    secondary = Secondary,
    tertiary = Accent,
    background = BackgroundLight,
    surface = SurfaceLight,
    onPrimary = TextWhite,
    onSecondary = TextDark,
    onBackground = TextDark,
    onSurface = TextGray,
    primaryContainer = PrimaryContainer,
    secondaryContainer = SecondaryContainer
)

@Composable
fun PageGatherTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    // 允许手动覆盖动态颜色
    overrideDynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // 允许手动覆盖动态颜色
        overrideDynamicColor -> if (darkTheme) DarkColorScheme else LightColorScheme
        
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            val dynamicScheme = if (darkTheme) {
                dynamicDarkColorScheme(context)
            } else {
                dynamicLightColorScheme(context)
            }
            
            // 确保品牌色一致性
            dynamicScheme.copy(
                primary = Primary,
                secondary = Secondary,
                tertiary = Accent,
                primaryContainer = PrimaryContainer,
                secondaryContainer = SecondaryContainer
            )
        }
        
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // 提供扩展颜色
    ProvideExtendedColors(
        extendedColors = ExtendedColors(
            primaryContainer = colorScheme.primaryContainer,
            secondaryContainer = colorScheme.secondaryContainer,
            success = Success,
            error = Error,
            warning = Warning,
            info = Info,
            titleColor = if (darkTheme) TextWhite else TextDark,
            bodyColor = if (darkTheme) TextWhite else TextDark,
            subtitleColor = if (darkTheme) TextGray else TextGray,
            descriptionColor = if (darkTheme) TextGray else TextGray,
            accentColor = Accent
        )
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

// 定义额外的自定义颜色
data class ExtendedColors(
    val primaryContainer: Color,
    val secondaryContainer: Color,
    val success: Color,
    val error: Color,
    val warning: Color,
    val info: Color,
    val titleColor: Color,
    val bodyColor: Color,
    val subtitleColor: Color,
    val descriptionColor: Color,
    val accentColor: Color
)

// 创建CompositionLocal
private val LocalExtendedColors = staticCompositionLocalOf {
    ExtendedColors(
        primaryContainer = Color.Unspecified,
        secondaryContainer = Color.Unspecified,
        success = Color.Unspecified,
        error = Color.Unspecified,
        warning = Color.Unspecified,
        info = Color.Unspecified,
        titleColor = Color.Unspecified,
        bodyColor = Color.Unspecified,
        subtitleColor = Color.Unspecified,
        descriptionColor = Color.Unspecified,
        accentColor = Color.Unspecified
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