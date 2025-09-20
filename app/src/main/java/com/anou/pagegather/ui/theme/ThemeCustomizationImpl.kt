package com.anou.pagegather.ui.theme

import android.graphics.Color as AndroidColor
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlin.math.max
import kotlin.math.min

/**
 * 主题自定义接口实现
 */
class ThemeCustomizationImpl : ThemeCustomization {
    
    /**
     * 创建自定义主题
     */
    override suspend fun createCustomTheme(
        name: String,
        primaryColor: Color,
        secondaryColor: Color?,
        description: String,
        emoji: String
    ): CustomTheme {
        // 如果未提供次要颜色，则基于主色调生成
        val actualSecondaryColor = secondaryColor ?: generateSecondaryColor(primaryColor)
        
        // 生成亮色和暗色主题方案
        val lightColorScheme = generateColorScheme(primaryColor, false)
        val darkColorScheme = generateColorScheme(primaryColor, true)
        
        // 生成扩展颜色
        val extendedLightColors = generateExtendedColors(primaryColor, false)
        val extendedDarkColors = generateExtendedColors(primaryColor, true)
        
        return CustomTheme(
            id = "custom_${System.currentTimeMillis()}",
            name = name,
            description = description,
            emoji = emoji,
            primaryColor = primaryColor,
            secondaryColor = actualSecondaryColor,
            lightColorScheme = lightColorScheme,
            darkColorScheme = darkColorScheme,
            extendedLightColors = extendedLightColors,
            extendedDarkColors = extendedDarkColors
        )
    }
    
    /**
     * 基于主色调生成完整的颜色方案
     */
    override fun generateColorScheme(
        baseColor: Color,
        isDark: Boolean
    ): ColorScheme {
        if (isDark) {
            return darkColorScheme(
                primary = baseColor,
                onPrimary = getContrastColor(baseColor),
                primaryContainer = adjustColorBrightness(baseColor, 0.3f),
                onPrimaryContainer = getContrastColor(adjustColorBrightness(baseColor, 0.3f)),
                secondary = adjustColorHue(baseColor, 30f),
                onSecondary = getContrastColor(adjustColorHue(baseColor, 30f)),
                secondaryContainer = adjustColorBrightness(adjustColorHue(baseColor, 30f), 0.3f),
                onSecondaryContainer = getContrastColor(adjustColorBrightness(adjustColorHue(baseColor, 30f), 0.3f)),
                tertiary = adjustColorHue(baseColor, 60f),
                onTertiary = getContrastColor(adjustColorHue(baseColor, 60f)),
                tertiaryContainer = adjustColorBrightness(adjustColorHue(baseColor, 60f), 0.3f),
                onTertiaryContainer = getContrastColor(adjustColorBrightness(adjustColorHue(baseColor, 60f), 0.3f)),
                background = Color(0xFF121212),
                onBackground = Color(0xFFE0E0E0),
                surface = Color(0xFF1E1E1E),
                onSurface = Color(0xFFE0E0E0),
                surfaceVariant = Color(0xFF2D2D2D),
                onSurfaceVariant = Color(0xFFC0C0C0),
                outline = adjustColorBrightness(baseColor, 0.5f),
                inverseOnSurface = Color(0xFF121212),
                inverseSurface = Color(0xFFE0E0E0)
            )
        } else {
            return lightColorScheme(
                primary = baseColor,
                onPrimary = getContrastColor(baseColor),
                primaryContainer = adjustColorBrightness(baseColor, 0.8f),
                onPrimaryContainer = baseColor,
                secondary = adjustColorHue(baseColor, 30f),
                onSecondary = getContrastColor(adjustColorHue(baseColor, 30f)),
                secondaryContainer = adjustColorBrightness(adjustColorHue(baseColor, 30f), 0.8f),
                onSecondaryContainer = adjustColorHue(baseColor, 30f),
                tertiary = adjustColorHue(baseColor, 60f),
                onTertiary = getContrastColor(adjustColorHue(baseColor, 60f)),
                tertiaryContainer = adjustColorBrightness(adjustColorHue(baseColor, 60f), 0.8f),
                onTertiaryContainer = adjustColorHue(baseColor, 60f),
                background = Color(0xFFFCFCFC),
                onBackground = Color(0xFF121212),
                surface = Color(0xFFF5F5F5),
                onSurface = Color(0xFF121212),
                surfaceVariant = Color(0xFFE0E0E0),
                onSurfaceVariant = Color(0xFF424242),
                outline = adjustColorBrightness(baseColor, 0.5f),
                inverseOnSurface = Color(0xFFFCFCFC),
                inverseSurface = Color(0xFF121212)
            )
        }
    }
    
