package com.anou.pagegather.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color

/**
 * 主题自定义接口
 * 为未来的自定义主题功能预留接口
 */
interface ThemeCustomization {
    
    /**
     * 创建自定义主题
     * 
     * @param name 主题名称
     * @param primaryColor 主色调
     * @param secondaryColor 次要色调 (可选，如果为空则自动生成)
     * @param description 主题描述
     * @param emoji 主题表情符号
     * @return 创建的自定义主题
     */
    suspend fun createCustomTheme(
        name: String,
        primaryColor: Color,
        secondaryColor: Color? = null,
        description: String = "",
        emoji: String = "🎨"
    ): CustomTheme
    
    /**
     * 基于主色调生成完整的颜色方案
     * 
     * @param baseColor 基础颜色
     * @param isDark 是否为暗色模式
     * @return 生成的颜色方案
     */
    fun generateColorScheme(
        baseColor: Color,
        isDark: Boolean = false
    ): ColorScheme
    
    /**
     * 生成协调的配色方案
     * 
     * @param primaryColor 主色调
     * @param colorHarmony 色彩和谐类型
     * @return 协调的颜色列表
     */
    fun generateHarmoniousColors(
        primaryColor: Color,
        colorHarmony: ColorHarmony = ColorHarmony.COMPLEMENTARY
    ): List<Color>
    
    /**
     * 验证颜色对比度是否符合可访问性标准
     * 
     * @param foreground 前景色
     * @param background 背景色
     * @param level 可访问性级别
     * @return 是否符合标准
     */
    fun validateColorContrast(
        foreground: Color,
        background: Color,
        level: AccessibilityLevel = AccessibilityLevel.AA
    ): Boolean
    
    /**
     * 调整颜色以满足对比度要求
     * 
     * @param foreground 前景色
     * @param background 背景色
     * @param targetRatio 目标对比度
     * @return 调整后的前景色
     */
    fun adjustColorForContrast(
        foreground: Color,
        background: Color,
        targetRatio: Float = 4.5f
    ): Color
}

/**
 * 主题提供者接口
 * 支持插件化主题系统
 */
interface ThemeProvider {
    
    /**
     * 主题唯一标识符
     */
    val themeId: String
    
    /**
     * 主题显示名称
     */
    val displayName: String
    
    /**
     * 主题描述
     */
    val description: String
    
    /**
     * 主题表情符号
     */
    val emoji: String
    
    /**
     * 主题版本
     */
    val version: String
    
    /**
     * 主题作者
     */
    val author: String
    
    /**
     * 获取亮色模式颜色方案
     */
    fun getLightColorScheme(): ColorScheme
    
    /**
     * 获取暗色模式颜色方案
     */
    fun getDarkColorScheme(): ColorScheme
    
    /**
     * 获取扩展颜色
     */
    fun getExtendedColors(isDark: Boolean): ExtendedColors
    
    /**
     * 验证主题是否有效
     */
    fun validate(): ThemeValidationResult
}

/**
 * 主题注册管理器
 * 管理动态主题的注册和发现
 */
interface ThemeRegistry {
    
    /**
     * 注册主题提供者
     */
    fun registerTheme(provider: ThemeProvider): Boolean
    
    /**
     * 注销主题提供者
     */
    fun unregisterTheme(themeId: String): Boolean
    
    /**
     * 获取所有可用主题
     */
    fun getAvailableThemes(): List<ThemeProvider>
    
    /**
     * 根据ID获取主题提供者
     */
    fun getThemeProvider(themeId: String): ThemeProvider?
    
    /**
     * 检查主题是否已注册
     */
    fun isThemeRegistered(themeId: String): Boolean
    
    /**
     * 获取注册的主题数量
     */
    fun getRegisteredThemeCount(): Int
}

/**
 * 自定义主题数据类
 */
data class CustomTheme(
    val id: String,
    val name: String,
    val description: String,
    val emoji: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val lightColorScheme: ColorScheme,
    val darkColorScheme: ColorScheme,
    val extendedLightColors: ExtendedColors,
    val extendedDarkColors: ExtendedColors,
    val createdAt: Long = System.currentTimeMillis(),
    val version: String = "1.0.0",
    val author: String = "User"
) {
    /**
     * 转换为 AppTheme 枚举 (如果需要兼容现有系统)
     */
    fun toAppTheme(): AppTheme? {
        // 这里可以实现自定义主题到 AppTheme 的转换逻辑
        // 或者扩展 AppTheme 以支持动态主题
        return null
    }
}

/**
 * 色彩和谐类型
 */
enum class ColorHarmony {
    COMPLEMENTARY,      // 互补色
    ANALOGOUS,          // 类似色
    TRIADIC,           // 三角色
    SPLIT_COMPLEMENTARY, // 分裂互补色
    TETRADIC,          // 四角色
    MONOCHROMATIC      // 单色调
}

/**
 * 可访问性级别
 */
enum class AccessibilityLevel(val ratio: Float) {
    AA(4.5f),          // WCAG AA 标准
    AAA(7.0f),         // WCAG AAA 标准
    AA_LARGE(3.0f)     // WCAG AA 大文本标准
}

/**
 * 主题验证结果
 */
data class ThemeValidationResult(
    val isValid: Boolean,
    val errors: List<String> = emptyList(),
    val warnings: List<String> = emptyList()
) {
    val hasErrors: Boolean get() = errors.isNotEmpty()
    val hasWarnings: Boolean get() = warnings.isNotEmpty()
}

/**
 * 主题扩展管理器
 * 为未来功能提供扩展点
 */
interface ThemeExtensionManager {
    
    /**
     * 注册主题扩展
     */
    fun registerExtension(extension: ThemeExtension)
    
    /**
     * 获取所有扩展
     */
    fun getExtensions(): List<ThemeExtension>
    
    /**
     * 应用扩展到主题
     */
    suspend fun applyExtensions(theme: AppTheme): AppTheme
}

/**
 * 主题扩展接口
 */
interface ThemeExtension {
    
    /**
     * 扩展名称
     */
    val name: String
    
    /**
     * 扩展版本
     */
    val version: String
    
    /**
     * 是否启用
     */
    val isEnabled: Boolean
    
    /**
     * 应用扩展
     */
    suspend fun apply(theme: AppTheme): AppTheme
    
    /**
     * 验证扩展兼容性
     */
    fun isCompatible(theme: AppTheme): Boolean
}