    /**
     * 生成协调的配色方案
     */
    override fun generateHarmoniousColors(
        primaryColor: Color,
        colorHarmony: ColorHarmony
    ): List<Color> {
        return when (colorHarmony) {
            ColorHarmony.COMPLEMENTARY -> {
                listOf(
                    primaryColor,
                    adjustColorHue(primaryColor, 180f) // 互补色
                )
            }
            ColorHarmony.ANALOGOUS -> {
                listOf(
                    adjustColorHue(primaryColor, -30f), // 类似色1
                    primaryColor,
                    adjustColorHue(primaryColor, 30f)  // 类似色2
                )
            }
            ColorHarmony.TRIADIC -> {
                listOf(
                    primaryColor,
                    adjustColorHue(primaryColor, 120f), // 三角色1
                    adjustColorHue(primaryColor, 240f)  // 三角色2
                )
            }
            ColorHarmony.SPLIT_COMPLEMENTARY -> {
                listOf(
                    primaryColor,
                    adjustColorHue(primaryColor, 150f), // 分裂互补色1
                    adjustColorHue(primaryColor, 210f)  // 分裂互补色2
                )
            }
            ColorHarmony.TETRADIC -> {
                listOf(
                    primaryColor,
                    adjustColorHue(primaryColor, 90f),  // 四角色1
                    adjustColorHue(primaryColor, 180f), // 四角色2
                    adjustColorHue(primaryColor, 270f)  // 四角色3
                )
            }
            ColorHarmony.MONOCHROMATIC -> {
                listOf(
                    adjustColorBrightness(primaryColor, 0.2f), // 单色调1
                    adjustColorBrightness(primaryColor, 0.4f), // 单色调2
                    primaryColor,                              // 原色
                    adjustColorBrightness(primaryColor, 0.8f), // 单色调3
                    adjustColorBrightness(primaryColor, 1.0f)  // 单色调4
                )
            }
        }
    }
    
    /**
     * 验证颜色对比度是否符合可访问性标准
     */
    override fun validateColorContrast(
        foreground: Color,
        background: Color,
        level: AccessibilityLevel
    ): Boolean {
        val contrastRatio = calculateContrastRatio(foreground, background)
        return contrastRatio >= level.ratio
    }
    
    /**
     * 调整颜色以满足对比度要求
     */
    override fun adjustColorForContrast(
        foreground: Color,
        background: Color,
        targetRatio: Float
    ): Color {
        var adjustedColor = foreground
        var contrastRatio = calculateContrastRatio(adjustedColor, background)
        
        // 如果对比度已经满足要求，直接返回
        if (contrastRatio >= targetRatio) {
            return adjustedColor
        }
        
        // 尝试调整亮度以提高对比度
        val isForegroundLighter = getLuminance(adjustedColor) > getLuminance(background)
        val makeLighter = (isForegroundLighter && getLuminance(background) < 0.5f) ||
                (!isForegroundLighter && getLuminance(background) > 0.5f)
        
        var attempts = 0
        while (contrastRatio < targetRatio && attempts < 20) {
            adjustedColor = if (makeLighter) {
                adjustColorBrightness(adjustedColor, 0.05f)
            } else {
                adjustColorBrightness(adjustedColor, -0.05f)
            }
            
            contrastRatio = calculateContrastRatio(adjustedColor, background)
            attempts++
        }
        
        return adjustedColor
    }
    
    /**
     * 生成次要颜色
     */
    private fun generateSecondaryColor(primaryColor: Color): Color {
        // 基于主色调生成次要颜色，调整色相
        return adjustColorHue(primaryColor, 30f)
    }
    
    /**
     * 生成扩展颜色
     */
    private fun generateExtendedColors(primaryColor: Color, isDark: Boolean): ExtendedColors {
        val backgroundColor = if (isDark) Color(0xFF121212) else Color(0xFFFCFCFC)
        val textColor = if (isDark) Color(0xFFE0E0E0) else Color(0xFF121212)
        
        return ExtendedColors(
            primaryContainer = adjustColorBrightness(primaryColor, if (isDark) 0.3f else 0.8f),
            secondaryContainer = adjustColorBrightness(adjustColorHue(primaryColor, 30f), if (isDark) 0.3f else 0.8f),
            tertiaryContainer = adjustColorBrightness(adjustColorHue(primaryColor, 60f), if (isDark) 0.3f else 0.8f),
            success = Color(0xFF4CAF50),
            error = Color(0xFFF44336),
            warning = Color(0xFFFF9800),
            info = Color(0xFF2196F3),
            titleColor = textColor,
            bodyColor = adjustColorBrightness(textColor, if (isDark) 0.1f else -0.1f),
            subtitleColor = adjustColorBrightness(textColor, if (isDark) 0.2f else -0.2f),
            descriptionColor = adjustColorBrightness(textColor, if (isDark) 0.3f else -0.3f),
            accentColor = adjustColorHue(primaryColor, 180f),
            bookmarkColor = Color(0xFFFFD700),
            readingProgress = adjustColorHue(primaryColor, 120f),
            noteHighlight = adjustColorHue(primaryColor, 240f),
            gradientStart = primaryColor,
            gradientEnd = adjustColorHue(primaryColor, 30f),
            gradientSecondary = adjustColorHue(primaryColor, 60f),
            neutral100 = if (isDark) Color(0xFF1E1E1E) else Color(0xFFF5F5F5),
            neutral200 = if (isDark) Color(0xFF2D2D2D) else Color(0xFFEEEEEE),
            neutral300 = if (isDark) Color(0xFF3C3C3C) else Color(0xFFE0E0E0),
            neutral500 = if (isDark) Color(0xFF757575) else Color(0xFF9E9E9E),
            neutral700 = if (isDark) Color(0xFF9E9E9E) else Color(0xFF616161),
            neutral900 = if (isDark) Color(0xFFE0E0E0) else Color(0xFF212121),
            borderColor = if (isDark) Color(0xFF3C3C3C) else Color(0xFFE0E0E0),
            dividerColor = if (isDark) Color(0xFF2D2D2D) else Color(0xFFEEEEEE),
            shadowColor = if (isDark) Color(0x40000000) else Color(0x1A000000)
        )
    }
    
    /**
     * 计算颜色对比度比率
     */
    private fun calculateContrastRatio(foreground: Color, background: Color): Float {
        val fgLuminance = getLuminance(foreground) + 0.05f
        val bgLuminance = getLuminance(background) + 0.05f
        
        return if (fgLuminance > bgLuminance) {
            fgLuminance / bgLuminance
        } else {
            bgLuminance / fgLuminance
        }
    }
    
    /**
     * 获取颜色亮度
     */
    private fun getLuminance(color: Color): Float {
        val red = color.red
        val green = color.green
        val blue = color.blue
        
        // 使用相对亮度公式
        return 0.2126f * red + 0.7152f * green + 0.0722f * blue
    }
    
    /**
     * 获取对比色（黑或白）
     */
    private fun getContrastColor(color: Color): Color {
        val luminance = getLuminance(color)
        return if (luminance > 0.5f) Color.Black else Color.White
    }
    
    /**
     * 调整颜色亮度
     */
    private fun adjustColorBrightness(color: Color, amount: Float): Color {
        val red = color.red
        val green = color.green
        val blue = color.blue
        
        val adjustedRed = max(0f, min(1f, red + amount))
        val adjustedGreen = max(0f, min(1f, green + amount))
        val adjustedBlue = max(0f, min(1f, blue + amount))
        
        return Color(adjustedRed, adjustedGreen, adjustedBlue)
    }
    
    /**
     * 调整颜色色相
     */
    private fun adjustColorHue(color: Color, degrees: Float): Color {
        // 将颜色转换为HSV
        val hsv = FloatArray(3)
        AndroidColor.RGBToHSV(
            (color.red * 255).toInt(),
            (color.green * 255).toInt(),
            (color.blue * 255).toInt(),
            hsv
        )
        
        // 调整色相
        hsv[0] = (hsv[0] + degrees) % 360f
        if (hsv[0] < 0) hsv[0] += 360f
        
        // 转换回RGB
        val rgb = AndroidColor.HSVToColor(hsv)
        return Color(rgb)
    }
}